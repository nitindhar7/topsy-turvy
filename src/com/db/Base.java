package com.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class Base extends SQLiteOpenHelper {
    
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
    
    // Table names
    private static final String DATABASE_GTABLE		= "game";
    private static final String DATABASE_UTABLE		= "user";
    private static final String DATABASE_STABLE		= "single_player";
    private static final String DATABASE_MTABLE		= "multi_player";
    
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
    
    private SQLiteDatabase db;
    private final Context ctx;

    
    // TODO: TEST: check if this even works
    public Base(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		this.ctx = context;
	}
    
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_GCREATE);
		db.execSQL(DATABASE_UCREATE);
		db.execSQL(DATABASE_SCREATE);
		db.execSQL(DATABASE_MCREATE);
	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_GTABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_UTABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_STABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_MTABLE);
        onCreate(db);
	}
	
	public Base open() throws SQLException {
        db = this.getWritableDatabase();
        return this;
    }
	
    /*
     * Close database
     */
    public void close() {
        db.close();
    }
	
	// Find a record by its id
	public Cursor find(int rowId) {
		Cursor cursor = null;
		return cursor;
	}
	
	// Find record(s) by position
	public Cursor find(String position, String where, String order) {
		Cursor cursor = null;
		return cursor;
	}
	
	/**
	 * Create | 
	 */
	public void create() {
	}
	
	/**
	 * Update | 
	 */
	public void update() {
	}
	
	/**
	 * Delete | 
	 */
	public void delete() {
	}
	
	/**
	 * Count | 
	 */
	public int count() {
		return 0;
	}
}
