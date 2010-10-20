package com.topsyturvy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class TopsyTurvy extends Activity implements OnClickListener {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        // Retrieve UI elements
        Button mainMenuSinglePlayerButton 	= (Button)findViewById(R.id.mainMenuSinglePlayer);
        Button mainMenuMultiPlayerButton 	= (Button)findViewById(R.id.mainMenuMultiPlayer);
		Button mainMenuSettingsButton 		= (Button)findViewById(R.id.mainMenuSettings);
		
		// Define listeners
		mainMenuSinglePlayerButton.setOnClickListener(this);
		mainMenuMultiPlayerButton.setOnClickListener(this);
		mainMenuSettingsButton.setOnClickListener(this);
    }

    public void onClick(View src) {
		switch(src.getId()) {
			case R.id.mainMenuSinglePlayer:
				Intent singlePlayerGame = new Intent(TopsyTurvy.this, SinglePlayer.class);
	        	startActivity(singlePlayerGame);
				break;
			case R.id.mainMenuMultiPlayer:
				Intent multiPlayerGame = new Intent(TopsyTurvy.this, MultiPlayer.class);
	        	startActivity(multiPlayerGame);
				break;
			case R.id.mainMenuSettings:
				Intent settings = new Intent(TopsyTurvy.this, Settings.class);
	        	startActivity(settings);
				break;
		}
	}
}