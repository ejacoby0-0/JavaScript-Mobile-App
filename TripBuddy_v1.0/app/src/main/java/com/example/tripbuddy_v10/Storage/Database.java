package com.example.tripbuddy_v10.Storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tripbuddy.db";
    private static final int DATABASE_VERSION = 1;

    // Table for Trips
    public static final String TABLE_TRIPS = "trips";
    public static final String COL_TRIP_ID = "id";
    public static final String COL_DESTINATION = "destination";
    public static final String COL_NOTES = "notes";
    public static final String COL_DATE = "date";
    public static final String COL_ACTIVITIES = "activities"; // store as comma-separated string
    public static final String COL_CUSTOM_EXPENSE = "custom_expense";
    public static final String COL_ADD_EXPENSE = "add_expense";
    public static final String COL_TOTAL_COST = "total_cost";
    public static final String COL_SUMMARY = "summary";

    // Table for Memories
    public static final String TABLE_MEMORIES = "memories";
    public static final String COL_MEMORY_ID = "id";
    public static final String COL_IMAGE_URI = "image_uri";
    public static final String COL_CAPTION = "caption";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TRIPS = "CREATE TABLE " + TABLE_TRIPS + " (" +
                COL_TRIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DESTINATION + " TEXT, " +
                COL_NOTES + " TEXT, " +
                COL_DATE + " TEXT, " +
                COL_ACTIVITIES + " TEXT, " +
                COL_CUSTOM_EXPENSE + " REAL, " +
                COL_ADD_EXPENSE + " REAL, " +
                COL_TOTAL_COST + " REAL, " +
                COL_SUMMARY + " TEXT)";
        db.execSQL(CREATE_TRIPS);

        String CREATE_MEMORIES = "CREATE TABLE " + TABLE_MEMORIES + " (" +
                COL_MEMORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_IMAGE_URI + " TEXT, " +
                COL_CAPTION + " TEXT)";
        db.execSQL(CREATE_MEMORIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMORIES);
        onCreate(db);
    }
}



