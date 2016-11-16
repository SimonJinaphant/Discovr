package org.cpen321.discovr;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * This class provides a list of all the events
 */

public class EventClientManager {

    final List<EventInfo> AllEvents;


    EventClientManager(){
        setUpEventsClient();
        AllEvents = new ArrayList<>();
    }

    public void updateEventsList(){
        setUpEventsClient();
    }

    public List<EventInfo> getAllEvents(){
        return AllEvents;
    }


    /**
     * Sets up the client for getting event information from the events database
     */
    private void setUpEventsClient(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://discovrweb.azurewebsites.net/api/Events", new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                AllEvents.clear();
                String r = new String(response);
                try {
                    JSONArray json = new JSONArray(r);
                    for(int i = 0; i < json.length(); i++){
                        JSONObject o = json.getJSONObject(i);
                        AllEvents.add(new EventInfo(o.getInt("Id"),
                                o.getString("Name"),
                                o.getString("Host"),
                                o.getString("Location"),
                                o.getString("StartTime"),
                                o.getString("EndTime"),
                                "",
                                o.getString("Description")));
                    }
                }
                catch (JSONException e){
                    throw new RuntimeException(e);
                }
                Log.v("events", "Raw client response: " + r);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d("events", "Failed to access the online database: " + statusCode);
                Log.d("events", e.toString());
            }

            @Override
            public void onRetry(int retryNo) {
                Log.d("events", "Retrying client connection");
            }
        });
    }

}
