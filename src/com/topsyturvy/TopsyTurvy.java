package com.topsyturvy;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class TopsyTurvy extends Activity implements OnClickListener {
    
    // DB
	private TopsyTurvyDbAdapter dbAdapter;
	private String activePlayer;
	private MediaPlayer mediaPlayer;
	
	// UI
	private Button mainMenuSinglePlayerButton;
	private Button mainMenuMultiPlayerButton;
	private Button mainMenuSettingsButton;

	// Return values
	private int MULTIPLAYER_RESULT;
	private int SETTINGS_RESULT;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        
        //Background Music
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.backgroundmusic);
        mp.start();
        
        // UI
        mainMenuSinglePlayerButton 	= (Button)findViewById(R.id.mainMenuSinglePlayer);
        mainMenuMultiPlayerButton 	= (Button)findViewById(R.id.mainMenuMultiPlayer);
        mainMenuSettingsButton 		= (Button)findViewById(R.id.mainMenuSettings);

		// Define listeners
		mainMenuSinglePlayerButton.setOnClickListener(this);
		mainMenuMultiPlayerButton.setOnClickListener(this);
		mainMenuSettingsButton.setOnClickListener(this);

		// DB
        dbAdapter = new TopsyTurvyDbAdapter(this);
        dbAdapter.open();
        activePlayer = null;
        
        registerForContextMenu(mainMenuSinglePlayerButton);
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		
		loadProfile();
		
		if (dbAdapter.state == 0)
			dbAdapter.open();
		
		if (activePlayer != null)
			Toast.makeText(getApplicationContext() , "Hi, " + activePlayer + "!", Toast.LENGTH_LONG).show();
	}
    
    @Override
	protected void onPause() {
		super.onPause();
	}
    
    @Override
	protected void onStop() {
		super.onStop();
		activePlayer = null;
	}
    
    @Override
	public void onBackPressed() {
    	dbAdapter.close();
    	activePlayer = null;
    	
    	if (mediaPlayer != null) {
	    	mediaPlayer.release();
	    	mediaPlayer = null;
    	}
    	
		finish();
	}

    public void onClick(View src) {
    	
		switch(src.getId()) {
			case R.id.mainMenuSinglePlayer:
				if (activePlayer == null)
					Toast.makeText(getApplicationContext() , "No Player Selected", Toast.LENGTH_LONG).show();
				else {
					Intent levels = new Intent(TopsyTurvy.this, Levels.class);
					levels.putExtra("activePlayer", activePlayer);
					startActivity(levels);
				}
				break;
			case R.id.mainMenuMultiPlayer:
				if (activePlayer == null)
					Toast.makeText(getApplicationContext() , "No Player Selected", Toast.LENGTH_LONG).show();
				else {
					Intent lobby = new Intent(TopsyTurvy.this, Lobby.class);
					lobby.putExtra("activePlayer", activePlayer);
					startActivityForResult(lobby, MULTIPLAYER_RESULT);
				}
				break;
			case R.id.mainMenuSettings:
				Intent settings = new Intent(TopsyTurvy.this, Settings.class);
				settings.putExtra("activePlayer", activePlayer);
				startActivityForResult(settings, SETTINGS_RESULT);
	        	break;
		}
	}
    
    private void loadProfile() {
    	Cursor pCursor, sCursor;
    	
    	sCursor = dbAdapter.find(TopsyTurvyDbAdapter.DATABASE_SESSIONS_TABLE, null);
    	if (sCursor != null && sCursor.getCount() > 0) {
    		pCursor = dbAdapter.find(TopsyTurvyDbAdapter.DATABASE_PLAYERS_TABLE, "name = '" + sCursor.getString(1) + "'");
    		if (pCursor != null && pCursor.getCount() > 0)
    			activePlayer = pCursor.getString(0);
    	}
    }
}