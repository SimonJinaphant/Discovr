package org.cpen321.discovr.model;

import java.util.Date;

/**
 * Created by Simon Jinaphant on 06-Nov-2016.
 */

public class Course {
    //public final int id;
    public String category;
    public String number;
    public String section;

    public String building;
    public String room;

    public long startTime;
    public long endTime;

    public Date startDate;
    public Date endDate;

    public String dayOfWeek;

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
        return building;
    }

    public String getRoom() {
        return room;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getStartDate() {
        return startDate.toString();
    }

    public String getEndDate() {
        return endDate.toString();
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}
