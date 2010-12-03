package com.topsyturvy;

import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.collision.CircleDef;
import org.jbox2d.collision.PolygonShape;
import org.jbox2d.collision.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Vibrator;
import android.view.Display;

class TopsyTurvyRenderer implements GLSurfaceView.Renderer {
	public PhysicsWorld pWorld;
	private Context mContext;
	private Display display;
	private SinglePlayer currentSPGame;
	
	SensorEventListener mSensorEventListener;
	private List<Sensor> sensors;
	private Sensor accSensor;
	
	private int width;
	private int height;
	private int game_level;
	public Body pTopBody;
	public BodyDef pTopBodyDef;
	private CircleDef pTopShape;	
	float FenceAttr[][];
	private DrawModel gBackground;
	private DrawModel gTable;
	private DrawModel gTop;
	private DrawModel gFence, gDestination, gRotator;
	private DrawModel gImpulsiveFence, gblock1;
	public Vec2 tempVec;
	public boolean flingReleased;
	private Intent resultIntent = new Intent();
	
	float size =.2f;

	public TopsyTurvyRenderer(Context context, Vibrator vibrator, SensorManager sensorMgr, Display display, SinglePlayer sp, int level)
	{
		setPhysicsWorld(pWorld);
		setContext(context);
		setDisplay(display);
		setSize(display.getWidth(), display.getHeight());
		this.currentSPGame = sp;
		
		game_level = level;
		
		tempVec = new Vec2(20*7/8, 40*7/8);
		
		gBackground	= new DrawModel(new float[] {-12, -20, 0, 12, -20, 0, 12, 20, 0, -12, 20, 0},
        		new float[] { 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f },
        		new short[] { 0, 1, 2, 3, 0 },
        		5);
		gTable	= new DrawModel(new float[] { -tempVec.x*4/7, -tempVec.y/2, 0, tempVec.x*4/7, -tempVec.y/2, 0, tempVec.x*4/7, tempVec.y/2, 0, -tempVec.x*4/7, tempVec.y/2, 0},
        		new float[] { 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f },
        		new short[] { 0, 1, 2, 3, 0 },
        		5);
		gTop = new DrawModel(new float[] { 0, 0, 0, 0, 1, 0, -.5f, .866f, 0,
				-.866f, .5f, 0, -1, 0, 0, -.866f, -.5f, 0, -.5f, -.866f, 0, 0,
				-1, 0, .5f, -.866f, 0, .866f, -.5f, 0, 1, 0, 0, .866f, .5f, 0,
				.5f, .866f, 0, 0f, 1f, 0 }, new float[] { 0.5f, 0.5f, 0.5f,
				0.0f, .25f, .067f, .067f, .25f, 0.0f, 0.5f, .067f, .75f, .25f,
				.933f, 0.5f, 1.0f, .75f, .933f, .933f, .75f, 1.0f, 0.5f, .933f,
				.25f, .75f, .067f, .5f, .0f }, new short[] { 0, 1, 2, 3, 4, 5,
				6, 7, 8, 9, 10, 11, 12, 13, 14 }, 14);
        gFence = new DrawModel(new float[]{-1,-1,0,1,-1,0,1,1,0,-1,1,0},
                 new float[]{ 0f,1f, 1f,1f, 1f,0f, 0f,0f },
                 new short[]{0,1,2,3,0},
                 5);        
		gDestination = new DrawModel(new float[] { 0, 0, 0, 0, 1, 0, -.5f,
				.866f, 0, -.866f, .5f, 0, -1, 0, 0, -.866f, -.5f, 0, -.5f,
				-.866f, 0, 0, -1, 0, .5f, -.866f, 0, .866f, -.5f, 0, 1, 0, 0,
				.866f, .5f, 0, .5f, .866f, 0, 0f, 1f, 0 }, new float[] { 0.5f,
				0.5f, 0.5f, 0.0f, .25f, .067f, .067f, .25f, 0.0f, 0.5f, .067f,
				.75f, .25f, .933f, 0.5f, 1.0f, .75f, .933f, .933f, .75f, 1.0f,
				0.5f, .933f, .25f, .75f, .067f, .5f, .0f }, new short[] { 0, 1,
				2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 }, 14);
        
		if (level == 1)
		{	//simple static fences
		FenceAttr = new float[][]	// { x, y, rot, sizeX, sizeY}
		                                {
		                                {-5.25f, 0.22026443f, -1.5707964f, 10.238326f, size},
		                              //  {-4.05f, 15.638767f, -1.5707964f, 1.8061686f, .5f},
		                              //  {-0.9375f, 13.171806f, -1.562198f, 4.361395f, size},
		                                {-0.2625f, -3.7885463f, -1.5664085f, 8.546337f, size},
		                             //   {-0.7875f, -14.933921f, 1.2393674f, 2.1896493f, size},                          
		                                {3.8625002f, 9.647577f, -1.562532f, 4.5376f, size},
		                                {5.6625f, 9.955947f, 0.0f, 1.0f, size},
		                              //  {3.2875f, 4.1409693f, 0.0f, 2.5375f, size},                                                    
		                              //  {5.2250004f, -0.4845816f, -1.5502872f, 3.657157f, size},
		                                {7.7250004f, -8.017621f, 0.0f, 1.875f, size},
		                                {3.6375f, -12.687225f, -0.033079084f, 2.6639576f, size},
		                                };
		}
		else if (level == 3)
		{	//level has rotating lines
			FenceAttr = new float[][]	// { x, y, rot, sizeX, sizeY}
	                                {
	                                {-5.25f, 0.22026443f, -1.5707964f, 10.238326f, size},
	                                {-5.55f, 15.638767f, -1.5707964f, 1.8061686f, size},
	                                {-0.2625f, -3.7885463f, -1.5664085f, 8.546337f, size},
	                                {-0.7875f, -14.933921f, 1.2393674f, 2.1896493f, size},                          
	                                {2.4625002f, 9.647577f, -1.562532f, 1.06f, size},
	                                {4.0625f, 10.955947f, 0.0f, 3.9f, .5f},
	                                {7.7250004f, -8.017621f, 0.0f, 1.875f, size},
	                                {3.6375f, -12.687225f, -0.033079084f, 2.6639576f, size},
	                                };

	        gRotator = new DrawModel(new float[]{-1,-1,0,1,-1,0,1,1,0,-1,1,0},
	                new float[]{ 0f,1f, 1f,1f, 1f,0f, 0f,0f },
	                new short[]{0,1,2,3,0},
	                5);
	        gImpulsiveFence = new DrawModel(new float[]{-1,-1,0,1,-1,0,1,1,0,-1,1,0},
	                new float[]{ 0f,1f, 1f,1f, 1f,0f, 0f,0f },
	                new short[]{0,1,2,3,0},
	                5);

		}
		else if (level == 2)
		{	//(level has simple fences)
			FenceAttr = new float[][]	// { x, y, rot, sizeX, sizeY}
	                                {
	                                {-5.25f, 0.22026443f, -1.5707964f, 10.238326f, size},
	                                {-4.05f, 15.638767f, -1.5707964f, 1.8061686f, .5f},
	                                {-0.9375f, 13.171806f, -1.562198f, 4.361395f, size},
	                                {-0.2625f, -3.7885463f, -1.5664085f, 8.546337f, size},
	                                {-0.7875f, -14.933921f, 1.2393674f, 2.1896493f, size},                          
	                                {3.8625002f, 9.647577f, -1.562532f, 4.5376f, size},
	                                {5.6625f, 9.955947f, 0.0f, 1.0f, size},
	                                {3.2875f, 4.1409693f, 0.0f, 2.5375f, size},                                                    
	                                {5.2250004f, -0.4845816f, -1.5502872f, 3.657157f, size},
	                                {7.7250004f, -8.017621f, 0.0f, 1.875f, size},
	                                {3.6375f, -12.687225f, -0.033079084f, 2.6639576f, size},
	                                };

		}
		//level4 (with rotting stuff)
		else {
		    FenceAttr=new float[][]	// { x, y, rot, sizeX, sizeY}
                                  {
	                                    {-4.55f, 2.22026443f, -1.5707964f, 1.538326f, size},
	                                    {-5.25f, 0.22026443f, -1.5707964f, 3.5f, size},
	                                    {-4.05f, 15.638767f, -1.5707964f, 1.5061686f, 1.5f},
	                                    {4.9f, 0.0f, -1.5664085f, 1.546337f, 1.5f},
	                                    {3.8625002f, 9.647577f, -1.562532f, 1.5376f, size},
	                                    {5.6625f, 9.955947f, 0.0f, 3.9f, size},
	                                    {7.7250004f, -8.017621f, 0.0f, 1.575f, 1.5f},
	                                    {2.6375f, -12.687225f, -0.033079084f, 1.5639576f, 1.5f},
                                  };
		    
	        gblock1 = new DrawModel(new float[]{-1,-1,0,1,-1,0,1,1,0,-1,1,0},
	                new float[]{ 0f,1f, 1f,1f, 1f,0f, 0f,0f },
	                new short[]{0,1,2,3,0},
	                5);

		}
		
        pWorld = new PhysicsWorld(context, vibrator, level);
        
        pTopBodyDef = new BodyDef();
        pTopShape = new CircleDef();
        pTopBodyDef.position.set(new Vec2(-7,15));
        pTopBodyDef.massData.mass = 100f;
        pTopBody = pWorld.createBody(pTopBodyDef);
        pTopShape.radius = 1;
        pTopShape.density = 1.0f;
        pTopShape.friction = 1.5f;
        pTopShape.restitution = 0.3f;

        pTopBody.putToSleep();
        pTopBody.createShape(pTopShape);
        pTopBody.setMassFromShapes();
        pTopBody.setUserData("TOP");
  
        int i=0,index=0;
        for(i=0; i<FenceAttr.length;i++)
        {
        	if (level == 3)
        	{
        		if(i==5)
					pWorld.addFence(FenceAttr[i][index], FenceAttr[i][index + 1],
							FenceAttr[i][index + 3], FenceAttr[i][index + 4],
							FenceAttr[i][index + 2], true, true, i);
	        	else
					pWorld.addFence(FenceAttr[i][index], FenceAttr[i][index + 1],
							FenceAttr[i][index + 3], FenceAttr[i][index + 4],
							FenceAttr[i][index + 2], false, true, i);
        	}
        	
        	else if (level == 4)
            {
	        	if(i == 5 || i== 1)
					pWorld.addFence(FenceAttr[i][index], FenceAttr[i][index + 1],
							FenceAttr[i][index + 3], FenceAttr[i][index + 4],
							FenceAttr[i][index + 2], true, true, i);
	        	else
					pWorld.addFence(FenceAttr[i][index], FenceAttr[i][index + 1],
							FenceAttr[i][index + 3], FenceAttr[i][index + 4],
							FenceAttr[i][index + 2], false, false, i);
            }
        	
        	else 
        		pWorld.addFence(FenceAttr[i][index], FenceAttr[i][index + 1],
						FenceAttr[i][index + 3], FenceAttr[i][index + 4],
						FenceAttr[i][index + 2], false, false, i);        	
        }
        
		mSensorEventListener = new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent event) {
				float xAxis = event.values[SensorManager.DATA_X];
				float yAxis = event.values[SensorManager.DATA_Y];

				pWorld.setGravity(-2*xAxis, -2*yAxis);
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

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		GLU.gluOrtho2D(gl, -12f, 12f, -20f, 20f);
		gl.glClearColor(0, 0, 0, 0);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

		gBackground.loadTexture(gl, mContext, R.drawable.table2);
		gTable.loadTexture(gl, mContext, R.drawable.newtable);
		gTop.loadTexture(gl, mContext, R.drawable.top);
		
		if(game_level == 3) {
			gFence.loadTexture(gl, mContext, R.drawable.lightgreen_obstacle);
			gDestination.loadTexture(gl, mContext, R.drawable.final_point);
			gRotator.loadTexture(gl, mContext, R.drawable.darkgreen_obstacle);
			gImpulsiveFence.loadTexture(gl, mContext, R.drawable.red_obstacle);
		}
		else if(game_level == 4) {
			gFence.loadTexture(gl, mContext, R.drawable.red_obstacle);
			gblock1.loadTexture(gl, mContext, R.drawable.block5);
			gDestination.loadTexture(gl, mContext, R.drawable.final_point);			
		}
		else {	
			gFence.loadTexture(gl, mContext, R.drawable.box);
			gDestination.loadTexture(gl, mContext, R.drawable.final_point);	
		}
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
		gl.glViewport(0, 0, w, h);
	}

