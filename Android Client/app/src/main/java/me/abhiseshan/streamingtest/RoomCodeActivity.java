package me.abhiseshan.streamingtest;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class RoomCodeActivity extends AppCompatActivity {

    private TextView codeTextView;
    private ImageView connectImageView;
    private String roomCode = "";
    public static Boolean connected = false;
    public static String IP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_code);

        codeTextView = (TextView) findViewById(R.id.codeTextView);

        connectImageView = (ImageView) findViewById(R.id.connect_Image_view);
        connectImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new GetRoomIPAsyncTask(codeTextView.getText().toString()).execute().get();
                    if (connected){
                        Intent intent = new Intent(RoomCodeActivity.this, SelectionActivity.class);
                        intent.putExtra("IP", IP);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Invalid Room Code", Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException e) {
                    Log.d("Exception", "Interrupted Exception");
                    e.printStackTrace();
                } catch (ExecutionException ex){
                    Log.d("Exception","Execution Exception");
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_room_code, menu);
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

    public void onNumberPressed(View v){
        Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);

        if (roomCode.length()<4) {
            Button numberButton = (Button) v;
            roomCode += numberButton.getText().toString();
            updateView();
        }
    }

    public void onBackSpacePressed(View v){
        Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);

        if (roomCode.length()>0) {
            roomCode = roomCode.substring(0, roomCode.length() - 1);
            updateView();
        }
    }

    private void updateView(){
        int red = getResources().getColor(R.color.background_dark);
        int green = getResources().getColor(R.color.background_green);
        int duration = 200;
        codeTextView.setText(roomCode);
        if (roomCode.length() == 4){
            ObjectAnimator.ofObject(connectImageView, "backgroundColor", new ArgbEvaluator(), red, green)
                    .setDuration(duration)
                    .start();
            ObjectAnimator.ofObject(codeTextView, "backgroundColor", new ArgbEvaluator(), red, green)
                    .setDuration(duration)
                    .start();
        }
        else {
            ObjectAnimator.ofObject(connectImageView, "backgroundColor", new ArgbEvaluator(), green, red)
                    .setDuration(duration)
                    .start();
            ObjectAnimator.ofObject(codeTextView, "backgroundColor", new ArgbEvaluator(), green, red)
                    .setDuration(duration)
                    .start();
        }
    }
}
