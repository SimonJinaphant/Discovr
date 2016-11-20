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
        SimpleDateFormat CourTimeFormatter = new SimpleDateFormat("HHmmss");
        SimpleDateFormat CurrTimeFormatter = new SimpleDateFormat("HHmmss");
        Calendar c = Calendar.getInstance();
        Date currDate = c.getTime();
        String currTime = CurrTimeFormatter.format(currDate);

        if(courseList == null){
            return null;
        }else {

            for (Course course : courseList) {
                Date courDate;
                if(new Long(course.getStartTime()).toString().length() == 5){
                    courDate = CourTimeFormatter.parse("0"+String.valueOf(course.getStartTime()));
                }else{
                    courDate = CourTimeFormatter.parse(String.valueOf(course.getStartTime()));
                }
                Date curr = CurrTimeFormatter.parse(currTime);
                long diff = courDate.getTime() - curr.getTime();

                long day = diff / (24 * 60 * 60 * 1000);
                long hour = (diff / (60 * 60 * 1000) - day * 24);
                long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
                long second = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);

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
