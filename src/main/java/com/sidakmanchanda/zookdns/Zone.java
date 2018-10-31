package com.sidakmanchanda.zookdns;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class Zone {
	ResourceRecord soa;
	ResourceRecord [] records;
	
	/**
	 * Builds the DNS zone given the master file.
	 * Throws if exceptions occur during file parsing
	 * @param zoneMasterFile
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public Zone(File zoneMasterFile) throws IOException, ParseException {
		MasterParser fileParser = new MasterParser(zoneMasterFile);
		configureZone(fileParser);
	}
	
	/**
	 * Builds the DNS zone given a master file and name. 
	 * Uses the name as the initial ORIGIN for parsing purposes.
	 * @param zoneMasterFile
	 * @param name
	 * @throws IOException
	 * @throws ParseException 
	 */
	public Zone(File zoneMasterFile, DNSName name) throws IOException, ParseException {
		MasterParser fileParser = new MasterParser(zoneMasterFile, name);
		configureZone(fileParser);
	}
	
	private void configureZone(MasterParser master) throws IOException, ParseException {
		master.parseFile();
		soa = master.getSOA();
		records = master.getRecords();
	}

	public ResourceRecord[] getRecords() {
		return records;
	}
	
}
