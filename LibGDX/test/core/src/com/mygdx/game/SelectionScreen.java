package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class SelectionScreen implements Screen{

    SpriteBatch batch;
    private BitmapFont artist;
    private BitmapFont name;
    private BitmapFont album;
    private Texture album_art;

    private String songName = "";
    private String songArtist = "";
    private String songAlbum = "";
    private String songAlbumArt = "test_cover.jpg";
    private String background = "background.jpg";

    Game g;

    String msg_received = "1000";
    String temp = "1000";

    public SelectionScreen(Game g){
        this.g = g;
        create();
    }

    private void getSongDetails(String songId){
        boolean found = false;
        for (int i=0; i<10 && !found; i++){
            if (songId.equals(MyGdxGame.tracks[i].getIdentifier()+ "")){
                songName = MyGdxGame.tracks[i].getSongName();
                songAlbumArt = MyGdxGame.tracks[i].getAlbum_art();
                songArtist = MyGdxGame.tracks[i].getArtist();
                songAlbum = MyGdxGame.tracks[i].getAlbum();
                background = MyGdxGame.tracks[i].getBackground();
                System.out.println("found");
                found = true;
            }
        }
    }

    private void create(){
        batch = new SpriteBatch();
        album_art = new Texture("test_cover.jpg");
        artist = new BitmapFont();
        name = new BitmapFont();
        album = new BitmapFont();


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                receiver();
            }
        });

        thread.start();
    }

    private void receiver(){
        while (!temp.equals("00")) {
            try {
                ServerSocket socket = new ServerSocket(5000);
                Socket clientSocket = socket.accept();       //This is blocking. It will wait.
                DataInputStream DIS = new DataInputStream(clientSocket.getInputStream());
                //msg_received = DIS.readUTF();
                temp = DIS.readUTF();
                if (!temp.trim().equals("00")) msg_received = temp;
                System.out.println(msg_received);
                if (!msg_received.equals("1000") && !msg_received.equals("00")) getSongDetails(msg_received.trim());
                clientSocket.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void render(float delta){
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(new Texture(background), 0, 0);
        //batch.draw(album_art, 600, 400, 300, 300);

        batch.draw(new Texture(songAlbumArt), 533, 400, 300, 300);

        name.draw(batch, songName, 600, 370);
        artist.draw(batch, songArtist, 600, 350);
        album.draw(batch, songAlbum, 600, 330);
        album.draw(batch, "Please Connect Using Code " + MyGdxGame.RoomID, 300, 100);

        batch.end();

        if (temp.equals("00")){
            System.out.println("I am here motherfucker");
            g.setScreen(new PlayerScreen(g, msg_received.trim()));
        }
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
