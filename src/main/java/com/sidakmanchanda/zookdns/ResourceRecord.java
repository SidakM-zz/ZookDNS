package com.sidakmanchanda.zookdns;

import java.io.IOException;
import java.text.ParseException;

abstract class ResourceRecord implements java.io.Serializable {
	protected DNSName name;
	protected RecordType type;

	protected RecordClass dClass;
	protected long ttl;
	
	// Size in bytes of RDataLength Field
	protected static final int DATA_LENGTH_SIZE = 2;
	
	ResourceRecord(RecordType type, RecordClass DNSClass, DNSName name, long ttl) {
		this.type = type;
		this.dClass = DNSClass;
		this.name = name;
		this.ttl = ttl;
	}

	public DNSName getName() {
		return name;
	}

	public RecordType getType() {
		return type;
	}

	public RecordClass getDNSClass() {
		return dClass;
	}

	public long getTtl() {
		return ttl;
	}

	public abstract void parseDataFromStream(MasterFileTokenizer st) throws IOException, ParseException;

	@Override
	public String toString() {
		return "ResourceRecord [name=" + name + ", type=" + type + ", DNSClass=" + dClass + ", ttl=" + ttl + "]";
	}

	/**
	 * Encodes the name, type, class and ttl of the record to the output stream
	 * 
	 * @param out
	 */
	protected void encodeBase(Output out) {
		name.encodeName(out);
		out.write16BitInt(type.ordinal() + 1);
		out.write16BitInt(dClass.ordinal() + 1);
		out.write32BitInt(ttl);
	}

	public abstract void encodeRecord(Output out);
}
