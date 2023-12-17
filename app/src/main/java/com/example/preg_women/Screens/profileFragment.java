package com.example.preg_women.Screens;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import com.example.preg_women.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class profileFragment extends Fragment {
   private FirebaseAuth mAuth;
    Button button,edit,save;
    TextView height,weight,bloodtype,memberShip;

EditText firstName,middleName,age,phoneNumber;
    private DownloadManager manager;

    private SharedPreferences sharedPref;
    private Button menu_drop;
    private SharedPreferences sharedPref2;
   // Premium premium=new Premium();
    private Button back,saveProfile,editProfile;

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_profile, container, false);
        sharedPref = getActivity().getSharedPreferences("STATUS", Context.MODE_PRIVATE);
        //user information after login
        sharedPref2 = getActivity().getSharedPreferences("PREF_PERSONAL_DATA", Context.MODE_PRIVATE);
        firstName=view.findViewById(R.id.firstName);
        middleName=view.findViewById(R.id.middleName);
        age=view.findViewById(R.id.age);
        phoneNumber=view.findViewById(R.id.phoneNumber);
        memberShip=view.findViewById(R.id.memberShip);
        button=view.findViewById(R.id.logout);
        mAuth=FirebaseAuth.getInstance();
        back=view.findViewById(R.id.back);
        saveProfile=view.findViewById(R.id.saveProfile);
        editProfile=view.findViewById(R.id.editProfile);
        height=view.findViewById(R.id.height);
        weight=view.findViewById(R.id.weight);
        bloodtype=view.findViewById(R.id.bloodtype);
        edit=view.findViewById(R.id.edit);
        save=view.findViewById(R.id.save);
        height.setText(sharedPref.getString("height",""));
        weight.setText(sharedPref.getString("weight",""));
        bloodtype.setText(sharedPref.getString("bloodtype",""));
        firstName.setText(sharedPref2.getString("first name",""));
        middleName.setText(sharedPref2.getString("middle name",""));
        age.setText(sharedPref2.getString("age",""));
        phoneNumber.setText(sharedPref2.getString("phone number 1",""));
        memberShip.setText(sharedPref2.getString("membership",""));
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableEditing();
            }
        });
editProfile.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        saveProfile.setVisibility(View.VISIBLE);
        firstName.setEnabled(true);
        middleName.setEnabled(true);
        phoneNumber.setEnabled(true);
        age.setEnabled(true);
    }
});
saveProfile.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User").document(sharedPref2.getString("user_id", null)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        try {
                            DocumentReference reference = document.getReference();
                            reference.update("first name", firstName.getText().toString());
                            reference.update("middle name", middleName.getText().toString());
                            reference.update("Age", age.getText().toString());
                            reference.update("phone number 1", phoneNumber.getText().toString());
                            firstName.setEnabled(false);
                            middleName.setEnabled(false);
                            phoneNumber.setEnabled(false);
                            age.setEnabled(false);
                            saveProfile.setVisibility(View.GONE);
                            SharedPreferences.Editor editor = sharedPref2.edit();
                            editor.putString("first name", firstName.getText().toString());
                            editor.putString("middle name", middleName.getText().toString());
                            editor.putString("Age", age.getText().toString());
                            editor.putString("phone number 1", phoneNumber.getText().toString());
                            editor.apply();
                            Toast.makeText(getActivity(), "Your Profile has Been Edited", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Failed to update profile. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Exception exception = task.getException();
                    if (exception != null) {
                        exception.printStackTrace();
                        Toast.makeText(getActivity(), "Failed to retrieve user profile. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                    saveProfile.setVisibility(View.GONE);
                    firstName.setText(sharedPref2.getString("first name", ""));
                    middleName.setText(sharedPref2.getString("middle name", ""));
                    age.setText(sharedPref2.getString("Age", ""));
                    phoneNumber.setText(sharedPref2.getString("phone number 1", ""));
                    firstName.setEnabled(false);
                    middleName.setEnabled(false);
                    phoneNumber.setEnabled(false);
                    age.setEnabled(false);
                }
            }
        });

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
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPref2.getString("membership",null).equals("premium")){
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new healthFragment()).commit();
                }
                else {
                    backToPrevious();
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
mAuth.signOut();
                Intent intent = new Intent(getActivity(), Login_Page.class);
                startActivity(intent);
            }
        });
return view;
    }

    private void save() {
        sharedPref = getActivity().getSharedPreferences("STATUS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("height",height.getText().toString());
        editor.putString("weight",weight.getText().toString());
        editor.putString("bloodtype",bloodtype.getText().toString());
        editor.apply();
        height.setEnabled(false);
        weight.setEnabled(false);
        bloodtype.setEnabled(false);
        save.setVisibility(View.INVISIBLE);
    }
    private void enableEditing() {
        height.setEnabled(true);
        weight.setEnabled(true);
        bloodtype.setEnabled(true);
        save.setVisibility(View.VISIBLE);

    }
    void downloadContent(){
        manager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse("https://www.pngall.com/wp-content/uploads/2/QR-Code-PNG-Images.png");
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        long reference = manager.enqueue(request);
    }
    private void backToPrevious() {
        Premium premium=new Premium();
     getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, premium).commit();
    }
}