package org.cpen321.discovr;


import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class TranslinkParser {
    static List<String> nextBuses(int stopnum) throws IOException, ParserConfigurationException {
        try {
            String url = "http://api.translink.ca/rttiapi/v1/stops/" + stopnum + "/estimates?apikey=VxujSiOu28llUoMXPgmw";

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new URL(url).openStream());

            List<String> result = new ArrayList<String>();

            for (int i = 0; i < doc.getElementsByTagName("RouteNo").getLength(); i++) {
                result.add(doc.getElementsByTagName("RouteNo").item(i).getTextContent());
                for (int j = 0; j < doc.getElementsByTagName("Schedules").item(i).getChildNodes().getLength(); j++) {
                    result.add(doc.getElementsByTagName("Schedules").item(i).getChildNodes().item(j).getFirstChild().getNextSibling().getNextSibling().getTextContent());
                }
            }
            return result;
        } catch (SAXException exception) {
            throw new IOException("error parsing");
        } catch (IOException exception) {
            throw new IOException("invalid bus number");
        }
    }

    public static void main(String[] args) throws IOException, ParserConfigurationException {
        for (String i : nextBuses(59275))
            System.out.println(i);
    }
}
