package com.topsyturvy;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddProfile extends Activity implements OnClickListener {
	
	// UI elements
	private EditText enterUser;
	private Button saveUser;
	
	// Create database instance
	private TopsyTurvyDbAdapter dbAdapter;
	
	// Store player name
	private String enteredPlayerName;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.add_profile);
        
        // Create and open db
        dbAdapter = new TopsyTurvyDbAdapter(this);
        
        // Retrieve UI elements
        enterUser	= (EditText)findViewById(R.id.enter_user);
        saveUser	= (Button)findViewById(R.id.save_user);
        
        // Define listeners
		saveUser.setOnClickListener(this);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		dbAdapter.open();
	}
	
	@Override
	public void onBackPressed() {
		dbAdapter.close();
		finish();
	}
	
	public void onClick(View src) {
		int playerId;
		Cursor cursor;
		
		switch(src.getId()) {
			case R.id.save_user:
				enteredPlayerName = enterUser.getText().toString();

	    		if (enteredPlayerName.length() == 0)
	    			Toast.makeText(AddProfile.this, "Player Not Created", 5).show();
	    		else {
	    			playerId = (int) dbAdapter.create("player", 0, 0, 0, 0, enteredPlayerName);

	    			cursor = dbAdapter.find("game");
	    			if (cursor != null)
	    				dbAdapter.delete("game", cursor.getInt(0));
		    		
	    			dbAdapter.create("game", 1, 1, 1, playerId, enteredPlayerName);
	    			dbAdapter.close();
	    			
	    			setResult(10);
	    			finish();
	    		}
	    		break;
		}
	}
}