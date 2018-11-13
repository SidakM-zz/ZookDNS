package com.sidakmanchanda.zookdns;

import java.io.IOException;
import java.net.InetAddress;
import java.text.ParseException;

public class ARecord extends ResourceRecord {
	InetAddress address;
	
	private static final int DATA_LENGTH = 4;
	
	public ARecord(RecordClass DNSClass, DNSName name, long ttl, InetAddress address) {
		super(RecordType.A, DNSClass, name, ttl);
		this.address = address;
	}
	
	public ARecord(RecordClass DNSClass, DNSName name, long ttl) {
		super(RecordType.A, DNSClass, name, ttl);
	}

	@Override
	public void parseDataFromStream(MasterFileTokenizer st) throws IOException, ParseException {
		String stringAddress = st.parseWord();
		try {
			this.address = InetAddress.getByName(stringAddress);
		} catch (Exception e) {
			throw new ParseException("invalid ip address specified in file", st.lineno());
		}
	}

	@Override
	public String toString() {
		return "ARecord [address=" + address + ", toString()=" + super.toString() + "]";
	}

	@Override
	public void encodeRecord(Output out) {
		// encode the base fields
		encodeBase(out);
		
		// address will be encoded in 4 bytes
		out.write16BitInt(DATA_LENGTH);
		
		// Jump Two Bytes in buffer; Will be filled with record length later
		out.writeBytes(address.getAddress());
	}
	
}
