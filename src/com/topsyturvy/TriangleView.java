package com.topsyturvy;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.Log;

public class TriangleView extends View implements Renderer {
	
	private final String TAG = "TOPSY";
	private Triangle triangle;
	
	/** Angle For The Pyramid */
	private float rtri;

	public TriangleView() {
		triangle = new Triangle();
		Log.i(TAG, "In TriangleView constructor");
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		Log.i(TAG, "In TriangleView onDrawFrame");
		
		// Clear Screen And Depth Buffer
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		// Reset The Current Modelview Matrix
		gl.glLoadIdentity();

		/*
		 * Minor changes to the original tutorial
		 * 
		 * Instead of drawing our objects here, we fire their own drawing
		 * methods on the current instance
		 */
		
		// Move up 2.5 Units
		gl.glTranslatef(0.5f, 0.8f, -10.0f);

		// Rotate The Triangle On The Y axis ( NEW )
		gl.glRotatef(rtri, 0.0f, 0.0f, 1.0f);
		
		// Draw the triangle
		triangle.drawing(gl);

		// TODO: THIS IS WHERE THE VELOCITY OF THE FLING WILL GO
		// Increase The Rotation Variable For The Triangle ( NEW )
		rtri += 8.9f;
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.i(TAG, "In TriangleView onSurfaceChanged");
		
		// Prevent A Divide By Zero By making Height Equal One
		if (height == 0) height = 1;

		// Reset The Current Viewport
		gl.glViewport(0, 0, width, height);
		
		// Select The Projection Matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);
		
		// Reset The Projection Matrix
		gl.glLoadIdentity(); 

		// Calculate The Aspect Ratio Of The Window
		GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100.0f);

		// Select The Modelview Matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		
		// Reset The Modelview Matrix
		gl.glLoadIdentity(); 
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Log.i(TAG, "In TriangleView onSurfaceCreated");
		
		// Enable Smooth Shading
		gl.glShadeModel(GL10.GL_SMOOTH);
		
		// Black Background
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		
		// Depth Buffer Setup
		gl.glClearDepthf(1.0f);
		
		// Enables Depth Testing
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
		// The Type Of Depth Testing To Do
		gl.glDepthFunc(GL10.GL_LEQUAL);

		// Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

	}

}