package com.example.preg_women.Screens;

import static android.graphics.PorterDuff.Mode.CLEAR;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.MacAddress;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.preg_women.R;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.sql.Time;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import ng.max.slideview.SlideView;

public class Org_Bottom_nav extends AppCompatActivity implements OnMapReadyCallback {
    private TextView fullName, address, houseNumber, phoneNumber1, phoneNumber2, healthCenterName, startAmbulance;
    private Button call1, call2, addAmbulance, show, sendPlateNumber;
    private SharedPreferences sharedPref;
    private FirebaseFirestore database;
    int x = 0;
    final int ALARM_REQUEST = 234324243;
    SlideView slider;
    String PN2;
    private LocationRequest locationRequest;
    private BottomSheetDialog bottomSheetDialog;
    TimerTask task;
    EditText idPlateNumber;
    private Button save;
    private Button cancel;
    MediaPlayer mp = new MediaPlayer();
    private int counter = 0;
    private ExpandableLayout expandableLayout;
    boolean state=true;
    AlertDialog.Builder builder;
    private Dialog dialog;
    private int originalWindowBackgroundColor;
    private TextView signOut;
    private FirebaseAuth mAuth;
    String PN1;
    Button Accept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_bottom_nav);
        if(Build.VERSION.SDK_INT>=23){
            View decore= Org_Bottom_nav.this.getWindow().getDecorView();
            if(decore.getSystemUiVisibility()!=View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR){
                decore.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            else{
                decore.setSystemUiVisibility(0);
            }
        }
        getLocationPermission();
        originalWindowBackgroundColor = ((ColorDrawable) getWindow().getDecorView().getBackground()).getColor();
        View view = getLayoutInflater().inflate(R.layout.ambulancepage, null, false);
        fullName = view.findViewById(R.id.fname);
        address = view.findViewById(R.id.address);
        mAuth=FirebaseAuth.getInstance();
        houseNumber = view.findViewById(R.id.hnumber);
        call1 = view.findViewById(R.id.call1);
        expandableLayout = findViewById(R.id.expandable_layout);
        call2 = view.findViewById(R.id.call2);
        phoneNumber1 = view.findViewById(R.id.pn1);
        phoneNumber2 = view.findViewById(R.id.pn2);
        show = findViewById(R.id.show);
        addAmbulance = findViewById(R.id.addAmbulance);
        locationRequest = LocationRequest.create();
        healthCenterName = findViewById(R.id.healthCenterName);
        startAmbulance = findViewById(R.id.startAmbulance);
        signOut=findViewById(R.id.signOut);
        bottomSheetDialog = new BottomSheetDialog(this,R.style.BottomSheetDialogStyle);
        bottomSheetDialog.setContentView(view);
        idPlateNumber = findViewById(R.id.idPlateNumber);
        sendPlateNumber = findViewById(R.id.turnOnPlateNumber);
        Button turnOff=findViewById(R.id.turnOffPlateNumber);
       bottomSheetDialog.show();
        dialog = new Dialog(getApplicationContext());
        dialog.setContentView(R.layout.dialogbox);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        save = dialog.findViewById(R.id.accept);
        cancel = dialog.findViewById(R.id.cancel);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        slider = view.findViewById(R.id.slideView);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        int collaps = R.drawable.collaps;
        int drop = R.drawable.dropdown;
        database = FirebaseFirestore.getInstance();
        mp=MediaPlayer.create(getApplicationContext(), R.raw.alarm);
        sharedPref = getSharedPreferences("PREF_PERSONAL_DATA", Context.MODE_PRIVATE);
        healthCenterName.setText(sharedPref.getString("hospname",null));
      Accept=bottomSheetDialog.findViewById(R.id.acceptRequest);
        //stop the notification by accepting the request
        Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task.cancel();
mp.stop();
                Log.d("HI", "onClick: "+"hi");
            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(Org_Bottom_nav.this, Login_Page.class);
                startActivity(intent);
            }
        });
        call1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPhoneNumber(PN1);
                task.cancel();
            }
        });
        call2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPhoneNumber(PN2);
                task.cancel();
            }
        });
        slider.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                task.cancel();
                CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Ambulance");
                DocumentReference documentReference = collectionReference.document(sharedPref.getString("car_id",""));
                PN1="";
                PN2="";
                documentReference.update("request", "");
                documentReference.update("state", "free");
                task.run();
            }
        });
        startAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (counter == 0) {
                    expandableLayout.expand();
                    startAmbulance.setCompoundDrawablesWithIntrinsicBounds(0, 0, collaps, 0);
                    startAmbulance.setCompoundDrawablePadding(8);
                    counter = +1;
                } else if (counter == 1) {
                    expandableLayout.collapse();
                    startAmbulance.setCompoundDrawablesWithIntrinsicBounds(0, 0, drop, 0);
                    startAmbulance.setCompoundDrawablePadding(8);
                    counter = 0;
                }
            }
        });
        sendPlateNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.collection("Ambulance").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            //check if the plate number belongs in the logged in hospital id
                            if(idPlateNumber.getText().toString().isEmpty()){
                                Toast.makeText(Org_Bottom_nav.this, "please insert plate number ", Toast.LENGTH_SHORT).show();
                            }
                            else{
                            if (idPlateNumber.getText().toString().equals(snapshot.getId())) {
                                DocumentReference reference=snapshot.getReference();
                                reference.update("status","on").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(Org_Bottom_nav.this, "You Turned On This Ambulance", Toast.LENGTH_SHORT).show();
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putString("car_id", idPlateNumber.getText().toString());
                                        editor.commit();
                                    }
                                });
                                 break;
                            }
                        }}
                        Log.d("TAG", "onComplete: "+sharedPref.getString("car_id",null));
                        bottomSheetDialog.show();
                    }
                });
            }
        });
        turnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.collection("Ambulance").document(idPlateNumber.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
DocumentSnapshot snapshot=task.getResult();
DocumentReference reference=snapshot.getReference();
reference.update("status","off").addOnCompleteListener(new OnCompleteListener<Void>() {
    @Override
    public void onComplete(@NonNull Task<Void> task) {
        Toast.makeText(Org_Bottom_nav.this, "You Turned Off This Ambulance", Toast.LENGTH_SHORT).show();
    }
});
                    }
                });
            }
        });

        addAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Org_Bottom_nav.this, AddAmbulance.class);
                startActivity(intent);
            }
        });
        sharedPref = getSharedPreferences("PREF_PERSONAL_DATA", Context.MODE_PRIVATE);
        String id = sharedPref.getString("user_id", "null");
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.my_map);
        mapFragment.getMapAsync(this);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Org_Bottom_nav.this, Login_Page.class);
        startActivity(intent);
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
    private void setStatusBarIconColor(boolean isDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            int flags = decor.getSystemUiVisibility();
            if (isDark) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            decor.setSystemUiVisibility(flags);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                getLocationPermission();
            }
        }
    }
    private void getCurrentLocation() {
        sharedPref = this.getSharedPreferences("PREF_PERSONAL_DATA", Context.MODE_PRIVATE);
        String userID = sharedPref.getString("user_id", "null");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(Org_Bottom_nav.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {
                    LocationServices.getFusedLocationProviderClient(Org_Bottom_nav.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                    LocationServices.getFusedLocationProviderClient(Org_Bottom_nav.this)
                                            .removeLocationUpdates(this);
                                    if (locationResult != null && locationResult.getLocations().size() > 0) {
                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();
                                        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Ambulance");
                                        DocumentReference documentReference = collectionReference.document(sharedPref.getString("car_id", "null"));
                                        documentReference.update("latitude", latitude);
                                        documentReference.update("longitude", longitude);
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
                    Toast.makeText(Org_Bottom_nav.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(Org_Bottom_nav.this, 2);
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
//        save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//                mp.stop();
//                task.run();
//            }
//        });

//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//                mp.stop();
//                task.run();
//            }
//        });
        task = new TimerTask() {
            @Override
            public void run() {
                checkForUpDate(googleMap);
                Log.d("CARLOC", "run: " + sharedPref.getString("car_id", "null") + x++);
            }
        };
        new Timer().scheduleAtFixedRate(task, 0, 1000);

    }

    public void checkForUpDate(GoogleMap googleMap) {
        sharedPref = getSharedPreferences("PREF_PERSONAL_DATA", Context.MODE_PRIVATE);
        String id = sharedPref.getString("car_id", "null");
        if (id.equals("null")) {
            // Toast.makeText(Org_Bottom_nav.this,"No ambulance Registered",Toast.LENGTH_LONG).show();
        } else if (!id.equals("null")) {
            Log.d("ided", "checkForUpDate: " + sharedPref.getString("car_id", "null"));
            database.collection("Ambulance").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot valueInfo, @Nullable FirebaseFirestoreException error) {
                    if (valueInfo.get("request").toString().equals("")) {
                        fullName.setText("----/----");
                        houseNumber.setText("----/----");
                        address.setText("----/----");
                        phoneNumber1.setText("----/----");
                        phoneNumber2.setText("----/----");
                    } else if (!valueInfo.get("request").toString().isEmpty()) {
                        //get The request
                        bottomSheetDialog.show();
                        mp.start();
                        CollectionReference ambulanceCollection = FirebaseFirestore.getInstance().collection("Ambulance");
                        DocumentReference documentReference = ambulanceCollection.document(sharedPref.getString("car_id",""));
                        documentReference.update("state", "occupied");
                        DocumentReference collectionReference = FirebaseFirestore.getInstance().collection("User").document(valueInfo.get("request").toString());
                        collectionReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                ShowNotification.showNotification(Org_Bottom_nav.this, "Request Incoming", value.get("first name").toString()+"Needs Help!");
                                LatLng latLng = new LatLng(Double.parseDouble(value.get("latitude").toString()), Double.parseDouble(value.get("longitude").toString()));
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.icon(seticon(Org_Bottom_nav.this, R.drawable.person_pin_circle_));
                                markerOptions.title(value.get("first name").toString() + value.get("middle name").toString());
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                                googleMap.addMarker(markerOptions);
                                fullName.setText(value.get("first name").toString()+"\t" + value.get("middle name").toString());
                                houseNumber.setText(value.get("House no").toString());
                                address.setText(value.get("Sub City").toString() + value.get("City").toString());
                                phoneNumber1.setText(value.get("phone number 1").toString());
                                phoneNumber2.setText(value.get("phone number 2").toString());
                                 PN1=value.get("phone number 1").toString();
                               PN2=value.get("phone number 2").toString();
                                //clear the request field
                            }
                        });
//                       database.collection("Ambulance").
//                               document(sharedPref.getString("car_id",null)).update("state","occupied");
                    }
                }
            });
        }
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
    public BitmapDescriptor seticon(AppCompatActivity context, int drawable) {
        Drawable drawable1 = ActivityCompat.getDrawable(context, drawable);
        drawable1.setBounds(0, 0, drawable1.getIntrinsicWidth(), drawable1.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(drawable1.getIntrinsicWidth(), drawable1.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable1.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void callPhoneNumber(String phoneNumber) {
        try {
            if (Build.VERSION.SDK_INT > 22) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 101);
                    return;
                }

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(callIntent);
            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(callIntent);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    //    public void alertNotification() {
//       // startAlert();
//        task.cancel();
////        mp=MediaPlayer.create(getApplicationContext(),R.raw.alarm);
////        mp.start();
//
//        builder = new AlertDialog.Builder(Org_Bottom_nav.this);
//        // Set the message show for the Alert time
//        builder.setMessage("Signal Incoming...");
//        // Set Alert Title
//        builder.setTitle("Alert !");
//        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
//        builder.setCancelable(false);
//        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
//        builder.setPositiveButton("Accept", (DialogInterface.OnClickListener) (dialog, which) -> {
//            // When the user click yes button then app will close
//            CollectionReference collectionReference=FirebaseFirestore.getInstance().collection("Ambulance");
//            DocumentReference documentReference=collectionReference.document(sharedPref.getString("car_id",null));
//            documentReference.update("state","occupied");
//            Toast.makeText(this, sharedPref.getString("car_id","null"), Toast.LENGTH_SHORT).show();
//            mp.stop();
//            finish();
//        });
//        // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
//        builder.setNegativeButton("Redirect", (DialogInterface.OnClickListener) (dialog, which) -> {
//            // If user click no then dialog box is canceled.
//            mp.stop();
//            dialog.cancel();
//        });
//        // Create the Alert dialog
//        AlertDialog alertDialog = builder.create();
//        // Show the Alert Dialog box
//        alertDialog.show();
//    }
    private void getLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(Org_Bottom_nav.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {
                    LocationServices.getFusedLocationProviderClient(Org_Bottom_nav.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                    LocationServices.getFusedLocationProviderClient(Org_Bottom_nav.this)
                                            .removeLocationUpdates(this);
                                    if (locationResult != null && locationResult.getLocations().size() > 0) {
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

