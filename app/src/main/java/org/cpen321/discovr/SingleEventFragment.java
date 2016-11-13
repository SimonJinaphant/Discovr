package org.cpen321.discovr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
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

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;
import static org.cpen321.discovr.R.id.singleEventLL2;
import static org.cpen321.discovr.R.id.subscribeButton;
import static org.cpen321.discovr.R.id.textView_event_info;
import static org.cpen321.discovr.R.id.textView_event_name;


public class SingleEventFragment extends Fragment {
    final int ALLEVENTS = 0;
    final int SUBSCRIBEDEVENTS = 1;

    private EventInfo event;

    private int PrevFragment;

    public SingleEventFragment() {

    }

    public void setEvent(EventInfo event){
        this.event = event;
    }

    public void setPrevFragment(int PrevFragment){
        this.PrevFragment = PrevFragment;
    }

    public int getPrevFragment(){
        return this.PrevFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final SQLiteDBHandler dbh = new SQLiteDBHandler(this.getActivity());
        //Inflate fragment from xml file
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.fragment_single_event, container, false);

        //Get tag of current fragment, which is the the event ID

        //Get textView inside of linearlayout and set text
        TextView tv = (TextView) this.getActivity().findViewById(textView_event_name);
        SpannableString titleText = new SpannableString(event.getName() + "\nHosted by " + event.getHostName());
        int index = titleText.toString().indexOf("\n");
        titleText.setSpan(new AbsoluteSizeSpan(100), 0, index, SPAN_INCLUSIVE_INCLUSIVE);
        titleText.setSpan(new AbsoluteSizeSpan(50), index, titleText.length(), SPAN_INCLUSIVE_INCLUSIVE);
        tv.setText(titleText);

        //Get second linear layout and change button background and textView w eventDetails
        LinearLayout ll2 = (LinearLayout) this.getActivity().findViewById(singleEventLL2);
        final Button subscribedButton = (Button) this.getActivity().findViewById(subscribeButton);
        TextView eventDetails = (TextView) this.getActivity().findViewById(textView_event_info);
            eventDetails.setText(
                    "Location: " + event.getBuildingName() + "\n"
                            + "Time: " + formatTime(event.getStartTime()) + " - " + formatTime(event.getEndTime()) + "\n"
                            + "Date: " + getDate(event.getStartTime())
                            + "\n" + "Details: " + event.getEventDetails());
            if(dbh.getEvent(event.getID()) != null) {
                subscribedButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.star_toggle_on));
            }
            else{
                subscribedButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.star_toggle_off));
            }

            //Set subscribe button to listen for clicks and delete the event when pressed, and set background to toggle_off
            subscribedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dbh.getEvent(event.getID()) != null){
                        subscribedButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.star_toggle_off));
                        dbh.deleteEvent(event.getID());
                    }
                    else{
                        subscribedButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.star_toggle_on));
                        dbh.addEvent(event);
                    }
                }
            });


            return ll;
        }

    public String formatTime(String Time){
        String[] dateTime = Time.split("T");
        return dateTime[1].substring(0, 5);
    }

    public String getDate(String Time){
        String[] dateTime = Time.split("T");
        return dateTime[0];
    }
}
