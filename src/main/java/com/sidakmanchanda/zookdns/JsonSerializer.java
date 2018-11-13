package com.sidakmanchanda.zookdns;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

public class JsonSerializer implements RecordSerializer {
	private Gson gson;
	private RuntimeTypeAdapterFactory<ResourceRecord> typeFactory;
	
	public JsonSerializer() {
		this.typeFactory = RuntimeTypeAdapterFactory
				.of(ResourceRecord.class, "type", true)
				.registerSubtype(ARecord.class, RecordType.A.toString())
				.registerSubtype(SOARecord.class, RecordType.SOA.toString())
				.registerSubtype(MXRecord.class, RecordType.MX.toString())
				.registerSubtype(NSRecord.class, RecordType.NS.toString())
				.registerSubtype(CNAMERecord.class, RecordType.CNAME.toString())
				.registerSubtype(TXTRecord.class, RecordType.TXT.toString())
				.registerSubtype(PTRRecord.class, RecordType.PTR.toString())
				.registerSubtype(SRVRecord.class, RecordType.SRV.toString());

		this.gson = new GsonBuilder().registerTypeAdapterFactory(typeFactory).create();
	}
	
	@Override
	public byte[] serializeRecord(ResourceRecord rr) throws IOException {
		String json = gson.toJson(rr);
		
		return json.getBytes();
	}

	@Override
	public ResourceRecord deserializeRecord(byte[] recordBytes) throws IOException {
		return gson.fromJson(new String(recordBytes), ResourceRecord.class);
	}

	
}
