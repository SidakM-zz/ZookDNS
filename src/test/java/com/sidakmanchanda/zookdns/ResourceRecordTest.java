package com.sidakmanchanda.zookdns;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import junit.framework.TestCase;

public class ResourceRecordTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testResourceRecordSerializable() throws IOException, ClassNotFoundException {
		ResourceRecord originalRR = new ARecord(RecordClass.IN, new DNSName("www.example.com"), 30, InetAddress.getByName("127.0.0.1"));
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(bao);
		
		// serialize record into stream
		os.writeObject(originalRR);
		
		byte[] originalRRBytes = bao.toByteArray();
		
		ByteArrayInputStream bai = new ByteArrayInputStream(originalRRBytes);
		ObjectInputStream oi = new ObjectInputStream(bai);
		
		ResourceRecord decodedRR = (ResourceRecord) oi.readObject();
		
		assertEquals(originalRR.toString(), decodedRR.toString());
	}

}
