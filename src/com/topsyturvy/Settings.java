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
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

public class Settings extends Activity implements OnClickListener {
	
	// All purpose
	Bundle bundle;
	
	private final int ADD_PROFILE_RESULT = 10;
	
	// UI elements
	private ImageButton moreMenuSwitchProfileButton;
	private ImageButton moreMenuNewProfileButton;
	private ImageButton moreMenuSingleplayerScoresButton;
	private ImageButton moreMenuMultiplayerScoresButton;
	private ImageButton moreMenuSingleplayerHintsButton;
	private ImageButton moreMenuMultiplayerHintsButton;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.settings);
        
        // Retrieve UI elements
        moreMenuSwitchProfileButton			= (ImageButton)findViewById(R.id.switchProfileButton);
        moreMenuNewProfileButton			= (ImageButton)findViewById(R.id.newProfileButton);
        moreMenuSingleplayerScoresButton	= (ImageButton)findViewById(R.id.singleplayerScoresButton);
        moreMenuMultiplayerScoresButton		= (ImageButton)findViewById(R.id.multiplayerScoresButton);
        moreMenuSingleplayerHintsButton		= (ImageButton)findViewById(R.id.singleplayerHintsButton);
        moreMenuMultiplayerHintsButton		= (ImageButton)findViewById(R.id.multiplayerHintsButton);
        
        // Set listeners
        moreMenuSwitchProfileButton.setOnClickListener(this);
        moreMenuNewProfileButton.setOnClickListener(this);
        moreMenuSingleplayerScoresButton.setOnClickListener(this);
        moreMenuMultiplayerScoresButton.setOnClickListener(this);
        moreMenuSingleplayerHintsButton.setOnClickListener(this);
        moreMenuMultiplayerHintsButton.setOnClickListener(this);
	}

	public void onClick(View src) {
		switch(src.getId()) {
			case R.id.newProfileButton:
				Intent newProfileIntent = new Intent(Settings.this, AddProfile.class);
	        	startActivityForResult(newProfileIntent, ADD_PROFILE_RESULT);
				break;
			case R.id.switchProfileButton:
				Intent switchProfileIntent = new Intent(Settings.this, SwitchProfile.class);
	        	startActivity(switchProfileIntent);
				break;
			case R.id.singleplayerScoresButton:
				Intent singleplayerScoresIntent = new Intent(Settings.this, Scores.class);
	        	startActivity(singleplayerScoresIntent);
				break;
			case R.id.multiplayerScoresButton:
				Intent multiplayerScoresIntent = new Intent(Settings.this, Scores.class);
	        	startActivity(multiplayerScoresIntent);
				break;
			case R.id.singleplayerHintsButton:
	        	startActivity(new Intent(Settings.this, Hints.class));
				break;
			case R.id.multiplayerHintsButton:
	        	startActivity(new Intent(Settings.this, Hints.class));
				break;
		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        
        switch (resultCode) {
        case ADD_PROFILE_RESULT:
        	Toast.makeText(Settings.this, "New Profile Created", 5).show();
        	break;
        case -1:
        	Toast.makeText(Settings.this, "User Not Created", 5).show();
        	break;
        }
    }
	
	@Override
	public void onBackPressed() {
		finish();
	}
}