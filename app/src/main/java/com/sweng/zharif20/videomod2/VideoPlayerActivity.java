package com.sweng.zharif20.videomod2;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

public class VideoPlayerActivity extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, VideoControllerView.MediaPlayerControl {

    SurfaceView videoSurface;
    MediaPlayer player;
    VideoControllerView controller;
    private int position = 0;
    private boolean mFullScreen;
    private boolean mMute;
    public String vidUrl = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoSurface = (SurfaceView) findViewById(R.id.videoSurface);
        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(this);

        player = new MediaPlayer();
        controller = new VideoControllerView(this);

        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(this, Uri.parse(vidUrl));

            /*player.seekTo(position);
            if (position == 0) {
                player.start();
            } else {
                //if from the resume activity, video playback will be paused
                player.pause();
            }*/

            player.setOnPreparedListener(this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controller.show();
        return false;
    }

    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        player.setDisplay(holder);
        player.prepareAsync();

        //Get the dimensions of the video
        int videoWidth = player.getVideoWidth();
        int videoHeight = player.getVideoHeight();

        //Get the width of the screen
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();

        //Get the SurfaceView layout parameters
        android.view.ViewGroup.LayoutParams lp = videoSurface.getLayoutParams();

        //Set the width of the SurfaceView to the width of the screen
        lp.width = screenWidth;

        //Set the height of the SurfaceView to match the aspect ratio of the video
        //be sure to cast these as floats otherwise the calculation will likely be 0
        lp.height = (int) (((float)videoHeight / (float)videoWidth) * (float)screenWidth);

        //Commit the layout parameters
        videoSurface.setLayoutParams(lp);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    // End SurfaceHolder.Callback

    // Implement MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {
        controller.setMediaPlayer(this);
        controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));

        //pgDialog.dismiss();
        //if we have a position on savedInstanceState, the video playback should start from here
        player.seekTo(position);
        if (position == 0) {
            player.start();
        } else {
            //if from the resume activity, video playback will be paused
            player.pause();
        }
    }
    // End MediaPlayer.OnPreparedListener

    // Implement VideoMediaController.MediaPlayerControl
    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return player.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public void pause() {
        player.pause();
    }

    @Override
    public void seekTo(int i) {
        player.seekTo(i);
    }

    @Override
    public void start() {
        player.start();
    }

    @Override
    public boolean isFullScreen() {
            if(mFullScreen){
            Log.v("FullScreen", "--set icon full screen--");
        return false;
        }else{
            Log.v("FullScreen", "--set icon small full screen--");
            return true;
        }
    }

    @Override
    public boolean isMute() {
        if (mMute) {
            Log.v("Unmute", "--set icon unmute--");
            return false;
        }else{
            Log.v("Mute", "--set icon mute--");
            return true;
        }

    }

    @Override
    public void toggleFullScreen() {
        Log.v("FullScreen", "-----------------click toggleFullScreen-----------");
        setFullScreen(isFullScreen());

    }

    @Override
    public void toggleMute() {
        Log.v("Mute", "-----------------click toggleMute-----------");
        setMute(isMute());

    }

    private void setMute(boolean mute) {
        AudioManager aManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mute = false;
        if (mMute) {
            aManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            //set icon mute
            mMute = mute;

        }else {
            aManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            //set icon mute
            mMute = !mute;
        }
    }

    private void setFullScreen(boolean fullScreen) {
        fullScreen = false;

        if (mFullScreen)

        {
            Log.v("FullScreen", "-----------Set full screen SCREEN_ORIENTATION_LANDSCAPE------------");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;
            android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) videoSurface.getLayoutParams();
            params.width = width;
            params.height=height;
            params.setMargins(0, 0, 0, 0);
            //set icon is full screen
            mFullScreen = fullScreen;
        }
        else{
            Log.v("FullScreen", "-----------Set small screen SCREEN_ORIENTATION_PORTRAIT------------");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            final FrameLayout mFrame = (FrameLayout) findViewById(R.id.videoSurfaceContainer);
            // int height = displaymetrics.heightPixels;
            int height = mFrame.getHeight();//get height Frame Container video
            int width = displaymetrics.widthPixels;
            android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) videoSurface.getLayoutParams();
            params.width = width;
            params.height= height;
            params.setMargins(0, 0, 0, 0);
            //set icon is small screen
            mFullScreen = !fullScreen;
        }
    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//        // Checks the orientation of the screen
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
//            // setContentView(R.layout.view_lang);
//            DisplayMetrics displaymetrics = new DisplayMetrics();
//            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//            int height = displaymetrics.heightPixels;
//            int width = displaymetrics.widthPixels;
//            android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) videoSurface.getLayoutParams();
//            params.width = width;
//            params.height=height;// -80 for android controls
//            params.setMargins(0, 0, 0, 0);
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
//            //setContentView(R.layout.view);
//            DisplayMetrics displaymetrics = new DisplayMetrics();
//            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//            int height = displaymetrics.heightPixels;
//            int width = displaymetrics.widthPixels;
//            android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) videoSurface.getLayoutParams();
//            params.width = width;
//            params.height=height / 3;
//            params.setMargins(0, 0, 0, 0);
//        }
//    }

    //recreating activity

//    @Override
//    protected void onSaveInstanceState(Bundle savedInstanceState) {
//        super.onSaveInstanceState(savedInstanceState);
//        //use onSaveInstanceState in order to store the video playback position for orientation change
//        savedInstanceState.putInt("Position", player.getCurrentPosition());
//        player.pause();
//    }
//
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        //use onRestoreInstanceState in order to play the video playback from the stored position
//        position = savedInstanceState.getInt("Position");
//        player.seekTo(position);
//
//    }
}
