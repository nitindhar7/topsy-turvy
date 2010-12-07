package com.topsyturvy;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Players extends Activity {
	
	// Create database instance
	private TopsyTurvyDbAdapter dbAdapter;
	private final int PLAYER_INFO_RESULT = 0;
	
	// UI
	private ListView playerScores;
	private List<ScoreBoard> scores;
	private ScoreBoardAdapter scoresListAdapter;
	private AlertDialog.Builder builder;
	private AlertDialog alert;
	private TextView playersNameLabel;
	private TextView playersTopScoreLabel;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.players);
        
        // Create and open db
        dbAdapter = new TopsyTurvyDbAdapter(this);
        dbAdapter.open();
    	
        // UI
        Typeface tf = Typeface.createFromAsset(getAssets(), "FORTE.TTF");
        playerScores = (ListView)findViewById(R.id.playersList);
        playersNameLabel = (TextView)findViewById(R.id.playersNameLabel);
        playersTopScoreLabel = (TextView)findViewById(R.id.playersTopScoreLabel);
        playersNameLabel.setTypeface(tf);
        playersTopScoreLabel.setTypeface(tf);
        
        scores = new ArrayList<ScoreBoard>();
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Reset all scores?")
               .setCancelable(false)
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   resetAllScores();
                	   populateList();
                   }
               })
               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                   }
               });
        alert = builder.create();

        // Register menu, used for long press delete
        registerForContextMenu(playerScores);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (dbAdapter.state == 0)
			dbAdapter.open();
		
		populateList();
		
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
		setResult(10);
		dbAdapter.close();
		finish();
	}
	
	/**
	 * Create menu for long press and keyboard menu button
	 * 
	 * @param menu Context menu that was registered for this activity
	 * @param v View attached to context menu
	 * @param menuInfo Details about each menu item
	 */
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        
        menu.setHeaderIcon(R.drawable.ic_menu_info_details);
        menu.setHeaderTitle("Options");
        menu.add(0, Menu.FIRST, 0, "Switch Profile");
        menu.add(0, Menu.FIRST + 1, 0, "Player Info");
        menu.add(0, Menu.FIRST + 2, 0, "Edit");
        menu.add(0, Menu.FIRST + 3, 0, "Reset");
        menu.add(0, Menu.FIRST + 4, 0, "Remove Player");
    }
	
	/**
	 * Create menu when list item long pressed
	 * 
	 * @param item Menu item that was pressed
	 * @return True if player deleted and list repopulated; false otherwise 
	 */
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	TextView playerNameView = (TextView)info.targetView.findViewById(R.id.playerListName);

        switch(item.getItemId()) {
            case Menu.FIRST:
            	switchPlayer(playerNameView.getText().toString());
                return true;
            case Menu.FIRST + 1:        
            	Intent viewPlayerInfo = new Intent(Players.this, PlayerInfo.class);
            	viewPlayerInfo.putExtra("playerName", playerNameView.getText().toString());
	        	startActivity(viewPlayerInfo);
                return true;
            case Menu.FIRST + 2:
            	Intent editPlayer = new Intent(Players.this, EditPlayer.class);
            	editPlayer.putExtra("playerName", playerNameView.getText().toString());
	        	startActivity(editPlayer);
                populateList();
                return true;
            case Menu.FIRST + 3:
            	resetPlayerScores(playerNameView.getText().toString());
            	populateList();
                return true;
            case Menu.FIRST + 4:
            	deletePlayer(playerNameView.getText().toString());
            	populateList();
            	return true;
        }
        
        return super.onContextItemSelected(item);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.layout.players_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.newPlayer:
	        	dbAdapter.close();
	        	Intent addProfile = new Intent(Players.this, NewPlayer.class);
				startActivity(addProfile);
	        	break;
	        case R.id.resetScores:
	        	alert.show();
	        	break;
	    }
	    return true;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
                      
        switch (resultCode) {
	        case PLAYER_INFO_RESULT:
	        	break;
        }
    }
	
	/**
	 * Retreive all player names, put into array, insert each element into textview
	 * and populate listview with textviews
	 */
	private void populateList() {
        Cursor cursor;
        
        cursor = dbAdapter.find(TopsyTurvyDbAdapter.DATABASE_PLAYERS_TABLE, null, "top_score DESC");
        scores.clear();
        
        if (cursor != null && cursor.getCount() > 0) {
	        do  {
	        	scores.add(new ScoreBoard(cursor.getString(0), cursor.getInt(1)));
			} while (cursor != null && cursor.moveToNext());
	        
	        scoresListAdapter = new ScoreBoardAdapter(this, scores);
			playerScores.setAdapter(scoresListAdapter);
        }
        else
        	Toast.makeText(getApplicationContext() , "No Players", Toast.LENGTH_LONG).show();
    }
	
	private void switchPlayer(String playerName) {
		Cursor pCursor, sCursor;
		
		pCursor = dbAdapter.find(TopsyTurvyDbAdapter.DATABASE_PLAYERS_TABLE, "name = '" + playerName + "'");
		sCursor = dbAdapter.find(TopsyTurvyDbAdapter.DATABASE_SESSIONS_TABLE, null);
		
		DatabaseUtils.dumpCursor(sCursor);
		DatabaseUtils.dumpCursor(pCursor);
		
		if (pCursor != null && pCursor.getCount() > 0 && sCursor != null && sCursor.getCount() > 0) {
			dbAdapter.update(sCursor.getString(1), pCursor.getString(0));
			Toast.makeText(getApplicationContext() , "Player Switched", Toast.LENGTH_LONG).show();
		}
		else if (pCursor != null && pCursor.getCount() > 0 && sCursor != null && sCursor.getCount() == 0) {
			dbAdapter.create(TopsyTurvyDbAdapter.DATABASE_SESSIONS_TABLE, pCursor.getString(0));
			Toast.makeText(getApplicationContext() , "Player Switched", Toast.LENGTH_LONG).show();
		}
		else
			Toast.makeText(getApplicationContext() , "Player Not Switch", Toast.LENGTH_LONG).show();	
	}
	
	private void resetPlayerScores(String playerName) {
		Cursor pCursor;
		int numRows = 0;
		
		pCursor = dbAdapter.find(TopsyTurvyDbAdapter.DATABASE_PLAYERS_TABLE, "name = '" + playerName + "'");
		
		if (pCursor != null && pCursor.getCount() > 0)
			numRows = dbAdapter.update(playerName, null, 0, 0, 0, -1, -1);
		
		if (numRows > 0)
			Toast.makeText(getApplicationContext() , "Reset Complete", Toast.LENGTH_LONG).show();
		else
			Toast.makeText(getApplicationContext() , "Reset Not Complete", Toast.LENGTH_LONG).show();
	}
	
	private void resetAllScores() {
		Cursor cursor;
		
		cursor = dbAdapter.find(TopsyTurvyDbAdapter.DATABASE_PLAYERS_TABLE, null);

		if (cursor != null && cursor.getCount() > 0) {
			while (!cursor.isAfterLast()) {
	        	dbAdapter.update(cursor.getString(0), null, 0, 0, 0, -1, -1);
	        	cursor.moveToNext();
			}
	        Toast.makeText(getApplicationContext() , "Reset Complete", Toast.LENGTH_LONG).show();
		}
		else
			Toast.makeText(getApplicationContext() , "Reset Not Complete", Toast.LENGTH_LONG).show();
	}

	private void deletePlayer(String playerName) {
		boolean deleted;
		Cursor sCursor;

		sCursor = dbAdapter.find(TopsyTurvyDbAdapter.DATABASE_SESSIONS_TABLE, null);
		deleted = dbAdapter.delete(TopsyTurvyDbAdapter.DATABASE_PLAYERS_TABLE, "name = '" + playerName + "'");

		if (deleted) {
			if (sCursor != null && sCursor.getCount() > 0)
				dbAdapter.delete(TopsyTurvyDbAdapter.DATABASE_SESSIONS_TABLE, "_id = " + Integer.toString(sCursor.getInt(0)));

			populateList();
			Toast.makeText(getApplicationContext() , "Player Removed", Toast.LENGTH_LONG).show();
		}
		else {
			Toast.makeText(getApplicationContext() , "Player Not Removed", Toast.LENGTH_LONG).show();
		}
	}
}