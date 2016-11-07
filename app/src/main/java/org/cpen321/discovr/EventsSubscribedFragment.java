package org.cpen321.discovr;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.List;
import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;
import static org.cpen321.discovr.R.dimen.button_margin;
import static org.cpen321.discovr.R.id.fragment_container;
import static org.cpen321.discovr.R.id.left;


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

        //Get DBHandler for this activity
        SQLiteDBHandler dbh = new SQLiteDBHandler(this.getActivity());
/*
        dbh.addEvent(new EventInfo(6, "CPEN321", "ECE", "DEMPSTER", "12:30", "2:00", "010010101", "yaaaaay 43204g jseogj 49tu2309irwoj fa029ti 20934i tjgreaif9o23jta0gspei gj 90aw tj02atira90po ut09fair ji3a2okjtkg9a0 urtjop2iat 934airkf 0"));
        dbh.addEvent(new EventInfo(2, "CPSC221", "CPSC", "DEMPSTER", "8:00", "9:30", "05435101", "wooooo"));
        dbh.addEvent(new EventInfo(3, "ELEC221", "ECE", "HEBB", "2:00", "3:00", "043630101", "fck"));
        dbh.addEvent(new EventInfo(4, "CPEN311", "ECE", "WOOD", "12:00", "1:00", "01342101", "crey"));
        dbh.addEvent(new EventInfo(5, "STAT302", "MATH", "CHEM", "11:00", "12:00", "01234", "lol"));
*/
        //Get List of all events stored in DB
        List<EventInfo> allEvents = dbh.getAllEvents();

        // Inflate the layout for this fragment
        final FrameLayout fm = (FrameLayout) inflater.inflate(R.layout.fragment_events_subscribed, container, false);
        ScrollView sv = (ScrollView) fm.getChildAt(0);
        //Get linearlayour and layoutParams for new button
        LinearLayout ll = (LinearLayout) sv.getChildAt(0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //Add new button for each event in DB
        for(EventInfo event : allEvents) {
            final Button button = new Button(getActivity());

            //Set ID of button = id of event
            button.setId(event.getID());

            //Set button properties - gravity, allCaps, padding, backgroundColor, textColor, text
            button.setGravity(left);
            button.setAllCaps(false);
            button.setPadding(getResources().getDimensionPixelSize(button_margin), getResources().getDimensionPixelSize(button_margin), getResources().getDimensionPixelSize(button_margin), getResources().getDimensionPixelSize(button_margin));
            button.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.button_press_colors));
            button.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryTextColor));
            SpannableString buttonText = new SpannableString(event.getName() + "\n" + event.getStartTime() + " - " + event.getEndTime() + ", in " + event.getLocation());
            int index = buttonText.toString().indexOf("\n");
            buttonText.setSpan(new AbsoluteSizeSpan(60), 0, index, SPAN_INCLUSIVE_INCLUSIVE);
            buttonText.setSpan(new AbsoluteSizeSpan(40), index, buttonText.length(), SPAN_INCLUSIVE_INCLUSIVE);
            button.setText(buttonText);

            //Add arrow to end of button
            Drawable arrow = ContextCompat.getDrawable(getContext(), R.drawable.right_arrow);
            button.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, arrow, null);
            ll.addView(button, lp);

            //Set button's on click listener to open new fragment of that single event on top of map
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SingleEventFragment fragment = new SingleEventFragment();
                    Fragment currentFrag = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    //hide current fragment, will reopen when back key pressed
                    transaction.remove(currentFrag);
                    transaction.add(R.id.fragment_container, fragment, String.valueOf(button.getId()));
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });


            //Add new button to linearlayout with all properties set above and layour params

        }

        return fm;
    }

}