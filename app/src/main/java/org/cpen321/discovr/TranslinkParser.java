package org.cpen321.discovr;


import java.io.IOException;
import java.net.URL;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;


import org.xml.sax.SAXException;

public class TranslinkParser {
	static void printproperties() throws IOException, ParserConfigurationException {
		
	
		try {
			String url = "http://api.translink.ca/rttiapi/v1/stops/59275/estimates?apikey=VxujSiOu28llUoMXPgmw";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new URL(url).openStream());

			
			for (int i = 0; i < doc.getElementsByTagName("RouteNo").getLength(); i++) {
				System.out.println(doc.getElementsByTagName("RouteNo").item(i).getTextContent());
				for (int j = 0; j < doc.getElementsByTagName("Schedules").item(i).getChildNodes().getLength(); j++)	{
					System.out.println(doc.getElementsByTagName("Schedules").item(i).getChildNodes().item(j).getFirstChild().getNextSibling().getNextSibling().getTextContent());
				}
			}
			/*
		    	for (int i = 0; i < 10; i++) {
					
					System.out.print(doc.getElementsByTagName("ExpectedLeaveTime").item(i).getTextContent());
					System.out.println(" in " + doc.getElementsByTagName("ExpectedCountdown").item(i).getTextContent() + " minutes");
				}
			*/
			
		} catch (SAXException exception) {
			System.out.print(exception);
			throw new IOException("rip");
		}
	}
	public static void main(String[] args) throws IOException, ParserConfigurationException {
		printproperties();
	}
}
