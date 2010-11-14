/*
 * Copyright (C) 2010 Topsy-Turvy
 *
 * Authors:		Nitin Dhar (nitindhar7@yahoo.com)
 * 				Mayank Jain (mjain01@students.poly.edu)
 * 				Chintan Jain (cjain01@students.poly.edu)
 * 
 * Date: 		10/20/2010
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"), to deal in 
 * the Software without restriction, including without limitation the rights to 
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of 
 * the Software, and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS 
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.topsyturvy;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SwitchProfile extends ListActivity {
	
	// Create database instance
	private TopsyTurvyDbAdapter dbAdapter;
	private final int PLAYER_INFO_RESULT = 0;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.switch_profile);
        
        // Create and open db
        dbAdapter = new TopsyTurvyDbAdapter(this);
        dbAdapter.open();
    
    	populateList();
    	
    	// Remove divider under each list item
    	getListView().setDivider(null);
    	getListView().setDividerHeight(0);

        // Register menu, used for long press delete
        registerForContextMenu(getListView());
	}
	
	@Override
	public void onBackPressed() {
		dbAdapter.close();
		finish();
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO: switch player (in game table)
        // TODO: jump back to settings menu
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
        // TODO: set title to name of player clicked
        
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
	    //selectedWordId = info.id;
        
        menu.setHeaderIcon(R.drawable.ic_menu_player_info);
        menu.setHeaderTitle(((TextView) info.targetView).getText());
        menu.add(0, Menu.FIRST, 0, R.string.menu_info);
        menu.add(0, Menu.FIRST + 1, 0, R.string.menu_edit);
        menu.add(0, Menu.FIRST + 2, 0, R.string.menu_delete);
        menu.add(0, Menu.FIRST + 3, 0, R.string.menu_reset);
    }
	
	/**
	 * Create menu when list item long pressed
	 * Opens a box to remove player.
	 * 
	 * @param item Menu item that was pressed
	 * @return True if player deleted and list repopulated; false otherwise 
	 */
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	String playerName = ((TextView) info.targetView).getText().toString();
    	
        switch(item.getItemId()) {
            case Menu.FIRST:
            	Intent playerInfo = new Intent(SwitchProfile.this, PlayerInfo.class);
            	playerInfo.putExtra("playerName", playerName);
	        	startActivityForResult(playerInfo, PLAYER_INFO_RESULT);
                return true;
            case Menu.FIRST + 1:        
            	Intent editProfile = new Intent(SwitchProfile.this, EditProfile.class);
				startActivity(editProfile);
                return true;
            case Menu.FIRST + 2:
                dbAdapter.delete("player", playerName);
                populateList();
                return true;
            case Menu.FIRST + 3:        
                // TODO: reset scores for player
                return true;
        }
        
        return super.onContextItemSelected(item);
    }
	
	/**
	 * Retreive all player names, put into array, insert each element into textview
	 * and populate listview with textviews
	 */
	private void populateList() {
		// Get all of the notes from the database and create the item list
        Cursor c = dbAdapter.findAll("player");
        startManagingCursor(c);
        
        String[] from = new String[] { TopsyTurvyDbAdapter.KEY_NAME };
        int[] to = new int[] { R.id.playerName };
        
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter players = new SimpleCursorAdapter(this, R.layout.users_row, c, from, to);
        setListAdapter(players);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.layout.switch_profile_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.newProfile:
	        	Intent addProfile = new Intent(SwitchProfile.this, AddProfile.class);
				startActivity(addProfile);
	        	break;
	        case R.id.resetProfiles:
	        	// TODO: reset all profiles
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
}