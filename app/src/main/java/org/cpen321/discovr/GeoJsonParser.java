package org.cpen321.discovr;

import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;

import org.apache.commons.io.IOUtils;
import org.cpen321.discovr.model.Building;
import org.cpen321.discovr.model.MapPolygon;
import org.cpen321.discovr.model.MapTransitStation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

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
     * Parse a geojson file to extract the geoMarkers objects from it.
     * @param fileStream - A input file stream of the .geojson file.
     * @return - A list of render-able MapTransitStation.
     * @throws IOException
     * @throws JSONException
     */
    public static List<MapPolygon> parsePolygons(InputStream fileStream) throws IOException, JSONException {
        JSONArray jsonFeatures = new JSONObject(IOUtils.toString(fileStream)).getJSONArray("features");
        List<MapPolygon> polygons = new ArrayList<>();

        for (int i = 0; i < jsonFeatures.length(); i++) {
            String name = jsonFeatures.getJSONObject(i).getJSONObject("properties").getString("Name");

            List<LatLng> vertices = new ArrayList<>();

            JSONArray jsonCoordinates = jsonFeatures.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(0);

            for (int j = 0; j < jsonCoordinates.length(); j++) {
                JSONArray latLng = jsonCoordinates.getJSONArray(j);

                // .geojson files typically store LatLng in order of [longitude, latitude]
                LatLng coordinate = new LatLng(latLng.getDouble(1), latLng.getDouble(0));

                vertices.add(coordinate);
            }

            polygons.add(new MapPolygon(name, vertices));
        }
        return polygons;
    }

    /**
     * Parse a geojson file to extract the geoPolygon objects from it.
     * @param fileStream - A input file stream of the .geojson file.
     * @return - A list of render-able MapPolygons
     * @throws IOException
     * @throws JSONException
     */
    public static List<MapTransitStation> parseTransitStations(InputStream fileStream) throws IOException, JSONException {
        JSONArray jsonFeatures = new JSONObject(IOUtils.toString(fileStream)).getJSONArray("features");
        List<MapTransitStation> stations = new ArrayList<>();

        for (int i = 0; i < jsonFeatures.length(); i++) {
            // Get the bus stop's name	ie: "Bay 1".
            String name = jsonFeatures.getJSONObject(i).getJSONObject("properties").getString("Name");

            // Get the bus stop's number; we use this to query the transit API for the schedule.
            String busStop = jsonFeatures.getJSONObject(i).getJSONObject("properties").getString("BusStop");

            // Get a list of all possible buses which stop at this station.
            List<String> buses = new ArrayList<>();
            JSONArray jsonBuses = jsonFeatures.getJSONObject(i).getJSONObject("properties").getJSONArray("Bus");

            for(int b = 0; b < jsonBuses.length(); b++){
                buses.add(String.valueOf(jsonBuses.get(b)));
            }

            JSONArray jsonCoordinate = jsonFeatures.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates");
            LatLng coordinate = new LatLng(jsonCoordinate.getDouble(1), jsonCoordinate.getDouble(0));

            stations.add(new MapTransitStation(busStop, coordinate, buses, name));
        }
        return stations;
    }

	/**
	 * Parse a geojson file to extract MapBox building objects from it.
	 * @param fileStream - A input file stream of the .geojson file.
	 * @return - A list of Buildings
	 * @throws IOException
	 * @throws JSONException
	 */
	public static List<Building> parseBuildings(InputStream fileStream) throws IOException, JSONException {
		JSONArray jsonFeatures = new JSONObject(IOUtils.toString(fileStream)).getJSONArray("features");
		List<Building> buildings = new ArrayList<>();

		for (int i = 0; i < jsonFeatures.length(); i++) {
			JSONObject jsonBuilding = jsonFeatures.getJSONObject(i).getJSONObject("properties");

			if(!jsonBuilding.has("Name")){
				Log.e("GeoParser", "There seems to be a nameless entry at index "+i);
				System.out.println(jsonBuilding);
				continue;
			}
			String name = jsonBuilding.getString("Name");
			String code = jsonBuilding.has("Code") ? jsonBuilding.getString("Code") : null;
			String address = jsonBuilding.has("Address") ? jsonBuilding.getString("Address") : null;
			String hours = jsonBuilding.has("Hours") ? jsonBuilding.getString("Hours") : null;

			List<LatLng> coordinates = new ArrayList<>();
			JSONArray jsonCoordinates = jsonFeatures.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(0);
			if(jsonCoordinates.length() > 18){
				continue;
			}
			for (int j = 0; j < jsonCoordinates.length()-1; j++) {
				JSONArray latLng = jsonCoordinates.getJSONArray(j);

				// .geojson files typically store LatLng in order of [longitude, latitude]
				LatLng coordinate = new LatLng(latLng.getDouble(1), latLng.getDouble(0));

				coordinates.add(coordinate);
			}
			System.out.println(coordinates.size());
			buildings.add(new Building(name, code, address, hours, coordinates));

		}

		return buildings;
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



