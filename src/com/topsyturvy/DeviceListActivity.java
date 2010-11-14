package com.topsyturvy;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DeviceListActivity extends Activity {

    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Member fields
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> bAvailableDevicesArrayAdapter;
	private ListView bAvailableDevicesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.lobby);

        // Set result CANCELED incase the user backs out
        setResult(Activity.RESULT_CANCELED);

        bAvailableDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.lobby_list_item);
	    bAvailableDevicesListView = (ListView)findViewById(R.id.lobbyList);
	    bAvailableDevicesListView.setAdapter(bAvailableDevicesArrayAdapter);
	    bAvailableDevicesListView.setOnItemClickListener(new OnItemClickListener () {
	    	public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
	            
	    		// Cancel discovery because it's costly and we're about to connect
	            mBtAdapter.cancelDiscovery();

	            // Get the device MAC address, which is the last 17 chars in the View
	            String info = ((TextView) v).getText().toString();
	            String address = info.substring(info.length() - 17);

	            // Create the result Intent and include the MAC address
	            Intent intent = new Intent();
	            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

	            // Set result and finish this Activity
	            setResult(Activity.RESULT_OK, intent);
	            finish();
	        }
	    });

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        
        // perform device discovery
        doDiscovery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {

        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                	bAvailableDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            // When discovery is finished, change the Activity title
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                if (bAvailableDevicesArrayAdapter.getCount() == 0)
                    bAvailableDevicesArrayAdapter.add("No Players");
            }
        }
    };

}