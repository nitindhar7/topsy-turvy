package com.topsyturvy;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class PlayerInfo extends Activity {
	
	// Create database instance
	private TopsyTurvyDbAdapter dbAdapter;
	
	private TextView playerName;
	private TextView playerTopScore;
	private TextView playerGamesPlayed;
	private TextView playerAverageScore;
	private TextView playerSoundPref;
	private TextView playerVibrationPref;
	
	private String selectedPlayerName;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.player_info);
        
        playerName = (TextView)findViewById(R.id.playerInfoName);
    	playerTopScore = (TextView)findViewById(R.id.topScoreValue);
    	playerGamesPlayed = (TextView)findViewById(R.id.gamesPlayedValue);
    	playerAverageScore = (TextView)findViewById(R.id.averageScoreValue);
    	playerSoundPref = (TextView)findViewById(R.id.soundValue);
    	playerVibrationPref = (TextView)findViewById(R.id.vibrationValue);
    	
    	selectedPlayerName = getIntent().getStringExtra("playerName");
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		
		Cursor cursor;
    	
        dbAdapter = new TopsyTurvyDbAdapter(this);
        dbAdapter.open();
        
        cursor = dbAdapter.find_by_name(selectedPlayerName);
        
        if (cursor != null) {
        	playerName.setText(cursor.getString(1));
        	playerTopScore.setText(Float.toString(cursor.getInt(2)));
        	playerGamesPlayed.setText(Float.toString(cursor.getInt(3)));
        	playerAverageScore.setText(Float.toString(cursor.getInt(4)));
        }
        else;
        
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		setResult(0);
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.layout.player_info_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Cursor cursor;
		
	    switch (item.getItemId()) {
	        case R.id.editProfile:
	        	Intent editProfile = new Intent(PlayerInfo.this, EditProfile.class);
				startActivity(editProfile);
	        	break;
	        case R.id.resetProfile:
	        	dbAdapter.update("player", selectedPlayerName, 1, 1, 0, -1, null, 0, 0, 0);
	        	cursor = dbAdapter.find_by_name(selectedPlayerName);
	            
	            if (cursor != null) {
	            	playerName.setText(cursor.getString(1));
	            	playerTopScore.setText(Float.toString(cursor.getInt(2)));
	            	playerGamesPlayed.setText(Float.toString(cursor.getInt(3)));
	            	playerAverageScore.setText(Float.toString(cursor.getInt(4)));
	            	Toast.makeText(this , "Profile Resetted!", Toast.LENGTH_LONG).show();
	            }
	        	break;
	        case R.id.deleteProfile:
	        	dbAdapter.delete("player", selectedPlayerName);
	        	finish();
	        	break;
	    }
	    return true;
	}
}