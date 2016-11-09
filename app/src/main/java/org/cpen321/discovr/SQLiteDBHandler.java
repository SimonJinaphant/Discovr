package org.cpen321.discovr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
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
    //Column names in table "SubscribedEvents"
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_HOST = "hostName";
    private static final String KEY_BUILDING = "buildingName";
    private static final String KEY_STARTTIME = "startTime";
    private static final String KEY_ENDTIME = "endTime";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_DETAILS = "eventDetails";

    private static final String TABLE_BUILDING_INFO = "BuildingInformation";
    private static final String KEY_BUILDING_NAME = "buildingName";
    private static final String KEY_CODE = "buildingCode";
    private static final String KEY_ADDRESS = "buildingAddress";
    private static final String KEY_HOURS = "buildingHours";
    private static final String KEY_COORDINATES = "buildingCoordinates";

    public SQLiteDBHandler (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Creates table when getReadableDatabase or getWritableDatabase is called and no DB exists
    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_SUBSCRIBED_EVENTS_TABLE = "CREATE TABLE IF NOT EXISTS" + TABLE_SUBSCRIBED_EVENTS + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_NAME + " TEXT," +
                KEY_HOST + " TEXT," +
                KEY_BUILDING + " TEXT," +
                KEY_STARTTIME + " TEXT," +
                KEY_ENDTIME + " TEXT," +
                KEY_LOCATION + " TEXT," +
                KEY_DETAILS + " TEXT" + ");";
        db.execSQL(CREATE_SUBSCRIBED_EVENTS_TABLE);

        String CREATE_BUILDING_TABLE = "CREATE TABLE IF NOT EXISTS" + TABLE_BUILDING_INFO + "(" +
                KEY_BUILDING_NAME + " TEXT," +
                KEY_CODE + " TEXT," +
                KEY_ADDRESS + " TEXT," +
                KEY_HOURS + " TEXT," +
                KEY_COORDINATES + " TEXT" + ");";
        db.execSQL(CREATE_BUILDING_TABLE);
    }

    //Creates new db if new version > old version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //Drop older tables if they existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBSCRIBED_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUILDING_INFO);
        //Recreate table
        onCreate(db);
    }

    //Adds new event to database
    public void addEvent(EventInfo data){
        SQLiteDatabase db = this.getWritableDatabase();

        //Place values in contentValues
        ContentValues values = new ContentValues();
        values.put(KEY_ID, data.getID());
        values.put(KEY_NAME, data.getName());
        values.put(KEY_HOST, data.getHostName());
        values.put(KEY_BUILDING, data.getBuildingName());
        values.put(KEY_STARTTIME, data.getStartTime());
        values.put(KEY_ENDTIME, data.getEndTime());
        values.put(KEY_LOCATION, data.getLocation());
        values.put(KEY_DETAILS, data.getEventDetails());

        //Insert new row
        db.insert(TABLE_SUBSCRIBED_EVENTS, null, values);

        //close database connection
        db.close();
    }

    public void addBuilding(BuildingInformation bi){
        SQLiteDatabase db = this.getWritableDatabase();

        //Place values in contentValues
        ContentValues values = new ContentValues();
        values.put(KEY_BUILDING_NAME, bi.getName());
        values.put(KEY_HOURS, bi.getHours());
        values.put(KEY_ADDRESS, bi.getAddress());
        values.put(KEY_CODE, bi.getCode());
        values.put(KEY_COORDINATES, bi.getCoordinates());

        //Insert new row
        db.insert(TABLE_BUILDING_INFO, null, values);

        //close database connection
        db.close();
    }

    //Get one specific builidng based on name
    public BuildingInformation getBuildingByCode(String code){
        SQLiteDatabase db = this.getReadableDatabase();
        BuildingInformation bi = null;
        //Select all rows from TABLE_SUBSCRIBED_EVENTS where key_ID = id
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BUILDING_INFO + " WHERE " + KEY_CODE + " = ? ", new String[]{"%"+code+"%"} );
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            bi = new BuildingInformation(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        }
        return bi;
    }

    public List<BuildingInformation> searchBuildings(String searchString){
        SQLiteDatabase db = this.getReadableDatabase();
        List<BuildingInformation> bi = new ArrayList<>();
        //Select all rows from TABLE_SUBSCRIBED_EVENTS where key_ID = id
        String query = "SELECT * FROM " + TABLE_BUILDING_INFO + " WHERE " + KEY_BUILDING_NAME
                + " LIKE ? OR " + KEY_CODE + " LIKE ?";

        Cursor cursor = db.rawQuery(query, new String[]{"%" + searchString + "%", "%" + searchString + "%"});
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            bi.add(new BuildingInformation(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
        }
        return bi;
    }

    //Get one specific event based on ID
    public EventInfo getEvent(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        EventInfo event = null;
        //Select all rows from TABLE_SUBSCRIBED_EVENTS where key_ID = id
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SUBSCRIBED_EVENTS + " WHERE " + KEY_ID + " = ? ", new String[]{String.valueOf(id)} );
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            event = new EventInfo(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7));
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
                EventInfo event = new EventInfo(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7));
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
                EventInfo event = new EventInfo(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7));
                eventList.add(event);
            }
            while (cursor.moveToNext());
        }

        //close cursor and return list of all events
        cursor.close();
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

    public int getBuildingCount(){
        String query = "SELECT * FROM " + TABLE_BUILDING_INFO;
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
        values.put(KEY_STARTTIME, event.getStartTime());
        values.put(KEY_ENDTIME, event.getEndTime());
        values.put(KEY_LOCATION, event.getLocation());
        values.put(KEY_DETAILS, event.getEventDetails());

        //update that row
        return db.update(TABLE_SUBSCRIBED_EVENTS, values, KEY_ID + " = ?", new String[]{String.valueOf(event.getID())});
    }

    //Delete single Event
    public void deleteEvent(int eventID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SUBSCRIBED_EVENTS, KEY_ID + " = ?", new String[]{String.valueOf(eventID)});
        db.close();
    }

}

