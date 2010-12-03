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

public class EditPlayer extends Activity implements OnClickListener {
	
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
        setContentView(R.layout.edit_player);
        
        // DB
        dbAdapter = new TopsyTurvyDbAdapter(this);
        dbAdapter.open();
        
        // UI
        playerName	= (EditText)findViewById(R.id.editPlayerName);
        savePlayer	= (TextView)findViewById(R.id.editPlayerSaveButton);
        enteredPlayerName = getIntent().getStringExtra("playerName");
        playerName.setText(enteredPlayerName);
        
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
	    			Toast.makeText(EditPlayer.this, "Player Not Editted", 5).show();
	    		else {
	    			editPlayer(enteredPlayerName);
	    			dbAdapter.close();
	    			setResult(10);
	    			finish();
	    		}
	    		break;
		}
	}
	
	private void editPlayer(String playerName) {
		int numRows = 0;
		Cursor cursor;

		cursor = dbAdapter.find(TopsyTurvyDbAdapter.DATABASE_PLAYERS_TABLE, "name = '" + playerName + "'");
		
		if (cursor != null && cursor.getCount() > 0)
			numRows = dbAdapter.update(cursor.getString(0), playerName, -1, -1, -1, -1, -1);
		
		if (numRows > 0)
			Toast.makeText(EditPlayer.this, "Player Editted", 5).show();
		else
			Toast.makeText(EditPlayer.this, "Player Not Editted", 5).show();
	}
}