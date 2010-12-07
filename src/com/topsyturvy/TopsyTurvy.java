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
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
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
	private ProgressDialog dialog;
	
	// Return values
	private int SINGLEPLAYER_RESULT;
	private int MULTIPLAYER_RESULT;
	private int SETTINGS_RESULT;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        
        //Background Music
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.backgroundmusic);
        //mp.start();
        
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
					openContextMenu(mainMenuSinglePlayerButton);
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
    
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        
        menu.setHeaderIcon(R.drawable.ic_menu_share);
        menu.setHeaderTitle("Select Level");
        menu.add(0, Menu.FIRST, 0, "Level 1");
        menu.add(0, Menu.FIRST + 1, 0, "Level 2");
        menu.add(0, Menu.FIRST + 2, 0, "Level 3");
        menu.add(0, Menu.FIRST + 3, 0, "Level 4");
    }
    
    public boolean onContextItemSelected(MenuItem item) {
    	dialog = ProgressDialog.show(TopsyTurvy.this, "", "Loading. Please wait...", true);
    	Intent singlePlayerGame;
    	
        switch(item.getItemId()) {
            case Menu.FIRST:
            	singlePlayerGame = new Intent(TopsyTurvy.this, SinglePlayer.class);
            	singlePlayerGame.putExtra("activePlayer", activePlayer);
            	singlePlayerGame.putExtra("level", 1);
            	startActivityForResult(singlePlayerGame, SINGLEPLAYER_RESULT);
            	return true;
            case Menu.FIRST + 1:
            	singlePlayerGame = new Intent(TopsyTurvy.this, SinglePlayer.class);
            	singlePlayerGame.putExtra("activePlayer", activePlayer);
            	singlePlayerGame.putExtra("level", 2);
            	startActivityForResult(singlePlayerGame, SINGLEPLAYER_RESULT);
            	return true;
            case Menu.FIRST + 2:
            	singlePlayerGame = new Intent(TopsyTurvy.this, SinglePlayer.class);
            	singlePlayerGame.putExtra("activePlayer", activePlayer);
            	singlePlayerGame.putExtra("level", 3);
            	startActivityForResult(singlePlayerGame, SINGLEPLAYER_RESULT);
            	return true;
            case Menu.FIRST + 3:
            	singlePlayerGame = new Intent(TopsyTurvy.this, SinglePlayer.class);
            	singlePlayerGame.putExtra("activePlayer", activePlayer);
            	singlePlayerGame.putExtra("level", 4);
            	startActivityForResult(singlePlayerGame, SINGLEPLAYER_RESULT);
            	return true;
        }
        
        
        return super.onContextItemSelected(item);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        
        dialog.cancel();
        int score = -1;

        if (dbAdapter.state == 0)
			dbAdapter.open();
        loadProfile();
        
        if (intent != null && intent.hasExtra("score"))
        	score = intent.getIntExtra("score", -1);
        
        switch (resultCode) {
	        case 10:
	        	MediaPlayer winSound = MediaPlayer.create(getApplicationContext(), R.raw.win);
	        	winSound.start();
	        	Toast.makeText(getApplicationContext() , "YOU WIN!", Toast.LENGTH_LONG).show();
	        	Intent win = new Intent(TopsyTurvy.this, Score.class);
	        	win.putExtra("score", score);
	        	win.putExtra("activePlayer", activePlayer);
	        	startActivity(win);
	        	break;
	        case 11:
	        	Intent timeOver = new Intent(TopsyTurvy.this, Score.class);
	        	timeOver.putExtra("score", score);
	        	timeOver.putExtra("activePlayer", activePlayer);
	        	startActivity(timeOver);
	        	break;
	        case 12:
	        	MediaPlayer fallSound = MediaPlayer.create(getApplicationContext(), R.raw.explosion);
	        	fallSound.start();
	        	Toast.makeText(getApplicationContext() , "YOU FELL!!!!!", Toast.LENGTH_LONG).show();
	        	Intent fell = new Intent(TopsyTurvy.this, Score.class);
	        	fell.putExtra("score", score);
	        	fell.putExtra("activePlayer", activePlayer);
	        	startActivity(fell);
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