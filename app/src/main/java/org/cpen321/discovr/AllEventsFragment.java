package org.cpen321.discovr;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.cpen321.discovr.model.Building;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;
import static org.cpen321.discovr.R.dimen.button_margin;
import static org.cpen321.discovr.R.id.LL1_ALLEVENTS;
import static org.cpen321.discovr.R.id.left;

/**
 * A simple {@link Fragment} subclass.
 */

public class AllEventsFragment extends Fragment {
    final int ALLEVENTS = 0;

    public AllEventsFragment() {
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


        SQLiteDBHandler dbh =new SQLiteDBHandler(this.getActivity());
        List<Building> bldgs = dbh.getAllBuildings();
        for (Building b: bldgs)
            Log.d("has building: ", b.name + " " + b.code);
        for(final EventInfo event : ((MainActivity) this.getActivity()).getAllEvents()) {
            final Button button = createButton(event);
            ll.addView(button, lp);
            //Set button's on click listener to open new fragment of that single event on top of map
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SingleEventFragment fragment = new SingleEventFragment();
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Fragment currentFrag = fm.findFragmentById(R.id.fragment_container);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
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
        String[] dateTime = Time.split("T");
        return dateTime[1].substring(0, 5);
    }

    public String getDate(String Time){
        String[] dateTime = Time.split("T");
        return dateTime[0];
    }


}
