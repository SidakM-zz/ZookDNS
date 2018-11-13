package com.sidakmanchanda.zookdns;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class MasterFileTokenizer extends StreamTokenizer {

	private static final char[] WORD_CHARS = new char[] { '.', '_','-','0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
	private static final Character[] IGNORE_CHARS = new Character[] { '(', ')'};
	private static final Set<Character> IGNORE_CHARS_SET = new HashSet<Character>(Arrays.asList(IGNORE_CHARS));
	private static final Map<String, RecordClass> DNS_CLASS_MAP;
	static {
		Map<String, RecordClass> temp = new HashMap<String, RecordClass>();
		temp.put("IN", RecordClass.IN);
		temp.put("CS", RecordClass.CS);
		temp.put("CH", RecordClass.CH);
		temp.put("HS", RecordClass.HS);
		
		DNS_CLASS_MAP = Collections.unmodifiableMap(temp);
	}
	
	public MasterFileTokenizer(Reader r) {
		super(r);
		configureTokenizer();
	}
	

	private void configureTokenizer() {
		commentChar(';');
		for (char word : WORD_CHARS) {
			ordinaryChar(word);
			wordChars(word, word);
		}
	}

	public int parseWordAsInt() throws IOException, ParseException {
		nextToken();
		if (ttype != TT_WORD) {
			throw new ParseException("expected word got " + (char) ttype, lineno());
		}
		
		try {
			return Integer.parseInt(sval);
		} catch(NumberFormatException e) {
			throw new ParseException("expected integer", lineno());
		}
	}
	
	public long parseWordAsLong() throws IOException, ParseException {
		nextToken();
		if (ttype != TT_WORD) {
			throw new ParseException("expected word got " + (char) ttype, lineno());
		}
		
		try {
			return Long.parseLong(sval);
		} catch(NumberFormatException e) {
			throw new ParseException("expected long", lineno());
		}
	}
	
	public String parseWord() throws IOException, ParseException {
		nextToken();
		if (ttype != TT_WORD) {
			throw new ParseException("expected word got " + (char) ttype, lineno());
		}
		
		return sval;
	}
	
	public String parseCharacterString() throws IOException, ParseException {
		nextToken();
		if (ttype != '"') {
			throw new ParseException("expected character string", lineno());
		}
		
		return sval;
	}
	
	public boolean isTTL() {
		try {
			Long.parseLong(sval);
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}

	public boolean isDNSClass() {
		return DNS_CLASS_MAP.containsKey(sval);
	}
	
	public RecordClass getTokenAsClass() {
		return DNS_CLASS_MAP.get(sval);
	}
	
	@Override
	public int nextToken() throws IOException {
		do {
			super.nextToken();
		} while(IGNORE_CHARS_SET.contains((char) ttype));
		return ttype;
	}
}
