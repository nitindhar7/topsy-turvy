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
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class SwitchProfile extends ListActivity {
	
	// Create database instance
	private TopsyTurvyDbAdapter dbAdapter;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.switch_profile);
        
        // Create and open db
        dbAdapter = new TopsyTurvyDbAdapter(this);
        dbAdapter.open();
    
    	populateList();

        // Register menu, used for long press delete
        registerForContextMenu(getListView());
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO: switch user (in game table)
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
        menu.add(0, Menu.FIRST + 1, 0, R.string.menu_delete);
    }
	
	/**
	 * Create menu when list item long pressed
	 * Opens a box to remove user.
	 * 
	 * @param item Menu item that was pressed
	 * @return True if user deleted and list repopulated; false otherwise 
	 */
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case Menu.FIRST + 1:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                dbAdapter.delete("user", info.id);
                populateList();
                return true;
        }
        return super.onContextItemSelected(item);
    }
	
	/**
	 * Retreive all user names, put into array, insert each element into textview
	 * and populate listview with textviews
	 */
	private void populateList() {
		// Get all of the notes from the database and create the item list
        Cursor c = dbAdapter.findAll("user");
        startManagingCursor(c);

        String[] from = new String[] { TopsyTurvyDbAdapter.KEY_NAME };
        int[] to = new int[] { R.id.listuser };
        
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter users = new SimpleCursorAdapter(this, R.layout.users_row, c, from, to);
        setListAdapter(users);
    }
}