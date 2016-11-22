package org.cpen321.discovr;

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
import android.widget.Toast;

import org.cpen321.discovr.model.transit.TransitEstimateSchedule;
import org.cpen321.discovr.parser.TransitScheduleParser;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Transit Schedule Fragment
 */
public class TransitFragment extends Fragment {

    ListView transitList;
    ArrayAdapter<String> transitListAdapter;

    public TransitFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View transitView = inflater.inflate(R.layout.fragment_transit, container, false);

        transitList = (ListView)transitView.findViewById(R.id.transit_list);
        transitListAdapter = new ArrayAdapter<String>(transitView.getContext(),
                android.R.layout.simple_list_item_1, new ArrayList<String>()){

            @Override
            public View getView(int position, View convertView, ViewGroup parent){
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
        protected void onPreExecute(){
            adapter = (ArrayAdapter<String>) transitList.getAdapter();
        }

        @Override
        protected String doInBackground(Void... params) {
            for(Map.Entry<String, List<TransitEstimateSchedule>> entry :
                    TransitScheduleParser.httpGetTransitSchedule("51479").entrySet()){
                String busNo = entry.getKey();
                publishProgress(busNo);

                StringBuilder sb = new StringBuilder();
                for(TransitEstimateSchedule schedule : entry.getValue()){
                    sb.append(MessageFormat.format("\t{0} {1}\n",
                            schedule.destination,
                            schedule.expectedCountdown <= 0 ? " departing now" :
                                    "in "+schedule.expectedCountdown + " minutes"
                    ));
                }
                publishProgress(sb.toString());

            }

            return "All items loaded";
        }

        @Override
        protected void onProgressUpdate(String... params){
            adapter.add(params[0]);
        }

        @Override
        protected void onPostExecute(String result){
            Toast.makeText(getContext().getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }
    }

}
