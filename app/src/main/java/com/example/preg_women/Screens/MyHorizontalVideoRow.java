package com.example.preg_women.Screens;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.preg_women.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;

public class MyHorizontalVideoRow extends RecyclerView.Adapter<MyHorizontalVideoRow.ViewHolder> {

    private ArrayList<String> localDataSet;
    private ArrayList<String> links;
    int count;
    private DocumentReference store;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private  TextView textView1,textView2;

private  YouTubePlayerView youTubePlayerView;
        public ViewHolder(View view) {
            super(view);
            textView1 = (TextView) view.findViewById(R.id.video_title);
            //textView2 = (TextView) view.findViewById(R.id.counter);
            youTubePlayerView =view.findViewById(R.id.vid);
        }
        public TextView getTextView1() {
            return textView1;
        }
//        public TextView getTextView2() {
//            return textView2;
//        }
        public YouTubePlayerView getYouTubePlayerView() {
            return youTubePlayerView;
        }
    }

    public MyHorizontalVideoRow( ArrayList<String>  dataSet,ArrayList<String>  links,int count) {
        this.links=links;
        this.localDataSet = dataSet;
        this.count=count;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.activity_video_container, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        holder.getTextView1().setText(localDataSet.get(position));
        //holder.getTextView2().setText("Exercise\t"+position+"\tOf"+localDataSet.size());
       holder.getYouTubePlayerView().getYouTubePlayerWhenReady(new YouTubePlayerCallback() {
            @Override
            public void onYouTubePlayer(@NonNull YouTubePlayer youTubePlayer) {
            youTubePlayer.loadVideo(links.get(holder.getAdapterPosition()),0);
            }
        });
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return count;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
}