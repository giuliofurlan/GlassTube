package com.giufu.youtube;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.giufu.youtube.utils.GlassGestureDetector;
import com.giufu.youtube.utils.YoutubeConfig;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ResultsActivity extends AppCompatActivity
        implements GlassGestureDetector.OnGestureListener {

    private GlassGestureDetector glassGestureDetector;
    private String JsonResults;
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    TextView titleTextVies;
    int current_video = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.results_activity);
        Intent intent = getIntent();
        String value = intent.getStringExtra("query");
        glassGestureDetector = new GlassGestureDetector(this, this);
        new JsonTask().execute(value);
        titleTextVies = findViewById(R.id.title_text_view);
    }

    void openVideo(String id){
        Intent intent = new Intent(this,VideoActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    void updatePreview(){
        titleTextVies.setText(titles.get(current_video));
    }

    void diplayResults(){
        try {
            JSONObject jsonObject = new JSONObject(JsonResults);
            JSONArray items = jsonObject.getJSONArray("items");
            for (int i=0; i < items.length(); i++) {
                String title = items.getJSONObject(i).getJSONObject("snippet").getString("title");
                String id = items.getJSONObject(i).getJSONObject("id").getString("videoId");
                titles.add(title);
                ids.add(id);
                updatePreview();
            }
        }
        catch (Exception e) { e.printStackTrace(); }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return glassGestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent ev) {
        return glassGestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {
        switch (gesture) {
            case TAP:
                Log.d("App", "TAPPED!");
                openVideo(ids.get(current_video));
                return true;
            case SWIPE_FORWARD:
                if(current_video<4){
                    current_video++;
                    updatePreview();
                }
                else {
                    current_video=0;
                    updatePreview();
                }
                return true;
            case SWIPE_BACKWARD:
                if(current_video>0){
                    current_video++;
                    updatePreview();
                }
                else {
                    current_video=0;
                    updatePreview();
                }
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
                String q = params[0].replace(" ", "%20");//ez url encoding, needed here but not in webview lol
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
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JsonResults = result;
            diplayResults();
        }
    }


}
