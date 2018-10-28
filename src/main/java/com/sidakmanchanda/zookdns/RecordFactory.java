package com.sidakmanchanda.zookdns;

public class RecordFactory {
	public ResourceRecord newRecord(String type, RecordClass DNSClass, DNSName name, long ttl) throws IllegalArgumentException {
		switch (type.toUpperCase()) {
		case "A":
			return new ARecord(DNSClass, name, ttl);
		case "SOA":
			return new SOARecord(DNSClass, name, ttl);
		case "MX":
			return new MXRecord(DNSClass, name, ttl);
		case "NS":
			return new NSRecord(DNSClass, name, ttl);
		case "CNAME":
			return new CNAMERecord(DNSClass, name, ttl);
		case "TXT":
			return new TXTRecord(DNSClass, name, ttl);
		case "PTR":
			return new PTRRecord(DNSClass, name, ttl);
		case "SRV":
			return new SRVRecord(DNSClass, name, ttl);
		default:
			throw new IllegalArgumentException("unknown record type: " + type);
		}
	}
}
