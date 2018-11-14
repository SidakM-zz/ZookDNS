package com.sidakmanchanda.zookdns;

import java.io.IOException;
import java.text.ParseException;

public class SRVRecord extends ResourceRecord {
	int priority;
	int weight;
	int port;
	DNSName target;

	public SRVRecord(RecordClass DNSClass, DNSName name, long ttl) {
		super(RecordType.SRV, DNSClass, name, ttl);
	}
	
	@Override
	public void parseDataFromStream(MasterFileTokenizer st) throws IOException, ParseException {
		priority = st.parseWordAsInt();
		weight = st.parseWordAsInt();
		port = st.parseWordAsInt();
		target = new DNSName(st.parseWord());
	}

	@Override
	public void encodeRecord(Output out) {
		encodeBase(out);
		
		int rdLengthPos = out.getPos();
		out.jump(DATA_LENGTH_SIZE);
		
		// Write data to output
		int rdStart = out.getPos();
		out.write16BitInt(priority);
		out.write16BitInt(weight);
		out.write16BitInt(port);
		target.encodeName(out);
		
		// calculate length of bytes written
		int rdLength = out.getPos() - rdStart;
		
		// write length to correct position in output
		out.write16BitInt(rdLength, rdLengthPos);
		
	}

	@Override
	public String toString() {
		return "SRVRecord [priority=" + priority + ", weight=" + weight + ", port=" + port + ", target=" + target
				+ ", toString()=" + super.toString() + "]";
	}
	
	public DNSName getTarget() {
		return target;
	}
	
}
