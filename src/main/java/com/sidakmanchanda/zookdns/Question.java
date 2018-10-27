package com.sidakmanchanda.zookdns;


public class Question {
	private DNSName name;
	private RecordType type;
	private RecordClass qClass;
	
	private byte[] questionBytes;
	
	public Question () {
		this.name = new DNSName();
	}

	/**
	 * Populates the question given a RequestInput buffer
	 * 
	 * @param Input - An Input buffer
	 */
	public void populateQuestion(Input input) {
		int initialPos = input.getPos();
		
		// Get Question Name
		this.name.populateName(input); 
		
		// Get Question type and Class
		this.type = RecordType.values()[input.get16BitInt() - 1];
		this.qClass = RecordClass.values()[input.get16BitInt() - 1];
		
		int finalPos = input.getPos();
		
		// Saved to be used when formulating response
		questionBytes = input.peekbyteArray(finalPos - initialPos, initialPos);
	}

	public RecordType getType() {
		return type;
	}

	public RecordClass getqClass() {
		return qClass;
	}

	public DNSName getName() {
		return name;
	}
	
	public void printQuestion() {
		System.out.println("name: " + this.name.getStringName());
		System.out.println("q type: " + this.type);
		System.out.println("Class: " + this.qClass);
	}

	public void encodeQuestion(Output out) {
		out.writeBytes(questionBytes);
	}
	
}
