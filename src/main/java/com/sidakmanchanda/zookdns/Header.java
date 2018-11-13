package com.sidakmanchanda.zookdns;

/**
 * A Dns Header. Can be populated from an Input buffer.
 * @author sidak 
 */
public class Header {
	private int id;
	private int qr;
	private int opCode;
	private boolean aa;


	private boolean tc;
	private boolean rd;
	private boolean ra;
	private ResponseCode rCode;
	private int questionCount;
	private int answerCount;
	private int nsCount;
	private int arCount;
	
	public void setQr(int qr) {
		this.qr = qr;
	}

	public void setAA(boolean aa) {
		this.aa = aa;
	}

	public void setrCode(ResponseCode rCode) {
		this.rCode = rCode;
	}

	public void setAnswerCount(int answerCount) {
		this.answerCount = answerCount;
	}

	public void setArCount(int arCount) {
		this.arCount = arCount;
	}
	
	public Header() {}
	
	/**
	 * Copy Ctor
	 * 
	 * @param h
	 */
	public Header(Header h) {
		id = h.id;
		qr = h.qr;
		opCode = h.opCode;
		aa = h.aa;
		tc = h.tc;
		rd = h.rd;
		ra = h.ra;
		rCode = h.rCode;
		questionCount = h.questionCount;
		answerCount = h.answerCount;
		nsCount = h.nsCount;
		arCount = h.arCount;
	}
	
	/**
	 * Populates header fields using the Input buffer
	 * 
	 * @param input
	 */
	public void populateHeader(Input input) {
		// populate request identifier
		this.id = input.get16BitInt();
		
		// populate flags and codes
		int flagsAndCodes = input.get16BitInt();
		
		this.rCode = ResponseCode.values()[flagsAndCodes & 0x4];
		this.ra = ((flagsAndCodes >> 7) & 0x1) != 0;
		this.rd = ((flagsAndCodes >> 8) & 0x1) != 0;
		this.tc = ((flagsAndCodes >> 9) & 0x1) != 0;
		this.aa = ((flagsAndCodes >> 10) & 0x1) != 0;
		this.opCode = (flagsAndCodes >> 11) & 0x4;
		this.qr = (flagsAndCodes >> 17) & 0x1;
		
		// populate record counts
		this.questionCount = input.get16BitInt();
		this.answerCount = input.get16BitInt();
		this.nsCount = input.get16BitInt();
		this.arCount = input.get16BitInt();
	}

	public void encodeHeader(Output out) {
		out.write16BitInt(id);
		
		byte flagOne = (byte) (qr << 7 | (opCode << 3) | getInt(aa) << 2 | getInt(tc) << 1 | getInt(rd));
		out.writeByte(flagOne);
		
		byte flagTwo = (byte) (getInt(ra) << 7 | rCode.ordinal());
		out.writeByte(flagTwo);
		
		out.write16BitInt(questionCount);
		out.write16BitInt(answerCount);
		out.write16BitInt(nsCount);
		out.write16BitInt(arCount);
	}
	
	private int getInt(boolean bool) {
		return bool ? 1 : 0;
	}
	
	public int getId() {
		return id;
	}

	public int getQr() {
		return qr;
	}

	public int getOpCode() {
		return opCode;
	}

	public boolean isAa() {
		return aa;
	}

	public boolean isTc() {
		return tc;
	}

	public boolean isRd() {
		return rd;
	}

	public boolean isRa() {
		return ra;
	}

	public ResponseCode getRCode() {
		return rCode;
	}

	public int getQuestionCount() {
		return questionCount;
	}

	public int getAnswerCount() {
		return answerCount;
	}

	public int getNsCount() {
		return nsCount;
	}

	public int getArCount() {
		return arCount;
	}

}
