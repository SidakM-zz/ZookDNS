package com.sidakmanchanda.zookdns;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.text.ParseException;

public class MXRecord extends ResourceRecord {
	int preference;
	DNSName exchange;
	
	public MXRecord(RecordClass DNSClass, DNSName name, long ttl, int preference, DNSName exchange) {
		super(RecordType.MX, DNSClass, name, ttl);
		this.preference = preference;
		this.exchange = exchange;
	}
	
	public MXRecord(RecordClass DNSClass, DNSName name, long ttl) {
		super(RecordType.MX, DNSClass, name, ttl);
	}
	
	@Override
	public void parseDataFromStream(MasterFileTokenizer st) throws IOException, ParseException {
		this.preference = st.parseWordAsInt();
		this.exchange = new DNSName(st.parseWord());
	}

	@Override
	public String toString() {
		return "MXRecord [preference=" + preference + ", exchange=" + exchange + ", toString()=" + super.toString()
				+ "]";
	}

	@Override
	public void encodeRecord(Output out) {
		encodeBase(out);
		
		int rdLengthPos = out.getPos();
		out.jump(DATA_LENGTH_SIZE);
		
		// Write Record Data
		int rdStart = out.getPos();
		out.write16BitInt(preference);
		name.encodeName(out);
		
		// Write Record Data Length
		int rdLength = out.getPos() - rdStart;
		
		out.write16BitInt(rdLength, rdLengthPos);
	}

}
