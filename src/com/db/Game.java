package com.db;

import android.content.Context;

public class Game extends Base {

	private Context ctx;
	
	private static final String DATABASE_NAME = "topsy_turvy";
    private static final int DATABASE_VERSION = 2;
	
	public Game(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.ctx = context;
	}

}
