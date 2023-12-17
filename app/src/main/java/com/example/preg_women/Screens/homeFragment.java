package com.example.preg_women.Screens;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.preg_women.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class homeFragment extends Fragment{
private Button premium;

private TextView Tips;
RecyclerView rv,rvf;
    Button slideRight,slideLeft;
    Premium premiumMembership = new Premium();
    TextView height,weight,daysleft,txtfacebook,txtinstagram,txttelegram,dr_name,dr_description;
    private ArrayList<String> fact=new ArrayList<>();
    private LinearLayoutManager layoutManager,layoutManager2;
    private SharedPreferences sharedPref;
    private MyCounselorsApter myCounselorsApter;
    private ArrayList<String> counselorName=new ArrayList<>();
    private ArrayList<String> counselorMname=new ArrayList<>();
    private ArrayList<String> imgLinks=new ArrayList<>();
    private ArrayList<String> facebook=new ArrayList<>();
    private ArrayList<String> instagram=new ArrayList<>();
    private ArrayList<String> telegram=new ArrayList<>();
    private ArrayList<String> drInformation=new ArrayList<>();
    private CircleImageView dr_picture;
    private FirebaseFirestore store;
    private TextView fullName;
    int pos=1;
    private Button menu_drop;
    private BottomSheetDialog bottomSheetDialog;
    private SharedPreferences sharedPref2;
    MyFactAdapter factAdapter;
    ProgressBar bar1,bar2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        sharedPref = getActivity().getSharedPreferences("STATUS", Context.MODE_PRIVATE);
        //user information after login
        sharedPref2 = getActivity().getSharedPreferences("PREF_PERSONAL_DATA", Context.MODE_PRIVATE);
        view.requestFocus();
        premium=view.findViewById(R.id.premium);
        rv=view.findViewById(R.id.rv);
        FrameLayout rvContainer=view.findViewById(R.id.rvContainer);
        rvf=view.findViewById(R.id.rvf);
        Tips=view.findViewById(R.id.tips);
        store= FirebaseFirestore.getInstance();
        slideRight=view.findViewById(R.id.slideRight);
        fullName=view.findViewById(R.id.fullName);
        fullName.setText(sharedPref2.getString("first name","")+"\t"+sharedPref2.getString("middle name",""));
        slideLeft=view.findViewById(R.id.slideLeft);
        height=view.findViewById(R.id.height);
        weight=view.findViewById(R.id.weight);
        daysleft=view.findViewById(R.id.daysleft);
        height.setText(sharedPref.getString("height","--/--"));
        weight.setText(sharedPref.getString("weight","--/--"));
        //update the month and the remaining day
        DailyCounter.checkAndIncrementField(getActivity().getApplicationContext());
        daysleft.setText(sharedPref2.getInt("month",0)+"\t/\t"+sharedPref2.getInt("days",0));
        new LoadCourse().execute();
        bottomSheetDialog=new BottomSheetDialog(getActivity(),R.style.BottomSheetDialogStyle);
        View bottomSheet=getLayoutInflater().inflate(R.layout.counselordetail,null,false);
        dr_description=bottomSheet.findViewById(R.id.dr_description);
        dr_picture=bottomSheet.findViewById(R.id.dr_picture);
        dr_name=bottomSheet.findViewById(R.id.dr_name);
        layoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        txtinstagram=bottomSheet.findViewById(R.id.instagram);
        txtfacebook=bottomSheet.findViewById(R.id.facebook);
        txttelegram=bottomSheet.findViewById(R.id.telegram);
        bar1=view.findViewById(R.id.spin_kit);
         bar2=view.findViewById(R.id.spin_kit2);
        layoutManager2=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        factAdapter=new MyFactAdapter(fact);
        rvf.setLayoutManager(layoutManager2);
        rvf.setAdapter(factAdapter);
        bar1.setVisibility(View.VISIBLE);
        bar2.setVisibility(View.VISIBLE);
        if(sharedPref2.getString("membership","").equals("premium")){
            rv.setVisibility(View.VISIBLE);
            premium.setVisibility(View.GONE);
        }
        else {
            rv.setVisibility(View.GONE);
            rvContainer.setVisibility(View.GONE);
        }
        rv.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                bottomSheetDialog.setContentView(bottomSheet);
                bottomSheetDialog.show();
                bottomSheetDialog.setCanceledOnTouchOutside(true);
                bottomSheetDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
               txtfacebook.setText(facebook.get(position));
                txtinstagram.setText(instagram.get(position));
                txttelegram.setText(telegram.get(position));
                dr_name.setText("Dr."+counselorName.get(position)+"\t"+counselorMname.get(position));
                Glide.with(getActivity()).load(imgLinks.get(position)).into(dr_picture);
                dr_description.setText(drInformation.get(position));
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        premium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoProfile();
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
        return view;
    }
    void gotoProfile(){
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, premiumMembership).commit();
    }
    @Override
    public void onResume() {
        super.onResume();
      //  clearData();
    }
    private class LoadCourse extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            store.collection("Counselor").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for(int i=0; i<task.getResult().size(); i++){
                        counselorName.add(task.getResult().getDocuments().get(i).get("fname").toString());
                        imgLinks.add(task.getResult().getDocuments().get(i).get("link").toString());
                        facebook.add(task.getResult().getDocuments().get(i).get("facebook").toString());
                        instagram.add(task.getResult().getDocuments().get(i).get("instagram").toString());
                        telegram.add(task.getResult().getDocuments().get(i).get("telegram").toString());
                        drInformation.add(task.getResult().getDocuments().get(i).get("description").toString());
                        counselorMname.add(task.getResult().getDocuments().get(i).get("mname").toString());
                    }
                    myCounselorsApter.notifyDataSetChanged();
                    bar1.setVisibility(View.GONE);
                }
            });
            store.collection("Fact").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    //for premium version release the whole content
                    if(sharedPref2.getString("membership","").equals("premium")){
                        for(int i=0; i<task.getResult().size();i++){
                            fact.add(task.getResult().getDocuments().get(i).get("content").toString());
                        }
                    }
                    //for basic version release the half content
                    else {
                        for(int i=0; i<task.getResult().size()/2;i++){
                            fact.add(task.getResult().getDocuments().get(i).get("content").toString());
                        }
                    }

