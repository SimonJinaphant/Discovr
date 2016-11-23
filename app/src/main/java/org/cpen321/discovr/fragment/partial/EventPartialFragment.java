package org.cpen321.discovr.fragment.partial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mapbox.mapboxsdk.geometry.LatLng;

import org.cpen321.discovr.MainActivity;
import org.cpen321.discovr.R;
import org.cpen321.discovr.SQLiteDBHandler;
import org.cpen321.discovr.model.Building;
import org.cpen321.discovr.model.EventInfo;
import org.cpen321.discovr.parser.GeoJsonParser;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;


public class EventPartialFragment extends Fragment {
    final int ALLEVENTS = 0;
    final int SUBSCRIBEDEVENTS = 1;

    private EventInfo event;

    private int PrevFragment;

    public EventPartialFragment() {

    }

    public void setEvent(EventInfo event) {
        this.event = event;
    }

    public int getPrevFragment() {
        return this.PrevFragment;
    }

    public void setPrevFragment(int PrevFragment) {
        this.PrevFragment = PrevFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final SQLiteDBHandler dbh = new SQLiteDBHandler(this.getActivity());
        //Inflate fragment from xml file
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.fragment_single_event, container, false);

        Building bldg = dbh.getBuildingByCode(event.getBuildingName());
        LatLng loc;
        if (bldg != null) {
            loc = GeoJsonParser.getCoordinates(bldg.getAllCoordinates());
            ((MainActivity) this.getActivity()).moveMap(loc);
        }

        //Get textView inside of linearlayout and set text
        TextView tv = (TextView) ll.getChildAt(0);

        SpannableString titleText = new SpannableString(event.getName() + "\nHosted by " + event.getHostName());
        int index = titleText.toString().indexOf("\n");
        titleText.setSpan(new AbsoluteSizeSpan(100), 0, index, SPAN_INCLUSIVE_INCLUSIVE);
        titleText.setSpan(new AbsoluteSizeSpan(50), index, titleText.length(), SPAN_INCLUSIVE_INCLUSIVE);
        tv.setText(titleText);

        Building bldgInfo = dbh.getBuildingByCode(event.getBuildingName());
        if (bldgInfo != null)
            Log.d("Got info: ", bldgInfo.getCoordinatesAsString() + " " + bldgInfo.address);
        //Get second linear layout and change button background and textView w eventDetails
        LinearLayout ll2 = (LinearLayout) ll.getChildAt(1);
        final Button subscribedButton = (Button) ll2.getChildAt(0);
        ScrollView sv = (ScrollView) ll2.getChildAt(1);
        TextView eventDetails = (TextView) sv.getChildAt(0);

        eventDetails.setText(
                "Location: " + event.getBuildingName() + "\n"
                        + "Time: " + EventInfo.getTimeString(event.getStartTime()) + " - " + EventInfo.getTimeString(event.getEndTime()) + "\n"
                        + "Date: " + EventInfo.getDateString(event.getStartTime())
                        + "\n" + "Details: " + event.getEventDetails());
        if (dbh.getEvent(event.getID()) != null) {
            subscribedButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.star_toggle_on));
        } else {
            subscribedButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.star_toggle_off));
        }

        //Set subscribe button to listen for clicks and delete the event when pressed, and set background to toggle_off
        subscribedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dbh.getEvent(event.getID()) != null) {
                    subscribedButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.star_toggle_off));
                    dbh.deleteEvent(event.getID());
                } else {
                    subscribedButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.star_toggle_on));
                    dbh.addEvent(event);
                }
            }
        });


        return ll;
    }

    public String formatTime(String Time) {
        String[] dateTime = Time.split("T");
        return dateTime[1].substring(0, 5);
    }

    public String getDate(String Time) {
        String[] dateTime = Time.split("T");
        return dateTime[0];
    }
}
