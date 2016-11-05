package org.cpen321.discovr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    //Column names in table "user"
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_BUILDING = "buildingName";
    private static final String KEY_TIME = "time";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_DETAILS = "eventDetails";
    private static final String KEY_TAGS = "tags";

    public SQLiteDBHandler (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS" + TABLE_SUBSCRIBED_EVENTS + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_NAME + " TEXT," +
                KEY_BUILDING + " TEXT," +
                KEY_TIME + " TEXT," +
                KEY_LOCATION + " TEXT," +
                KEY_DETAILS + " TEXT," +
                KEY_TAGS + ");";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //Drop older tables if they existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBSCRIBED_EVENTS);
        //Recreate table
        onCreate(db);
    }

    public void addEvent(EventInfo data){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, data.getID());
        values.put(KEY_NAME, data.getName());
        values.put(KEY_BUILDING, data.getBuildingName());
        values.put(KEY_TIME, data.getTime());
        values.put(KEY_LOCATION, data.getLocation());
        values.put(KEY_DETAILS, data.getEventDetails());
        values.put(KEY_TAGS, data.getTags());

        //Insert new row
        db.insert(TABLE_SUBSCRIBED_EVENTS, null, values);
        //close database connection
        db.close();
    }

    //Get one specific event based on ID
    public EventInfo getEvent(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SUBSCRIBED_EVENTS, new String[]{KEY_ID, KEY_NAME, KEY_BUILDING, KEY_TIME, KEY_LOCATION, KEY_DETAILS, KEY_TAGS }, KEY_ID + " =?", new String[]{String.valueOf(id) + "%"}, null, null, null, null);
        if (cursor != null)
        cursor.moveToFirst();

        EventInfo event = new EventInfo(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
        return event;
    }

    //Get all events in user's database
    public List<EventInfo> getAllEvents() {
        List<EventInfo> eventList = new ArrayList<EventInfo>();

        String selectQuery = "SELECT * FROM " + TABLE_SUBSCRIBED_EVENTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //loop through each row and add that event to the list
        if (cursor.moveToFirst()) {
            do {
                EventInfo event = new EventInfo(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
                eventList.add(event);
            }
            while (cursor.moveToNext());
        }

        //return list of all events
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
        values.put(KEY_TIME, event.getTime());
        values.put(KEY_LOCATION, event.getLocation());
        values.put(KEY_DETAILS, event.getEventDetails());
        values.put(KEY_TAGS, event.getTags());

        //update that row
        return db.update(TABLE_SUBSCRIBED_EVENTS, values, KEY_ID + " = ?", new String[]{String.valueOf(event.getID())});
    }

    //Delete single Event
    public void deleteEvent(EventInfo event){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SUBSCRIBED_EVENTS, KEY_ID + " = ?", new String[]{String.valueOf(event.getID())});
        db.close();
    }

}

