package com.topsyturvy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class Levels extends Activity {
	
	private int SINGLEPLAYER_RESULT;
	
	// DB
	private TopsyTurvyDbAdapter dbAdapter;
	private String activePlayer;
	
	// UI
	private ProgressDialog dialog;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.levels);
        
        // DB
        dbAdapter = new TopsyTurvyDbAdapter(this);
        dbAdapter.open();
        activePlayer = null;
        activePlayer = getIntent().getStringExtra("activePlayer");
        
        // UI
        TextView backButton = (TextView) findViewById(R.id.backButton);
        GridView gridview = (GridView) findViewById(R.id.levelsGrid);
        gridview.setAdapter(new LevelsImageAdapter(this));
        gridview.setNumColumns(3);
        gridview.setOnItemClickListener(new OnItemClickListener() {
        	Intent singlePlayerGame;
        	
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	dialog = ProgressDialog.show(Levels.this, "", "Loading. Please wait...", true);
            	
            	singlePlayerGame = new Intent(Levels.this, SinglePlayer.class);
            	singlePlayerGame.putExtra("activePlayer", activePlayer);
            	singlePlayerGame.putExtra("level", position+1);
            	startActivityForResult(singlePlayerGame, SINGLEPLAYER_RESULT);
            }
        });
        
        backButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	onBackPressed();
            } 
        });
	} 
	
	@Override
	public void onBackPressed() {
    	dbAdapter.close();
		finish();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        
        if (dialog != null)
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
	        	if (winSound != null) {
	        		winSound.release();
	        		winSound = null;
	        	}
	        	
	        	Toast.makeText(getApplicationContext() , "YOU WIN!", Toast.LENGTH_LONG).show();
	        	Intent win = new Intent(Levels.this, Score.class);
	        	win.putExtra("score", score);
	        	win.putExtra("activePlayer", activePlayer);
	        	startActivity(win);
	        	break;
	        case 11:
	        	Intent timeOver = new Intent(Levels.this, Score.class);
	        	timeOver.putExtra("score", score);
	        	timeOver.putExtra("activePlayer", activePlayer);
	        	startActivity(timeOver);
	        	break;
	        case 12:
	        	MediaPlayer fallSound = MediaPlayer.create(getApplicationContext(), R.raw.explosion);
	        	fallSound.start();
	        	if (fallSound != null) {
	        		fallSound.release();
	        		fallSound = null;
	        	}
	        	
	        	Toast.makeText(getApplicationContext() , "YOU FELL!!!!!", Toast.LENGTH_LONG).show();
	        	Intent fell = new Intent(Levels.this, Score.class);
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