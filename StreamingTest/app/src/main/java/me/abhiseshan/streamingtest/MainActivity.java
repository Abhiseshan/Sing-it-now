package me.abhiseshan.streamingtest;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();

        final LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);

        final TextView welcomeTextView = (TextView) findViewById(R.id.welcomeTextView);
        loginButton.setReadPermissions("user_friends");
        // If using in a fragment
        // Other app specific specialization
        final ProfilePictureView profileView = (ProfilePictureView) findViewById(R.id.profile_image);

        if (com.facebook.AccessToken.getCurrentAccessToken()!=null) {
            final Profile profile = Profile.getCurrentProfile();

            String urlProfileFacebook = "https://graph.facebook.com/" + profile.getId() + "/picture?type=large";
            profileView.animate();
            profileView.setProfileId(profile.getId());
            //profileView.animate();
            /*try{
                URL profilePic= new URL(urlProfileFacebook);
                Bitmap bitmap= (Bitmap) profilePic.getContent();
                profileView.setImageBitmap(bitmap);
            }
            catch(IOException c){};*/


            /*Glide.with(getApplicationContext())
                    .load(urlProfileFacebook)
                    .into(profileView);*/


            welcomeTextView.setText("Hi there,\n" + profile.getFirstName() + " " + profile.getLastName());
            loginButton.setVisibility(View.INVISIBLE);
        } else {
            profileView.setVisibility(View.INVISIBLE);
            welcomeTextView.setText("Please Log into facebook");
            loginButton.setVisibility(View.VISIBLE);
        }

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                final Profile profile = Profile.getCurrentProfile();
                String urlProfileFacebook = "https://graph.facebook.com/" + profile.getId() + "/picture?type=large";
                profileView.setProfileId(profile.getId());
                /*Glide.with(getApplicationContext())
                        .load(urlProfileFacebook)
                        .into(profileView);*/
                final ProfilePictureView profileView = (ProfilePictureView) findViewById(R.id.profile_image);
                profileView.setProfileId(profile.getId());
                profileView.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.INVISIBLE);
                welcomeTextView.setText("Hi there,\n" + profile.getFirstName() + " " + profile.getLastName());
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        RelativeLayout screenContainer = (RelativeLayout) findViewById(R.id.screen_container);
        screenContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RoomCodeActivity.class);
                startActivity(intent);
            }
        });

        Shimmer shimmer = new Shimmer();
        shimmer.start((ShimmerTextView) findViewById(R.id.shimmer_tv));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
