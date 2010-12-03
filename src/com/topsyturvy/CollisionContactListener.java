package com.topsyturvy;

import org.jbox2d.dynamics.ContactListener;
import org.jbox2d.dynamics.contacts.ContactPoint;
import org.jbox2d.dynamics.contacts.ContactResult;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;

public class CollisionContactListener implements ContactListener {
	private Vibrator vibrator;
	private Context mContext;
	private MediaPlayer mediaPlayer;
	float Cx;
	float Cy;
	int count = 0;

	CollisionContactListener(Vibrator vibration, Context context){
		setVibration(vibration);
		setContext(context);
	};
	
	public void add(ContactPoint cpoint) {
		Cx = cpoint.position.x;
		Cy = cpoint.position.y;
		mediaPlayer = MediaPlayer.create(mContext, R.raw.collision1);
		mediaPlayer.start();
		vibrator.vibrate(55);
	}

	public void persist(ContactPoint cpoint) {
		vibrator.vibrate(55);
	}

	public void remove(ContactPoint cpoint) {
	}

	public void result(ContactResult point) {
	}
	
	public void doVibrate(int seconds) {
		vibrator.vibrate(seconds);
	}

	public void doVibrate(int seconds, long[] pattern) {
		vibrator.vibrate(pattern, seconds);
	}
	public Vibrator getVibration() {
		return this.vibrator;
	}
	public void setVibration(Vibrator vibration) {
		this.vibrator = vibration;
	}
	public void setContext(Context context) {
		this.mContext = context;
	}
	public Context getContext() {
		return this.mContext;
	}
}