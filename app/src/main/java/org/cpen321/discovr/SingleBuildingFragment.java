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
        SpannableString titleText = new SpannableString("Macleod");
        int index = titleText.toString().indexOf("\n");
        titleText.setSpan(new AbsoluteSizeSpan(100), 0, index, SPAN_INCLUSIVE_INCLUSIVE);
        titleText.setSpan(new AbsoluteSizeSpan(50), index, titleText.length(), SPAN_INCLUSIVE_INCLUSIVE);
        BuildingInfo.setText(titleText);

        TextView BuildingDetails = (TextView) build.findViewById(R.id.buildingdetails);
        BuildingDetails.setText(
                "Name: " + "Macleod" + "\n"
                        + "Code: " + "MCLD" + "\n"
                        + "Hours: " + "6:00am-6:00pm"
                        + "\n" + "Address: " + "UBC ...."
        );

        return build;

    }







}
