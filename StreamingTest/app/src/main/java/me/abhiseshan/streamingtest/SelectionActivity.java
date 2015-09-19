package me.abhiseshan.streamingtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SelectionActivity extends AppCompatActivity {

    String IP;

    List<AlbumArt> albums = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        Intent intent = getIntent();
        IP = intent.getStringExtra("IP");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final GridView albumArtGridView = (GridView) findViewById(R.id.albumGridView);

        populate();

        final AlbumArtAdapter adapter = new AlbumArtAdapter(SelectionActivity.this, R.layout.gridview_album_art, albums.toArray(new AlbumArt[albums.size()]));
        albumArtGridView.setAdapter(adapter);

        albumArtGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final AlbumArt albumArt = (AlbumArt) albumArtGridView.getAdapter().getItem(position);
                Toast.makeText(getApplicationContext(),"" + albumArt.id, Toast.LENGTH_SHORT).show();
                Thread dataThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Socket socket = new Socket(IP.trim(), 5000);
                            DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());
                            DOS.writeUTF(albumArt.id + "");
                            socket.close();
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                });
                dataThread.start();
            }
        });
        /*RelativeLayout screenContainer = (RelativeLayout) findViewById(R.id.screen_container);
        screenContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectionActivity.this, RoomCodeActivity.class);
                intent.putExtra("IP", IP);
                startActivity(intent);
            }
        }); */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectionActivity.this, RecorderActivity.class);
                intent.putExtra("IP", IP);
                Thread dataThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Socket socket = new Socket(IP.trim(), 5000);
                            DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());
                            DOS.writeUTF("00");
                            socket.close();
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                });
                dataThread.start();
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void populate(){
        albums.add(new AlbumArt("http://img1.wikia.nocookie.net/__cb20130918124300/lyricwiki/images/2/23/Ylvis_-_The_Fox.jpg", "The Fox","Ylvis", 11000));
        albums.add(new AlbumArt("https://s-media-cache-ak0.pinimg.com/236x/ac/df/79/acdf79dfb65df7cd0bc79734ce4a0591.jpg", "The Scientist","Coldplay", 11555));
        albums.add(new AlbumArt("http://www.music-bazaar.com/album-images/vol18/816/816904/2670698-big/Blank-Space-Single-cover.jpg", "Blank Space", "Taylor Swift", 11587));
        albums.add(new AlbumArt("http://www.josepvinaixa.com/blog/wp-content/uploads/2013/05/Imagine-Dragons-Demons-2013-1200x1200.png", "Demons", "Imagine Dragons", 33625));
        albums.add(new AlbumArt("http://streamd.hitparade.ch/cdimages/the_script-for_the_first_time_s_1.jpg", "For The First Time", "The Script", 84756));
    }
}
