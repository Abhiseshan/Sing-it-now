package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.sound.sampled.*;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.file.Paths;

public class PlayerScreen implements Screen{

    SpriteBatch batch;

    private BitmapFont artist;
    private BitmapFont name;
    private BitmapFont album;
    private Texture album_art;
    private Texture back_button;
    private BitmapFont songName;
    private BitmapFont lyric_text;
    private BitmapFont timer_text;
    private ShapeRenderer shapeRenderer;

    private String songName_str = "";
    private String album_str = "";
    private String artist_str = "";
    private String album_art_str = "";
    private String lyrics_str = "";
    private String mp3_str = "";
    private String background = "background.jpg";

    static AudioInputStream ais;
    static AudioFormat format;
    static boolean status = true;
    static int port = 50005;
    static int sampleRate = 16000;//16000

    static DataLine.Info dataLineInfo;
    static SourceDataLine sourceDataLine;

    private boolean hasEnded = false;
    private LyricFile[] lyrics = new LyricFile[100];
    private String lyric =  "";
    private String timer = "";
    private int progress = 0;

    Game g;

    public PlayerScreen(Game g, String songId){
        this.g = g;
        getSongDetails(songId);
        create();
        Thread thread = new Thread(){
            public void run() {
                play_music();
            }
        };

        thread.start();
    }

    private void create(){

        System.out.println("I am here motherfucker, in the other fucking class.");

        batch = new SpriteBatch();
        album_art = new Texture(album_art_str);
        back_button = new Texture("back_button.png");
        artist = new BitmapFont();
        name = new BitmapFont();
        album = new BitmapFont();

        lyric_text = new BitmapFont();
        lyric_text.setColor(Color.WHITE);
        lyric_text.getData().setScale(2);
        timer_text = new BitmapFont();
        timer_text.setColor(Color.WHITE);
        timer_text.getData().setScale(7);
        timer_text.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        songName = new BitmapFont();
        songName.setColor(Color.WHITE);
        songName.getData().setScale(2);
        artist = new BitmapFont();
        artist.setColor(Color.WHITE);
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render(float delta){
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(new Texture(background), 0, 0);
        batch.draw(album_art, 600, 400, 300, 300);
        batch.draw(back_button, 50, 650 );

        name.draw(batch, songName_str, 600, 370);
        artist.draw(batch, artist_str, 600, 350);
        album.draw(batch, album_str, 600, 330);

        lyric_text.draw(batch, lyric, 300, 300);
        timer_text.draw(batch, timer, 550, 150);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(250, 50, progress, 5);
        shapeRenderer.end();

        if (hasEnded){
            g.setScreen( new SelectionScreen(g));
        }

        batch.end();
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

    private void getSongDetails(String songId){
        boolean found = false;
        for (int i=0; i<10 && !found; i++){
            if (songId.equals(MyGdxGame.tracks[i].getIdentifier() + "")){
                songName_str = MyGdxGame.tracks[i].getSongName();
                album_art_str = MyGdxGame.tracks[i].getAlbum_art();
                artist_str = MyGdxGame.tracks[i].getArtist();
                album_str = MyGdxGame.tracks[i].getAlbum();
                lyrics_str = MyGdxGame.tracks[i].getLyrics();
                mp3_str = MyGdxGame.tracks[i].getMp3();
                background = MyGdxGame.tracks[i].getBackground();
                found = true;
            }
        }
    }

    private void getLyricsFromFile(){
        BufferedReader br = null;
        int i = 0;

        try {

            String sCurrentLine;

            br = new BufferedReader(new FileReader(lyrics_str));

            while ((sCurrentLine = br.readLine()) != null) {
                //System.out.println(sCurrentLine);
                lyrics[i] = new LyricFile(sCurrentLine.substring(0, 3), sCurrentLine.substring(4));
                //System.out.println(lyrics[i].getTime() + " " + lyrics[i].getLyric());
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void updateLyric(double duration){
        int seconds = 0;
        int pos = 0;

        while (seconds < duration ){
            //System.out.println(lyrics[pos].getTime() + " " + seconds + " " + duration);
            if (lyrics[pos].getTime().startsWith("0")) lyrics[pos].setTime(lyrics[pos].getTime().substring(1));
            if (lyrics[pos] != null && lyrics[pos].getTime().equals(seconds + "")){
                lyric = lyrics[pos].getLyric();
                //System.out.println(lyric);
                pos++;
            }
            try {
                //Thread.currentThread().wait(1000);
                Thread.sleep(1000);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            timer = seconds + "";
            seconds++;
            progress = (int) (300 * (seconds/duration));
            //System.out.println(progress);
        }

        hasEnded = true;
    }

    private void play_music(){
        DatagramSocket serverSocket = null;
        try {
            serverSocket = new DatagramSocket(port);
        } catch (SocketException e){
            e.printStackTrace();
        }

        //System.out.println("Server Running");
        /**
         * Formula for lag = (byte_size/sample_rate)*2]
         * Byte size 9728 will produce ~ 0.45 seconds of lag. Voice slightly broken.
         * Byte size 1400 will produce ~ 0.06 seconds of lag. Voice extremely broken.
         * Byte size 4000 will produce ~ 0.18 seconds of lag. Voice slightly more broken then 9728.
         */

        Thread lyricThread = new Thread(){
            public void run(){
                getLyricsFromFile();
            }
        };

        lyricThread.start();

        //System.out.println("Run motherfucker");

        Thread thread = new Thread(){
            public void run() {
                JFXPanel fxPanel = new JFXPanel();
                //System.out.println("Thread Running");
                final Media hit = new Media(Paths.get(mp3_str).toUri().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(hit);
                mediaPlayer.play();
                mediaPlayer.setOnReady(new Runnable() {
                    @Override
                    public void run() {
                        //System.out.println("Seconds = " + hit.getDuration().toSeconds());
                        updateLyric(hit.getDuration().toSeconds());
                    }
                });
            }
        };

        thread.start();

        byte[] receiveData = new byte[1100];//1280

        try {
            format = new AudioFormat(sampleRate, 16, 1, true, false);
            dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(format);
            sourceDataLine.start();
        } catch (LineUnavailableException e){
            e.printStackTrace();
        }

        FloatControl volumeControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
        volumeControl.setValue(1.00f);

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        ByteArrayInputStream bias = new ByteArrayInputStream(receivePacket.getData());

        //Reducing the volume of the stream
        //FloatControl volume = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
        //volume.setValue(-10.0F);

        while (status) {
            try {
                serverSocket.receive(receivePacket);
            } catch (IOException e){
                e.printStackTrace();
            }
            ais = new AudioInputStream(bias, format, receivePacket.getLength());

            toSpeaker(receivePacket.getData());
        }
        sourceDataLine.drain();
        sourceDataLine.close();
    }

    public static void toSpeaker(byte soundbytes[]) {
        try {
            sourceDataLine.write(soundbytes, 0, soundbytes.length);

        } catch (Exception e) {
            System.out.println("Not working in speakers...");
            e.printStackTrace();
        } finally {
            //System.out.println("Receiving Byte.");
        }
    }
}
