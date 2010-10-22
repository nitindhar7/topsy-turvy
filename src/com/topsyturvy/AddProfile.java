package com.topsyturvy;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
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
	private String userName;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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
		switch(src.getId()) {
			case R.id.save_user:
				userName = enterUser.getText().toString();

	    		if (userName.length() == 0) {
	    			Toast toast = Toast.makeText(AddProfile.this, "User Not Created", 5);
	    			toast.show();
	    		}
	    		else {
	    			dbAdapter.create("user", 0, 0, 0, 0, userName);
	    			setResult(0);
	    			finish();
	    		}
	    		break;
		}
	}
}
