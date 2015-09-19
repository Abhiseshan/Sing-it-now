package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Abhinav on 9/19/2015.
 */
public class SelectionScreen implements Screen{


    SpriteBatch batch;
    private BitmapFont artist;
    private BitmapFont name;
    private BitmapFont album;
    private Texture album_art;
    private Texture background;

    Game g;

    public SelectionScreen(Game g){
        this.g = g;
        create();
    }

    private void create(){
        batch = new SpriteBatch();
        album_art = new Texture("test_cover.jpg");
        background = new Texture("background.jpg");
        artist = new BitmapFont();
        name = new BitmapFont();
        album = new BitmapFont();
    }

    @Override
    public void render(float delta){
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(background, 0, 0);
        batch.draw(album_art, 600, 400, 300, 300);

        name.draw(batch, "SONG NAME", 600, 370);
        artist.draw(batch, "ARTIST NAME", 600, 350);
        album.draw(batch, "ALBUM", 600, 330);

        batch.end();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        g.setScreen( new PlayerScreen(11000));

    }

    @Override
    public void resize(int width, int height){
    }

    @Override
    public void show(){

    }

    @Override
    public void hide(){

    }

    @Override
    public void dispose(){
    }

    @Override
    public void pause(){

    }

    @Override
    public void resume(){

    }
}
