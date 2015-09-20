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
	Texture background;
	Texture cover;

	public static String RoomID = "";

    private BitmapFont lyric_text;
	private BitmapFont timer_text;
	private BitmapFont name_text;
	private BitmapFont artist_text;
	private BitmapFont welcome_text;
    private String roomID = "";
	private ShapeRenderer shapeRenderer;
    static MyGdxGame http;

    static AudioInputStream ais;
    static AudioFormat format;
    static boolean status = true;
    static int port = 50005;
    static int sampleRate = 16000;//16000

    static DataLine.Info dataLineInfo;
    static SourceDataLine sourceDataLine;

	private boolean hasBegun = false;

	private String lyric =  "";
	private String timer = "";
	private String name = "SONG NAME";
	private String artist = "SONG ARTIST";
	private String welcome = "WELCOME";
	private String key = "Press space to continue";
	private int progress = 0;
	int scene = 1;
	public BitmapFont font;


	private LyricFile[] lyrics = new LyricFile[100];
	public static TrackID[] tracks = new TrackID[10];

    @Override
	public void create () {

		batch = new SpriteBatch();
		//Use LibGDX's default Arial font.
		font = new BitmapFont();

		populate_list();

		setScreen(new StartScreen(this));
		/*
		batch = new SpriteBatch();
		background = new Texture("background.jpg");
		cover = new Texture("test_cover.jpg");
        lyric_text = new BitmapFont();
        lyric_text.setColor(Color.WHITE);
		lyric_text.getData().setScale(2);
		timer_text = new BitmapFont();
		timer_text.setColor(Color.WHITE);
		timer_text.getData().setScale(7);
		timer_text.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		name_text = new BitmapFont();
		name_text.setColor(Color.WHITE);
		name_text.getData().setScale(2);
		artist_text = new BitmapFont();
		artist_text.setColor(Color.WHITE);
		welcome_text = new BitmapFont();
		welcome_text.setColor(Color.WHITE);
		welcome_text.getData().setScale(3);
		shapeRenderer = new ShapeRenderer();
		*/
    }

	@Override
	public void render() {


		super.render();

		//Gdx.gl.glClearColor(1, 0, 0, 1);
		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		/*
		switch (scene){
			case 1:
				//Welcome Screen
				Gdx.gl.glClearColor(1, 0, 0, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				batch.begin();
				batch.draw(background, 0, 0);
				welcome_text.draw(batch, welcome, 250, 300);
				artist_text.draw(batch, key, 250, 250);
				timer_text.draw(batch, roomID, 250, 200);
				batch.end();

				Thread register = new Thread(){
					public void run() {
						try {
							registerRoom();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};

				if (!hasBegun) {
					register.start();
					hasBegun = true;
				}

				if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
					scene = 3;
					hasBegun = false;
				}
				break;
			case 2:
				//Music Selection Screen

			case 3:
				//Play Music and Initalise UI Elements corresponding to that
				Gdx.gl.glClearColor(1, 0, 0, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				batch.begin();
				batch.draw(background, 0, 0);
				batch.draw(cover, 50, 50, 150, 150);
				lyric_text.draw(batch, lyric, 300, 300);
				timer_text.draw(batch, timer, 550, 150);
				name_text.draw(batch, name, 250, 200);
				artist_text.draw(batch, artist, 250, 160);
				batch.end();
				shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
				shapeRenderer.setColor(Color.WHITE);
				shapeRenderer.rect(250, 50, progress, 5);
				shapeRenderer.end();

				Thread thread = new Thread(){
					public void run() {
						play_music();
					}
				};

				if (!hasBegun) {
					thread.start();
					hasBegun = true;
				}
				break;
			default:
				scene = 1;
		}
		*/
	}

	private void getLyricsFromFile(){
		BufferedReader br = null;
		int i = 0;

		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader("test.txt"));

			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println(sCurrentLine);
				lyrics[i] = new LyricFile(sCurrentLine.substring(0, 3), sCurrentLine.substring(4));
				System.out.println(lyrics[i].getTime() + " " + lyrics[i].getLyric());
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
			System.out.println(progress);
		}
	}

	private void play_music(){
        DatagramSocket serverSocket = null;
        try {
            serverSocket = new DatagramSocket(port);
        } catch (SocketException e){
            e.printStackTrace();
        }

        /**
         * Formula for lag = (byte_size/sample_rate)*2]
         * Byte size 9728 will produce ~ 0.45 seconds of lag. Voice slightly broken.
         * Byte size 1400 will produce ~ 0.06 seconds of lag. Voice extremely broken.
         * Byte size 4000 will produce ~ 0.18 seconds of lag. Voice slightly more broken then 9728.
         */

		System.out.println("Starting Server.");

		Thread lyricThread = new Thread(){
			public void run(){
				getLyricsFromFile();
			}
		};

		lyricThread.run();

		Thread thread = new Thread(){
				public void run() {
					JFXPanel fxPanel = new JFXPanel();
					System.out.println("Thread Running");
					final Media hit = new Media(Paths.get("test.mp3").toUri().toString());
					MediaPlayer mediaPlayer = new MediaPlayer(hit);
					mediaPlayer.play();
					mediaPlayer.setOnReady(new Runnable() {
						@Override
						public void run() {
							System.out.println("Seconds = " + hit.getDuration().toSeconds());
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
			System.out.println("Receiving Byte.");
		}
	}

	private void registerRoom() throws Exception {
		String url = "http://spacecharge.co.nf/php/room_add.php";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		//con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		InetAddress IP = InetAddress.getLocalHost();

		//System.out.print(IP.getHostAddress());
		String urlParameters = "ipAdd=" + IP.getHostAddress();

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuilder response = new StringBuilder();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		//print result
		System.out.println("Room Code: " + response.toString());
		roomID = response.toString();
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
