package com.topsyturvy;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

public class Triangle {
	private final String TAG = "TOPSY";
	private FloatBuffer vertexbuffer;
	private FloatBuffer colorBuffer;
	
	/** The initial vertex definition */
	/** The initial vertex definition */
	private float vertices[] = { 
					 	0.0f,  -1.0f,  0.0f,		//Top Of Triangle (Front)
						-1.0f, 1.0f, 1.0f,		//Left Of Triangle (Front)
						 1.0f, 1.0f, 1.0f,		//Right Of Triangle (Front)
						 0.0f,  -1.0f, 0.0f,		//Top Of Triangle (left)
						 -1.0f, 1.0f, -1.0f,		//Left Of Triangle (left)
						 -1.0f, 1.0f, 1.0f,	//Right Of Triangle (left)
						 0.0f,  -1.0f, 0.0f,		//Top Of Triangle (right)
						 1.0f, 1.0f, 1.0f,	//Left Of Triangle (right)
						1.0f, 1.0f, -1.0f,	//Right Of Triangle (right)
						 0.0f,  -1.0f, 0.0f,		//Top Of Triangle (back)
						-1.0f, 1.0f, -1.0f,	//Left Of Triangle (back)
						1.0f, 1.0f, -1.0f		//Right Of Triangle (back)
											};
	/** The initial color definition */	
	private float colors[] = {
			    		1.0f, 0.0f, 0.0f, 1.0f, //Red
			    		0.0f, 1.0f, 0.0f, 1.0f, //Green
			    		0.0f, 0.0f, 1.0f, 1.0f, //Blue
			    		1.0f, 0.0f, 0.0f, 1.0f, //Red
			    		0.0f, 0.0f, 1.0f, 1.0f, //Blue
			    		0.0f, 1.0f, 0.0f, 1.0f, //Green
			    		1.0f, 0.0f, 0.0f, 1.0f, //Red
			    		0.0f, 1.0f, 0.0f, 1.0f, //Green
			    		0.0f, 0.0f, 1.0f, 1.0f, //Blue
			    		1.0f, 0.0f, 0.0f, 1.0f, //Red
			    		0.0f, 0.0f, 1.0f, 1.0f, //Blue
			    		0.0f, 1.0f, 0.0f, 1.0f 	//Green
						    					};
	
	public Triangle() {
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		vertexbuffer = byteBuf.asFloatBuffer();
		vertexbuffer.put(vertices);
		vertexbuffer.position(0);
		
		//
		byteBuf = ByteBuffer.allocateDirect(colors.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		colorBuffer = byteBuf.asFloatBuffer();
		colorBuffer.put(colors);
		colorBuffer.position(0);
	 }
	
	public void drawing(GL10 gl) {
		Log.i(TAG, "In Triangle drawing");
         gl.glFrontFace(GL10.GL_CW);
		
		//Point to our vertex buffer
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexbuffer);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);
		//Enable vertex buffer
		
	
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);		
		//Draw the vertices as triangle strip
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0,  vertices.length/ 3);
		
		//Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
	}
}