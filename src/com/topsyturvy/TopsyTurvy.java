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

	// Retrieve UI elements
    private Button mainMenuSinglePlayerButton 	= (Button)findViewById(R.id.mainMenuSinglePlayer);
    private Button mainMenuMultiPlayerButton 	= (Button)findViewById(R.id.mainMenuMultiPlayer);
    private Button mainMenuSettingsButton 		= (Button)findViewById(R.id.mainMenuSettings);
    
    // Game settings
    private int sound;
    private int vibration;
    
    // Managers
    private AudioManager audioManager;
    private Vibrator vibrator;
    
    // Create database instance
	private TopsyTurvyDbAdapter dbAdapter;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        // Create and open db
        dbAdapter = new TopsyTurvyDbAdapter(this);
        dbAdapter.open();
        
        // Retrieve game settings
        Cursor cursor = dbAdapter.fetchGame();
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
    	if (vibration == 0)
    		vibrator.cancel();
    }
}