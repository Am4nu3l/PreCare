package com.example.preg_women.Screens;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.preg_women.R;
import com.example.preg_women.contents.VideoContent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.security.PrivateKey;
import java.util.ArrayList;


public class healthFragment extends Fragment {
    private RecyclerView rv;
    private LinearLayoutManager layoutManager;
    MyHorizontalVideoRow horizontalVideoRow;
    Button slideRight,slideLeft;
    int dCount;
    CountDownTimer countDownTimer;
    int wCount;
    private FirebaseFirestore store;

ArrayList<String> titles=new ArrayList<>();
    ArrayList<String> content=new ArrayList<>();
    ArrayList<String> dietTitles=new ArrayList<>();
    ArrayList<String> dietLink=new ArrayList<>();
    ArrayList<String> dietDescription=new ArrayList<>();
    ArrayList<DietContent> dietContent=new ArrayList<>();
    TextView recommend;
    ArrayList<VideoContent> videoContents=new ArrayList<>();
    int pos=1;
    private ProgressBar progressBar;

    private Button menu_drop;
    private Button back;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_health, container, false);
        rv=view.findViewById(R.id.rv);
        layoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        slideRight=view.findViewById(R.id.slideRight);
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("PREF_PERSONAL_DATA", Context.MODE_PRIVATE);
        slideLeft=view.findViewById(R.id.slideLeft);
        back=view.findViewById(R.id.back);
        progressBar=view.findViewById(R.id.progress);
        store=FirebaseFirestore.getInstance();
        recommend=view.findViewById(R.id.recommendedTitle);
        ListView listView=view.findViewById(R.id.diet_list);
slideRight.setVisibility(View.GONE);
recommend.setVisibility(View.GONE);
slideLeft.setVisibility(View.GONE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToPrevious();
            }
        });
               countDownTimer=new CountDownTimer(3000, 500) {
            @Override
            public void onTick(long l) {
                store.collection("Video").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(sharedPreferences.getString("membership","").equals("premium")){
                            for(int i=0; i<task.getResult().size(); i++){
                            VideoContent videos=new VideoContent(task.getResult().getDocuments().get(i).get("title").toString(),task.getResult().getDocuments().get(i).get("link").toString());
                            videoContents.add(videos);
                            content.add(videoContents.get(i).getLink());
                            titles.add(videoContents.get(i).getTitle());
                            wCount=task.getResult().size();
                        }
                        }
                      else {
                            for(int i=0; i<task.getResult().size()/2; i++){
                                VideoContent videos=new VideoContent(task.getResult().getDocuments().get(i).get("title").toString(),task.getResult().getDocuments().get(i).get("link").toString());
                                videoContents.add(videos);
                                content.add(videoContents.get(i).getLink());
                                titles.add(videoContents.get(i).getTitle());
                                wCount=task.getResult().size()/2;
                        }
                      }
                    }
                });
                store.collection("Diet").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(sharedPreferences.getString("membership","").equals("premium")){
                            for(int i=0; i<task.getResult().size(); i++){
                                DietContent diets=new DietContent(task.getResult().getDocuments().get(i).get("title").toString(),task.getResult().getDocuments().get(i).get("link").toString(),task.getResult().getDocuments().get(i).get("description").toString());
                                dietContent.add(diets);
                                dietDescription.add(dietContent.get(i).getDescription());
                                dietTitles.add(dietContent.get(i).getTitle());
                                dietLink.add(dietContent.get(i).getLink());
                                dCount=task.getResult().size();
                            }
                        }
                        else {
                            for(int i=0; i<task.getResult().size()/2; i++){
                                DietContent diets=new DietContent(task.getResult().getDocuments().get(i).get("title").toString(),task.getResult().getDocuments().get(i).get("link").toString(),task.getResult().getDocuments().get(i).get("description").toString());
                                dietContent.add(diets);
                                dietDescription.add(dietContent.get(i).getDescription());
                                dietTitles.add(dietContent.get(i).getTitle());
                                dietLink.add(dietContent.get(i).getLink());
                                dCount=task.getResult().size()/2;
                                Log.d("TAG", "onComplete: "+task.getResult().size());
                            }
                        }
                    }
                });
            }
            @Override
            public void onFinish() {
                progressBar.setVisibility(View.GONE);
                slideRight.setVisibility(View.VISIBLE);
                recommend.setVisibility(View.VISIBLE);
                slideLeft.setVisibility(View.VISIBLE);
                horizontalVideoRow=new MyHorizontalVideoRow(titles,content,wCount);
                rv.setLayoutManager(layoutManager);
                rv.setAdapter(horizontalVideoRow);
                Diet_Row_Adapter  adapter=new Diet_Row_Adapter(dietTitles,dietDescription,dCount);
                listView.setAdapter(adapter);
            }
        };
        countDownTimer.start();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //save the clicked link to use it in the diet page class;
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("link",dietLink.get(i));
                editor.commit();
                gotoDietPage();
            }
        });
        menu_drop=view.findViewById(R.id.more);
        menu_drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu=new PopupMenu(getActivity(),view);
                popupMenu.inflate(R.menu.menus);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if(menuItem.getTitle().toString().equals("About")){
                            Intent intent=new Intent(getContext(),About.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                        else if(menuItem.getTitle().toString().equals("Profile")){
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new profileFragment()).commit();
                        }
                        return true;
                    }
                });
            }
        });
       slideRight.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(pos<=titles.size()){
                   layoutManager.scrollToPosition(pos);
                   ++pos;
               }
           }
       });
        slideLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pos<=titles.size()){
                    layoutManager.scrollToPosition(pos);
                    --pos;
                }
            }
        });
        return view;
    }


    void gotoDietPage(){
        Diet_page diet_page=new Diet_page();
        getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, diet_page).commit();
    }
    private void backToPrevious() {
        homeFragment homeFragment=new homeFragment();
       getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, homeFragment).commit();
    }


}