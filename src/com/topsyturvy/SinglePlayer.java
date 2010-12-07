package com.topsyturvy;

import org.jbox2d.common.Vec2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.SensorManager;
import android.os.Bundle;
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
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.Toast;

public class SinglePlayer extends Activity {

	private final int SCOREBOARD_RESULT = 0;
	
	private TopsyTurvyGLSurfaceView mGLView;
	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	private Vibrator vibrator;
	private Display display;
	private Chronometer chrono;

	private String currentTime = "00.30";
	private int count = 1;
	private int level;
	private int score = 0;
	private String activePlayer;
	
	// Create database instance
	public TopsyTurvyDbAdapter dbAdapter;
	public AlertDialog.Builder builder;
	public AlertDialog alert;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		activePlayer	= getIntent().getStringExtra("activePlayer");
		level			= getIntent().getIntExtra("level", 1);
		dbAdapter		= new TopsyTurvyDbAdapter(this);
		dbAdapter.open();
		
		// Get services
		vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        // Create GLSurfaceView with sensors
		mGLView = new TopsyTurvyGLSurfaceView(this, vibrator, (SensorManager) getSystemService(SENSOR_SERVICE), display, this, level);

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
        
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.game_timer);
        chrono = (Chronometer)findViewById(R.id.chrono);
        chrono.setText(currentTime);
        chrono.setOnChronometerTickListener(new OnChronometerTickListener() {
        	public void onChronometerTick(Chronometer arg0) {
				if (count <= 30) {
					long minutes = 0 ;
					long seconds = 30 - count;
					currentTime = minutes + ":" + seconds;
					arg0.setText(currentTime);
					count++;
					score += 1;
				}
				else {
					arg0.setText("0:30");
					chrono.stop();
					mGLView.renderer.pTopBody.putToSleep();
					Toast.makeText(getApplicationContext() , "TIME OVER!!!!!", Toast.LENGTH_LONG).show();
					alert.show();
				}
        	}
        });
	}
	
	@Override
	protected void onStart() {
		super.onStart();

		builder = new AlertDialog.Builder(this);
	    builder.setMessage("Restart?")
	           .setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
						count = 1;
						score = 0;
						currentTime = "00.30";
						chrono.setText(currentTime);
						chrono.stop();
						mGLView.renderer.pTopBody.setXForm(new Vec2(-7,15), 0);
						mGLView.renderer.pTopBody.putToSleep();
						dialog.cancel();
	               }
	           })
	           .setNegativeButton("No", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            	   	Cursor cursor;
		           		int topScore;
		           		int gamesPlayed;
		           		int totalScore;
	            	   
		           		cursor = dbAdapter.find(TopsyTurvyDbAdapter.DATABASE_PLAYERS_TABLE, "name = '" + activePlayer + "'");
						if (cursor != null && cursor.getCount() > 0) {
							topScore = (cursor.getInt(1) < score) ? score : cursor.getInt(1);
							gamesPlayed = cursor.getInt(3) + 1;
							totalScore = cursor.getInt(2) + score;

							dbAdapter.update(cursor.getString(0), null, topScore, totalScore, gamesPlayed, -1, -1);
						}
						
						Intent i = new Intent();
						i.putExtra("score", score);
						setResult(11, i);
						finish();
	               }
	           });
	    alert = builder.create();
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
	    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
	    {
	    	mGLView.renderer.pTopBody.setAngularVelocity(velocityY/270);
	    	mGLView.renderer.pTopBody.allowSleeping(false);
	    	chrono.start();

	    	vibrator.vibrate(25);

	        return false;
	    }
	    
	    public Vec2 toPhysicsCoords(float gestureX, float gestureY, Display display)
		{
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
	        	count = 1;
				score = 0;
				currentTime = "00.30";
				chrono.setText(currentTime);
				chrono.stop();
				mGLView.renderer.pTopBody.setXForm(new Vec2(-7,15), 0);
				mGLView.renderer.pTopBody.putToSleep();
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
	
	public void doVibrate(int seconds) {
		vibrator.vibrate(seconds);
	}
	
	public void doVibrate(int seconds, long[] pattern) {
		vibrator.vibrate(pattern, seconds);
	}
	
	public int getScore() {
		return this.score + (30-count)*2;
	}
	
	public TopsyTurvyDbAdapter getTopsyTurvyDbAdapter() {
		return dbAdapter;
	}
	
	public String getActivePlayer() {
		return activePlayer;
	}
}