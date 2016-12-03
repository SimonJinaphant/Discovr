package org.cpen321.discovr.fragment.partial;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.cpen321.discovr.R;
import org.cpen321.discovr.model.transit.TransitEstimateSchedule;
import org.cpen321.discovr.parser.TransitParser;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Transit Schedule Fragment
 */
public class TransitPartialFragment extends Fragment {

    private ListView transitList;
    private String stationNumber;

    public TransitPartialFragment() {
        // Required empty public constructor
    }

    public void setStation(String stationNumber) {
        this.stationNumber = stationNumber;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View transitView = inflater.inflate(R.layout.fragment_transit, container, false);

        transitList = (ListView) transitView.findViewById(R.id.transit_list);
        ArrayAdapter<String> transitListAdapter = new ArrayAdapter<String>(transitView.getContext(),
                android.R.layout.simple_list_item_1, new ArrayList<String>()) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // A cheap hack to change the UI of the listview without having to create custom views
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                tv.setTextColor(Color.WHITE);

                return view;
            }
        };

        transitList.setAdapter(transitListAdapter);
        new TransitAsyncTask().execute();

        return transitView;
    }

    private class TransitAsyncTask extends AsyncTask<Void, String, String> {

        ArrayAdapter<String> adapter;

        @Override
        protected void onPreExecute() {
            adapter = (ArrayAdapter<String>) transitList.getAdapter();
        }

        @Override
        protected String doInBackground(Void... params) {

            Set<Map.Entry<String, List<TransitEstimateSchedule>>> result =
                    TransitParser.httpGetTransitSchedule(stationNumber).entrySet();

            if (result.size() == 0){
                publishProgress("This bus route is not in service today");
                return "No items gathered";
            }

            for (Map.Entry<String, List<TransitEstimateSchedule>> entry : result) {
                String busNo = entry.getKey();
                publishProgress(busNo);

                StringBuilder sb = new StringBuilder();
                for (TransitEstimateSchedule schedule : entry.getValue()) {
                    sb.append(MessageFormat.format("\t{0} {1}\n",
                            schedule.destination,
                            schedule.expectedCountdown <= 0 ? " departing now" :
                                    "in " + schedule.expectedCountdown + " minutes"
                    ));
                }
                publishProgress(sb.toString());

            }

            return "All items loaded";
        }

        @Override
        protected void onProgressUpdate(String... params) {
            adapter.add(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }

}
