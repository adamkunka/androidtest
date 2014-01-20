package com.example.timetogo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import android.content.ContentProvider;
import android.content.Context;
import android.content.ContentProvider.PipeDataWriter;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class TasksProvider extends ContentProvider implements PipeDataWriter<Cursor> {

	 /**
     * Logging mechanism tag name
     */
    private static final String TAG = "TasksProvider";

    /**
     * The database that the provider uses as its underlying data store
     */
    private static final String DATABASE_NAME = "timetogo.db";

    /**
     * The database version
     */
    private static final int DATABASE_VERSION = 2;

    /**
    *
    * This class helps open, create, and upgrade the database file. Set to package visibility
    * for testing purposes.
    */
    static class DatabaseHelper extends SQLiteOpenHelper {


    	DatabaseHelper(Context context) {

    		// calls the super constructor, requesting the default cursor factory.
    		super(context, DATABASE_NAME, null, DATABASE_VERSION);
    	}

    	/**
    	 *
    	 * Creates the underlying database with table name and column names taken from the
    	 * NotePad class.
    	 */
    	@Override
    	public void onCreate(SQLiteDatabase db) {
    		db.execSQL("CREATE TABLE " + Tasks.TABLE_NAME + " ("
    				+ Tasks._ID + " INTEGER PRIMARY KEY,"
    				+ Tasks.COLUMN_NAME_TITLE + " TEXT,"
    				+ Tasks.COLUMN_NAME_DATE_START + " INTEGER,"
    				+ Tasks.COLUMN_NAME_DATE_FINISH + " INTEGER,"
    				+ Tasks.COLUMN_NAME_DATE_CREATED + " INTEGER,"
    				+ Tasks.COLUMN_NAME_DATE_MODIFIED + " INTEGER"
    				+ ");");
    	}

    	/**
    	 *
    	 * Demonstrates that the provider must consider what happens when the
    	 * underlying datastore is changed. In this sample, the database is upgraded the database
    	 * by destroying the existing data.
    	 * A real application should upgrade the database in place.
    	 */
    	@Override
    	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    		// Logs that the database is being upgraded
    		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
    				+ newVersion + ", which will destroy all old data");

    		// Kills the table and existing data
    		db.execSQL("DROP TABLE IF EXISTS " + Tasks.TABLE_NAME);

    		// Recreates the database with a new version
    		onCreate(db);
    	}
    }

    @Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	// Handle to a new DatabaseHelper.
    private DatabaseHelper mOpenHelper;

    @Override
	public boolean onCreate() {
		// Creates a new helper object. Note that the database itself isn't opened until
		// something tries to access it, and it's only created if it doesn't already exist.
		mOpenHelper = new DatabaseHelper(getContext());
		return false;
	}

    /**
     * Implementation of {@link android.content.ContentProvider.PipeDataWriter}
     * to perform the actual work of converting the data in one of cursors to a
     * stream of data for the client to read.
     */
    @Override
    public void writeDataToPipe(ParcelFileDescriptor output, Uri uri, String mimeType,
            Bundle opts, Cursor c) {
        // We currently only support conversion-to-text from a single note entry,
        // so no need for cursor data type checking here.
        FileOutputStream fout = new FileOutputStream(output.getFileDescriptor());
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new OutputStreamWriter(fout, "UTF-8"));
            pw.println(c.getString(READ_NOTE_TITLE_INDEX));
            pw.println("");
            pw.println(c.getString(READ_NOTE_NOTE_INDEX));
        } catch (UnsupportedEncodingException e) {
            Log.w(TAG, "Ooops", e);
        } finally {
            c.close();
            if (pw != null) {
                pw.flush();
            }
            try {
                fout.close();
            } catch (IOException e) {
            }
        }
    }

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
