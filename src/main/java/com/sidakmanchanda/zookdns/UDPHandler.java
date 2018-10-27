package com.sidakmanchanda.zookdns;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.KeeperException;

public class UDPHandler implements Runnable {
	private DatagramSocket socket;
	private Client client;
	private DNSDatabase db;

	private static final Logger log = LogManager.getLogger(UDPServer.class);

	public UDPHandler(DatagramSocket socket, Client client, DNSDatabase db) {
		this.socket = socket;
		this.client = client;
		this.db = db;
	}

	public void run() {
		Request request = client.parseClientRequest();
		Response response = new Response(request, db);
		Output out;
		try {
			out = response.buildResponse();
		} catch (IOException | InterruptedException | KeeperException e) {
			log.error("could not build response");
			return;
		}
		
		byte [] reply = out.getBytes();
		
		DatagramPacket packet = new DatagramPacket(reply, reply.length);
		packet.setAddress(client.getAddress());
		packet.setPort(client.getPort());
		
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
