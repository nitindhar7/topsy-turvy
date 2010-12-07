package com.topsyturvy;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class Score extends Activity {
	
	// DB
	private TopsyTurvyDbAdapter dbAdapter;
	
	// UI
	private TextView playerScore;
	private TextView playerTopScore;
	private TextView playerAverageScore;
	private int score;
	private String activePlayer;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.score);

        // Create and open db
        dbAdapter = new TopsyTurvyDbAdapter(this);
        dbAdapter.open();
        
        //UI
        playerScore = (TextView)findViewById(R.id.playerScore);
        playerTopScore = (TextView)findViewById(R.id.playerTopScore);
        playerAverageScore = (TextView)findViewById(R.id.playerAverageScore);
        
        score = getIntent().getIntExtra("score", -1);
        activePlayer = getIntent().getStringExtra("activePlayer");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (dbAdapter.state == 0)
			dbAdapter.open();
		populateFields();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		dbAdapter.close();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		dbAdapter.close();
	}
	
	@Override
	public void onBackPressed() {
		dbAdapter.close();
		finish();
	}
	
	/**
	 * Retreive all user names, put into array, insert each element into textview
	 * and populate listview with textviews
	 */
	private void populateFields() {
		Cursor pCursor;
		int avg;

		
        pCursor = dbAdapter.find(TopsyTurvyDbAdapter.DATABASE_PLAYERS_TABLE, "name = '" + activePlayer + "'");
    	if (pCursor != null && pCursor.getCount() > 0) {
    		avg = (pCursor.getInt(2)/pCursor.getInt(3));

        	playerScore.setText(Integer.toString(score));
        	playerTopScore.setText("Top Score: " + Integer.toString(pCursor.getInt(1)));
        	playerAverageScore.setText("Average Score: " + Integer.toString(avg));
    	}
    }
}