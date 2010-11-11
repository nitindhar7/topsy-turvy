package com.topsyturvy;

import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.collision.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.view.Display;

class TopsyTurvyRenderer implements GLSurfaceView.Renderer {
	public PhysicsWorld pWorld;
	private Context mContext;
	private Display display;

	SensorEventListener mSensorEventListener;
	private List<Sensor> sensors;
	private Sensor accSensor;
	
	private int width;
	private int height;
	
	private DrawModel gBackground;
	private DrawModel gTable;
	private DrawModel gTop;
	public Vec2 tempVec;
	private Vec2 tableBoundary;
	private Vec2 tlVertex;
	public boolean flingReleased;
	Body tempBody;

	public TopsyTurvyRenderer(Context context, SensorManager sensorMgr, Display display, final PhysicsWorld pWorld)
	{
		setPhysicsWorld(pWorld);
		setContext(context);
		setDisplay(display);
		setSize(display.getWidth(), display.getHeight());

		tempVec = new Vec2(20*6/8, 40*6/8);
		
		gBackground	= new DrawModel(new float[] { -15, -20, 0, 15, -20, 0, 15, 20, 0, -15, 20, 0},
        		new float[] { 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f },
        		new short[] { 0, 1, 2, 3, 0 }, 5);
		gTable	= new DrawModel(new float[] { -tempVec.x/2, -tempVec.y/2, 0, tempVec.x/2, -tempVec.y/2, 0, tempVec.x/2, tempVec.y/2, 0, -tempVec.x/2, tempVec.y/2, 0},
        		new float[] { 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f },
        		new short[] { 0, 1, 2, 3, 0 }, 5);
        gTop	= new DrawModel(new float[] { 0, 0, 0, 0, 1, 0, -.5f, .866f, 0,
				-.866f, .5f, 0, -1, 0, 0, -.866f, -.5f, 0, -.5f, -.866f, 0, 0,
				-1, 0, .5f, -.866f, 0, .866f, -.5f, 0, 1, 0, 0, .866f, .5f, 0,
				.5f, .866f, 0, 0f, 1f, 0 }, new float[] { 0.5f, 0.5f, 0.5f,
				0.0f, .25f, .067f, .067f, .25f, 0.0f, 0.5f, .067f, .75f, .25f,
				.933f, 0.5f, 1.0f, .75f, .933f, .933f, .75f, 1.0f, 0.5f, .933f,
				.25f, .75f, .067f, .5f, .0f }, new short[] { 0, 1, 2, 3, 4, 5,
				6, 7, 8, 9, 10, 11, 12, 13, 14 }, 14);

        tlVertex = toPhysicsCoords(1, 1, display);
        
		mSensorEventListener = new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent event)
			{
				float xAxis = event.values[SensorManager.DATA_X];
				float yAxis = event.values[SensorManager.DATA_Y];

				pWorld.setGravity(-10*xAxis, -10*yAxis);
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy)
			{
			}
		};

		sensors = sensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (sensors.size() > 0) {
			accSensor = sensors.get(0);
		}

		sensorMgr.registerListener(mSensorEventListener, accSensor, SensorManager.SENSOR_DELAY_GAME);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		GLU.gluOrtho2D(gl, -12f, 12f, -20f, 20f);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

		gBackground.loadTexture(gl, mContext, R.drawable.table);
		gTable.loadTexture(gl, mContext, R.drawable.newtable);
		gTop.loadTexture(gl, mContext, R.drawable.soccerball);
	}

	public void onSurfaceChanged(GL10 gl, int w, int h)
	{
		gl.glViewport(0, 0, w, h);
	}

	public void onDrawFrame(GL10 gl)
	{
		Vec2 vec;
		float rot;
		Body tempBody;
		Shape tempShape;

		gl.glClearColor(0f, 0, 1.5f, 1.0f);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);
		
		gBackground.setPosition(display.getWidth()/2, display.getHeight()/2, display);
		gBackground.draw(gl, gBackground.getPosition().x, gBackground.getPosition().y, 0, 0, 1, 1);
		
		gTable.setPosition(display.getWidth()/2, display.getHeight()/2, display);
		gTable.draw(gl, gTable.getPosition().x, gTable.getPosition().y, 0, 0, 1, 1);

		tempBody = pWorld.getBodyList();
		tempShape = tempBody.getShapeList();
		vec = toPhysicsCoords(tempBody.getPosition().x, tempBody.getPosition().y, display);
		rot = tempBody.getAngle() * 270;
		
		if (tempBody.getPosition().x < -tempVec.x/2 || tempBody.getPosition().x > tempVec.x/2 || tempBody.getPosition().y < -tempVec.y/2 || tempBody.getPosition().y > tempVec.y/2) {
			tempBody = null;
			tempBody = null;
			tempShape = null;
			gTop = null;
		}
		else {
			gTop.draw(gl, tempBody.getPosition().x, tempBody.getPosition().y, 0, rot, 1);
		}
		
		pWorld.update();
	}

	public Vec2 getSize()
	{
		return new Vec2(this.width, this.height);
	}
	
	public Context getContext()
	{
		return this.mContext;
	}
	
	public PhysicsWorld getPhysicsWorld()
	{
		return this.pWorld;
	}
	
	public Display getDisplay()
	{
		return this.display;
	}
	
	public DrawModel getTable()
	{
		return this.gTable;
	}
	
	public DrawModel getTop()
	{
		return this.gTop;
	}
	
	public void setSize(int width, int height)
	{
		this.width = width;
		this.height = height;
	}
	
	public void setContext(Context context)
	{
		this.mContext = context;
	}
	
	public void setPhysicsWorld(PhysicsWorld mWorld)
	{
		this.pWorld = mWorld;
	}
	
	public void setDisplay(Display display)
	{
		this.display = display;
	}
	
	public void touchEvent(float x, float y, int eventCode)
	{
	}
	
	public Vec2 toPhysicsCoords(float gestureX, float gestureY, Display display)
	{
		return new Vec2((20*gestureX)/display.getWidth() - 10, 20 - ((40 * gestureY)/display.getHeight()));
	}
}