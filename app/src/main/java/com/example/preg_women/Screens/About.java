package com.example.preg_women.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.preg_women.MainActivity;
import com.example.preg_women.R;

public class About extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Button back=findViewById(R.id.back);
        TextView textView = findViewById(R.id.descriptionTextView);
        String welcomeText = getString(R.string.about_description);
        if(Build.VERSION.SDK_INT>=23){
            View decor=About.this.getWindow().getDecorView();
            if(decor.getSystemUiVisibility()!=View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR){
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            else{
                decor.setSystemUiVisibility(0);
            }
        }
        textView.setText(Html.fromHtml(welcomeText));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(About.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}