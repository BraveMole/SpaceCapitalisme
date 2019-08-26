package com.world.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.world.Application;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width=1366;
		config.height=705*2/3;
		config.samples=16;
		//config.fullscreen=true;
		new LwjglApplication(new Application(), config);
	}
}
