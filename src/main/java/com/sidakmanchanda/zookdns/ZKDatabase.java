package com.sidakmanchanda.zookdns;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

/**
 * Represents the ZooKeeper instance. Writes and fetches ResourceRecords.
 * 
 * Currently stores records in /records/(host)/(version)/(RecordType)/(record)
 * 
 * As indicated resource records are versioned at the host level in the hierarchy
 * 
 * @see ResourceRecord
 * @see RecordType
 * @author sidak
 */
public class ZKDatabase implements DNSDatabase {
	private static ZooKeeper zookeeper;
	private static ZKConnection zkConnection;
	
	private static String RECORDS_PATH = "/records";
	private static String VERSION_PREFIX = "version";
	private static String RECORD_PREFIX = "record";
	private static String PATH_DELIMETER = "/";
	
	private static final ArrayList<ACL> OPEN_ACL = ZooDefs.Ids.OPEN_ACL_UNSAFE;
	
	// Stores the version of records to be accessed for a given host path
	private Map<String,Integer> hostPathToVersion;
	
	/**
	 * Connect to a ZooKeeper instance and retrieve a ZooKeeper object
	 * 
	 * @param host
	 * @throws IOException
	 */
	public ZKDatabase(String host) throws IOException {
		zkConnection = new ZKConnection();
		hostPathToVersion = new HashMap<String, Integer>();
		try {
			zookeeper = zkConnection.connect(host);
			if (!pathExists(RECORDS_PATH)) {
				createPersistentNode(RECORDS_PATH, null);
			}
		} catch(KeeperException | InterruptedException e) {
			throw new IOException("failed to connect to zookeeper instance", e);
		}
	}

	/**
	 * Close the connection to ZooKeeper
	 * 
	 * @throws InterruptedException
	 */
	public void closeConnection() throws InterruptedException {
		zkConnection.close();
	}
	
	/**
	 * Write the given resource records to ZooKeeper
	 * 
	 * @param records 
	 * @throws IOException 
	 */
	public void writeRecords(ResourceRecord[] records) throws IOException {
		for (ResourceRecord record : records) {
			writeRecord(record);
		}
	}
	
	/**
	 * Write the given resource record to ZooKeeper. Records are written as sequential and persistent znodes.
	 * 
	 * @param record ResourceRecord to be stored
	 * @throws IOException 
	 */
	public void writeRecord(ResourceRecord rr) throws IOException {
		try {
			// Create name path if it does not exist
			String hostPath = getHostPath(rr.getName().getStringName());
			if (!pathExists(hostPath)) {
				createPersistentNode(hostPath, null);
			}
			
			// Create version path for record if it does not exist
			Integer version = getHostVersion(hostPath);
			if (version == null) {
				version = createNewHostVersion(hostPath, null);
			}
			String versionedHostPath = hostPath + PATH_DELIMETER + VERSION_PREFIX + formatZNodeVersion(version);
			
			// create record type path if it does not exist
			String recordTypePath = getRecordTypePath(versionedHostPath, rr.getType());
			if (!pathExists(recordTypePath)) {
				createPersistentNode(recordTypePath, null);
			}
			
			// create a new sequential node at the child path
			createSequentialNode(recordTypePath + PATH_DELIMETER + RECORD_PREFIX, serializeRecord(rr));
		} catch(KeeperException | InterruptedException e) {
			throw new IOException("failed to write record", e);
		}
	}
	
	/**
	 * Creates a new version zNode for the given host.
	 * 
	 * @param hostPath - String 
	 * @param bytes - byte[]
	 * @return Integer - new host version
	 * @throws InterruptedException 
	 * @throws KeeperException 
	 */
	private Integer createNewHostVersion(String hostPath, byte[] bytes) throws KeeperException, InterruptedException {
		// Create new version znode under the host path
		String versionedHostPath = hostPath + PATH_DELIMETER + VERSION_PREFIX;
		createSequentialNode(versionedHostPath, null);
		
		// Set the version for the host. To be used future queries and writes through this instance
		int currentVersion = getSequentialNodeVersion(hostPath);
		hostPathToVersion.put(hostPath, currentVersion);
		
		return currentVersion;
	}
	
