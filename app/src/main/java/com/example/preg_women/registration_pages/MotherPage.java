package com.example.preg_women.registration_pages;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.preg_women.MainActivity;
import com.example.preg_women.R;
import com.example.preg_women.Screens.Login_Page;
import com.example.preg_women.Screens.Org_Bottom_nav;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.model.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MotherPage extends Fragment {
    FirebaseFirestore database;
    private EditText first_name;
    private EditText middle_name;
    private EditText age;
    private EditText city;
    private EditText sub_city;
    private EditText email;
    private EditText create_password;
    private EditText confirm_password;
    private EditText house_number;
    private EditText phone_number1;
    private EditText phone_number2;
    private boolean permanent_address;
    private EditText partners_name;
    private EditText partners_phone_number;
    private EditText any_allergy;
    private EditText current_medicine;
    private EditText current_hospital;
    private EditText month;
    private EditText day;
    private Button register;
    FirebaseDatabase db;
    private LocationRequest locationRequest;
    Map<String,Object>user=new HashMap<>();
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPref;
    private ProgressDialog progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mother_page, container, false);
      first_name=view.findViewById(R.id.ed_firstname);
       middle_name=view.findViewById(R.id.ed_middle_name);
       city=view.findViewById(R.id.ed_city);
       sub_city=view.findViewById(R.id.ed_subcity);
       create_password=view.findViewById(R.id.cr_password);
       confirm_password=view.findViewById(R.id.con_password);
       email=view.findViewById(R.id.ed_email);
       phone_number1=view.findViewById(R.id.ed_number1);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
       age=view.findViewById(R.id.ed_age);
       house_number=view.findViewById(R.id.house_number);
       database=FirebaseFirestore.getInstance();
       phone_number2=view.findViewById(R.id.ed_number2);
       partners_name=view.findViewById(R.id.ed_partners_name);
       partners_phone_number=view.findViewById(R.id.ed_partners_number);
       any_allergy=view.findViewById(R.id.ed_allergy);
       current_medicine=view.findViewById(R.id.ed_current_med);
       current_hospital=view.findViewById(R.id.ed_current_hosp);
       month=view.findViewById(R.id.ed_month);
       register=view.findViewById(R.id.Register_mother);
       day=view.findViewById(R.id.ed_day);
        mAuth= FirebaseAuth.getInstance();
        Button back=view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),Login_Page.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
       register.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               createUser();
           }
           });
               return view;

    }

    @Override
    public void onStart() {
        super.onStart();
      //  locationTrack();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

                if (isGPSEnabled()) {

                    getCurrentLocation();

                }else {

                    turnOnGPS();
                }
            }
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                getCurrentLocation();
            }
        }
    }
    private void turnOnGPS() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getActivity())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(getActivity(), "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(getActivity(), 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });

    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }
