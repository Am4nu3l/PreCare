package com.example.preg_women.registration_pages;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.preg_women.R;
import com.example.preg_women.Screens.Login_Page;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class OrganizationPage extends Fragment {
    private EditText name;
    private EditText city;
    private EditText sub_city;
    private EditText email;
    DatabaseReference reference;
    private LocationRequest locationRequest;
    private EditText phone_number1;
    private EditText phone_number2;
    private EditText licence_number;
    Map<String, Object> hospital;
    FirebaseFirestore database;
    private Button register;
    private FirebaseAuth mAuth;
    private EditText create_password;
    private EditText confirm_password;
    private SharedPreferences sharedPref;
    private ProgressDialog progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organization_page, container, false);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        name = view.findViewById(R.id.o_name);
        city = view.findViewById(R.id.o_city);
        sub_city = view.findViewById(R.id.o_subcity);
        email = view.findViewById(R.id.o_email);
        phone_number1 = view.findViewById(R.id.o_number1);
        mAuth= FirebaseAuth.getInstance();
        phone_number2 = view.findViewById(R.id.o_number2);
        licence_number = view.findViewById(R.id.o_license);
        create_password=view.findViewById(R.id.cr_password);
        confirm_password=view.findViewById(R.id.con_password);
        hospital = new HashMap<>();
        register = view.findViewById(R.id.Register_hospital);
//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//               locationtrack();
//            }
//        },0,1000);
        database = FirebaseFirestore.getInstance();
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
                createCareCenter();

            }
        });
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
     //   locationTrack();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    getCurrentLocation();

                } else {

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

                                    if (locationResult != null && locationResult.getLocations().size() > 0) {

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();
                                        sharedPref = getActivity().getSharedPreferences("PREF_PERSONAL_DATA", Context.MODE_PRIVATE);
                                        CollectionReference collectionReference=FirebaseFirestore.getInstance().collection("Hospital");
                                        DocumentReference documentReference=collectionReference.document(sharedPref.getString("user_id","null"));
                                        documentReference.update("latitude",latitude);
                                        documentReference.update("longitude",longitude);
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
    private void  createCareCenter(){
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
if(name.getText().toString().equals("")||city.getText().toString().equals("")
        ||sub_city.getText().toString().equals("")||email.getText().toString().equals("")
    ||phone_number1.getText().toString().equals("")|| licence_number.getText().toString().equals("")){
    alertDialog.setMessage("fill every field!!");
    alertDialog.show();
    progressBar.dismiss();
    }
else if(create_password.getText().toString().equals(confirm_password.getText().toString())&&!create_password.getText().toString().equals("")||confirm_password.getText().toString().equals("")){
    hospital.put(name.getHint().toString(), name.getText().toString());
    hospital.put(city.getHint().toString(), city.getText().toString());
    hospital.put(sub_city.getHint().toString(), sub_city.getText().toString());
    hospital.put(email.getHint().toString(), email.getText().toString());
    hospital.put(phone_number1.getHint().toString(), phone_number1.getText().toString());
    hospital.put(create_password.getHint().toString(), create_password.getText().toString());
    hospital.put(confirm_password.getHint().toString(), confirm_password.getText().toString());
    hospital.put(phone_number2.getHint().toString(), phone_number2.getText().toString());
    hospital.put(licence_number.getHint().toString(), licence_number.getText().toString());
    hospital.put("latitude", "");
    hospital.put("longitude", "");
    hospital.put("request", "");
    hospital.put("user_type","ORG");
    mAuth.createUserWithEmailAndPassword(email.getText().toString(),confirm_password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                database.collection("Hospital").document(name.getText().toString() + " " + phone_number1.getText().toString()).set(hospital).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        SharedPreferences sharedPref = getActivity().getSharedPreferences("PREF_PERSONAL_DATA", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("user_id", name.getText().toString() + " " + phone_number1.getText().toString());
                        editor.putString("user_type", "ORG");
                        editor.putString("car_id", "null");
                        editor.putString("hospname", name.getText().toString());
                        editor.commit();
                        getCurrentLocation();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "failed", Toast.LENGTH_LONG).show();
                    }
                });
                progressBar.dismiss();
                Toast.makeText(getActivity(), "Registration Successful!!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), Login_Page.class);
                getCurrentLocation();
                startActivity(intent);
            }
            else{
                progressBar.dismiss();
                Toast.makeText(getActivity(), "Registration Error!!" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                Log.d("ERO", "onComplete: " + task.getException().toString());
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

//                                if (locationResult != null && locationResult.getLocations().size() > 0) {
//
//                                    int index = locationResult.getLocations().size() - 1;
//                                    double latitude = locationResult.getLocations().get(index).getLatitude();
//                                    double longitude = locationResult.getLocations().get(index).getLongitude();
//                                }
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
