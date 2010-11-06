package com.topsyturvy;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.CircleDef;
import org.jbox2d.collision.PolygonDef;
import org.jbox2d.collision.ShapeType;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

import android.util.Log;

public class PhysicsWorld {
	public int targetFPS;
    public int iterations;
    public int velocityIterations;
    public int positionIterations;
    private int count;
    private int ball_count;
    
    public float timeStep;
    private float x;
    private float y;

    public Body[] bodies;
    public Body groundBody;

    private AABB worldAABB;  
    public World world;
    private BodyDef groundBodyDef;  
    private PolygonDef groundShapeDef;  
    
    public PhysicsWorld() {
    	targetFPS			= 40;
    	timeStep			= (1000 / targetFPS);
    	iterations			= 5;
    	velocityIterations	= 6;
    	positionIterations	= 2;
    	bodies				= new Body[10];
    	count				= 0;
    	ball_count			= 0;
    	x					= 1;
    	y					= 2;
    }
  
    public void create() {

        // Step 1: Create Physics World Boundaries  
        worldAABB = new AABB();  
        worldAABB.lowerBound.set(new Vec2((float) 0.0, (float) 0.0));  
        worldAABB.upperBound.set(new Vec2((float) 100.0, (float) 100.0));  
  
        // Step 2: Create Physics World with Gravity  
        Vec2 gravity	= new Vec2((float) 0.0, (float) -10.0);
        world			= new World(worldAABB, gravity, true);  
        
        // Step 3: Create Ground Box  
        groundBodyDef		= new BodyDef();
        groundBodyDef.position.set(new Vec2((float) 0.0, (float) 4.0));  
        groundBody			= world.createBody(groundBodyDef);  
        groundBody.m_mass	= 20.0f;

        groundShapeDef = new PolygonDef();
        groundShapeDef.setAsBox((float) 100.0, (float) 100.0);

        // Parameters that affect physics
        // A density of 0 will create a fixed Body that doesn't move
        groundShapeDef.density = (float)0.5;
        
        // How much friction
        groundShapeDef.friction = (float)0.3;
        
        groundBody.createShape(groundShapeDef);
    }  
  
    public void addBall() {

    	BodyDef bodyDef		= new BodyDef();
    	CircleDef circle	= new CircleDef();

    	// Create Dynamic Body
        bodyDef.position.set(new Vec2(x+10,y+10)); 
        bodyDef.massData.mass = 2.0f;
        
        bodies[count]					= world.createBody(bodyDef);  
        bodies[count].m_type			= Body.e_dynamicType;
        bodies[count].m_linearVelocity	= new Vec2(10.0f, 20.0f);
        bodies[count].m_angularVelocity	= 10.0f;
        
        // Create Shape with Properties
        circle.radius	= (float) 1.8;  
        circle.density	= (float) 1.0;  
        circle.type		= ShapeType.CIRCLE_SHAPE;

        // How bouncy is this Shape?
        circle.restitution = (float)0.5;
        
        // Assign shape to Body
        bodies[count].createShape(circle);
  
        // Increase Counter  
        count++;
    }  

    public void update() {  
        
    	// Update Physics World  
        world.step(timeStep, iterations);  
        
        // Print info of all bodies 
        for(ball_count=0;ball_count <count; ball_count++ ) {
            Vec2 position = bodies[ball_count].getPosition();  
            float angle = bodies[ball_count].getAngle();
            float ang_vl = bodies[ball_count].getAngularVelocity();
            Vec2 lin_vl = bodies[ball_count].getLinearVelocity();
            Log.i("Physics Test", "Pos: (" + position.x + ", " + position.y + "), Angle: " + angle +
            		" Ball" + ball_count + "LinVelocity"+lin_vl+"AngVelocity"+ang_vl);
        }  
    }

    public void changeGravity(Vec2 gravity) {
        world.setGravity(gravity);
    }
}