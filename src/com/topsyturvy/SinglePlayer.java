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

import org.jbox2d.collision.CircleDef;
import org.jbox2d.collision.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

public class SinglePlayer extends Activity
{
	private final int SCOREBOARD_RESULT = 0;
	
	private TopsyTurvyGLSurfaceView mGLView;
	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	private Vibrator vibrator;
	private Display display;
	private Chronometer chrono;
	
	private PhysicsWorld pWorld;
	private Body pTopBody;
	private Body[] pFenceBody;
	private BodyDef pTopBodyDef;
	private BodyDef[] pFenceBodyDef;
	private CircleDef pTopShape;
	private PolygonDef[] pFenceShape;

	private String currentTime = "01.00";
	private int count = 1;
	
	// Create database instance
	private TopsyTurvyDbAdapter dbAdapter;
	private AlertDialog.Builder builder;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		int j=110,k=25,l=40;
        int arr[][]= new int[][]{
    		   {j,j},{j,j+l},{j,j+l*2},{j,j+l*3},{j,j+l*4},{j,j+l*5},{j,j+l*6},
    		   {j+k,j+l*6+19},{j+l+k,j+l*6+19},
    		   {2*j,j},{2*j,j+l},{2*j,j+l*2},{2*j,j+l*3},{2*j,j+l*4},{2*j,j+l*5},{2*j,j+l*6}};

		// Get services
		vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        
        // Start physics
		pWorld = new PhysicsWorld();

        pTopBodyDef = new BodyDef();
        pTopShape = new CircleDef();
        pTopBodyDef.position.set(new Vec2(2,10));
        pTopBodyDef.massData.mass = 5;
        pTopShape.radius = 1;
        pTopShape.density = 1.0f;
        pTopBody = pWorld.createBody(pTopBodyDef);
        pTopBody.createShape(pTopShape);
        pTopBody.putToSleep();

        pFenceBody = new Body[arr.length];
        pFenceBodyDef = new BodyDef[arr.length];
        pFenceShape = new PolygonDef[arr.length];

		for(int i = 0; i < arr.length; i++) {
	        pFenceBodyDef[i] = new BodyDef();
	        pFenceShape[i] = new PolygonDef();
	        pFenceBodyDef[i].position.set(new Vec2(arr[i][0],arr[i][1]));
	        pFenceBodyDef[i].massData.mass = 0;
	        
	        if (i==7 || i==8)
	        	pFenceShape[i].setAsBox(2f,1f);
	        else
	        	pFenceShape[i].setAsBox(1f,2f);
	        
	        pFenceShape[i].density = 1.0f;
	        pFenceShape[i].friction = .5f;
	        pFenceShape[i].restitution = .5f;
	        pFenceBody[i] = pWorld.createBody(pFenceBodyDef[i]);
	        pFenceBody[i].createShape(pFenceShape[i]);
	    	if(i==7 || i==8)
	    		pFenceBody[i].setUserData("fence2");
	    	else 
	    		pFenceBody[i].setUserData("fence1");	    	
		}

        // Create GLSurfaceView with sensors
		mGLView = new TopsyTurvyGLSurfaceView(this, (SensorManager) getSystemService(SENSOR_SERVICE), display, pWorld);

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
				if (count <= 60) {
					long minutes = 0 ;
					long seconds = 60 - count;
					currentTime=minutes + ":" + seconds;
					arg0.setText(currentTime);
					count++;
				}
				else {
					long minutes = 1;
					long seconds = 00;
					currentTime = minutes + ":" + seconds;
					arg0.setText(currentTime);
					chrono.stop();
					Toast.makeText(getApplicationContext() , "TIME OVER!!!!!", Toast.LENGTH_LONG).show();
					Intent ScoresIntent = new Intent(SinglePlayer.this, Scores.class);
					startActivityForResult(ScoresIntent, SCOREBOARD_RESULT);
				}
        	}
        });
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		
		String playerName;
    	int playerCount;
    	int activePlayerId;
    	
        dbAdapter = new TopsyTurvyDbAdapter(this);
        dbAdapter.open();
		playerCount = dbAdapter.count("player");
		
		builder = new AlertDialog.Builder(this);
	    builder.setMessage("No Profile Selected\nCreate New Profile?")
	           .setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
						Intent addProfile = new Intent(SinglePlayer.this, AddProfile.class);
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
		
		if (playerCount < 1)
			alert.show();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		dbAdapter.close();
		mGLView.onPause();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		mGLView.onResume();
	}
	
	@Override
	public void onBackPressed() {
		dbAdapter.close();
		finish();
	}
	
	class MyGestureDetector extends SimpleOnGestureListener
	{
	    @Override
	    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
	    {
	    	pTopBody.setAngularVelocity(velocityY/1000);
	    	pTopBody.allowSleeping(false);
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
	        	finish();
	        	break;
	        case R.id.restart:
	        	pWorld.setGravity(0, 0);
	        	pTopBody.putToSleep();
	        	pTopBodyDef.position.set(display.getWidth()/2, display.getHeight()*6/8);
	        	pTopBody.setAngularVelocity(0);
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
}