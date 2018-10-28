package com.sidakmanchanda.zookdns;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.text.ParseException;

public class SOARecord extends ResourceRecord {
	
	DNSName mName;
	DNSName rName;
	long serial;
	long refresh;
	long retry;
	long expire;
	long minimum;
	
	public SOARecord(RecordClass DNSClass, DNSName name, long ttl) {
		super(RecordType.SOA, DNSClass, name, ttl);
	}
	
	@Override
	public void parseDataFromStream(MasterFileTokenizer st) throws IOException, ParseException {
		// TODO Auto-generated method stub
		mName = new DNSName(st.parseWord());
		rName = new DNSName(st.parseWord());
		serial = st.parseWordAsLong();
		refresh = st.parseWordAsLong();
		retry = st.parseWordAsLong();
		expire = st.parseWordAsLong();
		minimum = st.parseWordAsLong();
		
	}

	@Override
	public String toString() {
		return "SOARecord [mName=" + mName + ", rName=" + rName + ", serial=" + serial + ", refresh=" + refresh
				+ ", retry=" + retry + ", expire=" + expire + ", minimum=" + minimum + ", toString()="
				+ super.toString() + "]";
	}

	@Override
	public void encodeRecord(Output out) {
		// encode the base record
		encodeBase(out);
		
		// Retrieve index of data length field 
		int rDataLengthPtr = out.getPos();
		out.jump(DATA_LENGTH_SIZE);

		// Encode record fields
		int rDataStart = out.getPos();
		mName.encodeName(out);
		rName.encodeName(out);
		out.write32BitInt(serial);
		out.write32BitInt(refresh);
		out.write32BitInt(retry);
		out.write32BitInt(expire);
		out.write32BitInt(minimum);
		
		// Get Size of Data written for record
		int rDataSize = out.getPos() - rDataStart;
		
		// Write rDataLength at Index
		out.write16BitInt(rDataSize, rDataLengthPtr);
	}

}
