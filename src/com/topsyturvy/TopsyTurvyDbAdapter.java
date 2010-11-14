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
import android.util.Log;

public class TopsyTurvyDbAdapter {
	
	// Game table fields
	public static final String KEY_GROWID			= "_id";
	public static final String KEY_SOUND			= "sound";
	public static final String KEY_VIBRATION		= "vibration";
	public static final String KEY_PAUSE			= "pause";
	public static final String KEY_PLAYERID			= "player_id";
	
	// Player table fields
	public static final String KEY_PROWID			= "_id";
	public static final String KEY_NAME				= "name";
    public static final String KEY_TOPSCORE			= "top_score";
    public static final String KEY_TOTALSCORE		= "total_score";
    public static final String KEY_GAMESPLAYED		= "games_played";

    // DB specific
    private static final String DATABASE_NAME		= "topsy_turvy";
    private static final int DATABASE_VERSION		= 2;
    
    // Table names
    private static final String DATABASE_GTABLE		= "game";
    private static final String DATABASE_PTABLE		= "player";

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private final Context ctx;

    // Game table creation
    private static final String DATABASE_GCREATE	= "CREATE TABLE " + DATABASE_GTABLE + " (" +
												  	  KEY_GROWID	  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
												  	  KEY_SOUND		  + " INTEGER DEFAULT 1 NOT NULL, " +
												  	  KEY_VIBRATION	  + " INTEGER DEFAULT 1 NOT NULL, " +
												  	  KEY_PAUSE	  	  + " INTEGER NOT NULL, " +
												  	  KEY_PLAYERID	  + " INTEGER NOT NULL);";
    // Player table creation
    private static final String DATABASE_PCREATE	= "CREATE TABLE " + DATABASE_PTABLE + " (" +
    												  KEY_PROWID	  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    												  KEY_NAME		  + " TEXT NULL, " +
												      KEY_TOPSCORE	  + " INTEGER DEFAULT 1 NOT NULL, " +
												      KEY_TOTALSCORE  + " INTEGER DEFAULT 0 NOT NULL, " +
												      KEY_GAMESPLAYED + " INTEGER DEFAULT 0 NOT NULL);";
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_GCREATE);
            db.execSQL(DATABASE_PCREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_GTABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_PTABLE);
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
     * @param player_id	User id foreign key (Currently active profile)
     * @param name		Name for new user
     * @return			rowId or -1 if failed
     */
    public long create(String table, int sound, int vibration, int pause, int player_id, String name) {
        ContentValues initialValues = new ContentValues();
        
        if (table.equals(DATABASE_GTABLE)) {
        	initialValues.put(KEY_SOUND, sound);
            initialValues.put(KEY_VIBRATION, vibration);
            initialValues.put(KEY_PAUSE, pause);
            initialValues.put(KEY_PLAYERID, player_id);
            return db.insert(table, null, initialValues);
        }
        else if (table.equals(DATABASE_PTABLE)) {
        	initialValues.put(KEY_NAME, name);
        	return db.insert(table, null, initialValues);
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
    		mCursor = db.query(true, DATABASE_GTABLE, new String[] {KEY_GROWID, KEY_SOUND, KEY_VIBRATION, KEY_PAUSE, KEY_PLAYERID}, KEY_GROWID + "=" + rowId, null, null, null, null, null);
    		mCursor.moveToFirst();
    		return mCursor;
    	}
    	else if (table.equals("player")) {
    		mCursor = db.query(true, DATABASE_PTABLE, new String[] {KEY_PROWID, KEY_NAME, KEY_TOPSCORE, KEY_TOTALSCORE, KEY_GAMESPLAYED}, KEY_PROWID + "=" + rowId, null, null, null, null, null);
    		mCursor.moveToFirst();
    		return mCursor;
    	}
    	else {
    		return null;
    	}
    }
    
    public Cursor find_by_name(String name) {
    	Cursor mCursor;

		mCursor = db.query(true, DATABASE_PTABLE, new String[] {KEY_PROWID, KEY_NAME, KEY_TOPSCORE, KEY_TOTALSCORE, KEY_GAMESPLAYED}, KEY_NAME + "='" + name + "'", null, null, null, null, null);
		mCursor.moveToFirst();
		return mCursor;
    }
    
    public Cursor find(String table, String position) {
    	Cursor mCursor;
    	
    	if (table.equals("game"))
    		mCursor = db.query(DATABASE_GTABLE, new String[] {KEY_GROWID, KEY_SOUND, KEY_VIBRATION, KEY_PAUSE, KEY_PLAYERID}, null, null, null, null, null);
    	else if (table.equals("player"))
    		mCursor = db.query(DATABASE_PTABLE, new String[] {KEY_PROWID, KEY_NAME, KEY_TOPSCORE, KEY_TOTALSCORE, KEY_GAMESPLAYED}, null, null, null, null, null);
    	else
    		return null;
    	
    	if (position.equals("first"))
    		mCursor.moveToFirst();
    	else if (position.equals("last"))
    		mCursor.moveToLast();
    	else
    		return null;
    	
    	return mCursor;
    }
    
    /**
     * Return a Cursor over the list of all objects
     * 
     * @param table	Name of table to retrieve record from
     * @return 		Cursor over all players and scores
     */
    public Cursor findAll(String table) {    	
    	if (table.equals("game")) {
    		return db.query(DATABASE_GTABLE, new String[] {KEY_GROWID, KEY_SOUND, KEY_VIBRATION, KEY_PAUSE, KEY_PLAYERID}, null, null, null, null, null);
    	}
    	else if (table.equals("player")) {
    		return db.query(DATABASE_PTABLE, new String[] {KEY_PROWID, KEY_NAME, KEY_TOPSCORE, KEY_TOTALSCORE, KEY_GAMESPLAYED}, null, null, null, null, null);
    	}
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
     * @param player		User foreign key (Currently active profile)
     * @param rowId		Row ID of the game to be updated
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean update(String table, long rowId, int sound, int vibration, int pause, int player_id, String name, int topScore, int gamesPlayed, int totalScore) {
        ContentValues args = new ContentValues();
        
        if (table.equals("game")) {
        	args.put(KEY_SOUND, sound);
            args.put(KEY_VIBRATION, vibration);
            args.put(KEY_PAUSE, pause);
            args.put(KEY_PLAYERID, player_id);
            return db.update(DATABASE_GTABLE, args, KEY_GROWID + "=" + rowId, null) > 0;
        }
        else if (table.equals("player")) {
        	args.put(KEY_NAME, name);
        	args.put(KEY_TOPSCORE, topScore);
        	args.put(KEY_TOTALSCORE, totalScore);
        	args.put(KEY_GAMESPLAYED, gamesPlayed);
            return db.update(DATABASE_PTABLE, args, KEY_PROWID + "=" + rowId, null) > 0;
        }
        else {
        	return false;
        }
    }
    
    public boolean update(String table, String playerName, int sound, int vibration, int pause, int player_id, String name, int topScore, int gamesPlayed, int totalScore) {
        ContentValues args = new ContentValues();
        
		args.put(KEY_NAME, playerName);
		args.put(KEY_TOPSCORE, topScore);
		args.put(KEY_TOTALSCORE, totalScore);
		args.put(KEY_GAMESPLAYED, gamesPlayed);
		return db.update(DATABASE_PTABLE, args, KEY_NAME + "='" + playerName + "'", null) > 0;
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
    	else if (table.equals("player"))
    		return db.delete(DATABASE_PTABLE, KEY_PROWID + "=" + rowId, null) > 0;
    	else
    		return false;
    }
    
    public boolean delete(String table, String name) {
    	return db.delete(DATABASE_PTABLE, KEY_NAME + "='" + name + "'", null) > 0;
    }
    
    public int count(String table) {
    	Cursor cursor;
    	
    	if (table.equals("game"))
    		cursor = db.query(DATABASE_GTABLE, new String[] {KEY_GROWID, KEY_SOUND, KEY_VIBRATION, KEY_PAUSE, KEY_PLAYERID}, null, null, null, null, null);
    	else if (table.equals("player"))
    		cursor = db.query(DATABASE_PTABLE, new String[] {KEY_PROWID, KEY_NAME, KEY_TOPSCORE, KEY_TOTALSCORE, KEY_GAMESPLAYED}, null, null, null, null, null);
    	else
    		return -1;
    	
    	if (cursor != null)
    		return cursor.getCount();
    	else
    		return -1;
    }
}