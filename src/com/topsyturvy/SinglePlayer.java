package com.topsyturvy;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class SinglePlayer extends Activity {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.single_player);
	}
}