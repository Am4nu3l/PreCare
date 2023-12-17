package com.example.preg_women.Screens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.preg_women.MainActivity;
import com.example.preg_women.R;
import com.example.preg_women.contents.VideoContent;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class AddAmbulance extends AppCompatActivity {
    private EditText driverfname,driverphone1,driverphone2,platenumber;
    Map<String,Object> Ambulance=new HashMap<>();
    private Button addAmbulance;
    private ArrayList<Car> ambulances=new ArrayList<>();
    private FloatingActionButton displayAddSection;
    FirebaseFirestore database;
    private ProgressDialog progressBar;
    private LocationRequest locationRequest;
    CarAdapter carAdapter;
    private SharedPreferences sharedPref;
    private ListView ambulanceList;
    LinearLayout addSection;
    String key;
    int counter=0;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ambulance);
        driverfname=findViewById(R.id.dr_fname);
        driverphone1=findViewById(R.id.dr_number1);
        driverphone2=findViewById(R.id.dr_number2);
        platenumber=findViewById(R.id.plateNumber);
        carAdapter=new CarAdapter(ambulances);
        addAmbulance=findViewById(R.id.Add_ambulance);
        back=findViewById(R.id.back);
        key=getIntent().getStringExtra("key");
        addSection=findViewById(R.id.addSection);
        displayAddSection=findViewById(R.id.display);
        sharedPref =getSharedPreferences("PREF_PERSONAL_DATA", Context.MODE_PRIVATE);
        ambulanceList=findViewById(R.id.ambulance_list);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        database=FirebaseFirestore.getInstance();
        Log.d("key", "onEvent: "+sharedPref.getString("user_id","null"));
        if(Build.VERSION.SDK_INT>=23){
            View decore=AddAmbulance.this.getWindow().getDecorView();
            if(decore.getSystemUiVisibility()!=View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR){
                decore.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            else{
                decore.setSystemUiVisibility(0);
            }
        }
        new LoadAvailableAmbulances().execute();
        displayAddSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(counter==0){
                    addSection.setVisibility(View.VISIBLE);
                    counter=+1;
                } else if (counter==1) {
                    addSection.setVisibility(View.GONE);
                    counter=0;
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AddAmbulance.this,Org_Bottom_nav.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
addAmbulance.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        if(driverfname.getText().toString().isEmpty()||driverphone1.getText().toString().isEmpty()||
                driverphone2.getText().toString().isEmpty()||platenumber.getText().toString().isEmpty()){
            Toast.makeText(AddAmbulance.this, "Please Fill All The Necessary Fields!!", Toast.LENGTH_SHORT).show();
        }
        else {
        progressBar = new ProgressDialog(AddAmbulance.this);
        progressBar.setCancelable(true);
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.setMessage("please wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
        sharedPref = getSharedPreferences("PREF_PERSONAL_DATA", Context.MODE_PRIVATE);
        Ambulance.put(driverfname.getHint().toString(),driverfname.getText().toString());
        Ambulance.put(driverphone1.getHint().toString(),driverphone1.getText().toString());
        Ambulance.put(driverphone2.getHint().toString(),driverphone2.getText().toString());
        Ambulance.put(platenumber.getHint().toString(),platenumber.getText().toString());
        Ambulance.put("latitude","");
        Ambulance.put("state","free");
        Ambulance.put("status","on");
        Ambulance.put("request","");
        Ambulance.put("longitude","");
        Ambulance.put("hospital_id",sharedPref.getString("user_id","null"));
        Ambulance.put("hospitalName",sharedPref.getString("hospname","null"));
        SharedPreferences.Editor editor=sharedPref.edit();
        editor.putString("car_id",platenumber.getText().toString());
        editor.commit();
        database.collection("Ambulance").document(platenumber.getText().toString()).set(Ambulance).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressBar.dismiss();
                        Toast.makeText(AddAmbulance.this,"Ambulance Added Successfully",Toast.LENGTH_LONG).show();
                        getCurrentLocation();
                        addSection.setVisibility(View.GONE);
                    }
                });
    }}
});
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                getCurrentLocation();
            }
        }
    }

    @Override
    public void onBackPressed() {

    }
    @Nullable
    @Override
    public ComponentName startService(Intent service) {
        return super.startService(service);
    }
    public void getCurrentLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(AddAmbulance.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {
                    LocationServices.getFusedLocationProviderClient(AddAmbulance.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(AddAmbulance.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() > 0) {

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();
                                        sharedPref = getSharedPreferences("PREF_PERSONAL_DATA", Context.MODE_PRIVATE);
                                        CollectionReference collectionReference=FirebaseFirestore.getInstance().collection("Ambulance");
                                        DocumentReference documentReference=collectionReference.document(sharedPref.getString("car_id","null"));
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
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(AddAmbulance.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(AddAmbulance.this, 2);
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
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }
    private class LoadAvailableAmbulances extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {

            ambulanceList.setVisibility(View.INVISIBLE);
            CollectionReference collectionReference=FirebaseFirestore.getInstance().collection("Ambulance");
            collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(value.isEmpty()){
                        Toast.makeText(AddAmbulance.this,"Empty Collection",Toast.LENGTH_LONG).show();
                    } else if (!value.isEmpty()) {
                        sharedPref = getSharedPreferences("PREF_PERSONAL_DATA", Context.MODE_PRIVATE);
                        for(DocumentSnapshot snapshot: value){
                            //list ambulances under this hospital id
                            if(snapshot.get("hospital_id").equals(sharedPref.getString("user_id","0"))){
                                Car cars=new Car(snapshot.get(driverfname.getHint().toString()).toString(),
                                        snapshot.get(platenumber.getHint().toString()).toString(),
                                        snapshot.get(driverphone1.getHint().toString()).toString(),
                                        snapshot.get(driverphone2.getHint().toString()).toString());
                                Log.d("key", "onEvent: "+key);
                                ambulances.add(cars);

                            }
                        }
                    }
                }
            });
            return null;
        }
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            carAdapter.notifyDataSetChanged();
            ambulanceList.setAdapter(carAdapter);
            ambulanceList.setVisibility(View.VISIBLE);
        }
    }
    public static class TypeChecker extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            return null;
        }
    }
}