package com.sidakmanchanda.zookdns;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.text.ParseException;

public class NSRecord extends ResourceRecord {
	DNSName nameserver;
	
	public NSRecord(RecordClass DNSClass, DNSName name, long ttl, DNSName nameserver) {
		super(RecordType.NS, DNSClass, name, ttl);
		this.nameserver = nameserver;
	}

	public NSRecord(RecordClass DNSClass, DNSName name, long ttl) {
		super(RecordType.NS, DNSClass, name, ttl);
	}
	
	@Override
	public void parseDataFromStream(MasterFileTokenizer st) throws IOException, ParseException {
		this.nameserver = new DNSName(st.parseWord());
	}

	@Override
	public String toString() {
		return "NSRecord [nameserver=" + nameserver + ", toString()=" + super.toString() + "]";
	}

	@Override
	public void encodeRecord(Output out) {
		encodeBase(out);
		
		int rdLengthPos = out.getPos();
		out.jump(DATA_LENGTH_SIZE);
		
		// Write Record Data
		int rdStart = out.getPos();
		nameserver.encodeName(out);
		
		// Write Record Data Length
		int rdLength = out.getPos() - rdStart;
		
		out.write16BitInt(rdLength, rdLengthPos);
		
	}
}
