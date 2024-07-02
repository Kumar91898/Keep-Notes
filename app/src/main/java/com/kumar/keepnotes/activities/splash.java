package com.kumar.keepnotes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kumar.keepnotes.R;

public class splash extends AppCompatActivity {
    public static final int TIMER = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(this::moveToMain, TIMER);
    }

    private void moveToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}