package com.giufu.youtube;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.giufu.youtube.utils.GlassGestureDetector;
import com.giufu.youtube.utils.GlassGestureDetector;
import com.giufu.youtube.utils.YoutubeConfig;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class VideoActivity extends YouTubeBaseActivity  implements GlassGestureDetector.OnGestureListener{
    YouTubePlayer.OnInitializedListener mOnInitializedListener;
    private final String TAG = "VideoActivyty";
    private GlassGestureDetector glassGestureDetector;
    YouTubePlayerView youTubePlayerView;
    boolean isPaused = false;
    YouTubePlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_activity);
        glassGestureDetector = new GlassGestureDetector(this, this);

        Intent intent = getIntent();
        String video_id = intent.getStringExtra("id");


        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.player);

        youTubePlayerView.initialize(YoutubeConfig.getApiKey(),
            new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                    YouTubePlayer youTubePlayer, boolean b) {
                    Log.d(TAG, "onInitializationSuccess: success");
                    // do any work here to cue video, play video, etc.
                    youTubePlayer.loadVideo(video_id);
                    player = youTubePlayer;
                }
                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                    YouTubeInitializationResult youTubeInitializationResult) {
                    Log.d(TAG, "onInitializationFailure: fail");
                }
            });
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
                if (isPaused){
                    player.play();
                }
                else{
                    player.pause();
                }
                isPaused = !isPaused;
                return true;

            case SWIPE_BACKWARD:
                player.seekToMillis(player.getCurrentTimeMillis()+30000);
                return true;
            case SWIPE_FORWARD:
                if (player.getCurrentTimeMillis()>30000) {
                    player.seekToMillis(player.getCurrentTimeMillis() - 30000);
                }
                return true;
            default:
                return false;
        }
    }
}
