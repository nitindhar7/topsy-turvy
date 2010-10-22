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
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class Profile extends ListActivity implements OnClickListener {
	
	// local
	private String intent;
	
	// Create database instance
	private TopsyTurvyDbAdapter dbAdapter;
	
	// UI elements
	private EditText enterUser;
	private Button saveUser;
	
	// Store player name
	private String userName;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // retrieve parameters
        intent = this.getIntent().getStringExtra("intent");
        
        // Create and open db
        dbAdapter = new TopsyTurvyDbAdapter(this);
        dbAdapter.open();
        
        // Retrieve game settings
        Cursor cursor = dbAdapter.find("game", 1);
        startManagingCursor(cursor);
        
        // Display layout based on intent
    	if (intent.equals("new")) {
    		setContentView(R.layout.add_profile);
    		
    		// Retrieve UI elements
            enterUser	= (EditText)findViewById(R.id.enter_user);
            saveUser	= (Button)findViewById(R.id.save_user);
        	
            // Define listeners
    		saveUser.setOnClickListener(this);
    	}
    	else if (intent.equals("switch")) {
    		// Get all of the notes from the database and create the item list
            Cursor c = dbAdapter.findAll("user");
            startManagingCursor(c);

            String[] from = new String[] { TopsyTurvyDbAdapter.KEY_NAME };
            int[] to = new int[] { R.id.listuser };
            
            // Now create an array adapter and set it to display using our row
            SimpleCursorAdapter users = new SimpleCursorAdapter(this, R.layout.switch_profile, c, from, to);
            setListAdapter(users);
    	}
    	else {
    		// TODO: error
    	}
	}
	
	public void onClick(View src) {
		switch(src.getId()) {
			case R.id.save_user:
				long rowId;

				userName = enterUser.getText().toString();

	    		rowId = dbAdapter.create("user", 0, 0, 0, 0, userName);
	    		
	    		// unsuccessful
	    		if (rowId == -1) {
	    			setResult(-1);
	        	    finish();
	    		}
	    		// successful
	    		else {
	    			setResult(0);
	    			finish();
	    		}
				break;
		}
	}
	
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        // TODO: switch user (in game table)
        // TODO: jump back to settings menu
    }
}