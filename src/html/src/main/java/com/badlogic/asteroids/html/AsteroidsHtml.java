package com.badlogic.asteroids.html;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

public class AsteroidsHtml extends GwtApplication {
	@Override
	public ApplicationListener getApplicationListener() {
		return new Drop();
	}

	@Override
	public GwtApplicationConfiguration getConfig() {
		return new GwtApplicationConfiguration(480, 320);
	}
}
