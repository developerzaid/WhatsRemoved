package com.hazyaz.whatsRemoved.Adapters;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hazyaz.whatsRemoved.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class CustomAudioAdapter extends RecyclerView.Adapter<CustomAudioAdapter.ViewHolder> {


    private final Context mContext;
    private final File[] mFiles;
    MediaPlayer mPlayer;
    String filename;
    SeekBar seekBar;


    public CustomAudioAdapter(Context context, File[] file) {
        super();
        mContext = context;
        mFiles = file;
    }


    private static String getDate(String milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(milliSeconds));
        return formatter.format(calendar.getTime());
    }


    @NonNull
    @Override
    public CustomAudioAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.voice_notes, parent, false);

        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        String n = String.valueOf(mFiles[position].lastModified());
        holder.mPlayTime.setText(getDate(n, "dd MMM hh:mm a"));

        holder.mPlayButon.setVisibility(View.VISIBLE);
        holder.mPauseButton.setVisibility(View.GONE);
//        holder.nowPlayingText.setVisibility(View.INVISIBLE);
        String path = mFiles[position].toString();


        String tempname = path.substring(path.lastIndexOf("/") + 1);
        if (tempname.indexOf(".") > 0) {
            filename = tempname.substring(0, tempname.lastIndexOf("."));
        }
        holder.nowPlayingText.setText(filename);


        holder.mPlayButon.setOnClickListener(view -> {

            loadAudio(position, holder);
            holder.mPlayButon.setVisibility(View.GONE);
            holder.mPauseButton.setVisibility(View.VISIBLE);
            holder.nowPlayingText.setText("Your Media is now Playing");

        });


        holder.mPauseButton.setOnClickListener(view -> {

            holder.mPlayButon.setVisibility(View.VISIBLE);
            holder.mPauseButton.setVisibility(View.GONE);
            holder.nowPlayingText.setText(filename);
            mPlayer.stop();

        });

    }

    void clearMediaPlayer() {
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
    }

    private void loadAudio(int position, final ViewHolder holder) {

        if (mPlayer != null) {
            clearMediaPlayer();
            seekBar.setProgress(0);
        }

        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        }
        Uri uri = Uri.parse(mFiles[position].toString());
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mPlayer.setDataSource(mContext, uri);
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mPlayer.start();
        Timer t = new Timer();

        mPlayer.setOnCompletionListener(mediaPlayer -> {
            mPlayer.stop();
            holder.mPlayButon.setVisibility(View.VISIBLE);
            holder.mPauseButton.setVisibility(View.GONE);
            holder.nowPlayingText.setText(filename);
            t.cancel();
        });

        seekBar.setMax(mPlayer.getDuration());
        //Declare the timer
//Set the schedule function and rate
        t.scheduleAtFixedRate(new TimerTask() {

                                  @Override
                                  public void run() {
                                      if (mPlayer != null) {
                                          int currentPosition = mPlayer.getCurrentPosition();
                                          int total = mPlayer.getDuration();

                                          Log.d("TAG", "onProgressChanged-c" + currentPosition);
                                          Log.d("TAG", "onProgressChanged-t" + total);

                                          while (mContext != null && mPlayer.isPlaying() && currentPosition < total) {
                                              try {
                                                  Thread.sleep(1000);
                                                  currentPosition = mPlayer.getCurrentPosition();
                                              } catch (InterruptedException e) {
                                                  return;
                                              } catch (Exception e) {
                                                  return;
                                              }

                                              seekBar.setProgress(currentPosition);

                                          }
                                          Log.d("TAG", "onProgressChanged 22");
                                      }
                                  }

                              },
                //Set how long before to start calling the TimerTask (in milliseconds)
                10,
//Set the amount of time between each execution (in milliseconds)
                10);


    }


    @Override
    public int getItemCount() {
        return mFiles.length;
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mPlayButon;
        ImageView mPauseButton;
        LinearLayout paddedLinearLayout;
        TextView mPlayTime;
        TextView nowPlayingText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            paddedLinearLayout = itemView.findViewById(R.id.paddedLinearLayout);
            seekBar = itemView.findViewById(R.id.seekBar);
            mPlayButon = itemView.findViewById(R.id.imgPlay);
            mPauseButton = itemView.findViewById(R.id.imgPause);
            mPlayTime = itemView.findViewById(R.id.txtTime);
            nowPlayingText = itemView.findViewById(R.id.nowPlayingText);


        }

    }
}
