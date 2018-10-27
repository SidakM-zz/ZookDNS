package com.sidakmanchanda.zookdns;

import junit.framework.TestCase;
import javax.xml.bind.DatatypeConverter;

public class QuestionTest extends TestCase {
	private byte[] questionBytes;
	
	// www.sidakmanchanda.com: type A, class IN
	private final String questionHex = "037777770e736964616b6d616e6368616e646103636f6d0000010001";
	
	protected void setUp() throws Exception {
		super.setUp();
		questionBytes = DatatypeConverter.parseHexBinary(this.questionHex);
	}

	public void testPopulateQuestion() {
		Input rInput = new Input(this.questionBytes);
		Question q = new Question();
		q.populateQuestion(rInput);
		assertEquals("www.sidakmanchanda.com.", q.getName().getStringName());
		assertEquals(RecordType.A, q.getType());
		assertEquals(RecordClass.IN, q.getqClass());
	}

}
