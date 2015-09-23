package me.abhiseshan.streamingtest;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetRoomIPAsyncTask extends AsyncTask<String, String, Boolean> {

    String roomID;

    public GetRoomIPAsyncTask(String roomID){
        this.roomID = roomID;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected Boolean doInBackground(String... args) {
        Boolean connected;
        HttpURLConnection connection;
        OutputStreamWriter request;

        URL url;
        String response = "";
        String parameters = "roomid="+roomID;
        try
        {
            url = new URL("http://spacecharge.co.nf/php/room_get.php");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");

            request = new OutputStreamWriter(connection.getOutputStream());
            request.write(parameters);
            request.flush();
            request.close();
            String line;
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
                sb.append(line + "\n");
            response = sb.toString();
            if (response.equals("0") || "0".equals(response.trim())) {
                Log.d("Code Invalid", "Error registering to server");
                connected = false;
            }
            else{
                Log.d("Registered", "Registered with id: " + response);
                connected = true;
                RoomCodeActivity.IP = response;
                RoomCodeActivity.connected = connected;
            }
            Log.d("Server Message", "Message from Server: " + response);
            isr.close();
            reader.close();
        }
        catch(IOException e)
        {
            connected = false;
            e.printStackTrace();
        }
        return connected;
    }

    protected void onPostExecute(Boolean connected){
         RoomCodeActivity.connected = connected;
    }
}