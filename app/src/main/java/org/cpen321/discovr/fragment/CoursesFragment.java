package org.cpen321.discovr.fragment;

import android.app.AlertDialog;
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

import org.cpen321.discovr.R;
import org.cpen321.discovr.SQLiteDBHandler;
import org.cpen321.discovr.fragment.partial.CoursePartialFragment;
import org.cpen321.discovr.model.Course;
import org.cpen321.discovr.utility.AlertUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;
import static org.cpen321.discovr.R.dimen.button_margin;
import static org.cpen321.discovr.R.id.left;
import static org.cpen321.discovr.parser.CalendarFileParser.loadUserCourses;

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

        //Get DBHandler for this activity
        SQLiteDBHandler dbh = new SQLiteDBHandler(this.getActivity());

        try {
            //check if the ical file is already loaded to the app
            List<Course> flag = dbh.getAllCourses();
            //if nothing in it, then load ical file from the download folder
            if (flag.isEmpty()) {
                //load from local ical files
                List<Course> rawCourses = loadUserCourses();
                //Deal with the course duplicates here
                List<Course> myCourses = removeDuplicates(rawCourses);
                //add courses to the local database
                dbh.addCourses(myCourses);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Inflate the layout for this fragment
        final FrameLayout fm = (FrameLayout) inflater.inflate(R.layout.fragment_courses, container, false);
        ScrollView sv = (ScrollView) fm.getChildAt(0);

        //Get linearlayout and layoutParams for new button
        LinearLayout ll = (LinearLayout) sv.getChildAt(0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        List<Course> courseList = null;
        List<Course> courseTerm1 = new ArrayList<>();
        List<Course> courseTerm2 = new ArrayList<>();
        List<Course> courseTerm3 = new ArrayList<>();
        List<Course> courseSelected = new ArrayList<>();


        try {
            //get course object from local database
            courseList = dbh.getAllCourses();

            for(final Course course : courseList){
                if(course.getEndDate().contains("Nov") || course.getEndDate().contains("Dec")) {
                    courseTerm1.add(course);
                }else if(course.getEndDate().contains("Apr") || course.getEndDate().contains("May")){
                    courseTerm2.add(course);
                }else{
                    courseTerm3.add(course);
                }
            }

            courseSelected = courseSelector(courseTerm1, courseTerm2, courseTerm3);

            //Add new button for each course in DB
            for (final Course course : courseSelected) {
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
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            //check if there is any class in 10 mins
            if (AlertUtil.courseAlert(courseSelected) != null) {
                //get the course if there is any
                Course currCourse = AlertUtil.courseAlert(courseList);
                //show the alert message
                new AlertDialog.Builder(this.getActivity())
                        //set alert title with the course name
                        .setTitle(currCourse.getCategory() + " " + currCourse.getNumber() + " " + currCourse.getSection() + " will start in 10 mins")
                        //set the message with the location
                        .setMessage(currCourse.getBuilding() + " " + currCourse.getRoom())
                        //show the alert
                        .show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return fm;
    }

    private List<Course> courseSelector(List<Course> courseTerm1, List<Course> courseTerm2, List<Course> courseTerm3) {
        List<Course> result = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        Log.d("Get Current Month:  ", String.valueOf(month));

        if ( 0 <= month && month <= 4){
            result = courseTerm2;
        }else if( 8 <= month || month <= 11 ){
            result = courseTerm1;
        }else{
            result = courseTerm3;
        }
        return result;
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
                                        + course.getBuilding() + " "
                                        + course.getRoom() + "\n"
                                        + (startTime + " - " + endTime) );

        int index = buttonText.toString().indexOf("\n");
        buttonText.setSpan(new RelativeSizeSpan(2), 0, index, SPAN_INCLUSIVE_INCLUSIVE);
        buttonText.setSpan(new RelativeSizeSpan((float) 1.5), index, buttonText.length(), SPAN_INCLUSIVE_INCLUSIVE);
        bt.setText(buttonText);
        return bt;
    }

    //remove duplicates method
    private List<Course> removeDuplicates(List<Course> rawCourses) {

        List<Course> result = new ArrayList<Course>();
        //List<String> myStrings = new ArrayList<String>();

        for (int i = 0; i < rawCourses.size(); i++) {
            String rTitle = rawCourses.get(i).getCategory() + rawCourses.get(i).getNumber() + rawCourses.get(i).getSection();
            label:
            if (result.isEmpty()) {
                result.add(rawCourses.get(i));
                //myStrings.add(rTitle);
            } else {
                for (int j = 0; j < result.size(); j++) {
                    String nTitle = result.get(j).getCategory() + result.get(j).getNumber() + result.get(j).getSection();
                    if (rTitle.equals(nTitle)) {
                        //String dow = scheduleResult.get(j).getDayOfWeek();
                        result.get(j).setDayOfWeek(result.get(j).getDayOfWeek() + "/" + rawCourses.get(i).getDayOfWeek());
                        break label;
                    }
                }
                result.add(rawCourses.get(i));
            }
        }
        return result;
    }
}
