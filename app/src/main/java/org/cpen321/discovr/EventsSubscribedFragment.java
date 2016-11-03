package org.cpen321.discovr;


import android.app.usage.UsageEvents;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventsSubscribedFragment extends Fragment {

    Button button;
    public EventsSubscribedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SQLiteDBHandler dbh = new SQLiteDBHandler(this.getActivity());
        List<EventInfo> allEvents = dbh.getAllEvents();

        for(EventInfo event : allEvents) {
            Button button = new Button(getActivity());

            RelativeLayout.LayoutParams paramsd = new RelativeLayout.LayoutParams(150, 30);
            paramsd.height = 600;
            paramsd.width = 60;

            ViewGroup viewGroup = (ViewGroup) getView();
            viewGroup.addView(button, paramsd);
        }
        return inflater.inflate(R.layout.fragment_events_subscribed, container, false);
    }




}
