package org.cpen321.discovr;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.widget.Button;
import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;
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
        try {
            List<Course> flag = dbh.getAllCourses();
            if(flag.isEmpty()){
                //load from local ical files
                List<Course> rawCourses = loadUserCourses();
                //Deal with the course duplicates here
                List<Course> myCourses = removeDuplicates(rawCourses);
                dbh.addCourses(myCourses);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Inflate the layout for this fragment
        final FrameLayout fm = (FrameLayout) inflater.inflate(R.layout.fragment_courses, container, false);
        ScrollView sv = (ScrollView) fm.getChildAt(0);

        //Get linearlayour and layoutParams for new button
        LinearLayout ll = (LinearLayout) sv.getChildAt(0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        try {
            //get course object from local database
            List<Course> courseList = dbh.getAllCourses();

            //get current calendar object
            Calendar c = Calendar.getInstance();
            //get current day of week 0~6 -> SUN ~ SAT
            int dow = c.get(Calendar.DAY_OF_WEEK);
            String formattedDow = formatDow(dow);
            //get current hours & minutes
            int hr = c.get(Calendar.HOUR_OF_DAY);
            int min = c.get(Calendar.MINUTE);

            int currTime;
            if(min < 10) {
                //indicate curr time
                currTime = Integer.parseInt((Integer.toString(hr) + "0" + Integer.toString(min)));
            }else{
                currTime = Integer.parseInt((Integer.toString(hr)+Integer.toString(min)));
            }

            //Add new button for each course in DB
            for(Course course : courseList){
                //String courseTime = String.valueOf(course.getStartTime()).substring(0, 3);
                //implement a method to format course.getStartTime() to 4 digits
                int time = formatTime(course.getStartTime());

                if(course.getDayOfWeek().contains(formattedDow)){
                    if( (time - currTime <= 10) && (time - currTime) > 0){
                        //add notification here
                        new AlertDialog.Builder(this.getActivity())
                                .setTitle(course.getCategory()+" "+course.getNumber()+" "+course.getSection()+" will start in 10 mins")
                                .setMessage(course.getEndDate())
                                .show();
                    }
                }
                //formats button to be the same as the format we want in the fragment
                final Button button = createCourseButton(course);
                //Add this button to the layout
                ll.addView(button, lp);

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return fm;
    }

    private int formatTime(long startTime) {
        int result = 0;
        int hrs = Integer.parseInt(String.valueOf(startTime).substring(0,2));
        if(hrs > 22){
            result = Integer.parseInt(String.valueOf(startTime).substring(0,3));
            return result;
        }else if( 10 <= hrs &&  hrs <= 22){
            result = Integer.parseInt(String.valueOf(startTime).substring(0,4));
            return result;
        }
        return -1;
    }

    private String formatDow(int dow) {
        if(dow == 1 || dow == 7){
            return "Weekend";
        }else if(dow == 2){
            return "MO";
        }else if(dow == 3){
            return "TU";
        }else if(dow == 4){
            return "WE";
        }else if(dow == 5){
            return "TH";
        }else if(dow == 6){
            return  "FR";
        }
        return null;
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
        String startTime = timeFormatter(course.getStartTime());
        String endTime = timeFormatter(course.getEndTime());
        SpannableString buttonText = new SpannableString(course.getCategory()+ " " +course.getNumber()+" "+course.getSection() + "\n" + (startTime+ " - " + endTime) + "\n" + course.getBuilding()+ " "+course.getRoom());

        //should i add startDate and endDate as well?
        //when it reaches the endDate ,then the course button automatically disappear
        int index = buttonText.toString().indexOf("\n");
        buttonText.setSpan(new AbsoluteSizeSpan(50), 0, index, SPAN_INCLUSIVE_INCLUSIVE);
        buttonText.setSpan(new AbsoluteSizeSpan(30), index, buttonText.length(), SPAN_INCLUSIVE_INCLUSIVE);
        bt.setText(buttonText);
        return bt;
    }

    //for course time only
    //i got this worked on my phone... but i have no idea why- -
    private String timeFormatter(long startTime) {
        String time = String.valueOf(startTime);
        String[] timeArray = time.split("");
        if (timeArray.length == 6){
            return timeArray[0]+timeArray[1]+":"+timeArray[2]+timeArray[3];
        }else {
            return timeArray[0]+ timeArray[1]+timeArray[2]+":" +timeArray[3]+timeArray[4];
        }
    }

    //remove duplicates method
    private List<Course> removeDuplicates(List<Course> rawCourses) {

        List<Course> result = new ArrayList<Course>();
        //List<String> myStrings = new ArrayList<String>();

        for (int i = 0; i < rawCourses.size(); i++){
            String rTitle = rawCourses.get(i).getCategory()+rawCourses.get(i).getNumber()+rawCourses.get(i).getSection();
            label:
            if (result.isEmpty()) {
                result.add(rawCourses.get(i));
                //myStrings.add(rTitle);
            }else {
                for (int j = 0; j < result.size(); j++){
                    String nTitle = result.get(j).getCategory()+result.get(j).getNumber()+result.get(j).getSection();
                    if (rTitle.equals(nTitle)){
                        //String dow = result.get(j).getDayOfWeek();
                        result.get(j).setDayOfWeek(result.get(j).getDayOfWeek()+"/"+rawCourses.get(i).getDayOfWeek());
                        break label;
                    }
                }
                result.add(rawCourses.get(i));
            }
        }
        return result;
    }
}
