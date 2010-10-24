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

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Window;
import android.widget.SimpleCursorAdapter;

public class Scores extends ListActivity {
	
	// Create database instance
	private TopsyTurvyDbAdapter dbAdapter;
	
	// which mode
	private String intent;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        intent = this.getIntent().getStringExtra("mode");
        if (intent.equals("singleplayer")) {
        	setContentView(R.layout.scoreboard);
        }
        else {
        	setContentView(R.layout.scoreboard);
        }

        // Create and open db
        dbAdapter = new TopsyTurvyDbAdapter(this);
        dbAdapter.open();

        populateList();
        
        // Register menu, used for long press delete
        registerForContextMenu(getListView());
	}
	
	/**
	 * Retreive all user names, put into array, insert each element into textview
	 * and populate listview with textviews
	 */
	private void populateList() {
		// Get all of the notes from the database and create the item list
        Cursor c = dbAdapter.findAll("user");
        startManagingCursor(c);

        String[] from = new String[] { TopsyTurvyDbAdapter.KEY_NAME };
        int[] to = new int[] { R.id.listuser };
        
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter users = new SimpleCursorAdapter(this, R.layout.users_row, c, from, to);
        setListAdapter(users);
    }
}