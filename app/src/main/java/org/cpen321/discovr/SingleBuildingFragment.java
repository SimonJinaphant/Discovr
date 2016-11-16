package org.cpen321.discovr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.cpen321.discovr.model.Building;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;


/**
 * Created by David Wong on 2016-11-08.
 */

public class SingleBuildingFragment extends Fragment {

    private Building building;
    private int PrevFragment;

    public void setBuilding(Building building){
        this.building = building;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout build = (LinearLayout) inflater.inflate(R.layout.fragment_single_building, container, false);

        TextView BuildingInfo = (TextView) build.findViewById(R.id.buildinginfo);
        BuildingInfo.setText("Building Details");

        TextView BuildingDetails = (TextView) build.findViewById(R.id.buildingdetails);
        BuildingDetails.setText(
                "Name: " + building.name + "\n"
                        + getCode(building)+ "\n"
                        + getTime(building) + "\n"
                        + "Address: " + getAddress(building)
        );

        return build;

    }


    String getTime(Building building){
        String time = "";
        if(building.hours != null){

            time = "Hours:" + building.hours;
        }

        return time;
    }

    String getAddress(Building building){
        String address = "";
        if(building.address != null){

            address = "Address:" + building.address;
        }

        return address;
    }

    String getCode(Building building){
        String code = "";
        if(building.code != null){

            code = "Code:" + building.code;
        }

        return code;
    }





}
