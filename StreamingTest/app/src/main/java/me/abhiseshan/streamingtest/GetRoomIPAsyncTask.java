package me.abhiseshan.streamingtest;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetRoomIPAsyncTask extends AsyncTask<String, String, String> {

    String roomID;

    public GetRoomIPAsyncTask(String roomID){
        this.roomID = roomID;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected String doInBackground(String... args) {
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
            if (response.equals("0"))
                Log.d("Code Invalid", "Error registering to server");
            else{
                Log.d("Registered", "Registered with id: " + response);
                MainActivity.IP = response;
            }
            Log.d("Server Message", "Message from Server: " + response);
            isr.close();
            reader.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return response;
    }

    protected void onPostExecute(String file_url){
        //MainActivity.IP = file_url;
    }
}