package com.sidakmanchanda.zookdns;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
		Output out = response.buildResponse();
		
		byte [] reply = out.getBytes();
		
		DatagramPacket packet = new DatagramPacket(reply, reply.length);
		packet.setAddress(client.getAddress());
		packet.setPort(client.getPort());
		
		try {
			socket.send(packet);
		} catch (IOException e) {
			log.error(e);
		}	
	}
}
