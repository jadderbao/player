package com.fanwe.www.mediaplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fanwe.library.looper.ISDLooper;
import com.fanwe.library.looper.impl.SDSimpleLooper;
import com.fanwe.library.media.player.SDMediaPlayer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{

    private static final String TAG = "MainActivity";

    private SurfaceView sfv_media;

    private SDMediaPlayer mMediaPlayer = new SDMediaPlayer();

    private Button btn_start, btn_pause, btn_stop, btn_reset, btn_play_pause, btn_play_stop;
    private TextView tv_duration;
    private SeekBar sb_progress;
    private ISDLooper mLooper = new SDSimpleLooper();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sfv_media = (SurfaceView) findViewById(R.id.sfv_media);
        sb_progress = (SeekBar) findViewById(R.id.sb_progress);
        tv_duration = (TextView) findViewById(R.id.tv_duration);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_pause = (Button) findViewById(R.id.btn_pause);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_reset = (Button) findViewById(R.id.btn_reset);
        btn_play_pause = (Button) findViewById(R.id.btn_play_pause);
        btn_play_stop = (Button) findViewById(R.id.btn_play_stop);
        btn_start.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
        btn_play_pause.setOnClickListener(this);
        btn_play_stop.setOnClickListener(this);

        sb_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (fromUser)
                {
                    mMediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        sfv_media.getHolder().addCallback(new SurfaceHolder.Callback()
        {
            @Override
            public void surfaceCreated(SurfaceHolder holder)
            {
                mMediaPlayer.setDisplay(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
            {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder)
            {
            }
        });

        mMediaPlayer.setOnExceptionCallback(new SDMediaPlayer.OnExceptionCallback()
        {
            @Override
            public void onException(Exception e)
            {
                Log.i(TAG, "onException:" + String.valueOf(e));
            }
        });
        mMediaPlayer.setOnStateChangeCallback(new SDMediaPlayer.OnStateChangeCallback()
        {
            @Override
            public void onStateChanged(SDMediaPlayer.State oldState, SDMediaPlayer.State newState, SDMediaPlayer player)
            {
                Log.i(TAG, "onStateChanged:" + String.valueOf(newState));
            }
        });

        startDurationLooper();
    }

    /**
     * 开始进度查询
     */
    private void startDurationLooper()
    {
        mLooper.start(100, new Runnable()
        {
            @Override
            public void run()
            {
                int currentPosition = mMediaPlayer.getCurrentPosition();
                int totalDuration = mMediaPlayer.getDuration();
                if (mMediaPlayer.getState() == SDMediaPlayer.State.Stopped)
                {
                    currentPosition = 0;
                }

                sb_progress.setMax(totalDuration);
                sb_progress.setProgress(currentPosition);

                final String current = SDDateUtil.formatDuring2hhmmss(currentPosition);
                final String total = SDDateUtil.formatDuring2hhmmss(totalDuration);
                tv_duration.setText(current + "/" + total);
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_start:
//                mMediaPlayer.setDataRawResId(R.raw.cbg, this); //设置要播放的数据
                mMediaPlayer.setDataPath("http://liveimage.fanwe.net/public/attachment/201707/31/14/597ed39b46c5a.mp4");
                mMediaPlayer.start(); //播放
                break;
            case R.id.btn_pause:
                mMediaPlayer.pause(); //暂停
                break;
            case R.id.btn_stop:
                mMediaPlayer.stop(); //停止
                break;
            case R.id.btn_reset:
                mMediaPlayer.reset(); //重置
                break;
            case R.id.btn_play_pause:
                mMediaPlayer.performPlayPause(); // 播放/暂停
                break;
            case R.id.btn_play_stop:
                mMediaPlayer.performPlayStop(); // 播放/停止
                break;
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mMediaPlayer.release();
        mLooper.stop();
    }
}
