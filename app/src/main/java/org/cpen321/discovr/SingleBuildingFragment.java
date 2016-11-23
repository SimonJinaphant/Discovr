package org.cpen321.discovr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TabStopSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.cpen321.discovr.model.Building;


/**
 * Created by David Wong on 2016-11-08.
 */

public class SingleBuildingFragment extends Fragment {

    private Building building;
    private int PrevFragment;

    public void setBuilding(Building building) {
        this.building = building;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.fragment_single_building, container, false);

        TextView BuildingInfo = (TextView) ll.findViewById(R.id.buildinginfo);
        BuildingInfo.setText(building.name);

        TextView BuildingDetails = (TextView) ll.findViewById(R.id.buildingdetails);
        SpannableStringBuilder span = new SpannableStringBuilder(getCode(building) + getTime(building) + getAddress(building));
        span.setSpan(new TabStopSpan.Standard(300), 0, span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        BuildingDetails.setText(span, TextView.BufferType.SPANNABLE);

        return ll;

    }


    String getTime(Building building) {
        String time = "";
        if (building.hours != null) {
            String[] s = building.hours.split(",");
            time = "Hours:";
            for (String i : s)
                time = time + "\t" + i + "\n";
        }

        return time;
    }

    String getAddress(Building building) {
        String address = "";
        if (building.address != null) {
            address = "Address:\t" + building.address + "\n";
        }

        return address;
    }

    String getCode(Building building) {
        String code = "";
        if (building.code != null) {
            code = "Code:\t" + building.code + "\n";
        }

        return code;
    }


}
