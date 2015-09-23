package me.abhiseshan.streamingtest;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;
import android.media.audiofx.AutomaticGainControl;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class RecorderActivity extends Activity {

    public static String IP;

    //public byte[] buffer;
    //public static DatagramSocket socket;
    private int port=50005;


    AudioRecord recorder;
    Boolean isAvailable = false;

    private int sampleRate = 16000; // 44100 for music

    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_AC3;
    int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, AudioFormat.ENCODING_PCM_16BIT); //audioFormat


    private boolean status = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("VS", "minBuff " +minBufSize);
        minBufSize=1100;
        Log.e("VS", "minBuff " +minBufSize);
        Log.e("VS", "8bit " +AudioFormat.ENCODING_PCM_8BIT);
        Log.e("VS", "16bit " +AudioFormat.ENCODING_PCM_16BIT);
        Log.e("VS", "ac3 " + AudioFormat.ENCODING_AC3);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);

        Intent intent = getIntent();
        IP = intent.getStringExtra("IP");

        //Button startButton = (Button) findViewById (R.id.start_button);
        //Button stopButton = (Button) findViewById (R.id.stop_button);

        ImageView start= (ImageView) findViewById(R.id.offMic);
        ImageView stop= (ImageView) findViewById(R.id.onMic);


        stop.setVisibility(View.INVISIBLE);
        start.setVisibility(View.VISIBLE);

        start.setOnClickListener(startListener);
        //startButton.setOnClickListener(startListener);
       // stopButton.setOnClickListener(stopListener);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            isAvailable = AcousticEchoCanceler.isAvailable();
    }

    private final OnClickListener stopListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            ImageView start= (ImageView) findViewById(R.id.offMic);
            ImageView stop= (ImageView) findViewById(R.id.onMic);
            stop.setVisibility(View.INVISIBLE);
            start.setVisibility(View.VISIBLE);
            status = false;
            recorder.release();
            start.setOnClickListener(startListener);
            Log.d("VS","Kraken released");
        }

    };

    private final OnClickListener startListener = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            ImageView start= (ImageView) findViewById(R.id.offMic);
            ImageView stop= (ImageView) findViewById(R.id.onMic);
            stop.setVisibility(View.VISIBLE);
            start.setVisibility(View.INVISIBLE);
            status = true;
            startStreaming();
            stop.setOnClickListener(stopListener);
        }

    };

    public void startStreaming() {


        Thread streamThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    final DatagramSocket socket = new DatagramSocket();
                    Log.d("VS", "Socket Created");

                    final byte[] buffer = new byte[minBufSize];

                    Log.d("VS","Buffer created of size " + minBufSize);


                    Log.d("IP Add", IP);

                    final InetAddress destination = InetAddress.getByName(IP.trim());
                    Log.d("VS", "Address retrieved");
                    //recorder= new AudioRecord()

                    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate,channelConfig,AudioFormat.ENCODING_PCM_16BIT,minBufSize*10); //audioFormat
                    Log.d("VS", "Recorder initialized");

                    if (isAvailable)
                        Log.d("Acoustic Echo Canceller", "Acoustic Echo Canceller is enabled");

                    if (isAvailable && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        AcousticEchoCanceler.create(recorder.getAudioSessionId());
                        NoiseSuppressor.create(recorder.getAudioSessionId());
                        AutomaticGainControl.create(recorder.getAudioSessionId());
                    }

                    recorder.startRecording();
                    Thread sendThread= new Thread (new Runnable() {
                        @Override
                        public void run() {

                        while(status)

                        {
                            final DatagramPacket packet;
                            //reading data from MIC into buffer
                            minBufSize = recorder.read(buffer, 0, buffer.length);

                            //putting buffer in the packet
                            packet = new DatagramPacket(buffer, buffer.length, destination, port);
                            try {
                                socket.send(packet);
                            }
                            catch(IOException e){
                                Log.e("VS", "IOException");
                            }
                            System.out.println("MinBufferSize: " + minBufSize);

                        }
                    }
                    });

                    sendThread.run();

                } catch(UnknownHostException e) {
                    Log.e("VS", "UnknownHostException");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("VS", "IOException");
                }
            }

        });
        streamThread.start();
    }
}