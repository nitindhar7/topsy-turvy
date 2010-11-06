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
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class SinglePlayer extends Activity
{    
	private ClearGLSurfaceView mGLView;
	private GestureDetector gestureDetector;
	private View.OnTouchListener gestureListener;
	private Vibrator vibrator;
	private Display display;
	
	private PhysicsWorld pWorld;
	private Polygon pTable;
	private Circle pTop;

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
        
        pTable = new Polygon();
        pTable.setPosition(display.getWidth()/8, display.getHeight()/8, display);
        pTable.setDimensions(6*display.getWidth()/8, 6*display.getWidth()/8, display);
        pTable.setDensity(1);
        pTable.setFriction(0.5f);
        pTable.setRestitution(0.5f);
        pTable.setBody(pWorld.createBody(pTable.getBodyDef()));
        pTable.setShape(pTable.getShape());
        pTable.setMassFromShapes();
        
        pTop = new Circle();
        pTop.setPosition(display.getWidth()/2, display.getHeight()*6/8, display);
        pTop.setRadius(display.getWidth()/8);
        pTop.setDensity(1);
        pTop.setFriction(0.5f);
        pTop.setRestitution(0.5f);
        pTop.setBody(pWorld.createBody(pTop.getBodyDef()));
        pTop.setShape(pTop.getShape());
        pTop.setMassFromShapes();
        
        
        
        // Create GLSurfaceView with sensors
		mGLView = new ClearGLSurfaceView(this, (SensorManager) getSystemService(SENSOR_SERVICE), display, pWorld);

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

        
        pTable.setUserData(mGLView.renderer.getTable());
        pTable.getUserData().setPosition(display.getWidth()/2, display.getHeight()/2, display);
        pTop.setUserData(mGLView.renderer.getTop());
        pTop.getUserData().setPosition(display.getWidth()/2, display.getHeight()*6/8, display);
        
        setContentView(mGLView);
	}
	
	
	class MyGestureDetector extends SimpleOnGestureListener implements GestureDetector.OnDoubleTapListener
	{
		private ClearGLSurfaceView mGLView;
		private Vec2 ballPosition;
		
		public MyGestureDetector(ClearGLSurfaceView cglsv)
		{
			this.mGLView = cglsv;
		}
		
	    @Override
	    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
	    {
	    	//ballPosition = mGLView.renderer.mWorld.getBodyList().getWorldCenter();
	    	mGLView.renderer.flingReleased = true;
	    	vibrator.vibrate(100);
	    	
	    	/*if ((e2.getX() <= ballPosition.x+2 || e2.getX() >= ballPosition.x-2) && (e2.getY() <= ballPosition.y+2 || e2.getY() >= ballPosition.y-2)) {
		    	//mGLView.renderer.mWorld.setVelocity(new Vec2(velocityX/50, -velocityY/50));
		    	//mGLView.renderer.mWorld.setBallImpulse(new Vec2(1f, 1f), ballPosition);
	    	}*/

	        return false;
	    }
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