package com.example.preg_women.Screens;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.NoCopySpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.preg_women.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {
    Button resetButton,back;
    EditText resetEmail;
    FirebaseAuth mAuth;
    Dialog dialog;
    private ProgressDialog progressBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);
        mAuth=FirebaseAuth.getInstance();
        resetButton=findViewById(R.id.resetButton);
        resetEmail=findViewById(R.id.emailEditText);
        progressBar = new ProgressDialog(ResetPassword.this);
        progressBar.setCancelable(true);
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.setMessage("please wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ResetPassword.this, Login_Page.class);
                startActivity(intent);
                finish();
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(resetEmail.getText().toString().isEmpty()){
                    Toast.makeText(ResetPassword.this, "Email Field Can't be Empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    progressBar.show();
                    mAuth.sendPasswordResetEmail(resetEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressBar.dismiss();
                                Toast.makeText(ResetPassword.this, "Check Your Email/Spam Folder", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(ResetPassword.this, Login_Page.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(ResetPassword.this, "Enter Correct Email Address", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ResetPassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });








    }
}
