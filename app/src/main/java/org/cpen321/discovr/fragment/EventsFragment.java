package org.cpen321.discovr.fragment;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.mapbox.mapboxsdk.geometry.LatLng;

import org.cpen321.discovr.MainActivity;
import org.cpen321.discovr.R;
import org.cpen321.discovr.SQLiteDBHandler;
import org.cpen321.discovr.model.Building;
import org.cpen321.discovr.model.EventInfo;
import org.cpen321.discovr.fragment.partial.EventPartialFragment;
import org.cpen321.discovr.parser.GeojsonFileParser;
import org.cpen321.discovr.utility.IconUtil;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;
import static org.cpen321.discovr.R.dimen.button_margin;
import static org.cpen321.discovr.R.id.left;

/**
 * A simple {@link Fragment} subclass.
 */

public class EventsFragment extends Fragment {
    final int ALLEVENTS = 0;

    public EventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final FrameLayout fm = (FrameLayout) inflater.inflate(R.layout.fragment_all_event, container, false);
        final ScrollView sv = (ScrollView) fm.getChildAt(0);
        //Get linearlayour and layoutParams for new button
        final LinearLayout ll = (LinearLayout) sv.getChildAt(0);
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        final SQLiteDBHandler dbh = new SQLiteDBHandler(this.getActivity());

        for (final EventInfo event : ((MainActivity) this.getActivity()).getAllEvents()) {
            final Button button = createButton(event);
            ll.addView(button, lp);
            //Set button's on click listener to open new fragment of that single event on top of map
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventPartialFragment fragment = new EventPartialFragment();
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Fragment currentFrag = fm.findFragmentById(R.id.fragment_container);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    //Move map and add marker point
                    Building bldg = dbh.getBuildingByCode(event.getBuildingName());
                    LatLng loc;

                    if (bldg != null) {
                        loc = GeojsonFileParser.getCoordinates(bldg.getAllCoordinates());
                        ((MainActivity) getActivity()).moveMapWithUniqueMarker(loc, IconUtil.MarkerType.EVENT);
                    }

                    //hide current fragment, will reopen when back key pressed
                    fragment.setEvent(event);
                    fragment.setPrevFragment(ALLEVENTS);
                    transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_left);
                    transaction.remove(currentFrag);
                    transaction.add(R.id.fragment_container, fragment, String.valueOf(button.getId()));
                    transaction.addToBackStack(null);
                    transaction.commit();

                }
            });
        }

        return fm;
    }

    public Button createButton(EventInfo event) {
        //Set button properties - gravity, allCaps, padding, backgroundColor, textColor, text
        Button button = new Button(this.getActivity());
        button.setId(event.getID());
        button.setGravity(left);
        button.setAllCaps(false);
        button.setPadding(getResources().getDimensionPixelSize(button_margin), getResources().getDimensionPixelSize(button_margin), getResources().getDimensionPixelSize(button_margin), getResources().getDimensionPixelSize(button_margin));
        button.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_press_colors));
        button.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryTextColor));
        SpannableString buttonText = new SpannableString(event.getName() + "\n" + EventInfo.getTimeString(event.getStartTime()).substring(0, 5) + " - " + EventInfo.getTimeString(event.getEndTime()).substring(0, 5) + ", " + EventInfo.getDateString(event.getStartTime()) + "\n" + event.getBuildingName());
        int index = buttonText.toString().indexOf("\n");
        buttonText.setSpan(new RelativeSizeSpan(2), 0, index, SPAN_INCLUSIVE_INCLUSIVE);
        buttonText.setSpan(new RelativeSizeSpan((float) 1.5), index, buttonText.length(), SPAN_INCLUSIVE_INCLUSIVE);
        button.setText(buttonText);

        //Add arrow to end of button
        Drawable arrow = ContextCompat.getDrawable(getContext(), R.drawable.right_arrow);
        button.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, arrow, null);

        return button;
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
