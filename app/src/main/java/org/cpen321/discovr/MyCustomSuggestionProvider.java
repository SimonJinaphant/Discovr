package org.cpen321.discovr;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by jacqu on 11/20/2016.
 */

public class MyCustomSuggestionProvider extends ContentProvider {

    //Projection to change database column names into recognized column names for list of suggestions
    private static final HashMap<String, String> PROJECTION_MAP = new HashMap<String, String>();

    static {
        PROJECTION_MAP.put("_id", "BuildingID AS _id");
        PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_TEXT_1, "BuildingName AS " + SearchManager.SUGGEST_COLUMN_TEXT_1);
        PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_TEXT_2, "BuildingCode AS " + SearchManager.SUGGEST_COLUMN_TEXT_2);
        PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "BuildingID AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
    }

    SQLiteDBHandler dbh;

    @Override
    public boolean onCreate() {
        dbh = new SQLiteDBHandler(this.getContext());
        return true;
    }

    @Nullable
    @Override
    //Function is called when text changed for searchable
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
        String bldgCode = selectionArgs[0].replaceAll("[^A-Za-z]", "");
        String[] args = new String[2];
        args[0] = selectionArgs[0];
        args[1] = bldgCode;

        for(int i = 0; i < args.length; i++){
            args[i] = "%" + args[i] + "%";
            }
        sortOrder = "(CASE buildingCode WHEN '" + bldgCode.toUpperCase() + "' THEN 1 ELSE 100 END) ASC, buildingName ASC";

        qBuilder.setTables("Buildings");
        qBuilder.setProjectionMap(PROJECTION_MAP);
        return qBuilder.query(dbh.getReadableDatabase(), projection, selection, args, null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
