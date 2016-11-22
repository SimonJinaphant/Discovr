package org.cpen321.discovr.parser;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import net.fortuna.ical4j.data.ParserException;

import org.cpen321.discovr.model.transit.TransitEstimateSchedule;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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

    private static volatile boolean ready = true;
    private static HashMap<String, List<TransitEstimateSchedule>> scheduleResult = new HashMap<>();

    public static boolean canRead() {
        return ready;
    }

    public static Set<Map.Entry<String, List<TransitEstimateSchedule>>> read() {
        return scheduleResult.entrySet();
    }

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

    public static void httpGetTransitSchedule(String stationNumber) {
        scheduleResult.clear();
        ready = false;

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("accept", "application/json");
        client.get(TRANSIT_BASE_URL + stationNumber + TRANSIT_PARAM_HEADER + RESULT_LIMIT_PARAM,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        // called before request is started
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        String r = new String(response);
                        try {
                            JSONArray jsonAllSchedules = new JSONArray(r);

                            // Stations can have multiple buses
                            for (int i = 0; i < jsonAllSchedules.length(); i++) {
                                JSONObject jsonBusSchedule = jsonAllSchedules.getJSONObject(i);

                                // Get the bus number
                                String routeNumber = jsonBusSchedule.get("RouteNo").toString();

                                // Get the bus schedules
                                scheduleResult.put(routeNumber, extractSchedule(jsonBusSchedule.getJSONArray("Schedules")));
                            }
                            ready = true;
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        Log.d("Transit", "Failed to retrieve schedules: " + statusCode);
                        Log.d("Transit", e.toString());
                    }

                    @Override
                    public void onRetry(int retryNo) {
                        Log.d("events", "Retrying client connection");
                    }
                });
    }

}
