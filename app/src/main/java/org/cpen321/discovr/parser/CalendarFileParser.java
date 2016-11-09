package org.cpen321.discovr.parser;

import android.os.Environment;
import android.util.Log;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;

import org.cpen321.discovr.model.Course;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * Created by Yueyue Zhang on 2016/11/2.
 * Reference: http://www.cnblogs.com/parryyang/p/5948436.html
 * <p>
 * 1. See output in logcat
 * 2. Finish up parsing info from ical files only, havent saved them as "Event" object
 * 3. Test one ical file only, more tests are necessary
 * 4. Replace  log.e("") to store info in the format of "Event"
 * 5. Modified the Manifest.xml for testing only, dont merge for now!
 */

public class CalendarFileParser {

    private CalendarFileParser() {
        // Prevent instantiation of this utility class
    }

    /**
     * Parses the user's time table from the ical file.
     * Requires: The ical file to be located in their phone's Download folder.
     * @return
     */
    public static List<Course> loadUserCourses() {

        File icalFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS) + "/ical.ics");

        if (!icalFile.exists()) {
            System.out.println("Unable to find a ical file, please download one off of UBC SSC.");
            return null;
        }
        System.out.println("ICal file found :D");

        List<Course> courses = new ArrayList<>();

        try {
            Calendar calendar = new CalendarBuilder().build(new FileInputStream(icalFile));

            Date endDate = null;
            String dayOfWeek = null;

            Iterator calIterator = calendar.getComponents(Component.VEVENT).iterator();

            while (calIterator.hasNext()) {
                VEvent calendarEvent = (VEvent) calIterator.next();

                String[] courseName = calendarEvent.getSummary().getValue().split(" ");
                String[] location = calendarEvent.getLocation().getValue().split(", ");
                String[] room = location[1].split("Room ");

                String raw = calendarEvent.getProperty(Property.RRULE).getValue();

                for (String s : raw.split(";")) {
                    if (s.startsWith("UNTIL=")) {
                        String[] parts = s.split("UNTIL=");
                        String[] parts2 = parts[1].split("T");
                        endDate = parseCalendarDate(parts2[0]);
                    }
                    if (s.startsWith("BYDAY=")) {
                        String[] day = s.split("BYDAY=");
                        dayOfWeek = day[1];
                    }
                }

                String[] start = calendarEvent.getStartDate().getDate().toString().split("T");
                String[] end = calendarEvent.getEndDate().getDate().toString().split("T");

                Date startDate = parseCalendarDate(start[0]);

                // Time are stored as long for now... Java 8 has LocalTime
                long startTime = Long.parseLong(start[1]);
                long endTime = Long.parseLong(end[1]);

                Course course = new Course(
                        courseName[0], courseName[1], courseName[2],
                        location[0], room[1], startTime, endTime,
                        startDate, endDate, dayOfWeek
                );

                courses.add(course);
            }

        } catch (Exception e) {
            Log.e("Calendar", "Unable to parse calendar");
        }

        return courses;
    }

    /**
     * Parse the given string into a Date object.
     * @param rawFormat - A string in the format of yyyyMMdd
     * @return The corresponding Date object to the input date at time 00:00:00
     */
    public static Date parseCalendarDate(String rawFormat) {
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyyMMdd");
            return parser.parse(rawFormat);

        } catch (Exception e) {
            Log.e("Calendar", "Unable to parse calendar date: " + rawFormat);
            return null;
        }
    }

}

