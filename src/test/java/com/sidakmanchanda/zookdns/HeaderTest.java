package com.sidakmanchanda.zookdns;

import junit.framework.TestCase;
import javax.xml.bind.DatatypeConverter;

/**
 * @author sidak
 *
 *	Unit tests for Dns Header class
 */
public class HeaderTest extends TestCase {
	private Header header;
	private byte[] headerBytes;

	private final String headerHex = "254f01200001000000000001";
	/*
	 * (non-Javadoc)
	 * 	Domain Name System (query)
	 *  Transaction ID: 0x254f
	 *  Flags: 0x0120 Standard query
	 *      0... .... .... .... = Response: Message is a query
	 *      .000 0... .... .... = Opcode: Standard query (0)
	 *      .... ..0. .... .... = Truncated: Message is not truncated
	 *      .... ...1 .... .... = Recursion desired: Do query recursively
	 *     .... .... .0.. .... = Z: reserved (0)
	 *      .... .... ..1. .... = AD bit: Set
	 *      .... .... ...0 .... = Non-authenticated data: Unacceptable
	 *  Questions: 1
	 *  Answer RRs: 0
	 *  Authority RRs: 0
	 *  Additional RRs: 1
	 */

	
	
	protected void setUp() throws Exception {
		super.setUp();
		headerBytes = DatatypeConverter.parseHexBinary(this.headerHex);
		this.header = new Header();
	}

	/**
	 * Unit test for populating Header using RequestInput
	 */
	public void testPopulateHeader() {
		Input rInput = new Input(headerBytes);
		this.header.populateHeader(rInput);
		assertEquals(9551, this.header.getId());
		assertEquals(0, this.header.getQr());
		assertEquals(0, this.header.getOpCode());
		assertEquals(false, this.header.isAa());
		assertEquals(false, this.header.isTc());
		assertEquals(true, this.header.isRd());
		assertEquals(false, this.header.isRa());
		assertEquals(ResponseCode.NoError, this.header.getRCode());
		assertEquals(1, this.header.getQuestionCount());
		assertEquals(0, this.header.getAnswerCount());
		assertEquals(0, this.header.getNsCount());
		assertEquals(1, this.header.getArCount());
	}

}
