package com.example.preg_women.Screens;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.preg_women.MainActivity;
import com.example.preg_women.R;
import com.example.preg_women.registration_pages.User_Registration;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private LocationRequest locationRequest;
    private  TextView plate_no,hospitalName,driverName,bottomTitle;
    private Button make_call,show,back;
    private BottomSheetDialog bottomSheetDialog;
    private FirebaseFirestore database;
    private  Marker markerPerth;
    private Button callForHelp;
    ArrayList<LatLong> locations=new ArrayList<>();
    ArrayList<String> titles=new ArrayList<>();
    private SharedPreferences sharedPref;
    SupportMapFragment mapFragment;
    LatLng closelatLng;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if(Build.VERSION.SDK_INT>=23){
            View decore= MapsActivity.this.getWindow().getDecorView();
            if(decore.getSystemUiVisibility()!=View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR){
                decore.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            else{
                decore.setSystemUiVisibility(0);
            }
        }
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
       bottomSheetDialog=new BottomSheetDialog(this,R.style.BottomSheetDialogStyle);
        View view=getLayoutInflater().inflate(R.layout.bottom_draw,null,false);
        callForHelp=view.findViewById(R.id.emergency_call);
         bottomTitle=view.findViewById(R.id.bottomTitle);
         back=findViewById(R.id.back);
         driverName=view.findViewById(R.id.driverName);
         hospitalName=view.findViewById(R.id.ambulanceFrom);
        show=findViewById(R.id.show);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        progressBar=view.findViewById(R.id.spin_kit);
        if(bottomSheetDialog.isShowing()){
            if(Build.VERSION.SDK_INT>=23){
                View decore= MapsActivity.this.getWindow().getDecorView();
                if(decore.getSystemUiVisibility()!=View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR){
                    decore.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
                else{
                    decore.setSystemUiVisibility(0);
                }
            }
        }
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        plate_no=view.findViewById(R.id.plate_number);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.show();
            }
        });
