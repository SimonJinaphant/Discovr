package org.cpen321.discovr.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import org.cpen321.discovr.MainActivity;
import org.cpen321.discovr.R;
import org.cpen321.discovr.SQLiteDBHandler;
import org.cpen321.discovr.fragment.partial.CoursePartialFragment;
import org.cpen321.discovr.model.Course;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;
import static org.cpen321.discovr.R.dimen.button_margin;
import static org.cpen321.discovr.R.id.left;

/**
 * Created by zhangyueyue on 2016-11-08.
 */

public class CoursesFragment extends Fragment {
    private static final int LISTALLCOURSES = 1;

    public CoursesFragment() {
        // Required empty public constructor
    }

    //for course time only
    private static String timeFormatter(long startTime) {
        String time = String.valueOf(startTime);
        String[] timeArray = time.split("");
        if (timeArray.length == 6) {
            return timeArray[0] + timeArray[1] + ":" + timeArray[2] + timeArray[3];
        } else {
            return timeArray[0] + timeArray[1] + timeArray[2] + ":" + timeArray[3] + timeArray[4];
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SQLiteDBHandler dbh = new SQLiteDBHandler(this.getActivity());

        // Inflate the layout for this fragment
        final FrameLayout fm = (FrameLayout) inflater.inflate(R.layout.fragment_courses, container, false);
        ScrollView sv = (ScrollView) fm.getChildAt(0);

        //Get linearlayout and layoutParams for new button
        LinearLayout ll = (LinearLayout) sv.getChildAt(0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //get courses for the current term
        List<Course> allCourses = new ArrayList<>();
        List<Course> currCourses = new ArrayList<>();

        try {
            allCourses = dbh.getAllCourses();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        currCourses = MainActivity.courseSelector(allCourses);

        //Add new button for each course in DB
        for (final Course course : currCourses ) {
            //formats button to be the same as the format we want in the fragment
            final Button button = createCourseButton(course);
            //Add this button to the layout
            ll.addView(button, lp);

            //add button listener for each course button
            button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    CoursePartialFragment fragment = new  CoursePartialFragment();
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Fragment currentFrag = fm.findFragmentById(R.id.fragment_container);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    fragment.setCourse(course);
                    fragment.setPrevFragment(LISTALLCOURSES);

                    //hide the current fragment
                    transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_left);
                    transaction.remove(currentFrag);
                    transaction.add(R.id.fragment_container, fragment, String.valueOf(button.getId()));

                    //reopen the list all courses fragment when back key is pressed
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });

            Log.d("Get End Date:  ", course.getEndDate());

        }

        return fm;
    }

    private Button createCourseButton(Course course) {
        //Set button properties - gravity, allCaps, padding, backgroundColor, textColor, text
        Button bt = new Button(this.getActivity());

        //button.setId(course.get);
        bt.setGravity(left);
        bt.setAllCaps(false);
        bt.setPadding(getResources().getDimensionPixelSize(button_margin), getResources().getDimensionPixelSize(button_margin), getResources().getDimensionPixelSize(button_margin), getResources().getDimensionPixelSize(button_margin));
        bt.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_press_colors));
        bt.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryTextColor));
        String startTime = timeFormatter(course.getStartTime());
        String endTime = timeFormatter(course.getEndTime());
        SpannableString buttonText = new SpannableString(course.getCategory() + " "
                                        + course.getNumber() + " "
                                        + course.getSection() + "\n"
                                        + course.getDayOfWeek() + "\n"
                                        + course.getBuilding() + " "
                                        + course.getRoom() + "\n"
                                        + (startTime + " - " + endTime) );

        int index = buttonText.toString().indexOf("\n");
        buttonText.setSpan(new RelativeSizeSpan(2), 0, index, SPAN_INCLUSIVE_INCLUSIVE);
        buttonText.setSpan(new RelativeSizeSpan((float) 1.5), index, buttonText.length(), SPAN_INCLUSIVE_INCLUSIVE);
        bt.setText(buttonText);
        return bt;
    }
}
