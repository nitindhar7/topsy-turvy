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
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class TopsyTurvy extends Activity implements OnClickListener {
    
    // Game settings
    private int sound;
    private int vibration;
    
    // Managers
    private AudioManager audioManager;
    private Vibrator vibrator;
    
    // Create database instance
	private TopsyTurvyDbAdapter dbAdapter;
	
	// UI elements
	private Button mainMenuSinglePlayerButton;
	private Button mainMenuMultiPlayerButton;
	private Button mainMenuSettingsButton;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        // Retrieve UI elements
        mainMenuSinglePlayerButton 	= (Button)findViewById(R.id.mainMenuSinglePlayer);
        mainMenuMultiPlayerButton 	= (Button)findViewById(R.id.mainMenuMultiPlayer);
        mainMenuSettingsButton 		= (Button)findViewById(R.id.mainMenuSettings);
        
        // Create and open db
        dbAdapter = new TopsyTurvyDbAdapter(this);
        dbAdapter.open();
        
        // Retrieve game settings
        Cursor cursor = dbAdapter.find("game", 1);
        startManagingCursor(cursor);
        
		// Define listeners
		mainMenuSinglePlayerButton.setOnClickListener(this);
		mainMenuMultiPlayerButton.setOnClickListener(this);
		mainMenuSettingsButton.setOnClickListener(this);
		
		// Set sound setting using value from db
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(getAudioMode());
        
        // Set vibration setting using value from db
        setVibrator();
    }

    public void onClick(View src) {
		switch(src.getId()) {
			case R.id.mainMenuSinglePlayer:
				Intent singlePlayerGame = new Intent(TopsyTurvy.this, SinglePlayer.class);
	        	startActivity(singlePlayerGame);
				break;
			case R.id.mainMenuMultiPlayer:
				Intent multiPlayerGame = new Intent(TopsyTurvy.this, MultiPlayer.class);
	        	startActivity(multiPlayerGame);
				break;
			case R.id.mainMenuSettings:
				Intent settings = new Intent(TopsyTurvy.this, Settings.class);
	        	startActivity(settings);
				break;
		}
	}
    
    /**
     * Set the ringer ON or OFF based on db setting
     * 
     * @return Integer specifying ringer setting
     */
    private int getAudioMode () {
    	if (sound == 0)
    		return AudioManager.RINGER_MODE_SILENT;
    	else
    		return AudioManager.RINGER_MODE_NORMAL;
    }
    
    /**
     * Set the vibrator ON or OFF based on db setting
     */
    private void setVibrator() {
    	vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    	
    	if (vibration == 0)
    		vibrator.cancel();
    }
}