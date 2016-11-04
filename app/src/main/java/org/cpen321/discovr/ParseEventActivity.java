package org.cpen321.discovr;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

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
 * Created by Yueyue Zhang on 2016-11-04.
 */

public class ParseEventActivity {


    public class EventActivity extends AppCompatActivity {
        TextView textview;
        String path;
        /**
         * ATTENTION: This was auto-generated to implement the App Indexing API.
         * See https://g.co/AppIndexing/AndroidStudio for more information.
         */
        private GoogleApiClient client;

        public void onCreate(Bundle save) {
            super.onCreate(save);
            setContentView(R.layout.activity_parse_event);
            textview = (TextView) findViewById(R.id.textView);
            textview.setText("print parsed info in log..");

            try {
                InputStream fin = getClassLoader().getResourceAsStream("sampleCalFile/CourseSchedule.ics");
                CalendarBuilder build = new CalendarBuilder();
                Calendar calendar = build.build(fin);

                for (Iterator i = calendar.getComponents(Component.VEVENT).iterator(); i.hasNext(); ) {
                    VEvent event = (VEvent) i.next();
                    // stars
                    Log.e("error", "starts from" + event.getStartDate().getValue());
                    // ends at
                    Log.e("error", "ends at" + event.getEndDate().getValue());
                    if (null != event.getProperty("DTSTART")) {
                        ParameterList parameters = event.getProperty("DTSTART").getParameters();
                        if (null != parameters.getParameter("VALUE")) {
                            Log.e("error", parameters.getParameter("VALUE").getValue());
                        }
                    }
                    // title
                    Log.e("error", "title" + event.getSummary().getValue());
                    // 地点
                    if (null != event.getLocation()) {
                        Log.e("error", "location" + event.getLocation().getValue());
                    }
                    // info
                    if (null != event.getDescription()) {
                        Log.e("error", "description" + event.getDescription().getValue());
                    }
                    // created time
                    if (null != event.getCreated()) {
                        Log.e("error", "time created " + event.getCreated().getValue());
                    }
                    // last modified
                    if (null != event.getLastModified()) {
                        Log.e("error", "last modification" + event.getLastModified().getValue());
                    }
                    // rrule
                    if (null != event.getProperty("RRULE")) {
                        Log.e("error", "RRULE:" + event.getProperty("RRULE").getValue());
                    }
                    // holder
                    if (null != event.getProperty("ATTENDEE")) {
                        ParameterList parameters = event.getProperty("ATTENDEE").getParameters();
                        Log.e("error", event.getProperty("ATTENDEE").getValue().split(":")[1]);
                        Log.e("error", parameters.getParameter("PARTSTAT").getValue());
                    }
                    Log.e("error", "----------------------------");
                }

                Log.e("error", "sunquan" + "3");
            } catch (Exception e) {
                e.printStackTrace();
            }
            // ATTENTION: This was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        }

        /**
         * ATTENTION: This was auto-generated to implement the App Indexing API.
         * See https://g.co/AppIndexing/AndroidStudio for more information.
         */
        public Action getIndexApiAction() {
            Thing object = new Thing.Builder()
                    .setName("Event Page") // TODO: Define a title for the content shown.
                    // TODO: Make sure this auto-generated URL is correct.
                    .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                    .build();
            return new Action.Builder(Action.TYPE_VIEW)
                    .setObject(object)
                    .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                    .build();
        }

        @Override
        public void onStart() {
            super.onStart();

            // ATTENTION: This was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            client.connect();
            AppIndex.AppIndexApi.start(client, getIndexApiAction());
        }

        @Override
        public void onStop() {
            super.onStop();

            // ATTENTION: This was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            AppIndex.AppIndexApi.end(client, getIndexApiAction());
            client.disconnect();
        }
    }


}
