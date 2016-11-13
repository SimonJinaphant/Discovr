package org.cpen321.discovr;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
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
import static org.cpen321.discovr.R.id.LL1;
import static org.cpen321.discovr.R.id.left;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsSubscribedFragment extends Fragment {
    final int SUBSCRIBEDEVENTS = 1;

    public EventsSubscribedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Get DBHandler for this activity
        SQLiteDBHandler dbh = new SQLiteDBHandler(this.getActivity());

        //Get List of all events stored in DB
        List<EventInfo> allEvents = dbh.getAllEvents();

        // Inflate the layout for this fragment
        final FrameLayout fm = (FrameLayout) inflater.inflate(R.layout.fragment_events_subscribed, container, false);

        //Get linearlayour and layoutParams for new button
        LinearLayout ll = (LinearLayout) this.getActivity().findViewById(LL1);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //Add new button for each event in DB
        for(final EventInfo event : allEvents) {

            //formats button to be the same as the format we want in the fragment
            final Button button = createButton(event);
            //Add this button to the layout
            ll.addView(button, lp);

            //Set button's on click listener to open new fragment of that single event on top of map
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SingleEventFragment fragment = new SingleEventFragment();
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Fragment currentFrag = fm.findFragmentById(R.id.fragment_container);
                    Log.d("backstack", "From Subscribed Events: currFragment = " + currentFrag);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    fragment.setEvent(event);
                    fragment.setPrevFragment(SUBSCRIBEDEVENTS);


                    //hide current fragment, will reopen when back key pressed
                    transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_left);
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

    public Button createButton(EventInfo event){
        //Set button properties - gravity, allCaps, padding, backgroundColor, textColor, text
        Button button =  new Button(this.getActivity());
        button.setId(event.getID());
        button.setGravity(left);
        button.setAllCaps(false);
        button.setPadding(getResources().getDimensionPixelSize(button_margin), getResources().getDimensionPixelSize(button_margin), getResources().getDimensionPixelSize(button_margin), getResources().getDimensionPixelSize(button_margin));
        button.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.button_press_colors));
        button.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryTextColor));
        SpannableString buttonText = new SpannableString(event.getName() + "\n" + formatTime(event.getStartTime()) + " - " + formatTime(event.getEndTime()) + ", " + getDate(event.getStartTime()) + "\n" + event.getBuildingName());
        int index = buttonText.toString().indexOf("\n");
        buttonText.setSpan(new AbsoluteSizeSpan(100), 0, index, SPAN_INCLUSIVE_INCLUSIVE);
        buttonText.setSpan(new AbsoluteSizeSpan(60), index, buttonText.length(), SPAN_INCLUSIVE_INCLUSIVE);
        button.setText(buttonText);

        //Add arrow to end of button
        Drawable arrow = ContextCompat.getDrawable(getContext(), R.drawable.right_arrow);
        button.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, arrow, null);

        return button;
    }

    public String formatTime(String Time){
        Log.d("Got this time", Time);
        String[] dateTime = Time.split("T");
        return dateTime[1].substring(0, 5);
    }

    public String getDate(String Time){
        String[] dateTime = Time.split("T");
        return dateTime[0];
    }

}