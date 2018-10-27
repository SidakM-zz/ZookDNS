package com.sidakmanchanda.zookdns;

import java.io.IOException;

public abstract interface DNSDatabase {
	public void writeRecords(ResourceRecord[] rrs) throws IOException;
	
	public void writeRecord(ResourceRecord rr) throws IOException;
	
	public ResourceRecord retrieveRecord(String name, RecordType rt) throws IOException;
}
