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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

public class SinglePlayer extends Activity {
    
	private final String TAG = "TOPSY";
	private TextView xCoord;
	private TextView yCoord;
	private TextView velocityXTextView;
	private TextView velocityYTextView;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.single_player);
        
        xCoord				= (TextView)findViewById(R.id.xCoord);
        yCoord				= (TextView)findViewById(R.id.yCoord);
        velocityXTextView	= (TextView)findViewById(R.id.velocityX);
        velocityYTextView	= (TextView)findViewById(R.id.velocityY);
        
        FrameLayout frame	= (FrameLayout) findViewById(R.id.graphics_holder);
        PlayAreaView image	= new PlayAreaView(this);
        frame.addView(image);
	}

	private class PlayAreaView extends View {

        private GestureDetector gestures;
        private Matrix translate;
        private Bitmap droid;
        private PhysicsWorld mWorld;  
        private Handler mHandler;

        private Matrix animateStart;
        private Interpolator animateInterpolator;
        private long startTime;
        private long endTime;
        private float totalAnimDx;
        private float totalAnimDy;
        
        public PlayAreaView(Context context) {
            super(context);
            
            translate	= new Matrix();
            gestures	= new GestureDetector(SinglePlayer.this, new GestureListener(this));
            droid		= BitmapFactory.decodeResource(getResources(), R.drawable.top);
            
            mWorld = new PhysicsWorld();  
            mWorld.create();
            
            // Add a Ball
            mWorld.addBall();
            
            // Start Regular Update  
            mHandler = new Handler();  
            mHandler.post(update);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
        	xCoord.setText("X Coord: " + Float.toString(event.getX()));
        	yCoord.setText("Y Coord: " + Float.toString(event.getY()));

        	return gestures.onTouchEvent(event);
        }
        
        public void onAnimateMove(float dx, float dy, long duration) {
            animateStart		= new Matrix(translate);
            animateInterpolator	= new LinearInterpolator();
            startTime			= System.currentTimeMillis();
            endTime				= startTime + duration;
            totalAnimDx			= dx;
            totalAnimDy			= dy;

            post(new Runnable() {
                @Override
                public void run() {
                    onAnimateStep();
                }
            });
        }

        private void onAnimateStep() {
            long curTime			= System.currentTimeMillis();
            float percentTime		= (float) (curTime - startTime) / (float) (endTime - startTime);
            float percentDistance	= animateInterpolator.getInterpolation(percentTime);
            
            float curDx				= percentDistance * totalAnimDx;
            float curDy				= percentDistance * totalAnimDy;
            translate.set(animateStart);

            translate.setRotate(10);
            
            onMove(curDx, curDy);

            if (percentTime < 1.0f) {
                post(new Runnable() {
                    public void run() {
                        onAnimateStep();
                    }
                });
            }
        }

        public void onMove(float dx, float dy) {
            translate.postTranslate(dx, dy);
            invalidate();
        }

        public void onResetLocation() {
            translate.reset();
            invalidate();
        }

        public void onSetLocation(float dx, float dy) {
            translate.postTranslate(dx, dy);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(droid, translate, null);
        }
        
      
        private Runnable update = new Runnable() {  
            public void run() {  
                mWorld.update();  
                mHandler.postDelayed(update, (long) (mWorld.timeStep*1000));
            }  
        };
    }
	
    private class GestureListener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

        PlayAreaView view;

        public GestureListener(PlayAreaView view) {
            this.view = view;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, final float velocityX, final float velocityY) {
            final float distanceTimeFactor = 0.4f;
            final float totalDx = (distanceTimeFactor * velocityX / 2);
            final float totalDy = (distanceTimeFactor * velocityY / 2);
            
            view.mWorld.bodies[0].m_linearVelocity = new Vec2(velocityX, velocityY);
            
            velocityXTextView.setText("X velocity: " + Float.toString((int)velocityX));
            velocityYTextView.setText("Y velocity: " + Float.toString((int)velocityY));

            view.onAnimateMove(totalDx, totalDy, (long) (1000 * distanceTimeFactor));
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            view.onResetLocation();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //view.onMove(-distanceX, -distanceY);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }
        
        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return false;
        }
    }
}