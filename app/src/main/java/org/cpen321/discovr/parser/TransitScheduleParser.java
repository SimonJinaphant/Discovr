package org.cpen321.discovr.parser;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.cpen321.discovr.model.transit.TransitEstimateSchedule;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Simon Jinaphant on 21-Nov-2016.
 */

public class TransitScheduleParser {

    private static final String TRANSIT_API_PUBLIC_KEY = "VxujSiOu28llUoMXPgmw";

    private static final String TRANSIT_BASE_URL = "http://api.translink.ca/rttiapi/v1/stops/";
    private static final String TRANSIT_PARAM_HEADER = "/estimates?apikey=" + TRANSIT_API_PUBLIC_KEY;

    private static final String RESULT_LIMIT_PARAM = "&count=3";

    private TransitScheduleParser() {
    }

    /**
     * Parse a JSONArray of transit schedules for an individual vehicle.
     * @param jsonRawSchedules
     * @return List of TransitEstimateSchedules for the next 3 departure time.
     */
    private static List<TransitEstimateSchedule> extractSchedule(JSONArray jsonRawSchedules) {
        List<TransitEstimateSchedule> result = new ArrayList<>();
        try {
            for (int i = 0; i < jsonRawSchedules.length(); i++) {
                JSONObject jsonSchedule = jsonRawSchedules.getJSONObject(i);

                String destination = jsonSchedule.getString("Destination");
                int expectedCountdown = Integer.parseInt(jsonSchedule.getString("ExpectedCountdown"));
                String rawStatus = jsonSchedule.getString("ScheduleStatus");

                Date date = new Date();

                result.add(
                        new TransitEstimateSchedule(destination, expectedCountdown,
                                rawStatus, date)
                );
            }
        } catch (JSONException jsonException) {
            Log.e("Transit", "Unable to parse JSON data");
            jsonException.printStackTrace();
        }

        return result;
    }

    /**
     * Performs a synchonous HTTP GET to the Translink API given a station number
     *
     * @param stationNumber - The station number
     * @return A map whose keys are the vehicle number and values are a list of the next 3 arrival time.
     */
    public static HashMap<String, List<TransitEstimateSchedule>> httpGetTransitSchedule(String stationNumber) {
        final HashMap<String, List<TransitEstimateSchedule>> scheduleResult = new HashMap<>();

        SyncHttpClient client = new SyncHttpClient();
        client.addHeader("accept", "application/json");
        client.get(TRANSIT_BASE_URL + stationNumber + TRANSIT_PARAM_HEADER + RESULT_LIMIT_PARAM,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        try {
                            // Stations can have multiple buses
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonBusSchedule = response.getJSONObject(i);

                                // Get the bus number
                                String routeNumber = jsonBusSchedule.get("RouteNo").toString();

                                // Get the bus schedules
                                scheduleResult.put(routeNumber, extractSchedule(jsonBusSchedule.getJSONArray("Schedules")));
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
                        Log.d("Transit", "Failed to retrieve schedules: " + statusCode);
                        Log.d("Transit", e.toString());
                    }

                    @Override
                    public void onRetry(int retryNo) {
                        Log.d("Transit", "Retrying Translink API connection");
                    }
                });

        return scheduleResult;
    }

}
