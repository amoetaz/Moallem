package com.moallem.stu.adapters;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.moallem.stu.R;
import com.moallem.stu.models.Message;
import com.moallem.stu.models.Session;
import com.moallem.stu.ui.fragments.ViewImageFragment;
import com.moallem.stu.utilities.Utils;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private Session session;
    private MediaPlayer mPlayer = null;
    private static final String TAG = "ChatAdapterclass";
    private Context context ;
    private ArrayList<Message> messages = new ArrayList<>();
    private View oldView = null;
    private View oldProgressbar = null;
    private Handler mHandler;

    public ChatAdapter(Context context, ArrayList<Message> messages, Session session) {
        this.context = context;
        this.messages = messages;
        this.session = session;
    }

    @NonNull
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_row,parent,false);
        MyViewHolder holder = new MyViewHolder(v);
        holder.audioButton1.setTag("play");
        holder.audioButton2.setTag("play");
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.MyViewHolder holder, int position) {

        Message message = messages.get(position);
        setDefault(holder);

        if (Utils.getCurrentUserId().equals(message.getSenderId())){
            hideViews(holder.userText2,holder.userImage2,holder.userPhoto2,holder.audioRelative2);
            holder.userText1.setBackgroundResource(R.drawable.chat_box_shape_green);
            setMessage(message,holder.userText1,holder.userImage1,holder.audioButton1,holder.audioRelative1
                    ,holder.userPhoto1,holder.seekBar1);
        }else {
            hideViews(holder.userText1,holder.userImage1,holder.userPhoto1,holder.audioRelative1);
            holder.userText2.setBackgroundResource(R.drawable.chat_bax_shape);
            setMessage(message,holder.userText2,holder.userImage2,holder.audioButton2,holder.audioRelative2
                    ,holder.userPhoto2,holder.seekBar2);
        }

    }

    private void setDefault(MyViewHolder holder) {
        showViews(holder.userPhoto1,holder.userPhoto2,holder.audioButton1,holder.audioButton2);
    }

    private void setMessage(Message message, TextView userText1, ImageView userImage1, ImageView audioButton,
                            RelativeLayout relativeLayout, CircleImageView userPhoto, SeekBar seekBar) {
        if (message.getSenderType().equals("teacher")){
            Glide.with(context).load(session.getTeacherPic()).into(userPhoto);
        }else {
            userPhoto.setVisibility(View.INVISIBLE);
        }
        if ("text".equals(message.getMsgType())) {
            userImage1.setVisibility(View.GONE);
            userText1.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);
            userText1.setText(message.getMsg());
        } else if ("photo".equals(message.getMsgType())) {
            userText1.setVisibility(View.GONE);
            userImage1.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);

            setImage(message.getMsg(),userImage1);
        }else if ("audio".equals(message.getMsgType())){
            userText1.setVisibility(View.GONE);
            userImage1.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
            playAndStopAudio(audioButton,message,seekBar);
        }

    }

    private void setImage(String msg, ImageView userImage1) {

    RequestOptions requestOptions = new RequestOptions();
    requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(20));
    Glide.with(context)
            .load(msg)
            .apply(requestOptions)
            .into(userImage1);
    userImage1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ViewImageFragment viewImageFragment = new ViewImageFragment();
            Bundle bundle = new Bundle();
            bundle.putString("image_url",msg);
            viewImageFragment.setArguments(bundle);

            ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction()
                    .add(R.id.fchat,viewImageFragment).addToBackStack(null).commit();
        }
    });
    }


    private void hideViews(View ...views){
        for (View view : views){
            view.setVisibility(View.GONE);
        }
    }

    private void showViews(View ...views){
        for (View view : views){
            view.setVisibility(View.VISIBLE);
        }
    }




    private void playAndStopAudio(View view, Message message, SeekBar seekBar) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (v.getTag().equals("play")){
                        startPlaying(v,message.getMsg(),seekBar);

                        v.setTag("pause");
                        ((ImageView)v).setImageResource(R.drawable.ic_pause);

                    }else {
                        v.setTag("play");
                        ((ImageView)v).setImageResource(R.drawable.ic_play);
                        stopPlaying();
                    }
            }
        });

    }

    private void initializeHandler(SeekBar seekBar) {
        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
        if (oldProgressbar != null){
            ((ProgressBar)oldProgressbar).setProgress(0);
        }
        oldProgressbar = seekBar;
        mHandler = new Handler();
        ((AppCompatActivity)context).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(mPlayer != null){
                    int mCurrentPosition = mPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    Log.d(TAG, "run: CurrentPosition :"+mCurrentPosition);
                }
                mHandler.postDelayed(this, 1000);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userPhoto1,userPhoto2;
        ImageView userImage1 , userImage2,audioButton1,audioButton2;
        TextView userText1 ,userText2;
        RelativeLayout audioRelative1,audioRelative2;
        SeekBar seekBar1,seekBar2;

        public MyViewHolder(View itemView) {
            super(itemView);

            userPhoto1 = itemView.findViewById(R.id.user_photo1);
            userPhoto2 = itemView.findViewById(R.id.user_photo2);
            userImage1 = itemView.findViewById(R.id.user_image1);
            userImage2 = itemView.findViewById(R.id.user_image2);
            userText1 = itemView.findViewById(R.id.user_text1);
            userText2 = itemView.findViewById(R.id.user_text2);
            audioButton1 = itemView.findViewById(R.id.audio_button1);
            audioButton2 = itemView.findViewById(R.id.audio_button2);
            audioRelative1 = itemView.findViewById(R.id.audio_relative1);
            audioRelative2 = itemView.findViewById(R.id.audio_relative2);
            seekBar1 = itemView.findViewById(R.id.seekabr1);
            seekBar2 = itemView.findViewById(R.id.seekabr2);

        }
    }

    private void startPlaying(View v, String uri, SeekBar seekBar) {

        try {
            if (oldView != null){
                oldView.setTag("play");
                ((ImageView)oldView).setImageResource(R.drawable.ic_play);

            }
            stopPlaying();
            oldView = v;
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(uri);
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(preparedPlayer -> {
                preparedPlayer.start();
                initializeHandler(seekBar);
                seekBar.setMax(mPlayer.getDuration()/1000);
                Log.d(TAG, "startPlaying: duration "+mPlayer.getDuration()/1000);

            });


        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
        mPlayer.setOnCompletionListener(mp ->{
            v.setTag("play");
            ((ImageView)v).setImageResource(R.drawable.ic_play);
            stopPlaying();
            seekBar.setProgress(0);
            mHandler.removeCallbacksAndMessages(null);
                }

        );


    }

    private void stopPlaying() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }


}
