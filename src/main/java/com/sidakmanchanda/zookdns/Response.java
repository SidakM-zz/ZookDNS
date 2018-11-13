package com.sidakmanchanda.zookdns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Response {
	private Request request;
	private DNSDatabase db;
	private static final Logger log = LogManager.getLogger(ZookDNS.class);

	public Response(Request request, DNSDatabase db) {
		this.request = request;
		this.db = db;
	}

	public Output buildResponse() {
		ArrayList<ResourceRecord> rrs = new ArrayList<ResourceRecord>();
		
		for (Question question : request.getQuestions()) {
			try {
				// retrieve new records from ZooKeeper
				String recordName = question.getName().getStringName();
				RecordType type = question.getType();
				ResourceRecord[] answers = db.retrieveRecords(recordName, type);
				if (answers != null) rrs.addAll(Arrays.asList(answers));
			} catch(IOException e) {
				log.error("could not retreive records from db", e);
				return getErrorResponse();
			}
		}

		return generateOutput(rrs);
	}

	private Output generateOutput(ArrayList<ResourceRecord> rrs) {
		// Initialize new DNS output
		Output out = new Output();

		// Set the response code
		ResponseCode rCode = ResponseCode.NoError;
		if (rrs.isEmpty()) rCode = ResponseCode.NameError;
		
		// Encode Header
		Header responseHeader = getResponseHeader(rCode, rrs.size());
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

	private Output getErrorResponse() {
		Output out = new Output();
		Header responseHeader = getResponseHeader(ResponseCode.ServerFailure, 0);
		responseHeader.encodeHeader(out);
		
		return out;
	}

	private Header getResponseHeader(ResponseCode rCode, int answerCount) {
		Header head = new Header(request.getHeader());
		head.setQr(1);
		head.setAA(true);
		head.setrCode(rCode);
		head.setArCount(0);
		head.setAnswerCount(answerCount);
		return head;
	}

}
