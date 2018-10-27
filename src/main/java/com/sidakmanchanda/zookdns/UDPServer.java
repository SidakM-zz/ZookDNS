package com.sidakmanchanda.zookdns;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UDPServer implements Runnable {
	private String address;
	private int port;
	private int poolSize;
	private DNSDatabase db;
	private DatagramSocket socket;
	private static final Logger log = LogManager.getLogger(UDPServer.class);
	
	// RFC-1035 Sec. 2.3.4 Size Limits for UDP messages
	private static final int ipHeaderSize = 20;
	private static final int udpHeaderSize = 5;
	private static final int bufferSize = 512 + ipHeaderSize + udpHeaderSize;
	
	public volatile boolean stop = false;
	
	
	public UDPServer(String address, int port, int poolSize, DNSDatabase db) throws SocketException, UnknownHostException {
		this.address = address;
		this.port = port;
		this.poolSize = poolSize;
		this.db = db;
		socket = new DatagramSocket(this.port, InetAddress.getByName(this.address));
	}
	
	public void run() {
		log.traceEntry("Started UDP Server on " + this.address + ":" + this.port);
		byte[] buffer = new byte[bufferSize];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		ExecutorService threadPool = Executors.newFixedThreadPool(this.poolSize);
		
		// execute while server is not stopped
		while(!this.stop) {
				try {
					socket.receive(packet);
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
				Client client = new Client(packet.getAddress(), packet.getPort(), packet.getData());
				threadPool.execute(new UDPHandler(socket, client, db));
		}
		terminateUDPThreads(threadPool);
		log.traceExit();
	}
	
	private void terminateUDPThreads(ExecutorService threadPool) {
		// terminate UDPHandler threads on server shutdown
		log.traceEntry("Terminating all UDP threads");
		threadPool.shutdown();
		try {
			if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
				threadPool.shutdownNow();
			}
		} catch (InterruptedException e) {
			threadPool.shutdownNow();
		}
	}

}

