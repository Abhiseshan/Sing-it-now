package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.*;
import java.nio.file.Paths;

public class MyGdxGame extends Game {
	SpriteBatch batch;
	public BitmapFont font;

	//Global Variables
	public static String RoomID = "";
	public static TrackID[] tracks = new TrackID[10];

    @Override
	public void create () {

		batch = new SpriteBatch();
		//Use LibGDX's default Arial font.
		font = new BitmapFont();

		populate_list();

		setScreen(new StartScreen(this));
    }

	@Override
	public void render() {
		super.render();
	}
	public void dispose() {
		batch.dispose();
		font.dispose();
	}

	private void populate_list(){
		Thread popThread = new Thread(new Runnable() {
			@Override
			public void run() {
				tracks[0] = new TrackID("The Fox", "Singles", "Yelvis", "11000.jpg", "11000.txt", "11000.mp3", "11000_bk.jpg", 11000);
				tracks[1] = new TrackID("The Scientist", "test", "Coldplay", "11555.jpg", "11555.txt", "11555.mp3", "11555_bk.jpg", 11555);
				tracks[2] = new TrackID("Blank Space", "1989", "Taylor Swift", "11587.jpg", "11587.txt", "11587.mp3", "11587_bk.jpg", 11587);
				tracks[3] = new TrackID("Daemons", "Daemons", "Imagine Dragons", "33625.png", "33625.txt", "33625.mp3", "33625_bk.jpg", 33625);
				tracks[4] = new TrackID("For the first time", "Singles", "The Script", "84756.png", "84756.txt", "84756.mp3", "84756_bk.jpg", 84756);
			}
		});
		popThread.start();
	}
}
