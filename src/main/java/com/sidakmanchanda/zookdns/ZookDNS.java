package com.sidakmanchanda.zookdns;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.KeeperException;

public class ZookDNS {
	private static final Logger log = LogManager.getLogger(ZookDNS.class);

	public static void main(String[] args) throws IOException {	
		// Config class which implements multiple configuration interfaces
		ZookDNSConfig config = new ZookDNSConfig(getProperties());
		
		// Create zones from Master Files
		ArrayList<Zone> zones = new ArrayList<Zone>();
		File folder = new File("master_files/");
		for (File file : folder.listFiles()) {
			try {
				Zone zone = new Zone(file);
				zones.add(zone);
			} catch (IOException | ParseException e) {
				log.warn("malformed zone file, aborting parse: " + file.getName(), e);
			}
		}
		// Write Records to ZooKeeper
		DNSDatabase zk = new ZKDatabase(config);
		for (Zone zone : zones) {
			zk.writeRecords(zone.getRecords());
		}
		// Accept UDP Connections
		(new Thread(new UDPServer(config, zk))).start();
		
	}
	
	public static Properties getProperties() throws IOException {
		Properties properties = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream in = loader.getResourceAsStream("zookdns.properties");
		properties.load(in);
		return properties;
	}

}
