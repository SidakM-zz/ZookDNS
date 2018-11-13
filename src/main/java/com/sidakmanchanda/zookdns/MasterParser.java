package com.sidakmanchanda.zookdns;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** 
 * Parses provided Master files formatted as dictated by RFC-1035
 * @author sidak
 *
 */
public class MasterParser {
	// File objects
	private File masterFile;
	private FileReader reader;

	// stream for parsing
	private MasterFileTokenizer stream;

	// variables and flags storing state of file parsing
	private DNSName currentOrigin;
	private long currentTTL;
	private RecordClass currentClass;
	private DNSName currentName;
	private boolean parsedSOA = false;
	
	private ResourceRecord soa;
	private ArrayList<ResourceRecord> records;
	
	private static final Logger log = LogManager.getLogger(MasterParser.class);

	/**
	 * Initialize the file parser. Uses the file name as the default origin.
	 * 
	 * @param file The Master file
	 * @throws IOException
	 */
	public MasterParser(File file) throws IOException {
		log.traceEntry("Attempting to parse new file");
		
		if (!file.canRead()) {
			log.warn("failed to open file... aborting");
			throw new java.io.IOException("file cannot be read");
		}

		this.masterFile = file;
		this.reader = new FileReader(this.masterFile);
		this.stream = new MasterFileTokenizer(reader);
		
		this.currentOrigin = new DNSName(file.getName());
		this.records = new ArrayList<ResourceRecord>();
		
		log.traceExit("File: " + file.getName() + " ready to parse");
	}

	/**
	 * Initialize the file parser. Uses the provided DNSName as the default origin.
	 * 
	 * @param file The Master File
	 * @param name The initial origin for the DNSZone
	 * @throws IOException
	 */
	public MasterParser(File file, DNSName name) throws IOException {
		this(file);
		this.currentOrigin = name;
	}


	public void parseFile() throws IOException, ParseException {
		boolean eof = false;
		while (!eof) {
			int ttype = stream.nextToken();
			ResourceRecord rr;
			switch (ttype) {
			case MasterFileTokenizer.TT_EOF:
				eof = true;
				break;
			case MasterFileTokenizer.TT_WORD:
				if (stream.isDNSClass()) {
					// Format: <blank><rr> [<comment>] where <rr> is [<class>] [<TTL>] <type> <RDATA>
					rr = parseRecordWithClass(stream.getTokenAsClass());
				} else if (stream.isTTL()) {
					// Format: <blank><rr> [<comment>] where <rr> is [<TTL>] <type> <RDATA>
					rr = parseRecordWithTTL(Long.parseLong(stream.sval));
				} else {
					// Format: <domain-name><rr> [<comment>]
					rr = parseRecordWithName(new DNSName(stream.sval));
				}
				this.updateRecords(rr);
				break;
			case '$':
				parseDirective();
				break;
			case '@':
				// valid formats: <domain-name> <rr> [<comment>]
				rr = parseRecordWithName(this.currentOrigin);
				this.updateRecords(rr);
				break;
			default:
				log.error("encountered illegal token type: " + (char) ttype + " aborting file parsing");
				eof = true;
			}

		}
		log.info(this.records);
	}
	
	private void updateRecords(ResourceRecord rr) throws ParseException {
		this.currentClass = rr.getDNSClass();
		this.currentName = rr.getName();
		this.currentTTL = rr.getTtl();
		if (rr.getType() == RecordType.SOA) {
			if (parsedSOA) {
				throw new ParseException ("Encountered two SOA records in one zone", stream.lineno());
			}
			parsedSOA = true;
			soa = rr;
		}
		
		this.records.add(rr);
	}

	private ResourceRecord parseRecordWithName(DNSName name) throws IOException, ParseException {
		// set default ttl and class values
		long ttl = this.currentTTL;
		RecordClass DNSClass = this.currentClass;
		
		if (!name.isFQDN()) {
			name.qualifyName(this.currentOrigin);
		}
		
		while (true) {
			String word = stream.parseWord();
			// could be a class or a type
			if (stream.isDNSClass()) {
				// is a class
				DNSClass = stream.getTokenAsClass();
			} else if (stream.isTTL()) {
				// is a ttl
				ttl = Long.parseLong(word);
			} else {
				// should be a type
				return createRecord(word, DNSClass, name, ttl);
			}
		}

	}
	
	private ResourceRecord parseRecordWithTTL(long ttl) throws IOException, ParseException {
		currentTTL = ttl;
		return parseRecordWithName(this.currentName);
	}
	
	private ResourceRecord parseRecordWithClass(RecordClass DNSClass) throws IOException, ParseException {
		currentClass = DNSClass;
		return parseRecordWithName(this.currentName);
	}
	
	private ResourceRecord createRecord(String type, RecordClass DNSClass, DNSName recordName, long ttl) throws IOException, ParseException {
		RecordFactory factory = new RecordFactory();
		ResourceRecord rr;
		try {
			rr = factory.newRecord(type, DNSClass, recordName, ttl);
		} catch (IllegalArgumentException e) {
			throw new ParseException("invalid dns record type: " + stream.sval, stream.lineno());
		}
		rr.parseDataFromStream(stream);
		return rr;
	}
	
	/**
	 * Attempts to parse a directive from the stream. Throws exceptions on failure
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	private void parseDirective() throws IOException, ParseException {
		String directive = stream.parseWord();
		if (directive.equalsIgnoreCase("ORIGIN")) {
			DNSName name = parseName();
			currentOrigin = name;
			log.info("switched default origin to: " + this.currentOrigin.toString());
		} else if (directive.equalsIgnoreCase("TTL")) {
			currentTTL = stream.parseWordAsInt();
		} else {
			throw new java.text.ParseException("Unsupported directive: " + directive, stream.lineno());
		}
	}

	private DNSName parseName() throws ParseException, IOException {
		String stringName = stream.parseWord();
		DNSName name = new DNSName(stringName);
		return name;
	}
	
	public ResourceRecord getSOA() {
		return soa;
	}
	
	public ResourceRecord[] getRecords() {
		return records.toArray(new ResourceRecord[records.size()]);
	}
}
