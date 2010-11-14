package com.topsyturvy;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
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
	private String playerName;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.add_profile);
        
        // Create and open db
        dbAdapter = new TopsyTurvyDbAdapter(this);
        dbAdapter.open();
        
        // Retrieve UI elements
        enterUser	= (EditText)findViewById(R.id.enter_user);
        saveUser	= (Button)findViewById(R.id.save_user);
        
        // Define listeners
		saveUser.setOnClickListener(this);
	}
	
	public void onClick(View src) {
		long playerId;
		int playerCount;
		
		switch(src.getId()) {
			case R.id.save_user:
				playerName = enterUser.getText().toString();

	    		if (playerName.length() == 0)
	    			Toast.makeText(AddProfile.this, "Player Not Created", 5).show();
	    		else {
	    			playerId = dbAdapter.create("player", 0, 0, 0, 0, playerName);
	    			playerCount = dbAdapter.count("player");
	    			
	    			if (playerId != -1 && playerCount > 1)
		    			dbAdapter.update("game", 1, 1, 1, 0, (int) playerId, null, 1, 1, 1);
	    			else if (playerId != -1 && playerCount == 1) {
	    				dbAdapter.create("game", 1, 1, 0, (int) playerId, null);
	    			}
		    		
	    			dbAdapter.close();
	    			setResult(0);
	    			finish();
	    		}
	    		break;
		}
	}
	
	@Override
	public void onBackPressed() {
		dbAdapter.close();
		finish();
	}
}