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
    private static final String DATABASE_NAME = "userData";
    //Table name
    private static final String TABLE_USERDATA = "user";
    //Column names in user
    private static final String KEY_ID = "id";
    private static final String KEY_INFO = "info";

    public SQLiteDBHandler (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS" + TABLE_USERDATA + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_INFO + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //Drop older tables if they existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERDATA);
        //Recreate table
        onCreate(db);
    }

    public void addEvent(EventInfo data){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, data.getID());
        values.put(KEY_INFO, data.getInfoString());

        //Insert new row
        db.insert(TABLE_USERDATA, null, values);
        //close database connection
        db.close();
    }

    //Get one specific event based on ID
    public EventInfo getEvent(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERDATA, new String[]{KEY_ID, KEY_INFO}, KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null)
        cursor.moveToFirst();

        EventInfo event = new EventInfo(cursor.getString(0), cursor.getString(1));
        return event;
    }

    //Get all events in user's database
    public List<EventInfo> getAllEvents() {
        List<EventInfo> eventList = new ArrayList<EventInfo>();

        String selectQuery = "SELECT * FROM " + TABLE_USERDATA;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //loop through each row and add that event to the list
        if (cursor.moveToFirst()) {
            do {
                EventInfo event = new EventInfo(cursor.getString(1), cursor.getString(2));
                eventList.add(event);
            }
            while (cursor.moveToNext());
        }

        //return list of all events
        return eventList;
    }

    //Get count of all events in user's database
    public int getEventCount(){
        String query = "SELECT * FROM " + TABLE_USERDATA;
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
        values.put(KEY_INFO, event.getInfoString());

        //update that row
        return db.update(TABLE_USERDATA, values, KEY_ID + " = ?", new String[]{String.valueOf(event.getID())});
    }

    //Delete single Event
    public void deleteEvent(EventInfo event){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERDATA, KEY_ID + " = ?", new String[]{String.valueOf(event.getID())});
        db.close();
    }

}

