package com.therohitjagan.toolify.alltools.barcodetool.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "barcode_history.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_HISTORY = "history";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATA = "data";
    public static final String COLUMN_FORMAT = "format";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_HISTORY + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DATA + " TEXT,"
                + COLUMN_FORMAT + " TEXT,"
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    // Insert a new history record
    public void insertHistory(String data, String format) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATA, data);
        values.put(COLUMN_FORMAT, format);
        // Optionally, use current time if needed
        values.put(COLUMN_TIMESTAMP, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        db.insert(TABLE_HISTORY, null, values);
        db.close();
    }

    // Retrieve all history records
    public Cursor getAllHistory() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_HISTORY, null, null, null, null, null, COLUMN_TIMESTAMP + " DESC");
    }

    // Delete a history record using the data and timestamp as identifiers.
    public void deleteHistory(HistoryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HISTORY, COLUMN_DATA + "=? AND " + COLUMN_TIMESTAMP + "=?",
                new String[]{item.getData(), item.getTimestamp()});
        db.close();
    }
}
