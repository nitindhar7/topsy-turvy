package com.topsyturvy;

import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;


public class PhysicsWorld
{
    final private float FRAMERATE	= 30;
    final private float timeStep	= (1 / FRAMERATE);
    final private int iterations	= 5;

    private AABB worldAABB;
    private World world;
    private Vec2 gravity;
    private boolean doSleep;
    
    public PhysicsWorld()
    {
    	worldAABB = new AABB();
        worldAABB.lowerBound.set(new Vec2(-10f, -20f));
        worldAABB.upperBound.set(new Vec2(10f, 20f));

        gravity	= new Vec2(0f, 0f);
        doSleep	= false;
        world	= new World(worldAABB, gravity, doSleep);
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