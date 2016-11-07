package org.cpen321.discovr.model;

import java.util.Date;

/**
 * Created by Simon Jinaphant on 06-Nov-2016.
 */

public class Course {
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

    public Course(String category, String number, String section,
                  String building, String room, long startTime, long endTime,
                  Date startDate, Date endDate, String dayOfWeek) {

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

}
