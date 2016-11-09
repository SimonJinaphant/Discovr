package org.cpen321.discovr;
import android.content.res.AssetManager;
import android.util.Log;
import com.mapbox.mapboxsdk.geometry.LatLng;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.*;

import org.apache.commons.io.IOUtils;
import org.cpen321.discovr.model.*;

public class GeoJsonParser {


	/**
	 * 
	 * @param InputStream from the ubc buildings geojson datset in /assets
	 * @return a List of all the names of the buildings
	 * @throws IOException, JSONException
	 */

	public static List<String> allnames(InputStream is) throws IOException, JSONException {

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
	public static double[] getCoordinates(String name, InputStream is) throws IOException, JSONException {

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
	
	/**
	 * 
	 * @param stopnum the bus stop number
	 * @param is InputStream from the bus stop geojson file in /assets
	 * @return a MapTransitStation with the information for the stop number
	 * @throws IOException
	 */
	public static MapTransitStation getBusStopInfo(String stopnum, InputStream is) throws IOException {
	
		List<String> vehicles = new ArrayList<String>();
	
		String jsonTxt = IOUtils.toString(is);
		JSONObject obj = new JSONObject(jsonTxt.substring(1));
		JSONArray arr = obj.getJSONArray("features");
		int index = -1;
		for (int i = 0; i < arr.length(); i++) {
			if (arr.getJSONObject(i).getJSONObject("properties").has("BusStop")) {
				if (arr.getJSONObject(i).getJSONObject("properties").getString("BusStop").equals(stopnum)) {
					index = i;
					break;
				}
			}
		}
		if (index != -1) {
			JSONObject stopprop = arr.getJSONObject(index).getJSONObject("properties");
			JSONObject stopgeo = arr.getJSONObject(index).getJSONObject("geometry");
			String name = stopprop.getString("Name");
			for (int j = 0; j < stopprop.getJSONArray("Bus").length(); j++) {
				vehicles.add(stopprop.getJSONArray("Bus").getString(j));
			}
			LatLng location = new LatLng(stopgeo.getJSONArray("coordinates").getDouble(0), stopgeo.getJSONArray("coordinates").getDouble(1));
			MapTransitStation mapstat = new MapTransitStation(stopnum, location, vehicles, name);
			return mapstat;
		}
		else {
			return null;
		}
	}
	
	public static List<MapPolygon> createPolygons(InputStream is) throws IOException {
		String jsonTxt = IOUtils.toString(is);
		JSONObject obj = new JSONObject(jsonTxt.substring(1));
		JSONArray arr = obj.getJSONArray("features");
		
		
		List<MapPolygon> mappolys = new ArrayList<MapPolygon>();
		
		for (int i = 0; i < arr.length(); i++) {
			String name = new String(arr.getJSONObject(i).getJSONObject("properties").getString("Name"));
			List<LatLng> vertices = new ArrayList<LatLng>();
			JSONArray coords = arr.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(0);
			
			for (int j = 0; j < coords.length(); j++) {
				LatLng coordinate = new LatLng(coords.getJSONArray(j).getDouble(0), coords.getJSONArray(j).getDouble(1));
				vertices.add(coordinate);
			}
			mappolys.add(new MapPolygon(name, vertices));
		}
		return mappolys;
	}
}
		
	/*public static void main (String[] args) throws IOException {

		File f = new File("./app/src/main/java/org/cpen321/discovr/buildings.geojson");

		if (f.exists()) {
			InputStream is = new FileInputStream("./app/src/main/java/org/cpen321/discovr/buildings.geojson");
			
			double[] arr = getCoordinates("Civil And Mechanical Engineering Building", is);
			System.out.println(arr[0]);
			System.out.println(arr[1]);
			
		}
		return;
	}*/
	


