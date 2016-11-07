package org.cpen321.discovr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


public class SingleEventFragment extends Fragment {

    public SingleEventFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        final SQLiteDBHandler dbh = new SQLiteDBHandler(this.getActivity());
        //Inflate fragment from xml file
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.fragment_single_event, container, false);

        //Get tag of current fragment, which is the the event ID
        Fragment currentFrag = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        final int eventID = Integer.valueOf(currentFrag.getTag());
        //Get that event out of the database
        EventInfo event = dbh.getEvent(eventID);

        //Get textView inside of linearlayout and set text
        TextView tv = (TextView) ll.getChildAt(0);
        tv.setText(event.getName() + ", hosted by " + event.getHostName());

        //Get second linear layout and change button background and textView w eventDetails
        LinearLayout ll2 = (LinearLayout) ll.getChildAt(1);
        final Button subscribedButton = (Button) ll2.getChildAt(0);
        ScrollView sv = (ScrollView) ll2.getChildAt(1);
        TextView eventDetails = (TextView) sv.getChildAt(0);

        eventDetails.setText(
                "Location: " + event.getBuildingName() + "\n"
                        + "Time: " + event.getStartTime() + " - " + event.getEndTime()
                        + "\n" + "Details: " + event.getEventDetails() );
        subscribedButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.star_toggle_on));

        //Set subscribe button to listen for clicks and delete the event when pressed, and set background to toggle_off
        subscribedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbh.deleteEvent(eventID);
                subscribedButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.star_toggle_off));
            }
        });


        return ll;
    }

}