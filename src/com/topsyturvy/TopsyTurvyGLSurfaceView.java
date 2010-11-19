package com.topsyturvy;

import org.jbox2d.common.Vec2;

import android.content.Context;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.view.Display;
import android.view.MotionEvent;

class TopsyTurvyGLSurfaceView extends GLSurfaceView
{
	public TopsyTurvyRenderer renderer;
	private Display display;

	public TopsyTurvyGLSurfaceView(Context context, SensorManager sensorMgr, Display display, SinglePlayer currentSPGame)
	{
		super(context);

		setDisplay(display);
		renderer = new TopsyTurvyRenderer(context, sensorMgr, display, currentSPGame);
		setRenderer(renderer);
	}

	public boolean onTouchEvent(final MotionEvent event)
	{
        renderer.setSize(this.getWidth(),this.getHeight());

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