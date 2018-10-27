package com.sidakmanchanda.zookdns;

import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

public class DNSNameTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
		
	}

	public void testDNSNameString() {
		String add = "www.example.com.";
		ArrayList<String> expectedLabels = new ArrayList<String>(Arrays.asList("www", "example", "com"));
		
		DNSName addName = new DNSName(add);
		assertEquals(addName.getLabels(), expectedLabels);
		assertEquals(addName.isFQDN(), true);
	}

	public void testPartialDNSNameString() {
		String add = "www.example";
		ArrayList<String> expectedLabels = new ArrayList<String>(Arrays.asList("www", "example"));
		
		DNSName addName = new DNSName(add);
		assertEquals(addName.getLabels(), expectedLabels);
		assertEquals(addName.isFQDN(), false);
	}
	
	public void testQualifyName() {
		String partialNameString = "www.example";
		String originNameString = "com.";
		
		DNSName partialName = new DNSName(partialNameString);
		DNSName originName = new DNSName(originNameString);
		
		assertEquals(partialName.isFQDN(), false);
		
		partialName.qualifyName(originName);
		
		ArrayList<String> expectedLabels = new ArrayList<String>(Arrays.asList("www", "example", "com"));
		
		System.out.println(partialName);
		assertEquals(partialName.getLabels(), expectedLabels);
		assertEquals(partialName.isFQDN(), true);
	}

}
