package org.openmrs.module.medadherence.rest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.api.RestService;

public class MedBarriersResourceTest {
	@Test
	public void testMedBarriersResource() throws Exception {		
		// Use apache commons-httpclient to create the request/response
		HttpClient client = new HttpClient();
		Credentials defaultcreds = new UsernamePasswordCredentials("admin",
		    "test");
		client.getState().setCredentials(AuthScope.ANY, defaultcreds);
		
		// GET a patient's latest medication barriers data given this person's uuid
		GetMethod method = new GetMethod(//"http://172.30.204.50:8080/openmrs/ws/rest/v1/patient/8520ea49-b0f8-4b5d-8037-9d8736f38032");
		                               //"http://172.30.204.50:8080/openmrs/ws/rest/v1/medadherence/medbarriers/8520ea49-b0f8-4b5d-8037-9d8736f38032");
										"http://172.30.204.50:8080/openmrs/ws/rest/v1/medadherence/medbarriers/iu0002");
		client.executeMethod(method);
		InputStream in = method.getResponseBodyAsStream();
		String result = streamToString(in);
		System.out.print(result);
		
		// GET a patient's latest EMR data given this person's uuid
		method = new GetMethod("http://172.30.204.50:8080/openmrs/ws/rest/v1/patient/8520ea49-b0f8-4b5d-8037-9d8736f38032");
		                               //"http://172.30.204.50:8080/openmrs/ws/rest/v1/medadherence/medbarriers/8520ea49-b0f8-4b5d-8037-9d8736f38032");
		client.executeMethod(method);
		in = method.getResponseBodyAsStream();
		result = streamToString(in);
		System.out.println(result);
		
		// Use dom4j to parse the response and print nicely to the output stream
		//SAXReader reader = new SAXReader();
		//Document document = reader.read(in);
		//XMLWriter writer = new XMLWriter(System.out, OutputFormat
		//    .createPrettyPrint());
		//writer.write(document);
	}
	
	String streamToString(InputStream in) throws IOException { 
		  String out = new String(); 
		  BufferedReader br = new BufferedReader(new InputStreamReader(in)); 
		  for(String line = br.readLine(); line != null; line = br.readLine())  
		    out += line; 
		  return out; 
		} 

}