package com.sidakmanchanda.zookdns;

public class Request {
	private Header header;
	private Question[] questions;
	private Input input;
	
	private boolean parsed = false;
	
	public Request(byte[] packet) {
		this.input= new Input(packet.clone());
		this.header = new Header();
	}
	
	public void parseRequest() {
		this.header.populateHeader(input);
		this.parseQuestions();
		this.parsed = true;
	}
	
	public boolean isParsed() {
		return this.parsed;
	}

	/**
	 * Attempts to populate questions using the Input buffer associated with the instance
	 */
	private void parseQuestions() {
		int numQuestions = this.header.getQuestionCount();
		this.questions = new Question[numQuestions];
		for(int x = 0; x < numQuestions; x++) {
			this.questions[x] = new Question();	
			this.questions[x].populateQuestion(this.input);
			this.questions[x].printQuestion();
		}		
	}
	
	public Question[] getQuestions() {
		return questions;
	}

	public Header getHeader() {
		return header;
	}
	
}
