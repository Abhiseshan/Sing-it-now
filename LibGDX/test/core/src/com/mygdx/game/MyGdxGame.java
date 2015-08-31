package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
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
import java.io.*;
import java.net.*;
import java.nio.file.Paths;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture cover;
    private BitmapFont lyric_text;
	private BitmapFont timer_text;
    private String roomID;
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
	private int progress = 0;

	private LyricFile[] lyrics = new LyricFile[100];

    @Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("background.jpg");
		cover = new Texture("test_cover.jpg");
        lyric_text = new BitmapFont();
        lyric_text.setColor(Color.WHITE);
		timer_text = new BitmapFont();
		timer_text.setColor(Color.WHITE);
		shapeRenderer = new ShapeRenderer();
    }

	@Override
	public void render() {
        Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
        batch.draw(background, 0, 0);
		batch.draw(cover, 50, 50, 150, 150);
		lyric_text.draw(batch, lyric, 300, 300);
		timer_text.draw(batch, timer, 400, 300);
		batch.end();
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(Color.WHITE);
		shapeRenderer.rect(250, 50, progress, 5);
		shapeRenderer.end();

		Thread thread = new Thread(){
			public void run() {
				main();
			}
		};

		if (!hasBegun) {
			thread.start();
			hasBegun = true;
		}
	}

	private void getLyricsFromFile(){
		BufferedReader br = null;
		int i = 0;

		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader("test.txt"));

			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println(sCurrentLine);
				lyrics[i] = new LyricFile(sCurrentLine.substring(0, 2), sCurrentLine.substring(3));
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

	private void main(){
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

		http = new MyGdxGame();
        try {
            http.registerRoom();
        } catch (Exception e){
            e.printStackTrace();
        }

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
}
