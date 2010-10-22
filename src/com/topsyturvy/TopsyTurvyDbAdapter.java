/*
 * Copyright (C) 2010 Topsy-Turvy
 *
 * Authors:		Nitin Dhar (nitindhar7@yahoo.com)
 * 				Mayank Jain (mjain01@students.poly.edu)
 * 				Chintan Jain (cjain01@students.poly.edu)
 * 
 * Date: 		10/20/2010
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"), to deal in 
 * the Software without restriction, including without limitation the rights to 
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of 
 * the Software, and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS 
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.topsyturvy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TopsyTurvyDbAdapter {
	
	// Game table fields
	public static final String KEY_GROWID			= "_id";
	public static final String KEY_SOUND			= "sound";
	public static final String KEY_VIBRATION		= "vibration";
	public static final String KEY_PAUSE			= "pause";
	public static final String KEY_GUSERID			= "user_id";
	
	// User table fields
	public static final String KEY_UROWID			= "_id";
	public static final String KEY_NAME				= "name";
	
	// Single Player table fields
	public static final String KEY_SROWID			= "_id";
    public static final String KEY_TOPSCORE			= "top_score";
    public static final String KEY_SUSERID			= "user_id";
    
    // Multiplayer table fields
    public static final String KEY_MROWID			= "_id";
    public static final String KEY_TOTALSCORE		= "total_score";
    public static final String KEY_GAMESPLAYED		= "games_played";
    public static final String KEY_MUSERID			= "user_id";

    // DB specific
    private static final String DATABASE_NAME		= "topsy_turvy";
    private static final int DATABASE_VERSION		= 2;
    
    // Table names
    private static final String DATABASE_GTABLE		= "game";
    private static final String DATABASE_UTABLE		= "user";
    private static final String DATABASE_STABLE		= "single_player";
    private static final String DATABASE_MTABLE		= "multi_player";

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private final Context ctx;

    // Game table creation
    private static final String DATABASE_GCREATE	= "CREATE TABLE " + DATABASE_GTABLE + " (" +
												  	  KEY_GROWID	  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
												  	  KEY_SOUND		  + " INTEGER DEFAULT 1 NOT NULL, " +
												  	  KEY_VIBRATION	  + " INTEGER DEFAULT 1 NOT NULL, " +
												  	  KEY_PAUSE	  	  + " INTEGER NOT NULL, " +
												  	  KEY_GUSERID	  + " INTEGER NOT NULL);";
    // User table creation
    private static final String DATABASE_UCREATE	= "CREATE TABLE " + DATABASE_UTABLE + " (" +
    												  KEY_UROWID	  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    												  KEY_NAME		  + " TEXT NULL);";
    // Single Player table creation
    private static final String DATABASE_SCREATE	= "CREATE TABLE " + DATABASE_STABLE + " (" +
												      KEY_SROWID	  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
												      KEY_TOPSCORE	  + " INTEGER DEFAULT 1 NOT NULL, " +
												      KEY_SUSERID	  + " INTEGER NOT NULL);";
    // Multi Player table creation
    private static final String DATABASE_MCREATE	= "CREATE TABLE " + DATABASE_MTABLE + " (" +
												      KEY_MROWID	  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
												      KEY_TOTALSCORE  + " INTEGER DEFAULT 0 NOT NULL, " +
												      KEY_GAMESPLAYED + " INTEGER DEFAULT 0 NOT NULL, " +
												      KEY_MUSERID	  + " INTEGER NOT NULL);";
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_GCREATE);
            db.execSQL(DATABASE_UCREATE);
            db.execSQL(DATABASE_SCREATE);
            db.execSQL(DATABASE_MCREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_GTABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_UTABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_STABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_MTABLE);
            onCreate(db);
        }
	}
	
	/**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public TopsyTurvyDbAdapter(Context ctx) {
        this.ctx = ctx;
    }
    
    /**
     * Open database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public TopsyTurvyDbAdapter open() throws SQLException {
        dbHelper = new DatabaseHelper(ctx);
        db = dbHelper.getWritableDatabase();
        return this;
    }
    /*
     * Close database
     */
    public void close() {
        dbHelper.close();
    }
    
    /**
     * Create a new game settings. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param table		Table to insert to
     * @param sound		Sound setting: 0 = off, 1 = on
     * @param vibration Vibration setting: 0 = off, 1 = on
     * @param pause		Pause setting: 0 = unpaused, 1 = paused
     * @param user_id	User id foreign key (Currently active profile)
     * @param name		Name for new user
     * @return			rowId or -1 if failed
     */
    public long create(String table, int sound, int vibration, int pause, int user_id, String name) {
        ContentValues initialValues = new ContentValues();
        
        if (table.equals(DATABASE_GTABLE)) {
        	initialValues.put(KEY_SOUND, sound);
            initialValues.put(KEY_VIBRATION, vibration);
            initialValues.put(KEY_PAUSE, pause);
            initialValues.put(KEY_GUSERID, user_id);
            return db.insert(table, null, initialValues);
        }
        else if (table.equals(DATABASE_UTABLE)) {
        	initialValues.put(KEY_NAME, name);
        	return db.insert(table, null, initialValues);
        	// TODO: adding to user table also adds to singleplayer or multiplayer menu's
        }
        else {
        	return -1;
        }
    }
    
    
    /**
     * TODO: refactor to have just one Find method which returns 1 or all records based on parameter
     * Return a Cursor over one object
     * 
     * @param table	Name of table to retrieve record from
     * @param rowId Id for record row
     * @return		Cursor over one object or null
     */
    public Cursor find(String table, int rowId) {
    	Cursor mCursor;
    	
    	if (table.equals("game")) {
    		mCursor = db.query(true, DATABASE_GTABLE, new String[] {KEY_GROWID, KEY_SOUND, KEY_VIBRATION, KEY_PAUSE, KEY_GUSERID}, KEY_GROWID + "=" + rowId, null, null, null, null, null);
    		mCursor.moveToFirst();
    		return mCursor;
    	}
    	else if (table.equals("user")) {
    		mCursor = db.query(true, DATABASE_UTABLE, new String[] {KEY_UROWID, KEY_NAME}, KEY_UROWID + "=" + rowId, null, null, null, null, null);
    		mCursor.moveToFirst();
    		return mCursor;
    	}
    	// TODO: find for single_player and multiplayer tables
    	else {
    		return null;
    	}
    }
    
    /**
     * Return a Cursor over the list of all objects
     * 
     * @param table	Name of table to retrieve record from
     * @return 		Cursor over all users and scores
     */
    public Cursor findAll(String table) {    	
    	if (table.equals("game")) {
    		return db.query(DATABASE_GTABLE, new String[] {KEY_GROWID, KEY_SOUND, KEY_VIBRATION, KEY_PAUSE, KEY_GUSERID}, null, null, null, null, null);
    	}
    	else if (table.equals("user")) {
    		return db.query(DATABASE_UTABLE, new String[] {KEY_UROWID, KEY_NAME}, null, null, null, null, null);
    	}
    	// TODO: findAll for single_player and multi_player tables
    	else {
    		return null;
    	}
    }
    
    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param sound		Sound setting: 0 = off, 1 = on
     * @param vibration Vibration setting: 0 = off, 1 = on
     * @param pause		Pause setting: 0 = unpaused, 1 = paused
     * @param user		User foreign key (Currently active profile)
     * @param rowId		Row ID of the game to be updated
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean update(String table, long rowId, int sound, int vibration, int pause, int user_id, String name) {
        ContentValues args = new ContentValues();
        
        if (table.equals("game")) {
        	args.put(KEY_SOUND, sound);
            args.put(KEY_VIBRATION, vibration);
            args.put(KEY_PAUSE, pause);
            args.put(KEY_GUSERID, user_id);
            return db.update(DATABASE_GTABLE, args, KEY_GROWID + "=" + rowId, null) > 0;
        }
        else if (table.equals("user")) {
        	args.put(KEY_NAME, name);
            return db.update(DATABASE_UTABLE, args, KEY_UROWID + "=" + rowId, null) > 0;
        }
        // TODO: single and multi player tables
        else {
        	return false;
        }
    }

    /**
     * Delete the game with the given rowId
     * 
     * @param table Table to delete from
     * @param rowId Id of note to delete
     * @return 		True if deleted, false otherwise
     */
    public boolean delete(String table, long rowId) {
    	if (table.equals("game"))
    		return db.delete(DATABASE_GTABLE, KEY_GROWID + "=" + rowId, null) > 0;
    	else if (table.equals("user"))
    		// TODO: also delete from single or multiplayer table
    		return db.delete(DATABASE_UTABLE, KEY_UROWID + "=" + rowId, null) > 0;
    	else
    		return false;
    }
    
    public int count(String table) {
    	Cursor cursor;
    	
    	String sql = "SELECT COUNT(id) FROM " + table; 
    	
    	if (table.equals("game")) {
    		cursor = db.rawQuery(sql, null);
    		return cursor.getInt(0);
    	}
    	else if (table.equals("user")) {
    		cursor = db.rawQuery(sql, null);
    		return cursor.getInt(0);
    	}
    	else {
    		return -1;
    	}
    }
}