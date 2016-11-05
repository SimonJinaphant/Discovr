package org.cpen321.discovr;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.RelativeSizeSpan;
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
import static org.cpen321.discovr.R.color.button_focused;
import static org.cpen321.discovr.R.color.primaryTextColor;
import static org.cpen321.discovr.R.dimen.button_margin;
import static org.cpen321.discovr.R.id.center_vertical;
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
        // Inflate the layout for this fragment

        SQLiteDBHandler dbh = new SQLiteDBHandler(this.getActivity());

        List<EventInfo> allEvents = dbh.getAllEvents();

        FrameLayout fm = (FrameLayout) inflater.inflate(R.layout.fragment_events_subscribed, container, false);
        ScrollView sv = (ScrollView) fm.getChildAt(0);
        LinearLayout ll = (LinearLayout) sv.getChildAt(0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        int id = 0;
        for(EventInfo event : allEvents) {
            Button button = new Button(getActivity());

            button.setId(id);
            id++;
            /*
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:drawableEnd="@drawable/right_arrow"
            android:gravity="left|center_vertical"
            android:text="@string/fw_events_sub_1"
            android:textColor="@color/primaryTextColor"
            android:textAllCaps="false"
            android:background="@drawable/button_press_colors"
            android:padding="@dimen/button_margin
                    */
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

            ll.addView(button, lp);
        }

        return fm;
    }

}
