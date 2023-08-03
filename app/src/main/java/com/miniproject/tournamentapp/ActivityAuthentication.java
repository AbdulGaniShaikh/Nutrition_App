package com.miniproject.tournamentapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityAuthentication extends AppCompatActivity {

    AppCompatButton mLogin,mSignup;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        mLogin = findViewById(R.id.login_auth);
        mSignup = findViewById(R.id.signup_auth);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ActivityLogin.class));
                mLogin.setEnabled(false);
                mSignup.setEnabled(false);
            }
        });

        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ActivitySignup.class));
                mSignup.setEnabled(false);
                mLogin.setEnabled(false);
            }
        });
    }

    @Override
    protected void onStart() {
        if (mLogin!=null){
            mLogin.setEnabled(true);
        }
        if (mSignup!=null){
            mSignup.setEnabled(true);
        }
        super.onStart();
    }
}