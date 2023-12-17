package com.example.preg_women.Screens;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.icu.util.ValueIterator;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import com.example.preg_women.R;
public class Diet_page extends Fragment {
    String value;
    ProgressBar  progressBar;
    WebView browser;
    private Button back;
    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view= inflater.inflate(R.layout.fragment_diet_page, container, false);
        progressBar=view.findViewById(R.id.progress);;
        back=view.findViewById(R.id.back);
        progressBar.setVisibility(View.VISIBLE);
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("PREF_PERSONAL_DATA", Context.MODE_PRIVATE);
        value=sharedPreferences.getString("link","");
        browser = view.findViewById(R.id.webview);
      Button  menu_drop=view.findViewById(R.id.more);
     browser.setWebViewClient(new WebViewClient(){
         @Override
         public void onPageStarted(WebView view, String url, Bitmap favicon) {
             super.onPageStarted(view, url, favicon);
             progressBar.setVisibility(View.VISIBLE);
         }
         @Override
         public void onPageFinished(WebView view, String url) {
             super.onPageFinished(view, url);
             progressBar.setVisibility(View.GONE);
         }
     });
        browser.loadUrl(value);
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
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToPrevious();
            }
        });
        return view;
    }
    private void backToPrevious() {
        healthFragment healthFragment=new healthFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, healthFragment).commit();
    }

}