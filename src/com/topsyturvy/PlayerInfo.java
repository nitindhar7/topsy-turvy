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
	
	// DB
	private TopsyTurvyDbAdapter dbAdapter;
	
	// UI
	private TextView playerName;
	private TextView playerTopScore;
	private TextView playerAverageScore;
	private TextView playerGamesPlayed;
	private String selectedPlayerName;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.player_info);
        
        // DB
        dbAdapter = new TopsyTurvyDbAdapter(this);
        dbAdapter.open();
        
        // UI
        playerName = (TextView)findViewById(R.id.playerInfoName);
    	playerTopScore = (TextView)findViewById(R.id.playerInfoTopScore);
    	playerAverageScore = (TextView)findViewById(R.id.playerInfoAverage);
    	playerGamesPlayed = (TextView)findViewById(R.id.playerInfoGames);
    	selectedPlayerName = getIntent().getStringExtra("playerName");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (dbAdapter.state == 0)
			dbAdapter.open();
		populateFields();
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

	    switch (item.getItemId()) {
	        case R.id.editPlayer:
	        	Intent editPlayer = new Intent(PlayerInfo.this, EditPlayer.class);
            	editPlayer.putExtra("playerName", selectedPlayerName);
	        	startActivity(editPlayer);
	        	break;
	        case R.id.resetPlayer:
	        	resetPlayerScores(selectedPlayerName);
	        	break;
	        case R.id.deletePlayer:
	        	deletePlayer(selectedPlayerName);
	        	dbAdapter.close();
	        	finish();
	        	break;
	    }
	    return true;
	}
	
	private void populateFields() {
		Cursor cursor;
		int avg;

        cursor = dbAdapter.find(TopsyTurvyDbAdapter.DATABASE_PLAYERS_TABLE, "name = '" + selectedPlayerName + "'");

        if (cursor != null) {
        	avg = (cursor.getInt(3)/cursor.getInt(4));
        	
        	playerName.setText(cursor.getString(1));
        	playerTopScore.setText("Top Score: " + Integer.toString(cursor.getInt(2)));
        	playerAverageScore.setText("Average Score: " + Integer.toString(avg));
        	playerGamesPlayed.setText("Games Played: " + Integer.toString(cursor.getInt(4)));
        }
	}
	
	private void resetPlayerScores(String playerName) {
		Cursor pCursor;
		int numRows = 0;
		
		pCursor = dbAdapter.find(TopsyTurvyDbAdapter.DATABASE_PLAYERS_TABLE, "name = '" + playerName + "'");
		
		if (pCursor != null && pCursor.getCount() > 0)
			numRows = dbAdapter.update(pCursor.getString(0), null, 0, 0, 0, -1, -1);
		
		if (numRows > 0)
			Toast.makeText(getApplicationContext() , "Reset Complete", Toast.LENGTH_LONG).show();
	}
	
	private void deletePlayer(String playerName) {
		boolean deleted;
		
		deleted = dbAdapter.delete(TopsyTurvyDbAdapter.DATABASE_PLAYERS_TABLE, "name = '" + playerName + "'");
		
		if (deleted)
			Toast.makeText(getApplicationContext() , "Player Removed", Toast.LENGTH_LONG).show();
	}
}