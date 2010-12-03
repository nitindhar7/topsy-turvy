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

	// Common fields
	public static final String KEY_NAME				= "name";
	
	// Sessions table fields
	public static final String KEY_ID				= "_id";
	
	// Players table fields
    public static final String KEY_TOPSCORE			= "top_score";
    public static final String KEY_TOTALSCORE		= "total_score";
    public static final String KEY_GAMESPLAYED		= "games_played";
    public static final String KEY_SOUND			= "sound";
    public static final String KEY_VIBRATION		= "vibration";

    // DB specific
    private static final String DATABASE_NAME		= "topsy_turvy";
    private static final int DATABASE_VERSION		= 2;
    
    // Table names
    public static final String DATABASE_SESSIONS_TABLE	= "sessions";
    public static final String DATABASE_PLAYERS_TABLE	= "players";

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private final Context ctx;
    protected int state;

    // Sessions table creation
    private static final String DATABASE_SESSIONS_CREATE	= "CREATE TABLE " + DATABASE_SESSIONS_TABLE + " (" +
    														  KEY_ID	  	  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    														  KEY_NAME	      + " TEXT NOT NULL, " +
    														  "FOREIGN KEY (" + KEY_NAME + ") REFERENCES " + DATABASE_PLAYERS_TABLE + "(" + KEY_NAME + "));";
    // Players table creation
    private static final String DATABASE_PLAYERS_CREATE		= "CREATE TABLE " + DATABASE_PLAYERS_TABLE + " (" +
															  KEY_NAME		  + " TEXT PRIMARY KEY, " +
															  KEY_TOPSCORE	  + " INTEGER DEFAULT 0 NOT NULL, " +
															  KEY_TOTALSCORE  + " INTEGER DEFAULT 0 NOT NULL, " +
															  KEY_GAMESPLAYED + " INTEGER DEFAULT 0 NOT NULL, " +
															  KEY_SOUND		  + " INTEGER DEFAULT 1 NOT NULL, " +
															  KEY_VIBRATION	  + " INTEGER DEFAULT 1 NOT NULL);";
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_SESSIONS_CREATE);
            db.execSQL(DATABASE_PLAYERS_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_SESSIONS_CREATE);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_PLAYERS_CREATE);
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
        state = 0;
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
        state = 1;
        return this;
    }
    
    /**
     * Close database
     */
    public void close() {
        dbHelper.close();
        state = 0;
    }
    
    /**
     * Create new player with given name, default values for scores, sounds and vibration
     * 
     * @param name		Name for new player
     * @return			rowId or -1 on fail
     */
    public int create(String table, String name) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        
        if (table.equals(DATABASE_SESSIONS_TABLE)) {
        	return (int)db.insert(DATABASE_SESSIONS_TABLE, null, initialValues);
        }
        else if (table.equals(DATABASE_PLAYERS_TABLE)) {
        	return (int)db.insert(DATABASE_PLAYERS_TABLE, null, initialValues);
        }
        else
        	return -1;
    }
    
    /**
     * Return rows with given condition
     * 
     * @param table		Name of table to retrieve record from
     * @param condition	Condition to return on
     * @return			Cursor over one object or null
     */
    public Cursor find(String table, String condition) {
    	Cursor cursor;
    	
    	if (table.equals(DATABASE_SESSIONS_TABLE)) {
    		cursor = db.query(DATABASE_SESSIONS_TABLE, new String[] {KEY_ID, KEY_NAME}, condition, null, null, null, null, null);
    		cursor.moveToFirst();
    		return cursor;
    	}
    	else if (table.equals(DATABASE_PLAYERS_TABLE)) {
    		cursor = db.query(DATABASE_PLAYERS_TABLE, new String[] {KEY_NAME, KEY_TOPSCORE, KEY_TOTALSCORE, KEY_GAMESPLAYED, KEY_SOUND, KEY_VIBRATION}, condition, null, null, null, null, null);
    		cursor.moveToFirst();
    		return cursor;
    	}
    	else
    		return null;
    }
    
    /**
     * Return rows with given condition
     * 
     * @param table		Name of table to retrieve record from
     * @param condition	Condition to return on
     * @return			Cursor over one object or null
     */
    public Cursor find(String table, String condition, String order) {
    	Cursor cursor;
    	
    	if (table.equals(DATABASE_SESSIONS_TABLE)) {
    		cursor = db.query(DATABASE_SESSIONS_TABLE, new String[] {KEY_ID, KEY_NAME}, condition, null, null, null, order, null);
    		cursor.moveToFirst();
    		return cursor;
    	}
    	else if (table.equals(DATABASE_PLAYERS_TABLE)) {
    		cursor = db.query(DATABASE_PLAYERS_TABLE, new String[] {KEY_NAME, KEY_TOPSCORE, KEY_TOTALSCORE, KEY_GAMESPLAYED, KEY_SOUND, KEY_VIBRATION}, condition, null, null, null, order, null);
    		cursor.moveToFirst();
    		return cursor;
    	}
    	else
    		return null;
    }
    
    /**
     * Update session (with given rowId) with given playerId
     * 
     * @param rowId		Row id for session
     * @param playerId	New Player ID
     * @return			Number of rows affected
     */
    public int update(String oldName, String newName) {
        ContentValues args = new ContentValues();

		args.put(KEY_NAME, newName);
		return db.update(DATABASE_SESSIONS_TABLE, args, KEY_NAME + "='" + oldName + "'", null);
    }
    
    /**
     * Update player with given attributes
     * 
     * @param name			New name OR null
     * @param topScore		New top score OR -1
     * @param totalScore	New total score OR -1
     * @param gamesPlayed	New number of games played OR -1
     * @param sound			Sound setting: 0 = off, 1 = on OR -1
     * @param vibration		Vibration setting: 0 = off, 1 = on OR -1
     * @return				Number of rows updated
     */
    public int update(String oldName, String newName, int topScore, int totalScore, int gamesPlayed, int sound, int vibration) {
        ContentValues args = new ContentValues();

        if (newName != null)
        	args.put(KEY_NAME, newName);
        if (topScore != -1)
        	args.put(KEY_TOPSCORE, topScore);
        if (totalScore != -1)
        	args.put(KEY_TOTALSCORE, totalScore);
        if (gamesPlayed != -1)
        	args.put(KEY_GAMESPLAYED, gamesPlayed);
		if (sound != -1)
			args.put(KEY_SOUND, sound);
		if (vibration != -1)
			args.put(KEY_VIBRATION, vibration);
		
		return db.update(DATABASE_PLAYERS_TABLE, args, KEY_NAME + "='" + oldName + "'", null);
    }
    
    /**
     * Delete record in table with the given condition
     * 
     * @param table Table to delete from
     * @param rowId Id of record to delete
     * @return 		True if deleted, false otherwise
     */
    public boolean delete(String table, String condition) {
    	return db.delete(table, condition, null) > 0;
    }
    
    /**
     * Get row count from table
     * 
     * @param table	Table to get count of
     * @return		Number of rows in table
     */
    public int count(String table) {
    	Cursor cursor;
    	
    	if (table.equals(DATABASE_SESSIONS_TABLE))
    		cursor = db.query(DATABASE_SESSIONS_TABLE, new String[] {KEY_ID}, null, null, null, null, null);
    	else if (table.equals("player"))
    		cursor = db.query(DATABASE_PLAYERS_TABLE, new String[] {KEY_ID}, null, null, null, null, null);
    	else
    		return -1;
    	
    	if (cursor != null)
    		return cursor.getCount();
    	else
    		return -1;
    }
}