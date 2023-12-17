package com.example.preg_women.Screens;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.preg_women.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class MyCounselorsApter extends RecyclerView.Adapter<MyCounselorsApter.ViewHolder> {
    private    ArrayList<String>  link;
        private ArrayList<String> localDataSet;
        private Activity activity;
    ArrayList<String>  facebook;
    ArrayList<String>  telegram;
    ArrayList<String>  instagram;
        public static class ViewHolder extends RecyclerView.ViewHolder {
            private TextView textView;
          private CircleImageView couselorIMG;


            public CircleImageView getCouselorIMG() {
                return couselorIMG;
            }

            public ViewHolder(View view) {
                super(view);
                textView =view.findViewById(R.id.doctor_name);
                couselorIMG=view.findViewById(R.id.counselorImg);
            }
            public TextView getTextView() {
                return textView;
            }

        }

        public MyCounselorsApter(ArrayList<String>  dataSet, ArrayList<String>  link, Activity activity,ArrayList<String>  facebook,ArrayList<String>  instagram,ArrayList<String>  telegram) {
            this.localDataSet = dataSet;
            this.link=link;
            this.activity=activity;
            this.telegram=telegram;
            this.instagram=instagram;
            this.facebook=facebook;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item=
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.activity_counselors, viewGroup, false);
            return new ViewHolder(view);
        }

    @Override
    public void onBindViewHolder(@NonNull MyCounselorsApter.ViewHolder holder, int position) {
        holder.getTextView().setText(localDataSet.get(position));
        Glide.with(activity).load(link.get(position)).into(holder.getCouselorIMG());
holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

    }
});
    }
        @Override
        public int getItemCount() {
            return localDataSet.size();
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

