package com.badlogic.asteroids.android;

import android.os.Bundle;

//import com.badlogic.asteroids.core.Asteroids;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AsteroidsActivity extends AndroidApplication {

	@Override
	public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
//			initialize(new Test(), config);
	}
}