//                new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                getCurrentLocation();
//            }
//        },0,1000);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MapsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        database = FirebaseFirestore.getInstance();
    mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.my_map);
        mapFragment.getMapAsync(this);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                getCurrentLocation();
            }
        }
    }

    private void getCurrentLocation() {

        sharedPref = this.getSharedPreferences("PREF_PERSONAL_DATA", Context.MODE_PRIVATE);
        String userID=sharedPref.getString("user_id","null");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(MapsActivity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(MapsActivity.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() >0){

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();
                                        CollectionReference collectionReference=FirebaseFirestore.getInstance().collection("Hospital");
                                        DocumentReference documentReference=collectionReference.document(userID);
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
                    Toast.makeText(MapsActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(MapsActivity.this, 2);
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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        CollectionReference collectionReference= FirebaseFirestore.getInstance().collection("Ambulance");
        collectionReference
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()&&task.getResult().size()!=0) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //skip buses with empty longitude and latitude
                                if(!document.get("latitude").toString().isEmpty()||!document.get("longitude").toString().isEmpty()){
                                    //skip ambulances with status "off" and state "occupied"
                                    if (document.get("status").equals("off") || document.get("state").equals("occupied")) {
                                        continue;
                                    }
                                    else{
                                        LatLong latLong=new LatLong(Double.parseDouble(document.get("latitude").toString()),Double.parseDouble(document.get("longitude").toString()),document.getId());
                                        locations.add(latLong);
                                        titles.add(document.get("hospitalName").toString());
                                        Log.d("TAG", document.get("status") + " => " + document.get("state"));
                                    }
                                }
                            }
                            //draw available Ambulances
                            for(int i=0; i<locations.size(); i++){
                                    LatLng latLng=new LatLng(locations.get(i).getLatitude(),locations.get(i).getLongitude());
                                    Log.d("TAG", latLng.latitude + " => " + latLng.longitude);
                                    markerPerth = googleMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .icon(seticon(MapsActivity.this,R.drawable.directions_bus_24)).title(titles.get(i)));
                                    //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                            }
                            if(!locations.isEmpty()){
                                closestAmbulance(locations,task,googleMap);
                            }
                            else {
                                sharedPref = getApplication().getSharedPreferences("PREF_PERSONAL_DATA", Context.MODE_PRIVATE);
                                String userID=sharedPref.getString("user_id","null");
                                CollectionReference collectionReference= FirebaseFirestore.getInstance().collection("User");
                                collectionReference.document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                        LatLng latLng=new LatLng(Double.parseDouble(value.get("latitude").toString()),Double.parseDouble(value.get("longitude").toString()));
                                        Log.d("TAG", latLng.latitude + " => " + latLng.longitude);
                                        bottomTitle.setText("No Ambulance Available Currently");
                                        markerPerth = googleMap.addMarker(new MarkerOptions()
                                                .position(latLng)
                                                .icon(seticon(MapsActivity.this,R.drawable.directions_bus_24)).title("YOU"));
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                                    }
                                });
                                Log.d("TAG2", "Error getting documents: ", task.getException());
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG2", "Error getting documents: ", e.getCause());
                    }
                });
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
    public BitmapDescriptor seticon(AppCompatActivity context, int drawable){
        Drawable drawable1=ActivityCompat.getDrawable(context,drawable);
        drawable1.setBounds(0,0 ,drawable1.getIntrinsicWidth(),drawable1.getIntrinsicHeight());
        Bitmap bitmap=Bitmap.createBitmap(drawable1.getIntrinsicWidth(),drawable1.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        drawable1.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    public void closestAmbulance(ArrayList<LatLong> locations,Task<QuerySnapshot> ambulanceID,GoogleMap googleMap){
        final String[] closest = new String[1];
        float[] results = new float[locations.size()];
        ArrayList<LatLong> Mylocation=new ArrayList<>();
        sharedPref = this.getSharedPreferences("PREF_PERSONAL_DATA", Context.MODE_PRIVATE);
        String userID=sharedPref.getString("user_id","null");
        Log.d("DER",  userID);
            CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("User");
            collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (userID.equals(document.get("first name").toString() + " " + document.get("phone number 1").toString())) {
                                LatLong latLong = new LatLong(Double.parseDouble(document.get("latitude").toString()), Double.parseDouble(document.get("longitude").toString()), document.getId());
                                Mylocation.add(latLong);
                                Log.d("MYLOC", Mylocation.get(0).getLatitude().toString() + " " + Mylocation.get(0).getLongitude() + " " + latLong.getId());
                            }
                        }}
                  if(locations.size()>0){
                            for (int i = 0; i < locations.size(); i++) {
                                LatLng latLngFrom = new LatLng(Mylocation.get(0).getLatitude(), Mylocation.get(0).getLongitude());
                                LatLng latLngTo = new LatLng(locations.get(i).getLatitude(), locations.get(i).getLongitude());
                              //  results[i]=calculateDistance(latLngFrom.latitude,latLngFrom.longitude,locations.get(i).getLatitude(),locations.get(i).getLongitude());
                              results[i] = (float) SphericalUtil.computeDistanceBetween(latLngFrom, latLngTo);
                            }
                        }

                        float smallest = results[0];
                  for (int i = 0; i < results.length; i++) {
                            if (results[i] <=smallest) {
                                smallest = results[i];
                                closest[0] = locations.get(i).getId();
                                closelatLng = new LatLng(locations.get(i).getLatitude(), locations.get(i).getLongitude());
                            }
                        }
                        if(locations.size()==1){
                            closest[0] = locations.get(0).getId();
                        }

                    // display closest ambulance data
                    if (closest[0] != null && !closest[0].isEmpty()) {
                        database.collection("Ambulance").document(closest[0]).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                DocumentReference reference=value.getReference();
                                if (error != null) {
                                    // Handle the Firestore error here
                                    return;
                                }
                                if (value != null) {
                                    reference.update("state","occupied");
                                    plate_no.setText(closest[0]);
                                    driverName.setText(value.get("driver full name").toString());
                                    hospitalName.setText(value.get("hospitalName").toString());
                                    markerPerth = googleMap.addMarker(new MarkerOptions()
                                            .position(closelatLng)
                                            .icon(seticon(MapsActivity.this,R.drawable.directions_bus_24)).title(value.get("driver full name").toString()));
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(closelatLng, 17));
                                }
                            }
                        });
                    }

                    callForHelp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
progressBar.setVisibility(View.VISIBLE);
                                //uploading the request
                                database.collection("User").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                        String request = value.get("first name").toString() + " " + value.get("phone number 1").toString();
                                        database.collection("Ambulance").document(closest[0]).update("request", request);
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(MapsActivity.this, "Request Sent", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }

            });

    }


}
