package com.sidakmanchanda.zookdns;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Locale;
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
 * Currently stores records in /records/(record name)/(RecordType)/record(sequence_id)
 * 
 * @see ResourceRecord
 * @see RecordType
 * @author sidak
 */
public class ZKDatabase implements DNSDatabase {
	private static ZooKeeper zookeeper;
	private static ZKConnection zkConnection;
	
	private static String PARENT_PATH = "/records";
	private static String RECORD_PREFIX = "record";
	private static String PATH_DELIMETER = "/";
	
	private static final ArrayList<ACL> OPEN_ACL = ZooDefs.Ids.OPEN_ACL_UNSAFE;
	
	/**
	 * Connect to a ZooKeeper instance and retrieve a ZooKeeper object
	 * 
	 * @param host
	 * @throws IOException
	 */
	public ZKDatabase(String host) throws IOException {
		zkConnection = new ZKConnection();
		try {
			zookeeper = zkConnection.connect(host);
			if (!pathExists(PARENT_PATH)) {
				createPersistentNode(PARENT_PATH, null);
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
			// Create parent path if it does not exist
			String namePath = getNamePath(rr.getName().getStringName());
			if (!pathExists(namePath)) {
				createPersistentNode(namePath, null);
			}
			
			// create child path if it does not exist
			String recordPath = getRecordPath(namePath, rr.getType());
			if (!pathExists(recordPath)) {
				createPersistentNode(recordPath, null);
			}
			
			// create a new sequential node at the child path
			createSequentialNode(recordPath + PATH_DELIMETER + RECORD_PREFIX, serializeRecord(rr));
		} catch(KeeperException | InterruptedException e) {
			throw new IOException("failed to write record", e);
		}
	}
	
	/**
	 * Retrieves a ResourceRecord from ZooKeeper. Returns null if no such record found.
	 * 
	 * @param name String: Fully qualified name associated with record
	 * @param rt RecordType: Type of record being retrieved
	 * @return ResourceRecord
	 * @throws IOException 
	 */
	public ResourceRecord retrieveRecord(String name, RecordType rt) throws IOException {
		try {
			String recordPath = getRecordPath(getNamePath(name), rt);
			Stat stat = getNodeStats(recordPath);
			// Record Not found
			if (stat == null) return null;
			
			// CVersion is the counter used for sequential zNode creation
			int lastestRecordVersion = stat.getCversion() - 1;
			
			// Adds cversion to child path
			String recordVersionedPath = recordPath + PATH_DELIMETER + RECORD_PREFIX + String.format(Locale.ENGLISH, "%010d", lastestRecordVersion);
			byte[] data = fetchNodeData(recordVersionedPath);

			return deserializeRecord(data);
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
	
	private boolean pathExists(String path) throws KeeperException, InterruptedException {
		Stat stat = zookeeper.exists(path, true);
		if (stat != null) return true;
		return false;
	}
	
	private Stat getNodeStats(String path) throws KeeperException, InterruptedException {
		Stat stat = zookeeper.exists(path, true);
		return stat;
	}
	
	private String getRecordPath(String namePath, RecordType rt) {
		return namePath + PATH_DELIMETER + rt.toString();
	}
	
	private String getNamePath(String recordName) {
		String path = PARENT_PATH + PATH_DELIMETER + recordName;
		return path;
	}
	
}