factAdapter.notifyDataSetChanged();
                }
            });
            slideRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(pos<=fact.size()){
                        layoutManager2.scrollToPosition(pos);
                        pos++;
                    }
                    else {
                        pos=0;
                    }
                }
            });
            slideLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(pos<=fact.size()){
                        layoutManager2.scrollToPosition(pos);
                        pos--;
                    }
                    else {
                        pos=0;
                    }
                }
            });
            store.collection("Tip").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Random random=new Random();
                    //for premium version release the whole content
                    if(sharedPref2.getString("membership","").equals("premium")){
                        int min = 0;  // Minimum number
                        int max = task.getResult().size()-1;// Maximum number
                        int randomNumber = random.nextInt(max - min + 1) + min;
                        Tips.setText(task.getResult().getDocuments().get(randomNumber).get("content").toString());
                        bar2.setVisibility(View.GONE);
                    }
                    //for basic version release the half content
                    else {
                        int min = 0;  // Minimum number
                        int max = task.getResult().size()/2-1;// Maximum number
                        int randomNumber = random.nextInt(max - min + 1) + min;
                        Tips.setText(task.getResult().getDocuments().get(randomNumber).get("content").toString());
                        bar2.setVisibility(View.GONE);
                    }

                }
            });
            return null;
        }
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            myCounselorsApter=new MyCounselorsApter(counselorName,imgLinks,getActivity(),facebook,instagram,telegram);
            rv.setLayoutManager(layoutManager);
            rv.setAdapter(myCounselorsApter);
            fact.clear();
            counselorMname.clear();
            imgLinks.clear();
            facebook.clear();
            instagram.clear();
            telegram.clear();
            counselorName.clear();
            drInformation.clear();
        }
    }

}