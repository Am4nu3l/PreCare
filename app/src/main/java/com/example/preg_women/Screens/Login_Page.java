package com.example.preg_women.Screens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.preg_women.registration_pages.*;
import com.example.preg_women.MainActivity;
import com.example.preg_women.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class Login_Page extends AppCompatActivity {
    EditText username,password;
Button login;
TextView Register,forgotPassword;
private FirebaseAuth mAuth;
boolean usr=false;
    private ProgressDialog progressBar;
    private FirebaseFirestore firestore;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        login=findViewById(R.id.btn_login);
        mAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        username=findViewById(R.id.l_username);
        password=findViewById(R.id.l_password);
        forgotPassword=findViewById(R.id.forgotPassword);
        Register=findViewById(R.id.l_register);
        if(Build.VERSION.SDK_INT>=23){
            View decore=Login_Page.this.getWindow().getDecorView();
            if(decore.getSystemUiVisibility()!=View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR){
                decore.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            else{
                decore.setSystemUiVisibility(0);
            }
        }
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Login_Page.this, ResetPassword.class);
                startActivity(intent);
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              if(username.getText().toString().equals("")||password.getText().toString().equals("")){
                  Toast.makeText(Login_Page.this,"email or password is empty",Toast.LENGTH_LONG).show();
              }
              else{
                  progressBar = new ProgressDialog(Login_Page.this);
                  progressBar.setCancelable(true);
                  progressBar.setCanceledOnTouchOutside(false);
                  progressBar.setMessage("please wait...");
                  progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                  progressBar.setProgress(0);
                  progressBar.setMax(100);
                  progressBar.show();
                  mAuth.signInWithEmailAndPassword(username.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> tasks) {
                          if(tasks.isSuccessful()){
                              firestore.collection("Hospital").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                  @Override
                                  public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                      for(DocumentSnapshot snapshot: task.getResult()){
                                          if(snapshot.get("Email").equals(tasks.getResult().getUser().getEmail())){
                                              progressBar.dismiss();
                                              sharedPref =getSharedPreferences("PREF_PERSONAL_DATA", Context.MODE_PRIVATE);
                                              SharedPreferences.Editor editor = sharedPref.edit();
                                              editor.putString("user_id",snapshot.getId());
                                              editor.putString("hospname",snapshot.get("Name").toString());
                                              editor.putString("user_type","ORG");
                                              editor.commit();
                                              Toast.makeText(Login_Page.this,"Logged In Successfully",Toast.LENGTH_LONG).show();
                                              Intent intent=new Intent(Login_Page.this, Org_Bottom_nav.class);
                                              //get hospital id from the snapshot
                                              intent.putExtra("key",snapshot.getId());
                                              startActivity(intent);
                                              finish();
                                          }
                                      }
                                  }
                              });
                            firestore.collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for(DocumentSnapshot snapshot: task.getResult()){
                                        if(snapshot.get("Email").equals(tasks.getResult().getUser().getEmail())){
                                            progressBar.dismiss();
                                            sharedPref =getSharedPreferences("PREF_PERSONAL_DATA", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putString("user_id",snapshot.getId());
                                            editor.putString("first name",snapshot.get("first name").toString());
                                            editor.putString("middle name",snapshot.get("middle name").toString());
                                            editor.putInt("month", Integer.parseInt(snapshot.get("monthe's").toString()));
                                            Log.d("TAG", "onComplete: "+snapshot.get("monthe's").toString());
                                            editor.putInt("days", Integer.parseInt(snapshot.get("Days").toString()));
                                            editor.putString("phone number 1",snapshot.get("phone number 1").toString());
                                            editor.putString("phone number 2",snapshot.get("phone number 2").toString());
                                            editor.putString("email",snapshot.get("Email").toString());
                                            editor.putString("age",snapshot.get("Age").toString());
                                            editor.putString("membership",snapshot.get("membership").toString());
                                            editor.putString("user_type","USER");
                                            editor.commit();
                                            Toast.makeText(Login_Page.this,"Logged In Successfully",Toast.LENGTH_LONG).show();
                                            Intent intent=new Intent(Login_Page.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                            });
                          }
                          else{
                              progressBar.dismiss();
                              Toast.makeText(Login_Page.this,"email or password mismatch",Toast.LENGTH_LONG).show();
                          }
                      }
                  });
              }
          }
      });
Register.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent=new Intent(Login_Page.this, User_Registration.class);
        startActivity(intent);
    }
});
    }
}