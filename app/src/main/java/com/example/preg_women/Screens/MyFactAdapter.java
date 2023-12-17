package com.example.preg_women.Screens;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.preg_women.R;
import com.google.firebase.firestore.DocumentReference;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;

    public class MyFactAdapter extends RecyclerView.Adapter<MyFactAdapter.ViewHolder> {

        private ArrayList<String> localDataSet;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private TextView textView1;
            public ViewHolder(View view) {
                super(view);
                textView1 = (TextView) view.findViewById(R.id.facts);

            }
            public TextView getTextView1() {
                return textView1;
            }

        }

        public MyFactAdapter( ArrayList<String>  dataSet) {
            this.localDataSet = dataSet;

        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyFactAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.activity_daily_fact, viewGroup, false);
            return new MyFactAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyFactAdapter.ViewHolder holder, final int position) {
            holder.getTextView1().setText(localDataSet.get(position));

        }
        // Return the size of your dataset (invoked by the layout manager)
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

