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

import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Lobby extends Activity {

	public static final String TAG = "TOPSYTURVY";
	
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    
    // Local Bluetooth adapter
    private BluetoothAdapter bBluetoothAdapter = null;
    
    // Member object for the game services
    private BluetoothService mService = null;

    // List vars
	private ArrayAdapter<String> bAvailableDevicesArrayAdapter;
	private ListView bAvailableDevicesListView;
	Set<BluetoothDevice> pairedDevices;
	
	// Create database instance
	private TopsyTurvyDbAdapter dbAdapter;
	private AlertDialog.Builder builder;
	private String activePlayer;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    setContentView(R.layout.lobby);
	    
	    activePlayer	= getIntent().getStringExtra("activePlayer");
	    bBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    
	    // If the adapter is null, then Bluetooth is not supported
        if (bBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }
        
        bAvailableDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.lobby_list_item);
	    bAvailableDevicesListView = (ListView)findViewById(R.id.lobbyList);
	    bAvailableDevicesListView.setAdapter(bAvailableDevicesArrayAdapter);
	    bAvailableDevicesListView.setOnItemClickListener(new OnItemClickListener () {
	    	public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
	        	bBluetoothAdapter.cancelDiscovery();

	            // Get the device MAC address, which is the last 17 chars in the View
	            String address = ((TextView) v).getText().toString().substring(0, 17);
	            
	            // Get the BLuetoothDevice object
	            BluetoothDevice device = bBluetoothAdapter.getRemoteDevice(address);
	            
	            // Attempt to connect to the device
	            mService.connect(device);
	            if (mService.getState() == BluetoothService.STATE_CONNECTED) {
	            	Intent multiPlayerGame = new Intent(Lobby.this, Lobby.class);
	            	multiPlayerGame.putExtra("activePlayer", activePlayer);
					startActivityForResult(multiPlayerGame, 0);
	            }
	        }
	    });
	    
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);
        
        pairedDevices = bBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
            	bAvailableDevicesArrayAdapter.add(device.getAddress() + "\n" + device.getName());
            }
        }
        
	}
	
	@Override
	public void onStart() {
		super.onStart();
    	Cursor cursor;
    	
        dbAdapter = new TopsyTurvyDbAdapter(this);
        dbAdapter.open();
        
        cursor = dbAdapter.find(TopsyTurvyDbAdapter.DATABASE_SESSIONS_TABLE, null);
		
		builder = new AlertDialog.Builder(this);
	    builder.setMessage("No Profile Selected\nCreate New Profile?")
	           .setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
						Intent addProfile = new Intent(Lobby.this, NewPlayer.class);
						startActivity(addProfile);
	               }
	           })
	           .setNegativeButton("No", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
					dbAdapter.close();
					finish();
	               }
	           });
	    AlertDialog alert = builder.create();
		
		if (cursor == null || cursor.getCount() == 0)
			alert.show();
		else {
			if (!bBluetoothAdapter.isEnabled()) {
				Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	        	startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			}
			else {
				if (mService == null)
					setupService();
			}
		}
	}
	
	@Override
	public synchronized void onResume() {
		super.onResume();
		
		Log.i(TAG, "onResume");
		
		if (mService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mService.getState() == BluetoothService.STATE_NONE) {
              // Start the Bluetooth chat services
              mService.start();
            }
        }
	}
	
	private void setupService() {
		
		Log.i(TAG, "setupService");

		mService = new BluetoothService(this, mHandler);
	}
	
	@Override
	public void onBackPressed() {
		if (mService != null)
			mService.stop();
		
		this.unregisterReceiver(mReceiver);
		bBluetoothAdapter.disable();
		finish();
	}
	
	private void ensureDiscoverable() {
		
		Log.i(TAG, "ensureDiscoverable");
		
		if (bBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }
	
	private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	
        	Log.i(TAG, "handleMessage");
        	
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                switch (msg.arg1) {
                case BluetoothService.STATE_CONNECTED:
                	bAvailableDevicesArrayAdapter.clear();
                	// TODO: start game;
                    break;
                case BluetoothService.STATE_CONNECTING:
                    break;
                case BluetoothService.STATE_LISTEN:
                	break;
                case BluetoothService.STATE_NONE:
                    break;
                }
                break;
            case MESSAGE_WRITE:
                break;
            case MESSAGE_READ:
                break;
            case MESSAGE_DEVICE_NAME:
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.layout.lobby_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.refresh:
	        	bAvailableDevicesArrayAdapter.clear();
	        	mService = null;
	        	setupService();
	        	onResume();
                ensureDiscoverable();
                bBluetoothAdapter.startDiscovery();
	            return true;
	        case R.id.stop:
	    		if (bBluetoothAdapter != null)
	    			bBluetoothAdapter.cancelDiscovery();

	    		return true;
	        case R.id.quit:
	        	if (mService != null)
	    			mService.stop();

	    		if (bBluetoothAdapter != null)
	    			bBluetoothAdapter.cancelDiscovery();
	    		
	    		this.unregisterReceiver(mReceiver);
	        	bBluetoothAdapter.disable();
	        	
	        	finish();
	        	return true;
	    }
	    return false;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		Log.i(TAG, "onActivityResult");
		
        switch (requestCode) {
	        case REQUEST_CONNECT_DEVICE:
	            if (resultCode == Activity.RESULT_OK) {
	                String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
	                BluetoothDevice device = bBluetoothAdapter.getRemoteDevice(address);
	                mService.connect(device);
	            }
	            break;
	        case REQUEST_ENABLE_BT:
	            if (resultCode == Activity.RESULT_OK) {
	                setupService();
	                ensureDiscoverable();
	                bBluetoothAdapter.startDiscovery();
	            }
	            else {
	                Toast.makeText(this, "User did not enable Bluetooth or an error occured", Toast.LENGTH_SHORT).show();
	                finish();
	            }
	            break;
        }
    }
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            	
            	Log.i(TAG, "onReceive ACTION_FOUND");
            	
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                	bAvailableDevicesArrayAdapter.add(device.getAddress() + "\n" + device.getName());
                }
            }
        }
    };
}