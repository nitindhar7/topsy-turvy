package com.topsyturvy;

import org.jbox2d.collision.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;

import android.view.Display;

public class Polygon
{
	private BodyDef bodyDef;
	private Body body;
	private PolygonDef shapeDef;
	
	private Vec2 dimensions;

	public Polygon()
	{
		bodyDef = new BodyDef();
		shapeDef = new PolygonDef();
	}
	
	public Body getBody()
	{
		return this.body;
	}
	
	public BodyDef getBodyDef() 
	{
		return this.bodyDef;
	}
	
	public PolygonDef getShape()
	{
		return this.shapeDef;
	}
	
	public Vec2 getPosition()
	{
		return this.bodyDef.position;
	}
	
	public Vec2 getDimensions()
	{
		return this.dimensions;
	}
	
	public float getDensity()
	{
		return this.shapeDef.density;
	}
	
	public float getFriction()
	{
		return this.shapeDef.friction;
	}
	
	public float getRestitution()
	{
		return this.shapeDef.restitution;
	}
	
	public Vec2 getLinearVelocity()
    {
    	return this.body.getLinearVelocity();
    }
	
	public DrawModel getUserData()
    {
		return (DrawModel) this.body.getUserData();
    }
	
	public void setBody(Body body)
	{
		this.body = body;
	}
	
	public void setBodyDef(BodyDef bodyDef) 
	{
		this.bodyDef = bodyDef;
	}
	
	public void setMassFromShapes()
	{
		this.body.setMassFromShapes();
	}
	
	public void setShape(PolygonDef shapeDef)
	{
		this.body.createShape(shapeDef);
	}
	
	public void setPosition(float x, float y, Display display)
	{
		this.bodyDef.position.set(toPhysicsCoords(x, y, display));
	}

	public void setDimensions(float width, float height, Display display)
	{
		this.dimensions = toPhysicsCoords(width, height, display);
		this.shapeDef.setAsBox(dimensions.x, dimensions.y);
	}
	
	public void setDensity(float density)
	{
		this.shapeDef.density = density;
	}
	
	public void setFriction(float friction)
	{
		this.shapeDef.friction = friction;
	}
	
	public void setRestitution(float restitution)
	{
		this.shapeDef.restitution = restitution;
	}
	
	public void setLinearVelocity(Vec2 velocity)
    {
		this.body.setLinearVelocity(velocity);
    }
	
	public void setUserData(Object object)
    {
		this.body.setUserData(object);
    }
	
	public Vec2 toPhysicsCoords(float gestureX, float gestureY, Display display)
	{
		return new Vec2((20*gestureX)/display.getWidth() - 10, 20 - ((40 * gestureY)/display.getHeight()));
	}
	
	public void applyImpulse(Vec2 impulse, Vec2 point)
    {
		this.body.applyImpulse(impulse, point);
    }
}