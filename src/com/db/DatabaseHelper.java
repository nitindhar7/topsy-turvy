package com.db;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Database helper class used to manage the creation and upgrading of your database.
 * This class also usually provides the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "topsyturvy.db";
	private static final int DATABASE_VERSION = 5;

	// the DAO object we use to access tables
	private Dao<Sessions, Integer> sessionsDao = null;
	private Dao<Players, Integer> playersDao = null;
	private Dao<Levels, Integer> levelsDao = null;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * This is called when the database is first created.
	 * Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, Sessions.class);
			TableUtils.createTable(connectionSource, Players.class);
			TableUtils.createTable(connectionSource, Levels.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * This is called when your application is upgraded and
	 * it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, Sessions.class, true);
			TableUtils.dropTable(connectionSource, Players.class, true);
			TableUtils.dropTable(connectionSource, Levels.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the Database Access Object (DAO) for Sessions class.
	 * It will create it or just give the cached value.
	 */
	public Dao<Sessions, Integer> getSessionsDao() throws SQLException {
		if (sessionsDao == null) {
			sessionsDao = BaseDaoImpl.createDao(getConnectionSource(), Sessions.class);
		}
		return sessionsDao;
	}
	
	/**
	 * Returns the Database Access Object (DAO) for Players class.
	 * It will create it or just give the cached value.
	 */
	public Dao<Players, Integer> getPlayersDao() throws SQLException {
		if (playersDao == null) {
			playersDao = BaseDaoImpl.createDao(getConnectionSource(), Players.class);
		}
		return playersDao;
	}
	
	/**
	 * Returns the Database Access Object (DAO) for Levels class.
	 * It will create it or just give the cached value.
	 */
	public Dao<Levels, Integer> getSimpleDataDao() throws SQLException {
		if (levelsDao == null) {
			levelsDao = BaseDaoImpl.createDao(getConnectionSource(), Levels.class);
		}
		return levelsDao;
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		sessionsDao = null;
		playersDao = null;
		levelsDao = null;
	}
}