package com.sidakmanchanda.zookdns;

import java.util.ArrayList;
import java.util.Stack;

public class DNSName implements java.io.Serializable {
	private ArrayList<String> name;
	private String stringName;
	private boolean fqdn = false;
	
	private static final int POINTER_INDICATOR = 192;
	
	public DNSName() {}
	
	/**
	 * Initializes DNS name by splitting on "."
	 * @param name
	 */
	public DNSName(String name) {
		this.name = new ArrayList<String>();
		if (name.charAt(name.length() - 1) == '.') {
			this.fqdn = true;
		}
		String[] sepName = name.split("\\.");
		for (String label : sepName) {
			this.name.add(label);
		}
		
		rebuildStringName();
	}
	
	/**
	 * Parses and populates the Name. Supports parsing partially and fully compressed DNS names
	 * 
	 * @param input An Input buffer
	 */
	public void populateName(Input input) {
		this.name = new ArrayList<String>();
		Stack<Integer> stack = new Stack<Integer>();
		
		// Start parsing the name from the current position of the input buffer
		int bufferPos = input.getPos();
		stack.push(bufferPos);
		while(!stack.empty()) {
			int pos = stack.pop();
			byte curByte = input.peekByte(pos);
			int wordLength = curByte & 0xF;
			
			// If Name compression detected, jump to compression
			if (wordLength >= this.POINTER_INDICATOR) {
				/* next byte has pointer to compressed name
				 *	+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
				 *	| 1  1|                OFFSET                   |
				 *	+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
				 *  |------->curByte<-------|
				 */  
				int offset = (((byte)(curByte << 2) >> 2) << 8) | (input.peekByte(pos + 1) & 0xFF);
				stack.push(pos + 2);
				stack.push(offset);
			} else if (wordLength != 0) {
				byte[] letters = input.peekbyteArray(wordLength, pos + 1);
				String word = new String(letters);
				this.name.add(word);
				stack.push(pos + wordLength + 1);
			}
			
			// If complete name is parsed reset the state of the buffer
			if(stack.isEmpty()) {
				bufferPos = pos + 1;
			}
		}
		input.setPosition(bufferPos);	
		fqdn = true;
		this.rebuildStringName();	
	}
	
	public boolean isFQDN() {
		return this.fqdn;
	}
	
	/**
	 * Appends the passed DNSName to this DNSName. 
	 * Thus fully qualifying this domain name.
	 * @param origin DNSName
	 */
	public void qualifyName(DNSName origin) {
		this.name.addAll(origin.getLabels());
		this.fqdn = true;
		this.rebuildStringName();
	}
	
	@Override
	public String toString() {
		return "DNSName [name=" + name + "]";
	}

	/**
	 * Returns an ArrayList<String> representing the labels in the domain name
	 * 
	 * @return ArrrayList<String> labels
	 */
	public ArrayList<String> getLabels() {
		return this.name;
	}
	
	
	private void rebuildStringName() {
		StringBuilder sb = new StringBuilder("");
		for (int x = 0; x < name.size(); x++) {
			if (x != name.size() - 1) {
				sb.append(name.get(x) + ".");
			} else {
				sb.append(name.get(x));
			}
		}
		if (fqdn) {
			sb.append(".");
		}
		stringName = sb.toString();
	}
	
	/**
	 * Returns a string representation of the DNSName
	 * 
	 * @return String name 
	 */
	public String getStringName() {
		return stringName;
	}
	
	
	/**
	 * Encodes the name into the given output buffer
	 * 
	 * @param out Output Buffer
	 */
	public void encodeName(Output out) {
		for (String label : name) {
			byte[] bytes = label.getBytes();
			out.write8BitInt(bytes.length);
			out.writeBytes(bytes);
		}
		// needed to delimit a name
		out.write8BitInt(0);
	}
	
}
