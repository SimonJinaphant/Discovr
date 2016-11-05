package org.cpen321.discovr;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.IndexedComponentList;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;

import java.io.InputStream;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Yueyue Zhang on 2016/11/2.
 *
 * 1. See output in logcat rather than phone or emulator
 * 2. Finish up parsing info from ical files only, havent saved them as "Event" object
 * 3. Test one ical file only, more tests are necessary
 * 4. Replace  log.e("") to store info in the format of "Event"
 * 5. Modified the Manifest.xml for testing only
 */

public class EventActivity extends Activity {
    TextView textview;
    String path;

    public void onCreate(Bundle save) {
        super.onCreate(save);
        setContentView(R.layout.activity_event);
        textview = (TextView) findViewById(R.id.textView);
        textview.setText("print out in log...");

        try {
            InputStream fin=getClassLoader().getResourceAsStream("assets/CourseSchedule.ics");
            CalendarBuilder build = new CalendarBuilder();
            Calendar calendar = build.build(fin);

            for (Iterator i = calendar.getComponents(Component.VEVENT).iterator(); i.hasNext();) {
                VEvent event = (VEvent) i.next();
                // time starts
                Log.e("error","Starts from:  " + event.getStartDate().getValue());
                // time ends
                Log.e("error","Ends at:  " + event.getEndDate().getValue());
                if (null != event.getProperty("DTSTART")) {
                    ParameterList parameters = event.getProperty("DTSTART").getParameters();
                    if (null != parameters.getParameter("VALUE")) {
                        Log.e("error",parameters.getParameter("VALUE").getValue());
                    }
                }
                // Title
                Log.e("error","Title:  " + event.getSummary().getValue());

                // Location
                if (null != event.getLocation()) {
                    Log.e("error","Location:  " + event.getLocation().getValue());
                }

                // Description
                if (null != event.getDescription()) {
                    Log.e("error","Description:  " + event.getDescription().getValue());
                }
                // Time created
                if (null != event.getCreated()) {
                    Log.e("error","Time Created:  " + event.getCreated().getValue());
                }
                // Last Modification
                if (null != event.getLastModified()) {
                    Log.e("error","Last Modification:  " + event.getLastModified().getValue());
                }
                // Repeat Frequency
                if (null != event.getProperty("RRULE")) {
                    Log.e("error","Repeat Frequency:  " + event.getProperty("RRULE").getValue());
                }
                // Attendee
                if (null != event.getProperty("ATTENDEE")) {
                    ParameterList parameters = event.getProperty("ATTENDEE").getParameters();
                    Log.e("error",event.getProperty("ATTENDEE").getValue().split(":")[1]);
                    Log.e("error",parameters.getParameter("PARTSTAT").getValue());
                }
                Log.e("error","----------------------------");
            }

            Log.e("error","done parsing here");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

