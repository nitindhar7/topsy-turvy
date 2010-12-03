package com.topsyturvy;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewPlayer extends Activity implements OnClickListener {
	
	// UI
	private EditText playerName;
	private TextView savePlayer;
	private String enteredPlayerName;
	
	// DB
	private TopsyTurvyDbAdapter dbAdapter;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.new_player);
        
        // DB
        dbAdapter = new TopsyTurvyDbAdapter(this);
        dbAdapter.open();
        
        // UI
        playerName	= (EditText)findViewById(R.id.newPlayerName);
        savePlayer	= (TextView)findViewById(R.id.newPlayerSaveButton);
        playerName.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        
        // Listeners
        savePlayer.setOnClickListener(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (dbAdapter.state == 0)
			dbAdapter.open();
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
	
	public void onClick(View src) {
		switch(src.getId()) {
			case R.id.newPlayerSaveButton:
				enteredPlayerName = playerName.getText().toString();

	    		if (enteredPlayerName.length() == 0)
	    			Toast.makeText(NewPlayer.this, "Player Not Created", 5).show();
	    		else {
	    			newPlayer(enteredPlayerName);
	    			dbAdapter.close();
	    			setResult(10);
	    			finish();
	    		}
	    		break;
		}
	}
	
	private void newPlayer(String enteredPlayerName) {
		int playerId;
		Cursor cursor;
		
		playerId = dbAdapter.create(TopsyTurvyDbAdapter.DATABASE_PLAYERS_TABLE, enteredPlayerName);
		
		if (playerId != -1) {
			cursor = dbAdapter.find(TopsyTurvyDbAdapter.DATABASE_SESSIONS_TABLE, null);
			
			if (cursor != null && cursor.getCount() == 0)
				dbAdapter.create(TopsyTurvyDbAdapter.DATABASE_SESSIONS_TABLE, enteredPlayerName);
			else if (cursor != null && cursor.getCount() > 0)
				dbAdapter.update(cursor.getString(1), enteredPlayerName);
			
			Toast.makeText(NewPlayer.this, "Player Created", Toast.LENGTH_LONG).show();
		}
		else
			Toast.makeText(NewPlayer.this, "Player Not Created", Toast.LENGTH_LONG).show();
	}
}