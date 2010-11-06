package com.topsyturvy;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.common.Vec2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.view.Display;

public class DrawModel {
	private FloatBuffer mVertexBuffer;
	private ShortBuffer mIndexBuffer;
	private FloatBuffer mTexBuffer;
	private int vertexCount = 0;
	private boolean hasTexture = false;
	private int[] mTexture = new int[1];
	private Vec2 position;

	public DrawModel(float[] coords, float[] tcoords, short[] icoords, int vertexes) {
		this(coords, icoords, vertexes);
		mTexBuffer = makeFloatBuffer(tcoords);
		position = new Vec2(0, 0);
	}

	public DrawModel(float[] coords, short[] icoords, int vertexes) {
		vertexCount = vertexes;
		mVertexBuffer = makeFloatBuffer(coords);
		mIndexBuffer = makeShortBuffer(icoords);
	}

	protected static FloatBuffer makeFloatBuffer(float[] arr) {
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}

	protected static ShortBuffer makeShortBuffer(short[] arr) {
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
		bb.order(ByteOrder.nativeOrder());
		ShortBuffer ib = bb.asShortBuffer();
		ib.put(arr);
		ib.position(0);
		return ib;
	}

	public void loadTexture(GL10 gl, Context mContext, int mTex) {
		hasTexture = true;
		gl.glGenTextures(1, mTexture, 0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[0]);
		Bitmap bitmap;
		bitmap = BitmapFactory.decodeResource(mContext.getResources(), mTex);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.recycle();
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	}

	public void draw(GL10 gl) {
		if (hasTexture) {
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[0]);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexBuffer);
		} else {
			gl.glDisable(GL10.GL_TEXTURE_2D);
		}
		
		gl.glFrontFace(GL10.GL_CCW);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
		gl.glDrawElements(GL10.GL_TRIANGLE_FAN, vertexCount, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}

	public void draw(GL10 gl, float x, float y, float z, float rot, float scale) {
		this.draw(gl, x, y, z, rot, scale, scale);
	}

	public void draw(GL10 gl, float x, float y, float z, float rot, float scaleX, float scaleY) {
		gl.glPushMatrix();
		gl.glTranslatef(x, y, z);
		gl.glRotatef(rot, 0f, 0f, 1f);
		//gl.glScalef(scaleX, scaleY, 0f);
		this.draw(gl);
		gl.glPopMatrix();
	}

	public void draw(GL10 gl, float x, float y, float z, float rot) {
		gl.glPushMatrix();
		gl.glTranslatef(x, y, z);
		gl.glRotatef(rot, 0f, 0f, 1f);
		this.draw(gl);
		gl.glPopMatrix();
	}

	public void draw(GL10 gl, float x, float y, float z) {
		gl.glPushMatrix();
		gl.glTranslatef(x, y, z);
		this.draw(gl);
		gl.glPopMatrix();
	}
	
	public Vec2 getPosition()
	{
		return this.position;
	}
	
	public void setPosition(float x, float y, Display display)
	{
		this.position = toPhysicsCoords(x, y, display);
	}
	
	public Vec2 toPhysicsCoords(float gestureX, float gestureY, Display display)
	{
		return new Vec2((20*gestureX)/display.getWidth() - 10, 20 - ((40 * gestureY)/display.getHeight()));
	}
}