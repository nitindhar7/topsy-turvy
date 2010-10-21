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

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Profile extends Activity implements OnClickListener {
	
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
    		setContentView(R.layout.switch_profile);
    	}
    	else {
    		// TODO: error
    	}
	}
	
	public void onClick(View src) {
		switch(src.getId()) {
			case R.id.save_user:
				long rowId;
	    		Toast toast;
	    		
				userName = enterUser.getText().toString();

	    		rowId = dbAdapter.create("user", 0, 0, 0, 0, userName);
	    		
	    		if (rowId == -1) {
	    			toast = Toast.makeText(Profile.this, "User Not Created", 5);
					toast.show();
	    		}
	    		else {
	    			toast = Toast.makeText(Profile.this, "User Created", 5);
					toast.show();
	    		}
				break;
		}
	}
}