package org.cpen321.discovr;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.cpen321.discovr.model.EventInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import cz.msebera.android.httpclient.Header;

/**
 * This class provides a list of all the events
 */

public class EventClientManager {

    final List<EventInfo> AllEvents;


    EventClientManager(){
        AllEvents = new ArrayList<>();
        updateEventsList();
    }

    /**
     * Constructor with a list of events
     * @param events
     */
    EventClientManager(List<EventInfo> events){
        AllEvents = new ArrayList<>(events);
    }

    public void updateEventsList(){
        setUpEventsClient();
    }


    public List<EventInfo> getAllEvents(){
        List<EventInfo> events = new ArrayList<>(AllEvents);
        pruneList(events);
        return AllEvents;
    }

    public List<EventInfo> getUpcomingEvents(){
        List<EventInfo> events = new ArrayList<>(AllEvents);
        pruneList(events);
        upcomingEventFilter(events);
        return events;
    }

    private void upcomingEventFilter(List<EventInfo> events){
        Date tomorrow = addOneDay(new Date());
        ListIterator<EventInfo> li = events.listIterator();
        while (li.hasNext()){
            EventInfo ei = li.next();
            if (ei.getEndTime().after(tomorrow)){
                li.remove();
            }
        }
    }

    public static Date addOneDay(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }

    public EventInfo findEvent(int eventID){
        ListIterator<EventInfo> li = AllEvents.listIterator();
        while (li.hasNext()){
            EventInfo event = li.next();
            if (event.getID() == eventID){
                return event;
            }
        }
        return null;
    }


    /**
     * Sorts the event list from earliest to latest
     */
    private void sortList(List<EventInfo> events) {
        Collections.sort(events, new Comparator<EventInfo>() {
            @Override
            public int compare(EventInfo o1, EventInfo o2) {
                return o1.getStartTime().compareTo(o2.getStartTime());
            }
        });

    }

    /**
     * Sorts and removes all events before the current Time period
     */
    private void pruneList(List<EventInfo> events){
        sortList(events);
        ListIterator<EventInfo> li = events.listIterator();
        while (li.hasNext()) {
            EventInfo event = li.next();
            if (event.getEndTime().before(new Date())){
                li.remove();
            }
        }
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
