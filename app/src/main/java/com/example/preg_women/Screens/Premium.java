package com.example.preg_women.Screens;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.preg_women.R;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

public class Premium extends Fragment {
    private ImageView qrCode;
    private Button payment;
 // healthFragment healthFragment=new healthFragment();
    String myurl="https://firebasestorage.googleapis.com/v0/b/pregnancy-a27fd.appspot.com/o/qrCode%2Fqr.jpg?alt=media&token=d891da86-704d-48f6-b08f-a25851518c67";
    private Button menu_drop;
    private Button back;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.premium, container, false);
        WebView webView = view.findViewById(R.id.paymentWebView);
        ProgressDialog progressDialog=new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("please wait....");
      progressDialog.show();
        SharedPreferences sharedPref =getActivity().getSharedPreferences("PREF_PERSONAL_DATA", Context.MODE_PRIVATE);
        webView.getSettings().setJavaScriptEnabled(true);
        TextView tvPremiumPlanDescription = view.findViewById(R.id.tvPremiumPlanDescription);
        String premiumPlanDescription = getString(R.string.premium_plan_description);
        tvPremiumPlanDescription.setText(Html.fromHtml(premiumPlanDescription, Html.FROM_HTML_MODE_LEGACY));
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Check if the URL is the confirmation page URL
                if (url.equals("https://node-api-lnes.onrender.com/pay")) {
                    webView.loadUrl(url);
                    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                    firebaseAuth.signOut();
                    Intent intent = new Intent(getActivity(), Login_Page.class);
                    startActivity(intent);
                    return true;
                }
                Log.d("url", "shouldOverrideUrlLoading: "+url);
                return super.shouldOverrideUrlLoading(view, url);
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressDialog.show();
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String token = UUID.randomUUID().toString().replace("-", "");
                progressDialog.dismiss();
              String email=sharedPref.getString("email","insert your email here");
                String javascript = "document.getElementById('inp_email').value = '" + email + "';";
                String javascript1 = "document.getElementById('inp_email').readOnly = true;";
                String javascript2 = "document.getElementById('tex_ref').value = '" + token + "';";
                String javascript2_1 = "document.getElementById('tex_ref').readOnly = true;";
                String javascript3 = "document.getElementById('inp_amount').value = '" + "500" + "';";
                String javascript4 = "document.getElementById('inp_amount').readOnly = true;";
                webView.evaluateJavascript(javascript, null);
                webView.evaluateJavascript(javascript1, null);
                webView.evaluateJavascript(javascript2, null);
                webView.evaluateJavascript(javascript2_1, null);
                webView.evaluateJavascript(javascript3, null);
                webView.evaluateJavascript(javascript4,null);
            }
        });
        webView.loadUrl("https://node-api-lnes.onrender.com");
//        qrCode=view.findViewById(R.id.qrcode);
//payment=view.findViewById(R.id.pay);
        haveStoragePermission();
//        Glide.with(getActivity()).load(myurl).into(qrCode);
        back=view.findViewById(R.id.back);
       // Glide.with(getActivity()).download(myurl);
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
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToPrevious();
            }
        });
//        payment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                downloadImageNew("payQR",myurl);
//            }
//        });
 //  mp.start();
        return view;
    }
    private void downloadImageNew(String filename, String downloadUrlOfImage){
        try{
            DownloadManager dm = (DownloadManager)getActivity().getSystemService(getActivity().DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(downloadUrlOfImage);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(filename)
                    .setMimeType("image/jpeg") // Your file type. You can use this code to download other file types also.
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator + filename + ".jpg");
            dm.enqueue(request);
            Toast.makeText(getActivity(), "Saving....", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(getActivity(), "Saving..... failed.", Toast.LENGTH_SHORT).show();
        }
    }
    public  boolean haveStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error","You have permission");
                return true;
            } else {

                Log.e("Permission error","You have asked for permission");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error","You already have the permission");
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getActivity(), "Download on Progress", Toast.LENGTH_SHORT).show();
        }
    }
    private void backToPrevious() {
        healthFragment healthFragment=new healthFragment();
   getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, healthFragment).commit();
    }

}