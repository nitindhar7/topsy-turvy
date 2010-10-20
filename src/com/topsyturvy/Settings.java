package com.topsyturvy;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Settings extends ListActivity {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Retrieve settings given in "settingsList" string array
        String[] settingsList = getResources().getStringArray(R.array.settingsList);
        setListAdapter(new ArrayAdapter<String>(this, R.layout.settings, settingsList));

        // Retrieve list view to display on the screen
        // enable filtering the list items when user types text
        ListView settingsListView = getListView();
        settingsListView.setTextFilterEnabled(true);

        // When user clicks a list item, fire this
        settingsListView.setOnItemClickListener(new OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	  switch(position) {
        	  case 0:
        	  }
          }
        });
	}
}