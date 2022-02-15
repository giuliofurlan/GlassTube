package com.giufu.youtube;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.giufu.youtube.utils.GlassGestureDetector;
import com.giufu.youtube.utils.YoutubeConfig;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity
        implements GlassGestureDetector.OnGestureListener {

    private GlassGestureDetector glassGestureDetector;
    private String JsonResults;
    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> ids = new ArrayList<>();
    private ArrayList<String> thumbnails = new ArrayList<>();
    private TextView titleTextVies;
    private ImageView thumbnailView;
    private ProgressBar progressBar;
    private int current_video = 0;
    private int videosCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.results_activity);
        glassGestureDetector = new GlassGestureDetector(this, this);
        titleTextVies = findViewById(R.id.title_text_view);
        thumbnailView = findViewById(R.id.thumbnail_image_view);
        progressBar = findViewById(R.id.progressBar);

        Intent intent = getIntent();
        String value = intent.getStringExtra("query");
        if(value!=null){
            new JsonTask().execute(value);
        }
        else
        {
            String jsonVideoList = intent.getStringExtra("list");

        }

    }

    private void openVideo(String id){
        Intent intent = new Intent(this,VideoActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    private void updatePreview(){
        titleTextVies.setText(titles.get(current_video));
        Glide.with(this)
            .load(thumbnails.get(current_video))
            .override(480, 270)
            .into(thumbnailView);
        progressBar.setProgress(current_video+1);
    }

    private void displayResults(){
        try {
            JSONObject jsonObject = new JSONObject(JsonResults);
            JSONArray items = jsonObject.getJSONArray("items");
            for (int i=0; i < items.length(); i++) {
                String title = items.getJSONObject(i)
                        .getJSONObject("snippet")
                        .getString("title");
                String id = items.getJSONObject(i)
                        .getJSONObject("id")
                        .getString("videoId");
                String thumbnail = items.getJSONObject(i)
                        .getJSONObject("snippet")
                        .getJSONObject("thumbnails")
                        .getJSONObject("medium")
                        .getString("url");
                titles.add(title);
                ids.add(id);
                thumbnails.add(thumbnail);
                videosCount++;
                updatePreview();
            }
            progressBar.setMax(videosCount);
        }
        catch (Exception e) { e.printStackTrace(); }
    }
    //for EE2
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return glassGestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }
    //for XE - EE1
    @Override
    public boolean onGenericMotionEvent(MotionEvent ev) {
        return glassGestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {
        switch (gesture) {
            case TAP:
                openVideo(ids.get(current_video));
                return true;
            case SWIPE_BACKWARD: //next video
                if(current_video<(videosCount-1)){
                    current_video++;
                }
                else {
                    current_video=0;
                }
                updatePreview();
                return true;
            case SWIPE_FORWARD: //previous video
                if(current_video>0){
                    current_video--;
                }
                else {
                    current_video=videosCount-1;
                }
                updatePreview();
                return true;
            case SWIPE_DOWN:
                finish();
                return true;
            default:
                return false;
        }
    }

    private class JsonTask extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();

        }
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                String q = params[0].replace(" ", "%20");//ez url encoding, needed here
                URL url = new URL(YoutubeConfig.getApiUrl()+q);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                return buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JsonResults = result;
            displayResults();
        }
    }
}
