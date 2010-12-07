package com.topsyturvy;

import org.jbox2d.common.Vec2;

import android.content.Context;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Vibrator;
import android.view.Display;
import android.view.MotionEvent;

class MultiPlayerGLSurfaceView extends GLSurfaceView {

	public MultiPlayerRenderer renderer;

	public MultiPlayerGLSurfaceView(Context context, Vibrator vibrator, SensorManager sensorMgr, Display display, MultiPlayer currentMPGame) {
		super(context);

		renderer = new MultiPlayerRenderer(context, vibrator, sensorMgr, display, currentMPGame);
		setRenderer(renderer);
	}

	public boolean onTouchEvent(final MotionEvent event) {
        renderer.setSize(this.getWidth(),this.getHeight());

		queueEvent(new Runnable() {
			public void run() {
				renderer.touchEvent(event.getX(), event.getY(), event.getAction());
			}
		});

		return true;
	}
	
	public Vec2 toPhysicsCoords(float gestureX, float gestureY, Display display) {
		return new Vec2((20*gestureX)/display.getWidth() - 10, 20 - ((40 * gestureY)/display.getHeight()));
	}
}