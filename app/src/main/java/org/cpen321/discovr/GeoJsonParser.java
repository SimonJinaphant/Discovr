package org.cpen321.discovr;
import org.json.*;

public class GeoJsonParser {
	
	
	
	static String parse() {
		JSONObject obj = new JSONObject("/Discovr/mapbox geojson/buildings.geojson".trim());
		//JSONArray arr = obj.getJSONArray("features");
		
	
		return "a";
	}
	
	
	public static void main (String[] args) {
		System.out.print(parse());
		return;
	}
	
	
}
