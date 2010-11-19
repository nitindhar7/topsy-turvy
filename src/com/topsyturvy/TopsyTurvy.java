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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

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

	private AlertDialog.Builder builder;
	private AlertDialog alert;
	
	// Return values
	private int SINGLEPLAYER_RESULT;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        
        // Retrieve UI elements
        mainMenuSinglePlayerButton 	= (Button)findViewById(R.id.mainMenuSinglePlayer);
        mainMenuMultiPlayerButton 	= (Button)findViewById(R.id.mainMenuMultiPlayer);
        mainMenuSettingsButton 		= (Button)findViewById(R.id.mainMenuSettings);

		// Define listeners
		mainMenuSinglePlayerButton.setOnClickListener(this);
		mainMenuMultiPlayerButton.setOnClickListener(this);
		mainMenuSettingsButton.setOnClickListener(this);
		
		// Set sound setting using value from db
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(getAudioMode());

        dbAdapter = new TopsyTurvyDbAdapter(this);
        
        // Set vibration setting using value from db
        setVibrator();
        
        builder = new AlertDialog.Builder(this);
	    builder.setMessage("No Profile Selected\nCreate New Profile?")
	           .setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
						Intent addProfile = new Intent(TopsyTurvy.this, AddProfile.class);
						startActivity(addProfile);
	               }
	           })
	           .setNegativeButton("No", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            	   dialog.cancel();
	               }
	           });
	    alert = builder.create();
    }
    
    @Override
	protected void onStart()
	{
		super.onStart();
        dbAdapter.open();
	}
    
    @Override
	protected void onPause()
	{
		super.onPause();
		dbAdapter.close();
	}
    
    @Override
	public void onBackPressed() {
    	dbAdapter.close();
		finish();
	}

    public void onClick(View src) {
    	int gameCount;
    	int activePlayerId;
    	
    	gameCount = dbAdapter.count("game");
    	
		switch(src.getId()) {
			case R.id.mainMenuSinglePlayer:
				if (gameCount > 0) {
					activePlayerId = dbAdapter.find("game").getInt(4);
					Intent singlePlayerGame = new Intent(TopsyTurvy.this, SinglePlayer.class);
					singlePlayerGame.putExtra("activePlayerID", activePlayerId);
		        	startActivityForResult(singlePlayerGame, SINGLEPLAYER_RESULT);
				}
				else
					alert.show();
				break;
				
			case R.id.mainMenuMultiPlayer:
				if (gameCount > 0) {
					Intent multiPlayerGame = new Intent(TopsyTurvy.this, Lobby.class);
		        	startActivity(multiPlayerGame);
				}
				else
					alert.show();
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
    
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
                      
        switch (resultCode) {
	        case 10:
	        	Toast.makeText(getApplicationContext() , "YOU WIN!", Toast.LENGTH_LONG).show();
	        	break;
	        case 11:
	        	Toast.makeText(getApplicationContext() , "TIME OVER!!!!!", Toast.LENGTH_LONG).show();
	        	break;
	        case 12:
	        	Toast.makeText(getApplicationContext() , "YOU FELL!!!!!", Toast.LENGTH_LONG).show();
	        	break;
        }
        
        Intent scores = new Intent(TopsyTurvy.this, Scores.class);
    	startActivity(scores);
    }
}