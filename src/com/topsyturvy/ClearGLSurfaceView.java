package com.topsyturvy;

import org.jbox2d.common.Vec2;

import android.content.Context;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;

class ClearGLSurfaceView extends GLSurfaceView
{
	public ClearRenderer renderer;
	private Display display;

	public ClearGLSurfaceView(Context context, SensorManager sensorMgr, Display display, PhysicsWorld mWorld)
	{
		super(context);

		setDisplay(display);
		renderer = new ClearRenderer(context, sensorMgr, display, mWorld);
		setRenderer(renderer);
	}

	public boolean onTouchEvent(final MotionEvent event)
	{
		Vec2 physCoords = toPhysicsCoords(event.getX(), event.getY(), display);

		Log.i("TOPSYTURVY", "Screen:  " + Float.toString(event.getX()) + " | " + Float.toString(event.getY()));
		Log.i("TOPSYTURVY", "Physics: " + Float.toString(physCoords.x) + " | " + Float.toString(physCoords.y));
		
		queueEvent(new Runnable() {
			public void run()
			{
				renderer.touchEvent(event.getX(), event.getY(), event.getAction());
			}
		});

		return true;
	}
	
	public void setDisplay(Display display) {
		this.display = display;
	}
	
	public Vec2 toPhysicsCoords(float gestureX, float gestureY, Display display)
	{
		return new Vec2((20*gestureX)/display.getWidth() - 10, 20 - ((40 * gestureY)/display.getHeight()));
	}
}