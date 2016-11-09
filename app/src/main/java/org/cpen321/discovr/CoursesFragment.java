package org.cpen321.discovr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;

import org.cpen321.discovr.model.Course;

import java.text.ParseException;
import java.util.List;

import android.widget.Button;
import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;
import static com.loopj.android.http.AsyncHttpClient.log;
import static org.cpen321.discovr.R.dimen.button_margin;
import static org.cpen321.discovr.R.id.left;

import static org.cpen321.discovr.parser.CalendarFileParser.loadUserCourses;

/**
 * Created by zhangyueyue on 2016-11-08.
 */

public class CoursesFragment extends Fragment {
    public CoursesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        //Get DBHandler for this activity
        SQLiteDBHandler dbh = new SQLiteDBHandler(this.getActivity());

        //read from the local ical file --
        // question : am I adding the courses as long as i open this page???
        List<Course> myICalFile = loadUserCourses();
        dbh.addCourses(myICalFile);
        //log.e("error", "it hits this line...");

        // Inflate the layout for this fragment
        final FrameLayout fm = (FrameLayout) inflater.inflate(R.layout.fragment_courses, container, false);
        ScrollView sv = (ScrollView) fm.getChildAt(0);

        //Get linearlayour and layoutParams for new button
        LinearLayout ll = (LinearLayout) sv.getChildAt(0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        try {
            List<Course> myCourses = dbh.getAllCourses();

            //Add new button for each course in DB
            for(Course course : myCourses){
                //formats button to be the same as the format we want in the fragment
                final Button button = createCourseButton(course);
                //Add this button to the layout
                ll.addView(button, lp);

                //add onClick listener here

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return fm;
    }

    private Button createCourseButton(Course course) {
        //Set button properties - gravity, allCaps, padding, backgroundColor, textColor, text
        Button bt =  new Button(this.getActivity());

        //button.setId(course.get);
        bt.setGravity(left);
        bt.setAllCaps(false);
        bt.setPadding(getResources().getDimensionPixelSize(button_margin), getResources().getDimensionPixelSize(button_margin), getResources().getDimensionPixelSize(button_margin), getResources().getDimensionPixelSize(button_margin));
        bt.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.button_press_colors));
        bt.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryTextColor));
        SpannableString buttonText = new SpannableString(course.getCategory()+ " " +course.getNumber()+" "+course.getSection() + "\n" + (course.getStartTime() + " - " + course.getEndTime()) + ", "  + "\n" + course.getBuilding()+course.getRoom());

        //should i add startDate and endDate as well?
        //when it reaches the endDate ,then the course button automatically disappear
        int index = buttonText.toString().indexOf("\n");
        buttonText.setSpan(new AbsoluteSizeSpan(100), 0, index, SPAN_INCLUSIVE_INCLUSIVE);
        buttonText.setSpan(new AbsoluteSizeSpan(60), index, buttonText.length(), SPAN_INCLUSIVE_INCLUSIVE);
        bt.setText(buttonText);
        return bt;
    }
}
