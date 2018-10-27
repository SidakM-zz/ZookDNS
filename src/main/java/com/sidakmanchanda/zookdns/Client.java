package com.sidakmanchanda.zookdns;

import java.net.InetAddress;

/**
 * @author sidak
 *
 * Stores client network and request information 
 */
public class Client {
	private int port;
	private InetAddress address;
	private byte[] requestBytes;
	private Request request;
	
	public Client(InetAddress address, int port) {
		this.address = address;
		this.port = port;
	}
	
	public Client(InetAddress address, int port, byte[] requestBytes) {
		this.address = address;
		this.port = port;
		this.requestBytes = requestBytes;
	}
	
	/**
	 * Returns a parsed internal representation of the client's DNS query
	 * @return Request
	 */
	public Request parseClientRequest() {
		if (this.request != null && this.request.isParsed())  {
			return this.request;
		}
		this.request = new Request(this.requestBytes);
		this.request.parseRequest();
		return this.request;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public InetAddress getAddress() {
		return this.address;
	}
	
	public byte[] getRequestBytes() {
		return this.requestBytes;
	}
	
	public String getRequestString() {
		return new String(this.requestBytes);
	}
	
	
	
}
