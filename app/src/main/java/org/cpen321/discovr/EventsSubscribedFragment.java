package org.cpen321.discovr;


import android.app.usage.UsageEvents;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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

        return inflater.inflate(R.layout.fragment_events_subscribed, container, false);
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SQLiteDBHandler dbh = new SQLiteDBHandler(this.getActivity());
        List<EventInfo> allEvents = dbh.getAllEvents();

        for(EventInfo event : allEvents) {
            Button button = new Button(getActivity());

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(150, 30);
            params.height = 600;
            params.width = 60;
            button.setText(event.getID() + " " + event.getName() + "/n" + event.getTime() + "," + event.getLocation());

            ViewGroup viewGroup = (ViewGroup) getView();
            viewGroup.addView(button, params);
        }
    }

}
