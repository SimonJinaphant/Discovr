package org.cpen321.discovr.model;

import java.util.Date;

/**
 * Created by Simon Jinaphant on 06-Nov-2016.
 */

public class Course {
    //public final int id;
    public final String category;
    public final String number;
    public final String section;

    public final String building;
    public final String room;

    public final long startTime;
    public final long endTime;

    public final Date startDate;
    public final Date endDate;

    public final String dayOfWeek;

    public Course( String category, String number, String section,
                  String building, String room, long startTime, long endTime,
                  Date startDate, Date endDate, String dayOfWeek) {

        //this.id = id;
        this.category = category;
        this.number = number;
        this.section = section;
        this.building = building;
        this.room = room;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dayOfWeek = dayOfWeek;
    }

    public String getCategory() {
        return category;
    }

    public String getNumber() {
        return number;
    }

    public String getSection() {
        return section;
    }

    public String getBuilding() {
        return category;
    }

    public String getRoom() {
        return number;
    }

    public String getStartTime() {
        return section;
    }

    public long getEndTime() {
        return endTime;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }
}
