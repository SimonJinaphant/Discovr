package org.cpen321.discovr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.cpen321.discovr.model.Building;
import org.cpen321.discovr.model.Course;
import org.cpen321.discovr.model.EventInfo;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by jacqueline on 10/28/2016.
 */

public class SQLiteDBHandler extends SQLiteOpenHelper{
    //Database version
    private static final int DATABASE_VERSION = 1;
    //Database name
    private static final String DATABASE_NAME = "LocalEvents";
    //Table name
    private static final String TABLE_SUBSCRIBED_EVENTS = "SubscribedEvents";
    private static final String TABLE_SUBSCRIBED_COURSES = "SubscribedCourses";
    private static final String TABLE_BUILDINGS = "Buildings";

    //Column names in table "SubscribedEvents"
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_HOST = "hostName";
    private static final String KEY_BUILDING = "buildingName";
    private static final String KEY_STARTTIME = "startTime";
    private static final String KEY_ENDTIME = "endTime";
    private static final String KEY_DETAILS = "eventDetails";

    //Added column names in table "SubscribedCourses"
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_NUMBER = "number";
    private static final String KEY_SECTION = "section";
    private static final String KEY_ROOM = "room";
    private static final String KEY_START_DATE= "startDate";
    private static final String KEY_END_DATE = "endDate";
    private static final String KEY_DAY_OF_WEEK = "dayOfWeek";

    //Column names int table of building
    private static final String KEY_BLDG_NAME = "BuildingName";
    private static final String KEY_BLDG_CODE = "BuildingCode";
    private static final String KEY_BLDG_ADDRESS = "BuildingAddress";
    private static final String KEY_BLDG_HOURS = "BuildingHours";
    private static final String KEY_BLDG_COORDINATES = "BuildingCoordinates";


    public static final String CREATE_TABLE_SUBBED_EVENTS = "CREATE TABLE " + TABLE_SUBSCRIBED_EVENTS + "(" +
            KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_NAME + " TEXT," +
            KEY_HOST + " TEXT," +
            KEY_BUILDING + " TEXT," +
            KEY_STARTTIME + " TEXT," +
            KEY_ENDTIME + " TEXT," +
            KEY_DETAILS + " TEXT" + ")";

    public static final String CREATE_TABLE_COURSES = "CREATE TABLE " + TABLE_SUBSCRIBED_COURSES + "(" +
            KEY_CATEGORY + " TEXT," +
            KEY_NUMBER + " TEXT," +
            KEY_SECTION + " TEXT," +
            KEY_BUILDING + " TEXT," +
            KEY_ROOM + " TEXT," +
            KEY_STARTTIME + " TEXT," +
            KEY_ENDTIME + " TEXT," +
            KEY_START_DATE + " TEXT," +
            KEY_END_DATE + " TEXT," +
            KEY_DAY_OF_WEEK + " TEXT" + ")";

    public static final String CREATE_TABLE_BUILDINGS = "CREATE TABLE " + TABLE_BUILDINGS + "(" +
            KEY_BLDG_NAME+ " TEXT," +
            KEY_BLDG_CODE + " TEXT," +
            KEY_BLDG_ADDRESS + " TEXT," +
            KEY_BLDG_HOURS + " TEXT," +
            KEY_BLDG_COORDINATES + " TEXT" + ")";


