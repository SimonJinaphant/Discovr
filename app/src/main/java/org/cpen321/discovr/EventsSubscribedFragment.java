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
