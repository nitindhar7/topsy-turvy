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

import org.jbox2d.common.Vec2;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
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

public class SinglePlayer extends Activity
{    
	private TopsyTurvyGLSurfaceView mGLView;
	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	private Vibrator vibrator;
	private Display display;
	
	private PhysicsWorld pWorld;
	private Circle pTop;
	private Polygon[] pFences;
	private Polygon pFence;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// Get services
		vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        
        // Start physics
		pWorld = new PhysicsWorld();
		
        pTop = new Circle();
        pTop.setPosition(display.getWidth()/2, display.getHeight()*7/8, display);
        pTop.getBodyDef().massData.mass = 2;
        pTop.setBody(pWorld.createBody(pTop.getBodyDef()));
        pTop.setRadius(display.getWidth()/8);
        pTop.setDensity(1);
        pTop.setFriction(0.5f);
        pTop.setRestitution(0.2f);
        pTop.setShape(pTop.getShape());

        /*pFence = new Polygon();
		pFence.setPosition(display.getWidth()/2, 0, display);
		pFence.getBodyDef().massData.mass = 0;
		pFence.setBody(pWorld.createBody(pFence.getBodyDef()));
		pFence.setAsBox(3.75f,1f);
		pFence.setDensity(1.0f);
		pFence.setFriction(.5f);
		pFence.setRestitution(.1f);
		pFence.setShape(pFence.getShape());
        
        int j=110,k=25,l=40;
        int arr[][]= new int[][]{
    		   {j,j},{j,j+l},{j,j+l*2},{j,j+l*3},{j,j+l*4},{j,j+l*5},{j,j+l*6},
    		   {j+k,j+l*6+19},{j+l+k,j+l*6+19},
    		   {2*j,j},{2*j,j+l},{2*j,j+l*2},{2*j,j+l*3},{2*j,j+l*4},{2*j,j+l*5},{2*j,j+l*6}};
        
        pFences = new Polygon[arr.length];
		for(int i=0;i<arr.length;i++)
		{
	        pFences[i] = new Polygon();
	        pFences[i].setPosition(arr[i][0],arr[i][1] , display);
	        if(i==7 || i==8)
	        {
		        pFences[i].setAsBox(2f,1f);	        	
	        }
	        else 
	        {
		        pFences[i].setAsBox(1f,2f);
	        }
	        pFences[i].setDensity(1.0f );
	        pFences[i].setFriction(.5f);
	        pFences[i].setRestitution(.5f);
	    	pFences[i].setBody(pWorld.createBody(pFences[i].getBodyDef()));
	    	pFences[i].setShape(pFences[i].getShape());
	    	if(i==7 || i==8)
	    		pFences[i].setUserData("fence2");
	    	else 
	    		pFences[i].setUserData("fence1");	    	
		}*/

        // Create GLSurfaceView with sensors
		mGLView = new TopsyTurvyGLSurfaceView(this, (SensorManager) getSystemService(SENSOR_SERVICE), display, pWorld);

		// Create gesture detector
		gestureDetector = new GestureDetector(new MyGestureDetector(mGLView));
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
        
        pTop.setUserData(mGLView.renderer.getTop());
        pTop.getBody().putToSleep();

        setContentView(mGLView);
	}
	
	
	class MyGestureDetector extends SimpleOnGestureListener implements GestureDetector.OnDoubleTapListener
	{
		private TopsyTurvyGLSurfaceView mGLView;
		private Vec2 tempPos;
		
		public MyGestureDetector(TopsyTurvyGLSurfaceView cglsv)
		{
			this.mGLView = cglsv;
		}
		
	    @Override
	    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
	    {
	    	pTop.getBody().allowSleeping(false);
	    	pTop.getBody().setAngularVelocity(velocityY/270);

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
	        	pTop.getBody().putToSleep();
	        	pTop.setPosition(display.getWidth()/2, display.getHeight()*6/8, display);
	        	pTop.getBody().setAngularVelocity(0);
	        	break;
	    }
	    return true;
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		mGLView.onPause();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		mGLView.onResume();
	}
}