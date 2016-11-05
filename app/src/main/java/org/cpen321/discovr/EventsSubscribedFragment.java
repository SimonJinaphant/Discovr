package org.cpen321.discovr;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventsSubscribedFragment extends Fragment {

    public EventsSubscribedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        SQLiteDBHandler dbh = new SQLiteDBHandler(this.getActivity());

        EventInfo event1 = new EventInfo(1, "ELEC221 Midterm", "Lampe", "HEBB100", "2:00", "3:00", "12929939", "RIP");
        EventInfo event2 = new EventInfo(2, "CPSC321 Midterm", "Farshid", "ICICS", "2:00", "3:00", "12929939", "RIP");
        EventInfo event3 = new EventInfo(3, "CPSC123 Midterm", "Lampe", "ICICS", "2:00", "3:00", "12929939", "RIP");
        EventInfo event4 = new EventInfo(4, "123", "Midterm", "ICIS", "2:00", "3:00", "12929939", "RIP");
        EventInfo event5 = new EventInfo(5, "43456", "Lampe", "Midterm", "2:00", "3:00", "12929939", "RIP");
        EventInfo event6 = new EventInfo(6, "Lampe Midterm", "Evans", "HEBB100", "2:00", "3:00", "12929939", "RIP");
        dbh.addEvent(event1);
        dbh.addEvent(event2);
        dbh.addEvent(event3);
        dbh.addEvent(event4);
        dbh.addEvent(event5);
        dbh.addEvent(event6);

        Log.d("Searching..", "Search for midterm and Lampe");
        EventInfo searchID = dbh.getEvent(4);
        Log.d("Searching for event 4", searchID.getName());
        List<EventInfo> searchMidterm = dbh.getEventbySearch("Midterm");
        List<EventInfo> searchLampe = dbh.getEventbySearch("Lampe");

        for(EventInfo event : searchMidterm) {
            Log.d("Searched midterm...", event.getName() + " " + event.getHostName() + " " + event.getBuildingName());
        }

        for(EventInfo event : searchLampe) {
            Log.d("Searched lampe...", event.getName() + " " + event.getHostName() + " " + event.getBuildingName());
        }

        List<EventInfo> allEvents = dbh.getAllEvents();

        LinearLayout linearLayout = new LinearLayout(getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL); //or VERTICAL

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        int id = 0;
        for(EventInfo event : allEvents) {
            Log.d("Getting events", event.getName());
            Button button = new Button(getActivity());

            button.setId(id);
            id++;

            button.setText(event.getID() + " " + event.getName() + "\n" + event.getStartTime() + "-" + event.getEndTime() +  "," + event.getLocation());
            button.setLayoutParams(buttonParams);
            linearLayout.addView(button);
        }

        container.addView(linearLayout);

        return inflater.inflate(R.layout.fragment_events_subscribed, container, false);
    }

}
