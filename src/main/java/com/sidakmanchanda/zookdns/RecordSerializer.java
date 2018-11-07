package com.sidakmanchanda.zookdns;

import java.io.IOException;

public abstract interface RecordSerializer {
	public byte[] serializeRecord(ResourceRecord rr) throws IOException;
	public ResourceRecord deserializeRecord(byte[] recordBytes) throws IOException;
}
