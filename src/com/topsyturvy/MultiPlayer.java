package com.topsyturvy;

import org.jbox2d.common.Vec2;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MultiPlayer extends Activity {

	private final int SCOREBOARD_RESULT = 0;
	
	// Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
	
	private MultiPlayerGLSurfaceView mGLView;
	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	private Vibrator vibrator;
	private Display display;
	private String activePlayer;
	private String role;
	private String address = null;
	
	// Create database instance
	public TopsyTurvyDbAdapter dbAdapter;
	public AlertDialog.Builder builder;
	public AlertDialog alert;
	
	// Local Bluetooth adapter
    private BluetoothAdapter bBluetoothAdapter = null;
    private BluetoothService mService = null;
    private BluetoothDevice device;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		activePlayer	= getIntent().getStringExtra("activePlayer");
		role			= getIntent().getStringExtra("role");
		address			= getIntent().getStringExtra("address");
		
		dbAdapter		= new TopsyTurvyDbAdapter(this);
		dbAdapter.open();
		
		bBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    
	    // If the adapter is null, then Bluetooth is not supported
        if (bBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }
        
        // Get the BLuetoothDevice object
        if (role.equals("host"))
        	device = bBluetoothAdapter.getRemoteDevice(address);
		
		// Get services
		vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        // Create GLSurfaceView with sensors
		mGLView = new MultiPlayerGLSurfaceView(this, vibrator, (SensorManager) getSystemService(SENSOR_SERVICE), display, this);

		// Create gesture detector
		gestureDetector = new GestureDetector(new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };
        
        // Create gesture listener
        mGLView.setOnTouchListener(gestureListener);
        setContentView(mGLView);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	    
	    if (mService == null)
			setupService();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		//dbAdapter.close();
		mGLView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLView.onResume();
	}
	
	@Override
	public void onBackPressed() {
		dbAdapter.close(); 
		setResult(13);
		finish();
	}
	
	class MyGestureDetector extends SimpleOnGestureListener {
	    @Override
	    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
	    	mGLView.renderer.pTopBody1.setAngularVelocity(velocityY/270);
	    	mGLView.renderer.pTopBody1.allowSleeping(false);

	    	vibrator.vibrate(25);
	    	mService.write("hello".getBytes());

	        return false;
	    }
	    
	    public Vec2 toPhysicsCoords(float gestureX, float gestureY, Display display) {
			return new Vec2((20*gestureX)/display.getWidth() - 10, 20 - ((40 * gestureY)/display.getHeight()));
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.layout.single_player, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.quit:
	        	dbAdapter.close();
	        	finish();
	        	break;
	        case R.id.restart:
				mGLView.renderer.pTopBody1.setXForm(new Vec2(-7,15), 0);
				mGLView.renderer.pTopBody2.setXForm(new Vec2(7,-15), 0);
				mGLView.renderer.pTopBody1.putToSleep();
				mGLView.renderer.pTopBody2.putToSleep();
	        	break;
	    }
	    return true;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
                      
        switch (resultCode) {
	        case SCOREBOARD_RESULT:
	        	finish();
	        	break;
        }
    }
	
	private void setupService() {
		if (role.equals("host"))
			mService = new BluetoothService(this, mHandler);

		// Attempt to connect to the device
		if (role.equals("host"))
	        mService.connect(device);
	}
	
	private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                switch (msg.arg1) {
                case BluetoothService.STATE_CONNECTED:
                case BluetoothService.STATE_CONNECTING:
                case BluetoothService.STATE_LISTEN:
                case BluetoothService.STATE_NONE:
                }
                break;
            case MESSAGE_WRITE:
                break;
            case MESSAGE_READ:
            	byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                Toast.makeText(getApplicationContext(), readMessage, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_DEVICE_NAME:
                break;
            case MESSAGE_TOAST:
                break;
            }
        }
    };
	
	public void doVibrate(int seconds) {
		vibrator.vibrate(seconds);
	}
	
	public void doVibrate(int seconds, long[] pattern) {
		vibrator.vibrate(pattern, seconds);
	}
	
	public int getScore() {
		return 1;
	}
	
	public TopsyTurvyDbAdapter getTopsyTurvyDbAdapter() {
		return dbAdapter;
	}
	
	public String getActivePlayer() {
		return activePlayer;
	}
	
	/**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendData(Object data[]) {

        if (data.length > 0) {
            //mService.write(send);
        }
    }
}