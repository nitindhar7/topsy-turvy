package com.topsyturvy;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Settings extends Activity implements OnClickListener, OnTouchListener {
	
	// DB
	private TopsyTurvyDbAdapter dbAdapter;
	private String activePlayer;
	
	// Return values
	private final int PLAYERS_RESULT = 10;
	
	// UI elements
	private TextView settingsMenuPlayers;
	private TextView settingsMenuHints;
	private TextView settingsMenuAbout;
	private TextView settingsMenuSoundLabel;
	private TextView settingsMenuVibrationLabel;
	private ToggleButton settingsMenuSound;
	private ToggleButton settingsMenuVibration;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.settings_menu);
        
        // Retrieve UI elements
        settingsMenuPlayers			= (TextView)findViewById(R.id.settingsMenuPlayers);
        settingsMenuHints			= (TextView)findViewById(R.id.settingsMenuHints);
        settingsMenuAbout			= (TextView)findViewById(R.id.settingsMenuAbout);
        settingsMenuSoundLabel		= (TextView)findViewById(R.id.settingsMenuSound);
        settingsMenuVibrationLabel	= (TextView)findViewById(R.id.settingsMenuVibration);
        settingsMenuSound			= (ToggleButton)findViewById(R.id.settingsMenuSoundButton);
        settingsMenuVibration		= (ToggleButton)findViewById(R.id.settingsMenuVibrationButton);
        
        // UI
        Typeface tf = Typeface.createFromAsset(getAssets(), "FORTE.TTF");
        settingsMenuPlayers.setTypeface(tf);
        settingsMenuHints.setTypeface(tf);
        settingsMenuAbout.setTypeface(tf);
        settingsMenuSoundLabel.setTypeface(tf);
        settingsMenuVibrationLabel.setTypeface(tf);
        
        // Set listeners
        settingsMenuPlayers.setOnClickListener(this);
        settingsMenuHints.setOnClickListener(this);
        settingsMenuAbout.setOnClickListener(this);
        settingsMenuSound.setOnClickListener(this);
        settingsMenuVibration.setOnClickListener(this);
        settingsMenuPlayers.setOnTouchListener(this);
        settingsMenuHints.setOnTouchListener(this);
        settingsMenuAbout.setOnTouchListener(this);
        
        // DB
        dbAdapter = new TopsyTurvyDbAdapter(this);
        dbAdapter.open();
        activePlayer = getIntent().getStringExtra("activePlayer");
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN)
			v.setBackgroundColor(Color.rgb(0, 170, 0));
		else
			v.setBackgroundColor(Color.TRANSPARENT);
		return false;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		Log.i("TOPSYTURVY", "onResume");
		
		if (dbAdapter.state == 0)
			dbAdapter.open();
	}
    
    @Override
	protected void onPause() {
		super.onPause();
		
		Log.i("TOPSYTURVY", "onPause");
		
		dbAdapter.close();
	}
    
    @Override
	protected void onStop() {
		super.onStop();
		dbAdapter.close();
		
		Log.i("TOPSYTURVY", "onStop");
		
		activePlayer = null;
	}
    
    @Override
	public void onBackPressed() {
    	dbAdapter.close();
    	activePlayer = null;
		finish();
	}
	
	public void onClick(View src) {
		
		switch(src.getId()) {
			case R.id.settingsMenuSound:
				// TODO:
				// sound on off
				if (settingsMenuSound.isChecked()) {	
					if (activePlayer != null)
						dbAdapter.update(activePlayer, null, -1, -1, -1, 1, -1);
				}
				else {
					if (activePlayer != null)
						dbAdapter.update(activePlayer, null, -1, -1, -1, 0, -1);
				}
					
				break;
			case R.id.settingsMenuVibration:
	        	// TODO: 
				// vibration on off
				if (settingsMenuSound.isChecked()) {	
					if (activePlayer != null)
						dbAdapter.update(activePlayer, null, -1, -1, -1, -1, 1);
				}
				else {
					if (activePlayer != null)
						dbAdapter.update(activePlayer, null, -1, -1, -1, -1, 0);
				}
				break;
			case R.id.settingsMenuPlayers:
				Intent newProfileIntent = new Intent(Settings.this, Players.class);
	        	startActivityForResult(newProfileIntent, PLAYERS_RESULT);
				break;			
			case R.id.settingsMenuHints:
				Intent hintsIntent = new Intent(Settings.this, Hints.class);
	        	startActivity(hintsIntent);
				break;
			case R.id.settingsMenuAbout:
				//TODO: about section
				Toast.makeText(getApplicationContext() , "Developed by: Nitin Dhar", Toast.LENGTH_SHORT).show();
				break;
		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        
        switch (resultCode) {
        case PLAYERS_RESULT:
        	if (dbAdapter.state == 0)
    			dbAdapter.open();
        	loadProfile();
        	break;
        case -1:
        	Toast.makeText(Settings.this, "User Not Created", 5).show();
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