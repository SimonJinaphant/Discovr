package org.cpen321.discovr.utility;

import org.cpen321.discovr.model.Course;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by zhangyueyue on 2016-11-19.
 */
public class AlertUtil {
    public static Course courseAlert(List<Course> courseList) throws ParseException {
        //declare formatters for courses and curr time
        SimpleDateFormat CourTimeFormatter = new SimpleDateFormat("HHmmss");
        SimpleDateFormat CurrTimeFormatter = new SimpleDateFormat("HHmmss");
        //get curr calendar
        Calendar c = Calendar.getInstance();
        //get curr time
        Date currDate = c.getTime();
        //format the curr time
        String currTime = CurrTimeFormatter.format(currDate);

        if(courseList == null){
            return null;
        }else {
            //go over the courseList to check if there is any course in 10 mins
            for (Course course : courseList) {
                Date courDate;
                //get course time
                if(new Long(course.getStartTime()).toString().length() == 5){
                    courDate = CourTimeFormatter.parse("0"+String.valueOf(course.getStartTime()));
                }else{
                    courDate = CourTimeFormatter.parse(String.valueOf(course.getStartTime()));
                }
                //get curr time
                Date curr = CurrTimeFormatter.parse(currTime);
                //get the diff between curr and course
                long diff = courDate.getTime() - curr.getTime();

                //evaluate the diff
                long day = diff / (24 * 60 * 60 * 1000);
                long hour = (diff / (60 * 60 * 1000) - day * 24);
                long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
                long second = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);

                //if diff <= 10 mins, return curr course
                if(day == 0 && hour == 0){
                    if (second > 0 && min < 10 && min >= 0) {
                        return course;
                    }
                }
            }
        }
        return null;
    }
}
