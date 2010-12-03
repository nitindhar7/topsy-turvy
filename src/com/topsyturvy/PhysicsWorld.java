package com.topsyturvy;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import android.content.Context;
import android.os.Vibrator;


public class PhysicsWorld
{
    final private float FRAMERATE	= 30;
    final private float timeStep	= (1 / FRAMERATE);
    final private int iterations	= 5;

    private AABB worldAABB;
    private World world;
    private Vec2 gravity;
    private boolean doSleep;
    private int level;
    RevoluteJointDef jointdef = new RevoluteJointDef();
    RevoluteJoint joint;
    
    public PhysicsWorld(Context context, Vibrator vibrator, int Level)
    {
    	worldAABB = new AABB();
        worldAABB.lowerBound.set(new Vec2(-10f, -20f));
        worldAABB.upperBound.set(new Vec2(10f, 20f));

        gravity	= new Vec2(0f, 0f);
        doSleep	= false;
        world	= new World(worldAABB, gravity, doSleep);
        world.setContactListener(new CollisionContactListener(vibrator, context));
        level = Level;
    }
    
    public void addFence(float x, float y, float xr, float yr, float angle,
    		boolean dynamic, boolean flag, int count)    
    {
    		
    	BodyDef groundBodyDef;
		groundBodyDef = new BodyDef();
		groundBodyDef.position.set(new Vec2(x, y));
		groundBodyDef.angle = angle;
		Body groundBody = world.createBody(groundBodyDef);
	
		PolygonDef groundShapeDef;
		groundShapeDef = new PolygonDef();
		groundShapeDef.setAsBox(xr, yr);
		groundShapeDef.density = 1.0f;
		groundShapeDef.friction = 1.0f;
    	
    	if(level == 4) {
			if(count == 1)
			    groundShapeDef.restitution = 0.5f;
			else if (count == 5)
				groundShapeDef.restitution = 2.5f;
			else {
				groundShapeDef.restitution = 1.0f;
				groundShapeDef.friction = 0.5f;
			}
			
			groundBody.createShape(groundShapeDef);

			if(dynamic) {
				groundBody.setMassFromShapes();
				groundBody.setUserData("rotating_fence"+count);
			}
			else
				groundBody.setUserData("block");
		
			// JOINT CREATION		
			if( count == 4 ) {
				jointdef.body1 =  groundBody;
				groundBody.setUserData("rotating_fence" + count);
			}
			if( count == 5 ) {
				jointdef.body2 =  groundBody;
			    jointdef.maxMotorTorque = 20.0f;		
			    jointdef.motorSpeed = 15.0f;
			    jointdef.enableMotor = true;
			    joint= (RevoluteJoint) world.createJoint(jointdef);
			}
		
			if( count == 0 ) {
				jointdef.body1 =  groundBody;
				groundBody.setUserData("rotating_fence"+count);
			}
			if( count == 1 ) {
				jointdef.body2 =  groundBody;
		        jointdef.maxMotorTorque = 5.0f;		
		        jointdef.motorSpeed = 5.0f;
		        jointdef.enableMotor = true;
		        joint= (RevoluteJoint) world.createJoint(jointdef);
			}
		}
		else if (level == 2) {
			if(count == 5 || count ==7)
			    groundShapeDef.restitution = 3.5f;
			else
				groundShapeDef.restitution = 0.5f;
		
			groundBody.createShape(groundShapeDef);
			
			if(dynamic) {
				groundBody.setMassFromShapes();
			}
			else if(count == 7)
				groundBody.setUserData("impulsivefence");	
			else	
				groundBody.setUserData("fence");
		
			if(level == 2) {
				// JOINT CREATION		
				if( count == 4 )
					jointdef.body1 =  groundBody;
				else if( count == 5 ) {
					jointdef.body2 =  groundBody;
				    jointdef.maxMotorTorque = 20.0f;
				    jointdef.motorSpeed = 15.0f;		
				    jointdef.enableMotor = true;
				    joint= (RevoluteJoint) world.createJoint(jointdef);
				    groundBody.setUserData("rotator"+count);
				}
			}		
		}
		else
			groundBody.createShape(groundShapeDef);
    }

    public void setGravity(float componentX, float componentY)
    {
    	world.setGravity(new Vec2(componentX, componentY));
    }
    
    public int getContactCount()
    {
    	return world.getContactCount();
    }
    
    public Vec2 getWorldLowerBound()
    {
    	return world.getWorldAABB().lowerBound;
    }
    
    public Vec2 getWorldUpperBound()
    {
    	return world.getWorldAABB().upperBound;
    }

    public void update()
    {
        world.step(timeStep, iterations);
    }

    public Body getBodyList()
    {
        return world.getBodyList();
    }

	public Body createBody(BodyDef bodyDef)
	{
		return world.createBody(bodyDef);
	}
}