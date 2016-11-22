package org.cpen321.discovr;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import org.cpen321.discovr.model.Building;

import java.util.HashMap;

import static android.content.SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES;

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
        for(int i = 0; i < selectionArgs.length; i++){
            selectionArgs[i] = "%" + selectionArgs[i] + "%";
        }
        for (String s: selectionArgs){
            s = "%" + s + "%";
        }
        Log.d("Querying: ",  selection + selectionArgs[0]);
        qBuilder.setTables("Buildings");
        qBuilder.setProjectionMap(PROJECTION_MAP);
        return qBuilder.query(dbh.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
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
