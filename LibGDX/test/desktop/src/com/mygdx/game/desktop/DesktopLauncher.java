package com.mygdx.game.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Kareoke Test";
		config.width = 1366;
		config.height = 768;
		config.fullscreen = true;
		LwjglApplication application = new LwjglApplication(new MyGdxGame(), config);
    }
}
