package com.topsyturvy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TopsyTurvyDbAdapter {
	
	public static final String KEY_ROWID		= "_id";
	public static final String KEY_SOUND		= "sound";
	public static final String KEY_VIBRATION	= "vibration";
	public static final String KEY_PAUSE		= "pause";
	public static final String KEY_USER			= "user";

    private static final String DATABASE_NAME	= "topsy_turvy";
    private static final String DATABASE_TABLE	= "game";
    private static final int DATABASE_VERSION	= 2;
    
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private final Context ctx;

    /*
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE	= "CREATE TABLE " + DATABASE_TABLE + " (" +
												  KEY_ROWID		  + " INTEGER NOT NULL, " +
												  KEY_SOUND		  + " INTEGER NOT NULL, " +
												  KEY_VIBRATION	  + " INTEGER NOT NULL, " +
												  KEY_PAUSE	  	  + " INTEGER NOT NULL, " +
												  KEY_USER		  + " INTEGER NOT NULL, " +
												  "PRIMARY KEY("  + KEY_ROWID + "), "+
												  "FOREIGN KEY("  + KEY_USER  + ") REFERENCES user(_id));";
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS game");
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
     * @param sound		Sound setting: 0 = off, 1 = on
     * @param vibration Vibration setting: 0 = off, 1 = on
     * @param pause		Pause setting: 0 = unpaused, 1 = paused
     * @param user		User foreign key (Currently active profile)
     * @return			rowId or -1 if failed
     */
    public long createGame(int sound, int vibration, int pause, int user) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SOUND, sound);
        initialValues.put(KEY_VIBRATION, vibration);
        initialValues.put(KEY_PAUSE, pause);
        initialValues.put(KEY_USER, user);

        return db.insert(DATABASE_TABLE, null, initialValues);
    }
    
    /**
     * Delete the game with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteGame(long rowId) {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchGames() {
        return db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_SOUND, KEY_VIBRATION, KEY_PAUSE, KEY_USER}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the game that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchGame(long rowId) throws SQLException {

        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_SOUND, KEY_VIBRATION, KEY_PAUSE, KEY_USER}, KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
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
    public boolean updateGame(long rowId, int sound, int vibration, int pause, int user) {
        ContentValues args = new ContentValues();
        args.put(KEY_SOUND, sound);
        args.put(KEY_VIBRATION, vibration);
        args.put(KEY_PAUSE, pause);
        args.put(KEY_USER, user);

        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}