private void createUser(){
    final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
    final AlertDialog alertDialog = alert.create();
    alertDialog.setCanceledOnTouchOutside(true);
    progressBar = new ProgressDialog(getActivity());
    progressBar.setCancelable(true);
    progressBar.setCanceledOnTouchOutside(false);
    progressBar.setMessage("please wait...");
    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    progressBar.setProgress(0);
    progressBar.setMax(100);
    progressBar.show();
    if(first_name.getText().toString().equals("")||middle_name.getText().toString().equals("")
            ||create_password.getText().toString().equals("") ||confirm_password.getText().toString().equals("")
            || phone_number1.getText().toString().equals("")||email.getText().toString().equals("")
            ||city.getText().toString().equals("")||sub_city.getText().toString().equals("")
            || house_number.getText().toString().equals("")||any_allergy.getText().toString().equals("")
            ||current_medicine.getText().toString().equals("")||current_hospital.getText().toString().equals("")){
        alertDialog.setMessage("fill every field!!");
        alertDialog.show();
        progressBar.dismiss();
    }
   else if(create_password.getText().toString().equals(confirm_password.getText().toString())&&!create_password.getText().toString().equals("")||!confirm_password.getText().toString().equals("")) {
        user.put(first_name.getHint().toString(), first_name.getText().toString());
        user.put(middle_name.getHint().toString(), middle_name.getText().toString());
        user.put(age.getHint().toString(), age.getText().toString());
        user.put(city.getHint().toString(), city.getText().toString());
        user.put(sub_city.getHint().toString(), sub_city.getText().toString());
        user.put(email.getHint().toString(), email.getText().toString());
        user.put(house_number.getHint().toString(), house_number.getText().toString());
        user.put(phone_number1.getHint().toString(), phone_number1.getText().toString());
        user.put(phone_number2.getHint().toString(), phone_number2.getText().toString());
        user.put(partners_name.getHint().toString(), partners_name.getText().toString());
        user.put(partners_phone_number.getHint().toString(), partners_phone_number.getText().toString());
        user.put(any_allergy.getHint().toString(), any_allergy.getText().toString());
        user.put(current_medicine.getHint().toString(), current_medicine.getText().toString());
        user.put(current_hospital.getHint().toString(), current_hospital.getText().toString());
        user.put("confirm", confirm_password.getText().toString());
        user.put(month.getHint().toString(), month.getText().toString());
        user.put(day.getHint().toString(), day.getText().toString());
        user.put("latitude", "");
        user.put("user_type","MOTHER");
        user.put("longitude", "");
        user.put("membership", "basic");
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), create_password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    database.collection("User").document(first_name.getText().toString()+" "+phone_number1.getText().toString()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            sharedPref = getActivity().getSharedPreferences("PREF_PERSONAL_DATA",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("user_id",first_name.getText().toString()+" "+phone_number1.getText().toString());
                            editor.putString("user_type","USER");
                            editor.commit();
                            CollectionReference collectionReference=FirebaseFirestore.getInstance().collection("User");
                            DocumentReference documentReference=collectionReference.document(first_name.getText().toString()+" "+phone_number1.getText().toString());
                            getCurrentLocation();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(),"failed",Toast.LENGTH_LONG).show();
                            Log.d("ER",e.toString());
                        }
                    });
                    progressBar.dismiss();
                    Toast.makeText(getActivity(), "Registration Successful!!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), Login_Page.class);
                    startActivity(intent);
                } else {
                    progressBar.dismiss();
                    Toast.makeText(getActivity(), "Registration Error!!"+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    Log.d("ERO", "onComplete: "+task.getException().toString());
                }

            }
        });
    }
    else{
        alertDialog.setMessage("Password Mismatch!!");
        alertDialog.show();
    }

}
    private void locationTrack(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {
                    LocationServices.getFusedLocationProviderClient(getActivity())
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(getActivity())
                                            .removeLocationUpdates(this);

//                                    if (locationResult != null && locationResult.getLocations().size() > 0) {
//
//                                        int index = locationResult.getLocations().size() - 1;
//                                        double latitude = locationResult.getLocations().get(index).getLatitude();
//                                        double longitude = locationResult.getLocations().get(index).getLongitude();
//                                    }
                                }
                            }, Looper.getMainLooper());

                } else {
                    turnOnGPS();
                }

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

    }

    private void getCurrentLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(getActivity())
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(getActivity())
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() >0){

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();
                                        //continuous location update
                                        sharedPref = getActivity().getSharedPreferences("PREF_PERSONAL_DATA", Context.MODE_PRIVATE);
                                        CollectionReference collectionReference=FirebaseFirestore.getInstance().collection("User");
                                        DocumentReference documentReference=collectionReference.document(sharedPref.getString("user_id","null"));
                                        documentReference.update("latitude",latitude);
                                        documentReference.update("longitude",longitude);
                                        Log.d("SHP", "onLocationResult: "+sharedPref.getString("user_id","null"));
                                    }
                                }
                            }, Looper.getMainLooper());

                } else {
                    turnOnGPS();
                }

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

}