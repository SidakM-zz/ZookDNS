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
		
		int rdLengthPos = out.getPos();
		out.jump(DATA_LENGTH_SIZE);
		
		int rdStart = out.getPos();
		
		byte[] textBytes = text.getBytes();
		out.write8BitInt(textBytes.length);
		out.writeBytes(textBytes);
		
		int rdLength = out.getPos() - rdStart;
		
		out.write16BitInt(rdLength, rdLengthPos);
	}

	@Override
	public String toString() {
		return "TXTRecord [text=" + text + ", toString()=" + super.toString() + "]";
	}
	
	

}
