package org.cpen321.discovr;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.*;

import org.apache.commons.io.IOUtils;

public class GeoJsonParser {

	/**
	 * 
	 * @param InputStream from the ubc buildings geojson datset in /assets
	 * @return a List of all the names of the buildings
	 * @throws IOException
	 */
	public static List<String> allnames(InputStream is) throws IOException {
		
		List<String> names = new ArrayList<String>();
		String jsonTxt = IOUtils.toString(is);
		JSONObject obj = new JSONObject(jsonTxt.substring(1));
		JSONArray arr = obj.getJSONArray("features");
		for (int i = 0; i < arr.length(); i++) {
			if (arr.getJSONObject(i).getJSONObject("properties").has("Name")) {
				names.add(arr.getJSONObject(i).getJSONObject("properties").getString("Name"));
			}
			else {
				names.add("does not exist");
			}
		}
		return names;	
	}
	/**
	 * 
	 * @param name - name of the building
	 * @param is - InputStream from the ubc buildings geojson datset in /assets 
	 * @return an array of double of size 2, long / lat
	 * @throws IOException
	 */
	
	public static double[] getCoordinates(String name, InputStream is) throws IOException {

		double temp[] = new double[2];

		int index = -1;
		
		String jsonTxt = IOUtils.toString(is);
						
		JSONObject obj = new JSONObject(jsonTxt.substring(1));
		JSONArray arr = obj.getJSONArray("features");
		
		for (int i = 0; i < arr.length(); i++) {
			if (arr.getJSONObject(i).getJSONObject("properties").has("Name")) {
				if (arr.getJSONObject(i).getJSONObject("properties").getString("Name").equals(name)) {
					index = i;
					break;
				}
			}
		}
	
		if (index != -1) {
			JSONArray coords = arr.getJSONObject(index).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(0);
			
			for (int j = 0; j < coords.length(); j++) {
				temp[0] += coords.getJSONArray(j).getDouble(0);
				
				temp[1] += coords.getJSONArray(j).getDouble(1);

			}
			temp[0] = temp[0] / coords.length();
			temp[1] = temp[1] / coords.length();
				
		}
		return temp;
}

	
	
	
	
	public static void main (String[] args) throws IOException {
		File f = new File("./app/src/main/java/org/cpen321/discovr/buildings.geojson");

		if (f.exists()) {
			InputStream is = new FileInputStream("./app/src/main/java/org/cpen321/discovr/buildings.geojson");
			
			double[] arr = getCoordinates("Civil And Mechanical Engineering Building", is);
			System.out.println(arr[0]);
			System.out.println(arr[1]);
			
		}
		return;
	}
}