	/**
	 * Retrieves a ResourceRecord from ZooKeeper. Returns null if no such record found.
	 * 
	 * @param name String: Fully qualified name associated with record
	 * @param rt RecordType: Type of record being retrieved
	 * @return ResourceRecord
	 * @throws IOException 
	 */
	public ResourceRecord[] retrieveRecords(String name, RecordType rt) throws IOException {
		try {
			// Get Host Path
			String hostPath = getHostPath(name);
			if (!pathExists(hostPath)) return null;
			
			// Get version path for the host. If a version to use is not set use the latest version 
			Integer version = getHostVersion(hostPath);
			if (version == null) version = getSequentialNodeVersion(hostPath);
			String versionedHostPath = hostPath + PATH_DELIMETER + VERSION_PREFIX + formatZNodeVersion(version);
			
			// Get the path leading up to the type of the record
			String recordTypePath = getRecordTypePath(versionedHostPath, rt);
			if(!pathExists(recordTypePath)) return null;
			
			// Get a list of all the records of the record type
			List<String> recordNames = getNodeChildren(recordTypePath);
			
			// Fetch data for all records of the record type. Deserialize into record objects
			ResourceRecord[] records = new ResourceRecord[recordNames.size()];
			for(int x = 0; x < recordNames.size(); x++) {
				String curPath = recordTypePath + PATH_DELIMETER + recordNames.get(x);
				byte[] data = fetchNodeData(curPath);
				records[x] = deserializeRecord(data);
			}
			
			return records;
		} catch (KeeperException | InterruptedException e) {
			throw new IOException("failed to retrieve record", e);
		}
	}

	private byte[] serializeRecord(ResourceRecord rr) throws IOException {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		ObjectOutputStream oa = new ObjectOutputStream(bao);
		
		oa.writeObject(rr);
		return bao.toByteArray();
	}
	
	private ResourceRecord deserializeRecord(byte[] recordBytes) throws IOException {
		ByteArrayInputStream bai = new ByteArrayInputStream(recordBytes);
		ObjectInputStream oi = new ObjectInputStream(bai);
		ResourceRecord rr;
		try {
			rr = (ResourceRecord) oi.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException("invalid data at znode");
		}
		return rr;
	}
	
	private void createPersistentNode(String path, byte[] data) throws KeeperException, InterruptedException {
		zookeeper.create(path, data, OPEN_ACL, CreateMode.PERSISTENT);
	}
 	
	private void createSequentialNode(String path, byte[] data) throws KeeperException, InterruptedException {
		zookeeper.create(path, data, OPEN_ACL, CreateMode.PERSISTENT_SEQUENTIAL);
	}
	
	private byte[] fetchNodeData(String path) throws KeeperException, InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		byte[] data = zookeeper.getData(path, false, null);
		return data;
	}
	
	private List<String> getNodeChildren(String path) throws KeeperException, InterruptedException {
		 return zookeeper.getChildren(path, false);
	}
	
	private boolean pathExists(String path) throws KeeperException, InterruptedException {
		Stat stat = zookeeper.exists(path, true);
		if (stat != null) return true;
		return false;
	}
	
	private Stat getNodeStats(String path) throws KeeperException, InterruptedException {
		Stat stat = zookeeper.exists(path, true);
		return stat;
	}
	
	private String getRecordTypePath(String namePath, RecordType rt) {
		return namePath + PATH_DELIMETER + rt.toString();
	}
	
	private String getHostPath(String recordName) {
		return RECORDS_PATH + PATH_DELIMETER + recordName;
	}
	
	private int getSequentialNodeVersion(String parentPath) throws KeeperException, InterruptedException {
		Stat stat = getNodeStats(parentPath);
		return stat.getCversion() - 1;
	}
	
	private String formatZNodeVersion(int version) {
		return String.format(Locale.ENGLISH, "%010d", version);
	}

	private Integer getHostVersion(String hostPath) {
		return hostPathToVersion.get(hostPath);
	}
}