    public SQLiteDBHandler (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Creates table when getReadableDatabase or getWritableDatabase is called and no DB exists
    @Override

    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE_SUBBED_EVENTS);
        db.execSQL(CREATE_TABLE_COURSES);
        db.execSQL(CREATE_TABLE_BUILDINGS);

    }

    //Creates new db if new version > old version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //Drop older tables if they existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBSCRIBED_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBSCRIBED_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUILDINGS);
        //Recreate table
        onCreate(db);
    }

    /*
    The following implements these events for the EventsTable:
    addEvent(EventInfo);
    getEvent(int id);
    getEventBySearch(String query);
    getAllEvents();
    getEventCounts();
    UpdateEvent(EventInfo);
    deleteEvent(int id);
     */

    //Adds new event to database
    public void addEvent(EventInfo data){
        SQLiteDatabase db = this.getWritableDatabase();

        //Place values in contentValues
        ContentValues values = new ContentValues();
        values.put(KEY_ID, data.getID());
        values.put(KEY_NAME, data.getName());
        values.put(KEY_HOST, data.getHostName());
        values.put(KEY_BUILDING, data.getBuildingName());
        values.put(KEY_STARTTIME, data.getStartTimeString());
        values.put(KEY_ENDTIME, data.getEndTimeString());
        values.put(KEY_DETAILS, data.getEventDetails());
        //Insert new row
        db.insert(TABLE_SUBSCRIBED_EVENTS, null, values);

        //close database connection
        db.close();
    }

    //Get one specific event based on ID
    public EventInfo getEvent(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        EventInfo event = null;
        //Select all rows from TABLE_SUBSCRIBED_EVENTS where key_ID = id
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SUBSCRIBED_EVENTS + " WHERE " + KEY_ID + " = ? ", new String[]{String.valueOf(id)} );
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            event = new EventInfo(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
        }
        return event;
    }

    public List<EventInfo> getEventbySearch(String searchString){
        List<EventInfo> eventList = new ArrayList<EventInfo>();
        SQLiteDatabase db = this.getReadableDatabase();

        //Select all rows from TABLE_SUBSCRIBED_EVENTS where KEY_NAME contains searchString or KEY_BUILDING contains searchString or KEY_HOST contains searchString
        String query = "SELECT * FROM " + TABLE_SUBSCRIBED_EVENTS + " WHERE " + KEY_NAME + " LIKE ? OR " + KEY_BUILDING + " LIKE ? OR " + KEY_HOST + " LIKE ?";

        Cursor cursor = db.rawQuery(query, new String[]{"%" + searchString + "%", "%" + searchString + "%", "%" + searchString + "%"});

        //Add all those elements to a list
        if (cursor.moveToFirst()) {
            do {
                Log.d("found events", cursor.getString(1));
                EventInfo event = new EventInfo(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                eventList.add(event);
            }
            while (cursor.moveToNext());

        }

        //close cursor and return list of all events
        cursor.close();
        return eventList;
    }

    //Get all events in user's database
    public List<EventInfo> getAllEvents() {
        List<EventInfo> eventList = new ArrayList<EventInfo>();

        //Select all rows from TABLE_SUBSCRIBED_EVENTS
        String selectQuery = "SELECT * FROM " + TABLE_SUBSCRIBED_EVENTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //loop through each row and add that event to the list
        if (cursor.moveToFirst()) {
            do {
                Log.d("eventDate", "StartTime: " + cursor.getString(4) + " |EndTime: " + cursor.getString(5));
                EventInfo event = new EventInfo(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                Log.d("eventDate", "From database: " + cursor.getString(0) + " " + cursor.getString(1) + " " + cursor.getString(2) + " " + cursor.getString(3) + " " + cursor.getString(4) + " " + cursor.getString(5) + " " + cursor.getString(6));
                eventList.add(event);
            }
            while (cursor.moveToNext());
        }

        //close cursor and return list of all events
        cursor.close();
        db.close();
        return eventList;
    }

    //Get count of all events in user's database
    public int getEventCount(){
        String query = "SELECT * FROM " + TABLE_SUBSCRIBED_EVENTS;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        cursor.close();

        return cursor.getCount();
    }

    //Updates single Event
    public int updateEvent(EventInfo event){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_ID, event.getID());
        values.put(KEY_NAME, event.getName());
        values.put(KEY_BUILDING, event.getBuildingName());
        values.put(KEY_HOST, event.getHostName());
        values.put(KEY_STARTTIME, event.getStartTime().toString());
        values.put(KEY_ENDTIME, event.getEndTime().toString());
        values.put(KEY_DETAILS, event.getEventDetails());

        //update that rowserCou
        return db.update(TABLE_SUBSCRIBED_EVENTS, values, KEY_ID + " = ?", new String[]{String.valueOf(event.getID())});
    }

    //Delete single Event
    public void deleteEvent(int eventID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SUBSCRIBED_EVENTS, KEY_ID + " = ?", new String[]{String.valueOf(eventID)});
        db.close();
    }

    /*
    The following creates these methods for the SUBSCRIBED_COURSES_TABLE
    addCourses(List<Course> courses>);
    getAllCourses();

     */

    //Add new courses to the local database
    public void addCourses(List<Course> courses){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("sql", db.toString());
        for(int i = 0; i < courses.size();i++) {
            Course myCourse = courses.get(i);

            //Place values in contentValues
            ContentValues values = new ContentValues();

            values.put(KEY_CATEGORY, myCourse.getCategory());
            values.put(KEY_NUMBER, myCourse.getNumber());
            values.put(KEY_SECTION, myCourse.getSection());
            values.put(KEY_BUILDING, myCourse.getBuilding());
            values.put(KEY_ROOM, myCourse.getRoom());
            values.put(KEY_STARTTIME, myCourse.getStartTime());
            values.put(KEY_ENDTIME, myCourse.getEndTime());
            values.put(KEY_START_DATE, myCourse.getStartDate());
            values.put(KEY_END_DATE, myCourse.getEndDate());
            values.put(KEY_DAY_OF_WEEK , myCourse.getDayOfWeek());


            //Insert new row
            if (db.insert(TABLE_SUBSCRIBED_COURSES, null, values) == -1){
                Log.d("sql", "Failed to insert the entry");
            }
        }
        //close database connection
        db.close();
    }

    //get all courses
    public List<Course> getAllCourses() throws ParseException {
        List<Course> myCourses = new ArrayList<>();

        //Select all rows from TABLE_SUBSCRIBED_COURSES
        String selectQuery = "SELECT * FROM " + TABLE_SUBSCRIBED_COURSES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //loop through each row and add that course to the list
        if (cursor.moveToFirst()) {
            do {
                String start = cursor.getString(7);
                String end = cursor.getString(8);
                SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:SS zzz yyy");
                Date startDate = formatter.parse(start);
                Date endDate = formatter.parse(end);

                Course course = new Course(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getLong(5), cursor.getLong(6), startDate, endDate, cursor.getString(9));
                myCourses.add(course);
            }
            while (cursor.moveToNext());
        }

        //close cursor and return list of all courses
        cursor.close();
        return  myCourses;
    }


    /*
    The following allows these methods for the TABLE_BUILDiNGS
    addBuilding(Building);
    getBuildiing(String name);
    getBuildingCount();
     */
    //Adds single building to database
    public void addBuilding(Building bldg){
        SQLiteDatabase db = this.getWritableDatabase();

        //Place values in contentValues
        ContentValues values = new ContentValues();
        values.put(KEY_BLDG_NAME, bldg.name);
        values.put(KEY_BLDG_CODE, bldg.code);
        values.put(KEY_BLDG_ADDRESS, bldg.address);
        values.put(KEY_BLDG_COORDINATES, bldg.getCoordinatesAsString());
        values.put(KEY_BLDG_HOURS, bldg.hours);

        //Insert new row
        db.insert(TABLE_BUILDINGS, null, values);
        //close database connection
        db.close();
    }

    //Get one specific event based on ID
    public Building getBuildingByCode(String code){
        SQLiteDatabase db = this.getReadableDatabase();
        code = "%" + code.replaceAll("[^A-Za-z]", "") + "%";
        Log.d("Searching for: ", code);
        Building bldg = null;

        //Select all rows from TABLE_BUILDING where KEY_BLDG_CODE is code
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BUILDINGS + " WHERE " + KEY_BLDG_CODE + " LIKE ? OR " + KEY_BLDG_NAME + " LIKE ?", new String[]{code, code} );;
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            bldg = new Building(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        }
        return bldg;
    }

    public int getBuildingCount(){
        String query = "SELECT * FROM " + TABLE_BUILDINGS;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        cursor.close();

        return cursor.getCount();
    }
    public List<Building> getAllBuildings() {
        List<Building> bldgs = new ArrayList<Building>();

        //Select all rows from TABLE_SUBSCRIBED_EVENTS
        String selectQuery = "SELECT * FROM " + TABLE_BUILDINGS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //loop through each row and add that event to the list
        if (cursor.moveToFirst()) {
            do {
                Building bldg = new Building(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                bldgs.add(bldg);
            }
            while (cursor.moveToNext());
        }

        //close cursor and return list of all events
        cursor.close();
        db.close();
        return bldgs;
    }
}

