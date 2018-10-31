package com.sidakmanchanda.zookdns;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.KeeperException;

public class ZookDNS {
	private static final Logger log = LogManager.getLogger(ZookDNS.class);

	public static void main(String[] args) throws IOException, ParseException, InterruptedException, KeeperException {	
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
		DNSDatabase zk = new ZKDatabase("localhost");
		for (Zone zone : zones) {
			zk.writeRecords(zone.getRecords());
		}
		// Accept UDP Connections
		(new Thread(new UDPServer("127.0.0.1", 4500, 10, zk))).start();
		
	}

}
