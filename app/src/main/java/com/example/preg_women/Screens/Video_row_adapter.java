package com.example.preg_women.Screens;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.preg_women.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;

public class Video_row_adapter extends BaseAdapter {
    private final ArrayList<String> title_list;
    private final ArrayList<String> links;
    private final AppCompatActivity context;

    public Video_row_adapter(AppCompatActivity context,ArrayList<String> title_list, ArrayList<String> links) {
        this.title_list = title_list;
        this.links = links;
        this.context = context;
    }

    @Override
    public int getCount() {
        return title_list.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(viewGroup.getContext(), R.layout.activity_video_container,null);
        YouTubePlayerView youTubePlayerView =v.findViewById(R.id.vid);
//        youTubePlayerView.getYouTubePlayerWhenReady(new YouTubePlayerCallback() {
//            @Override
//            public void onYouTubePlayer(@NonNull YouTubePlayer youTubePlayer) {
//               // youTubePlayer.loadVideo("lKx0sOz31C4", 0);
//            }
//        });
    TextView txv=v.findViewById(R.id.video_title);
    txv.setText(title_list.get(i));

        return v;
    }
}
