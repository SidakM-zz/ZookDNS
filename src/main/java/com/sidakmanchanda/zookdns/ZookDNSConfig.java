package com.sidakmanchanda.zookdns;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class ZookDNSConfig implements ServerConfig, DatabaseConfig {
	InetAddress databaseAddress;
	int databasePort;
	
	InetAddress serverAddress;
	int serverPort;
	int serverThreadPool;
	
	public ZookDNSConfig(Properties properties) throws IOException {
		databaseAddress = InetAddress.getByName(properties.getProperty("databaseAddress"));
		databasePort = Integer.parseInt(properties.getProperty("databasePort"));
		
		serverAddress = InetAddress.getByName(properties.getProperty("serverAddress"));
		serverPort = Integer.parseInt(properties.getProperty("serverPort"));
		serverThreadPool = Integer.parseInt(properties.getProperty("serverThreadPool"));
	}
	
	@Override
	public InetAddress getDatabaseAddress() {
		return databaseAddress;
	}

	@Override
	public int getDatabasePort() {
		return databasePort;
	}

	@Override
	public InetAddress getServerAddress() {
		return serverAddress;
	}

	@Override
	public int getServerPort() {
		return serverPort;
	}

	@Override
	public int getServerThreadPool() {
		return serverThreadPool;
	}
}
