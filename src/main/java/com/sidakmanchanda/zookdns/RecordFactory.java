package com.sidakmanchanda.zookdns;

public class RecordFactory {
	public ResourceRecord newRecord(String type, RecordClass DNSClass, DNSName name, long ttl) throws IllegalArgumentException {
		switch (type.toUpperCase()) {
		case "A":
			return new ARecord(DNSClass, name, ttl);
		default:
			throw new IllegalArgumentException("unknown record type: " + type);
		}
	}
}
