package com.sidakmanchanda.zookdns;

import java.io.IOException;
import java.text.ParseException;

public class CNAMERecord extends ResourceRecord {
	DNSName cName;
	
	CNAMERecord(RecordClass DNSClass, DNSName name, long ttl) {
		super(RecordType.CNAME, DNSClass, name, ttl);
	}

	@Override
	public void parseDataFromStream(MasterFileTokenizer st) throws IOException, ParseException {
		cName = new DNSName(st.parseWord());
	}

	@Override
	public String toString() {
		return "CNAMERecord [cName=" + cName + ", toString()=" + super.toString() + "]";
	}

	@Override
	public void encodeRecord(Output out) {
		encodeBase(out);
		
		int rdLengthPos = out.getPos();
		out.jump(DATA_LENGTH_SIZE);
		
		// Write Record Data
		int rdStart = out.getPos();
		cName.encodeName(out);
		
		// Write Record Data Length
		int rdLength = out.getPos() - rdStart;
		
		out.write16BitInt(rdLength, rdLengthPos);		
	}

}
