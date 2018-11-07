package com.sidakmanchanda.zookdns;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class JavaSerializer implements RecordSerializer{

	@Override
	public byte[] serializeRecord(ResourceRecord rr) throws IOException {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		ObjectOutputStream oa = new ObjectOutputStream(bao);
		
		oa.writeObject(rr);
		return bao.toByteArray();
	}

	@Override
	public ResourceRecord deserializeRecord(byte[] recordBytes) throws IOException {
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
}
