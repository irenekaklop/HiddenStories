package com.example.eirini.hiddenstories;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ErrorActivity extends AppCompatActivity{
    TextView textView;
    Button toStart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textView = findViewById(R.id.tv);
        toStart = findViewById(R.id.bt);
        toStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Return to start
                startActivity(new Intent(ErrorActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
