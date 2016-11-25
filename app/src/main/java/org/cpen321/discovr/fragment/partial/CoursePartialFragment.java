package org.cpen321.discovr.fragment.partial;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mapbox.mapboxsdk.geometry.LatLng;

import org.cpen321.discovr.MainActivity;
import org.cpen321.discovr.R;
import org.cpen321.discovr.SQLiteDBHandler;
import org.cpen321.discovr.model.Building;
import org.cpen321.discovr.model.Course;
import org.cpen321.discovr.parser.GeojsonFileParser;

/**
 * Created by zhangyueyue on 2016-11-24.
 */
public class CoursePartialFragment extends Fragment {

    //declare properties
    final int LISTALLCOURSES = 1 ;

    private  Course course;

    private  int prevFragment;

    //constructor
    public CoursePartialFragment(){

    }

    //getters & setters
    public int getPrevFragment() {
        return prevFragment;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setPrevFragment(int prevFragment) {
        this.prevFragment = prevFragment;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //get db handler
        final SQLiteDBHandler dbh = new SQLiteDBHandler(this.getActivity());

        //Inflate fragment from xml file for specific course
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.fragment_single_course, container, false);

        //get location from the local db
        //Building bldg = dbh.getBuildingByCode(course.getBuilding());
        Building bldg = dbh.getBuildingByCode(course.getBuilding());

        if (bldg != null)
            Log.d("Got info:  ", bldg.address);

        LatLng loc;
        if (bldg != null){
            //move to the location on the map
            loc = GeojsonFileParser.getCoordinates(bldg.getAllCoordinates());
            ((MainActivity) this.getActivity()).moveMap(loc);
        }

        //set course title
        SpannableString courseTitle = new SpannableString(course.getCategory() + " "
                                        + course.getNumber() + " "
                                        + course.getSection() + "\n");
        courseTitle.setSpan(new RelativeSizeSpan(2f), 0, courseTitle.length(), 0);
        courseTitle.setSpan(new ForegroundColorSpan(Color.WHITE), 0, courseTitle.length(), 0);
        //get the textview
        TextView tv0 = (TextView)ll.getChildAt(0);
        tv0.setText(courseTitle);

        //set time
        String st = timeFormatter(course.getStartTime());
        String et = timeFormatter(course.getEndTime());
        SpannableString coursePeriod = new SpannableString(course.getDayOfWeek() + " "
                                        + st + "-" + et);
        coursePeriod.setSpan(new RelativeSizeSpan(1.2f), 0, coursePeriod.length(), 0);
        coursePeriod.setSpan(new ForegroundColorSpan(Color.WHITE), 0, coursePeriod.length(), 0);
        //get the textview
        TextView tv1 = (TextView)ll.getChildAt(1);
        tv1.setText(coursePeriod);

        //set location
        SpannableString courseLocation = new SpannableString(course.getBuilding() + " "
                                        + course.getRoom());
        courseLocation.setSpan(new RelativeSizeSpan(1.2f), 0, courseLocation.length(), 0);
        courseLocation.setSpan(new ForegroundColorSpan(Color.WHITE), 0, courseLocation.length(), 0);
        //get the textview
        TextView tv2 = (TextView)ll.getChildAt(2);
        tv2.setText(courseLocation);

        return ll;
    }
}