package com.sidakmanchanda.zookdns;

import java.io.IOException;
import java.text.ParseException;

public class TXTRecord extends ResourceRecord {
	String text;
	
	public TXTRecord(RecordClass DNSClass, DNSName name, long ttl) {
		super(RecordType.TXT, DNSClass, name, ttl);
	}
	
	@Override
	public void parseDataFromStream(MasterFileTokenizer st) throws IOException, ParseException {
		text = st.parseCharacterString();
	}

	@Override
	public void encodeRecord(Output out) {
		encodeBase(out);
		
		byte[] textBytes = text.getBytes();
		out.write16BitInt(textBytes.length);
		out.writeBytes(textBytes);
	}

	@Override
	public String toString() {
		return "TXTRecord [text=" + text + ", toString()=" + super.toString() + "]";
	}
	
	

}
