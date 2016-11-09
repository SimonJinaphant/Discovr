//package org.cpen321.discovr;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.json.*;
//
//import org.apache.commons.io.IOUtils;
//
//public class GeoJsonParser {
//
//
//	public static List<String> allnames(InputStream is) throws IOException {
//
//		List<String> names = new ArrayList<String>();
//		String jsonTxt = IOUtils.toString(is);
//		JSONObject obj = new JSONObject(jsonTxt.substring(1));
//		JSONArray arr = obj.getJSONArray("features");
//		for (int i = 0; i < arr.length(); i++) {
//			if (arr.getJSONObject(i).getJSONObject("properties").has("Name")) {
//				names.add(arr.getJSONObject(i).getJSONObject("properties").getString("Name"));
//			}
//			else {
//				names.add("does not exist");
//			}
//		}
//		return names;
//	}
//
//	public static double[] getCoordinates(String name, InputStream is) throws IOException {
//		//List<String> listnames = allnames(is);
//
//		double temp[] = new double[2];
//		temp[0] = 0;
//		temp[1] = 0;
//
//		int index = -1;/*
//		for (int i = 0; i < listnames.size(); i++) {
//			if (listnames.get(i).equals(name)) {
//				index = i;
//			}
//		}*/
//
//		String jsonTxt = IOUtils.toString(is);
//
//		JSONObject obj = new JSONObject(jsonTxt.substring(1));
//		JSONArray arr = obj.getJSONArray("features");
//
//		for (int i = 0; i < arr.length(); i++) {
//			if (arr.getJSONObject(i).getJSONObject("properties").has("Name")) {
//				//System.out.println(arr.getJSONObject(i).getJSONObject("properties").getString("Name"));
//				//System.out.println(arr.getJSONObject(i).getJSONObject("properties").getString("Name").equals(name));
//				if (arr.getJSONObject(i).getJSONObject("properties").getString("Name").equals(name)) {
//					index = i;
//					break;
//				}
//			}
//		}
//		//System.out.print(index);
//
//		if (index != -1) {
//			JSONArray coords = arr.getJSONObject(index).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(0);
//
//			for (int j = 0; j < coords.length(); j++) {
//				temp[0] += coords.getJSONArray(j).getDouble(0);
//				//System.out.println(temp[0]);
//				temp[1] += coords.getJSONArray(j).getDouble(1);
//				//System.out.println(temp[1]);
//			}
//			temp[0] = temp[0] / coords.length();
//			temp[1] = temp[1] / coords.length();
//
//		}
//
//		//System.out.println(arr.getJSONObject(index).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(0).length());
//	return temp;
//}
//
//
//	public static void main (String[] args) throws IOException {
//		File f = new File("./app/src/main/java/org/cpen321/discovr/buildings.geojson");
//
//		if (f.exists()) {
//			InputStream is = new FileInputStream("./app/src/main/java/org/cpen321/discovr/buildings.geojson");
//
//			double[] arr = getCoordinates("Civil And Mechanical Engineering Building", is);
//			System.out.println(arr[0]);
//			System.out.println(arr[1]);
//
//		}
//		return;
//	}
//}
//
