package com.sidakmanchanda.zookdns;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.KeeperException;

public class Response {
	private Request request;
	private DNSDatabase db;
	private static final Logger log = LogManager.getLogger(ZookDNS.class);
	
	public Response(Request request, DNSDatabase db){
		this.request = request;
		this.db = db;
	}
	
	public Output buildResponse() throws IOException, InterruptedException, KeeperException {
		ArrayList<ResourceRecord> rrs = new ArrayList<ResourceRecord>();
		
		for (Question question : request.getQuestions()) {
			// retrieve new record from ZooKeeper
			String recordName = question.getName().getStringName();
			RecordType type = question.getType();
			ResourceRecord rr = db.retrieveRecord(recordName, type);
			rrs.add(rr);
		}
		
		return generateOutput(rrs);
	}
	
	private Output generateOutput(ArrayList<ResourceRecord> rrs) {
		// Initialize new DNS output
		Output out = new Output();
		
		// Encode Header
		Header responseHeader = new Header(request.getHeader());
		responseHeader.setQr(1);
		responseHeader.setAA(true);
		responseHeader.setrCode(0);
		responseHeader.setAnswerCount(rrs.size());
		responseHeader.setArCount(0);
		responseHeader.encodeHeader(out);
		
		
		// Encode questions
		for(Question question : request.getQuestions()) {
			question.encodeQuestion(out);
		}
		
		// Encode Answers
		for(ResourceRecord rr : rrs) {
			rr.encodeRecord(out);
		}
		
		return out;
	}
	
	
	
}