	public void drawInitialScenario(GL10 gl) {
		gBackground.setPosition(display.getWidth()/2, display.getHeight()/2, display);
		gBackground.draw(gl, gBackground.getPosition().x, gBackground.getPosition().y, 0, 0, 1, 1);
		gTable.setPosition(display.getWidth()/2, display.getHeight()/2, display);
		gTable.draw(gl, gTable.getPosition().x, gTable.getPosition().y, 0, 0, 1, 1);
		gDestination.setPosition(display.getWidth()*0.89f, display.getHeight()*0.89f, display);
		gDestination.draw(gl, gDestination.getPosition().x,
		gDestination.getPosition().y, 0, 0, 1.7f);
    }  
    
	public void onDrawFrame(GL10 gl)
	{

		gl.glClearColor(0, 0, 0, 0);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
				
		drawInitialScenario(gl);

		ContinuousDraw( gl, game_level );
		
		pWorld.update();
	}
	
	public void ContinuousDraw(GL10 gl, int level) {
		Vec2 vec;
		float rot;
		Body tempBody;
		Shape tempShape; 
		int score;
		TopsyTurvyDbAdapter dbAdapter;
		Cursor cursor;
		int topScore;
		int gamesPlayed;
		int totalScore;

		if(level == 3)
		{
			tempBody = pWorld.getBodyList();
			while (tempBody != null) {
				tempShape = tempBody.getShapeList();
				
				if (tempShape != null) {				
					vec = tempBody.getPosition();
	                rot = tempBody.getAngle() * 57f;
					switch (tempShape.getType()) {
						case CIRCLE_SHAPE:
						if (tempBody.getPosition().x < -tempVec.x * 4 / 7
								|| tempBody.getPosition().x > tempVec.x * 4 / 7
								|| tempBody.getPosition().y < -tempVec.y / 2
								|| tempBody.getPosition().y > tempVec.y / 2) {

							score = this.currentSPGame.getScore();
							dbAdapter = this.currentSPGame.getTopsyTurvyDbAdapter();
							cursor = dbAdapter.find(TopsyTurvyDbAdapter.DATABASE_PLAYERS_TABLE, "name = '" + this.currentSPGame.getActivePlayer() + "'");
							if (cursor != null && cursor.getCount() > 0) {
								topScore = (cursor.getInt(1) < score) ? score : cursor.getInt(1);
								gamesPlayed = cursor.getInt(3) + 1;
								totalScore = cursor.getInt(2) + score;

								dbAdapter.update(cursor.getString(0), null, topScore, totalScore, gamesPlayed, -1, -1);
							}
							
							resultIntent.putExtra("score", score);
							this.currentSPGame.doVibrate(50);
							this.currentSPGame.setResult(12, resultIntent);
							this.currentSPGame.finish();
						}
						else
							gTop.draw(gl, tempBody.getPosition().x, tempBody.getPosition().y, 0, rot, 1);
	
						if(tempBody.getPosition().x < gDestination.getPosition().x + 1 &&
							tempBody.getPosition().x > gDestination.getPosition().x - 1 &&
							tempBody.getPosition().y < gDestination.getPosition().y + 1 &&
							tempBody.getPosition().y > gDestination.getPosition().y - 1){

							score = this.currentSPGame.getScore() + 100;
							dbAdapter = this.currentSPGame.getTopsyTurvyDbAdapter();
							cursor = dbAdapter.find(TopsyTurvyDbAdapter.DATABASE_PLAYERS_TABLE, "name = '" + this.currentSPGame.getActivePlayer() + "'");
							if (cursor != null && cursor.getCount() > 0) {
								topScore = (cursor.getInt(1) < score) ? score : cursor.getInt(1);
								
								gamesPlayed = cursor.getInt(3) + 1;
								totalScore = cursor.getInt(2) + score;

								dbAdapter.update(cursor.getString(0), null, topScore, totalScore, gamesPlayed, -1, -1);
							}
							
							resultIntent.putExtra("score", score);
							this.currentSPGame.doVibrate(50);
							this.currentSPGame.setResult(10, resultIntent);
							this.currentSPGame.finish();
						}	
							
						break;
					case POLYGON_SHAPE:
						Vec2[] vertexes = ((PolygonShape)tempShape).getVertices();

						if(tempBody.getUserData().toString().contains("rotator"))	                    
	                      	gRotator.draw(gl, vec.x, vec.y, 0f, rot, vertexes[2].x, .4f);
	                    else
	                      if (tempBody.getUserData().toString().contains("impulsive"))
	                       	gImpulsiveFence.draw(gl, vec.x, vec.y, 0f, rot, vertexes[2].x, .2f);	
	                        
	                      else
	                       	gFence.draw(gl, vec.x, vec.y, 0f, rot, vertexes[2].x, .2f);
							
						break;
					}
				}
				tempBody = tempBody.getNext();
				tempShape = null;
			}
		}
		
		else if(level ==4)
		{
			tempBody = pWorld.getBodyList();
			while (tempBody != null) {
				tempShape = tempBody.getShapeList();
				
				if (tempShape != null) {				
					vec = tempBody.getPosition();
	                rot = tempBody.getAngle() * 57f;
	                
					switch (tempShape.getType()) {
						case CIRCLE_SHAPE:
						if (tempBody.getPosition().x < -tempVec.x * 4 / 7
								|| tempBody.getPosition().x > tempVec.x * 4 / 7
								|| tempBody.getPosition().y < -tempVec.y / 2
								|| tempBody.getPosition().y > tempVec.y / 2) {

							score = this.currentSPGame.getScore();
							dbAdapter = this.currentSPGame.getTopsyTurvyDbAdapter();
							cursor = dbAdapter.find(TopsyTurvyDbAdapter.DATABASE_PLAYERS_TABLE, "name = '" + this.currentSPGame.getActivePlayer() + "'");
							if (cursor != null && cursor.getCount() > 0) {
								topScore = (cursor.getInt(1) < score) ? score : cursor.getInt(1);
								gamesPlayed = cursor.getInt(3) + 1;
								totalScore = cursor.getInt(2) + score;

								dbAdapter.update(cursor.getString(0), null, topScore, totalScore, gamesPlayed, -1, -1);
							}

							resultIntent.putExtra("score", score);
							this.currentSPGame.doVibrate(50);
							this.currentSPGame.setResult(12, resultIntent);
							this.currentSPGame.finish();
						}
						else
							gTop.draw(gl, tempBody.getPosition().x, tempBody.getPosition().y, 0, rot, 1);							
							if(tempBody.getPosition().x < gDestination.getPosition().x + 1 &&
								tempBody.getPosition().x > gDestination.getPosition().x - 1 &&
								tempBody.getPosition().y < gDestination.getPosition().y + 1 &&
								tempBody.getPosition().y > gDestination.getPosition().y - 1){
								
								score = this.currentSPGame.getScore() + 100;
								dbAdapter = this.currentSPGame.getTopsyTurvyDbAdapter();
								cursor = dbAdapter.find(TopsyTurvyDbAdapter.DATABASE_PLAYERS_TABLE, "name = '" + this.currentSPGame.getActivePlayer() + "'");
								if (cursor != null && cursor.getCount() > 0) {
									topScore = (cursor.getInt(1) < score) ? score : cursor.getInt(1);
									gamesPlayed = cursor.getInt(3) + 1;
									totalScore = cursor.getInt(2) + score;

									dbAdapter.update(cursor.getString(0), null, topScore, totalScore, gamesPlayed, -1, -1);
								}
								
								resultIntent.putExtra("score", score);
								this.currentSPGame.doVibrate(50);
								this.currentSPGame.setResult(10, resultIntent);
								this.currentSPGame.finish();
							}								
							break;
							
						case POLYGON_SHAPE:
							Vec2[] vertexes = ((PolygonShape)tempShape).getVertices();
	                        if(tempBody.getUserData().toString().contains("rotating"))                                                	
	                        	gFence.draw(gl, vec.x, vec.y, 0f, rot, vertexes[2].x, size);                        
	                        else
	                        	gblock1.draw(gl, vec.x, vec.y, 0f, rot, vertexes[2].x, 1.5f);	                        
							break;
					}
				}
				tempBody = tempBody.getNext();
				tempShape = null;
			}
		}
		else
		{
			tempBody = pWorld.getBodyList();
			while (tempBody != null) {
				tempShape = tempBody.getShapeList();
				
				if (tempShape != null) {				
					vec = tempBody.getPosition();
	                rot = tempBody.getAngle() * 57f;
	                
					switch (tempShape.getType()) {
						case CIRCLE_SHAPE:
						if (tempBody.getPosition().x < -tempVec.x * 4 / 7
								|| tempBody.getPosition().x > tempVec.x * 4 / 7
								|| tempBody.getPosition().y < -tempVec.y / 2
								|| tempBody.getPosition().y > tempVec.y / 2) {

							score = this.currentSPGame.getScore();
							dbAdapter = this.currentSPGame.getTopsyTurvyDbAdapter();
							cursor = dbAdapter.find(TopsyTurvyDbAdapter.DATABASE_PLAYERS_TABLE, "name = '" + this.currentSPGame.getActivePlayer() + "'");
							if (cursor != null && cursor.getCount() > 0) {
								topScore = (cursor.getInt(1) < score) ? score : cursor.getInt(1);
								gamesPlayed = cursor.getInt(3) + 1;
								totalScore = cursor.getInt(2) + score;

								dbAdapter.update(cursor.getString(0), null, topScore, totalScore, gamesPlayed, -1, -1);
							}
							
							resultIntent.putExtra("score", score);
							this.currentSPGame.doVibrate(50);
							this.currentSPGame.setResult(12, resultIntent);
							this.currentSPGame.finish();
						}
						else
							gTop.draw(gl, tempBody.getPosition().x, tempBody.getPosition().y, 0, rot, 1);							
							if(tempBody.getPosition().x < gDestination.getPosition().x + 1 &&
								tempBody.getPosition().x > gDestination.getPosition().x - 1 &&
								tempBody.getPosition().y < gDestination.getPosition().y + 1 &&
								tempBody.getPosition().y > gDestination.getPosition().y - 1){
								
								score = this.currentSPGame.getScore() + 100;
								dbAdapter = this.currentSPGame.getTopsyTurvyDbAdapter();
								cursor = dbAdapter.find(TopsyTurvyDbAdapter.DATABASE_PLAYERS_TABLE, "name = '" + this.currentSPGame.getActivePlayer() + "'");
								if (cursor != null && cursor.getCount() > 0) {
									topScore = (cursor.getInt(1) < score) ? score : cursor.getInt(1);
									gamesPlayed = cursor.getInt(3) + 1;
									totalScore = cursor.getInt(2) + score;
									dbAdapter.update(cursor.getString(0), null, topScore, totalScore, gamesPlayed, -1, -1);
								}
								
								resultIntent.putExtra("score", score);
								this.currentSPGame.doVibrate(50);
								this.currentSPGame.setResult(10, resultIntent);
								this.currentSPGame.finish();
							}								
							break;
							
						case POLYGON_SHAPE:
							Vec2[] vertexes = ((PolygonShape)tempShape).getVertices();		                                                	
	                        	gFence.draw(gl, vec.x, vec.y, 0f, rot, vertexes[2].x, size);                        					                        
							break;
					}
				}
				tempBody = tempBody.getNext();
				tempShape = null;
			}
		}
	}

	public Vec2 getSize() {
		return new Vec2(this.width, this.height);
	}
	
	public Context getContext() {
		return this.mContext;
	}
	
	public PhysicsWorld getPhysicsWorld() {
		return this.pWorld;
	}
	
	public Display getDisplay() {
		return this.display;
	}
	
	public DrawModel getTable() {
		return this.gTable;
	}
	
	public DrawModel getTop() {
		return this.gTop;
	}
	
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public void setContext(Context context) {
		this.mContext = context;
	}
	
	public void setPhysicsWorld(PhysicsWorld mWorld) {
		this.pWorld = mWorld;
	}
	
	public void setDisplay(Display display) {
		this.display = display;
	}
	
	public Vec2 toPhysicsCoords(float gestureX, float gestureY, Display display) {
		return new Vec2((20*gestureX)/display.getWidth() - 10, 20 - ((40 * gestureY)/display.getHeight()));
	}

	public void touchEvent(float x, float y, int action) {
	}
}