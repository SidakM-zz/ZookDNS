package com.sidakmanchanda.zookdns;

import java.io.IOException;
import java.text.ParseException;

public class PTRRecord extends ResourceRecord {
	DNSName hostName;
	
	public PTRRecord(RecordClass DNSClass, DNSName name, long ttl) {
		super(RecordType.PTR, DNSClass, name, ttl);
	}
	
	@Override
	public void parseDataFromStream(MasterFileTokenizer st) throws IOException, ParseException {
		hostName = new DNSName(st.parseWord());
	}

	@Override
	public String toString() {
		return "PTRRecord [name=" + hostName + ", toString()=" + super.toString() + "]";
	}

	@Override
	public void encodeRecord(Output out) {
		encodeBase(out);
		
		int rdLengthPos = out.getPos();
		out.jump(DATA_LENGTH_SIZE);
		
		// Write Record Data
		int rdStart = out.getPos();
		hostName.encodeName(out);
		
		// Write Record Data Length
		int rdLength = out.getPos() - rdStart;
		
		out.write16BitInt(rdLength, rdLengthPos);
	}
	
}
