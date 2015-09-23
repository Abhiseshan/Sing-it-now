import javax.sound.sampled.*;
import java.io.*;
import java.net.*;
import java.nio.file.Paths;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Server{

    private String roomID;
    static Server http;

    static AudioInputStream ais;
    static AudioFormat format;
    static boolean status = true;
    static int port = 50005;
    static int sampleRate = 16000;//16000

    static DataLine.Info dataLineInfo;
    static SourceDataLine sourceDataLine;

    public static void main(String args[]) throws Exception {

        DatagramSocket serverSocket = new DatagramSocket(port);

        /**
         * Formula for lag = (byte_size/sample_rate)*2
         * Byte size 9728 will produce ~ 0.45 seconds of lag. Voice slightly broken.
         * Byte size 1400 will produce ~ 0.06 seconds of lag. Voice extremely broken.
         * Byte size 4000 will produce ~ 0.18 seconds of lag. Voice slightly more broken then 9728.
         */

        System.out.println("Starting Server.");

        http = new Server();
        http.registerRoom();

        Thread thread = new Thread(){
            public void run() {
                JFXPanel fxPanel = new JFXPanel();
                System.out.println("Thread Running");
                Media hit = new Media(Paths.get("music/test.mp3").toUri().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(hit);
                mediaPlayer.play();
            }
        };

        thread.start();

        byte[] receiveData = new byte[1100];//1280

        format = new AudioFormat(sampleRate, 16, 1, true, false);
        dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
        sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
        sourceDataLine.open(format);
        sourceDataLine.start();

        FloatControl volumeControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
        volumeControl.setValue(1.00f);

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        ByteArrayInputStream bias = new ByteArrayInputStream(receivePacket.getData());

        //Reducing the volume of the stream
        //FloatControl volume = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
        //volume.setValue(-10.0F);

        while (status) {
            serverSocket.receive(receivePacket);
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

    private void closeRoom() throws Exception {
        String url = "http://spacecharge.co.nf/php/room_close.php";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        //con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        //System.out.print(IP.getHostAddress());
        String urlParameters = "roomid=" + roomID;

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
        System.out.println(response.toString());
    }

    protected void finalize() throws Throwable {
        http.closeRoom();
        super.finalize();
    }
}