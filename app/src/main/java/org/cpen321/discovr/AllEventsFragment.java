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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;
import static org.cpen321.discovr.R.dimen.button_margin;
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
        ScrollView sv = (ScrollView) fm.getChildAt(0);
        //Get linearlayour and layoutParams for new button
        final LinearLayout ll = (LinearLayout) sv.getChildAt(0);
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Inflate the layout for this fragment
        final AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://discovrweb.azurewebsites.net/api/Events", new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {

                String r = new String(response);
                try {
                    JSONArray json = new JSONArray(r);
                    for(int i = 0; i < json.length(); i++){
                        JSONObject o = json.getJSONObject(i);

                        final Button button = new Button(getActivity());
                        final EventInfo event = new EventInfo(o.getInt("Id"),
                                o.getString("Name"),
                                o.getString("Host"),
                                o.getString("Location"),
                                o.getString("StartTime"),
                                o.getString("EndTime"),
                                "",
                                o.getString("Description"));

                        //Set ID of button = id of event
                        button.setId(o.getInt("Id"));

                        //Set button properties - gravity, allCaps, padding, backgroundColor, textColor, text
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
                        ll.addView(button, lp);

                        //Set button's on click listener to open new fragment of that single event on top of map
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SingleEventFragment fragment = new SingleEventFragment();
                                Fragment currentFrag = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                //hide current fragment, will reopen when back key pressed
                                fragment.setEvent(event);
                                fragment.setPrevFragment(ALLEVENTS);

                                transaction.remove(currentFrag);
                                transaction.add(R.id.fragment_container, fragment, String.valueOf(button.getId()));
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }
                        });

                    }
                }
                catch (JSONException e){
                    throw new RuntimeException(e);
                }


                System.out.println(r);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                System.out.println(":(");
            }
            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
        return fm;
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
