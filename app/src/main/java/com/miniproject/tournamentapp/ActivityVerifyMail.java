package com.miniproject.tournamentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityVerifyMail extends AppCompatActivity {

    Button mNext,mResend;
    FirebaseAuth fAuth;
    MaterialToolbar toolbar;

    TextView txtView;

    CountDownTimer countDownTimer;

    String countStr = "Didn't recieve Verifcation mail?\nResend in ";
    String finalStr = "Didn't recieve Verifcation mail?\nClick on Resend Button";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_mail);

        mNext = findViewById(R.id.next_verifymail);
        mResend = findViewById(R.id.resend_verifymail);

        txtView = findViewById(R.id.timer_verifymail);


        if (getSupportActionBar()==null){
            toolbar = findViewById(R.id.toolbar_verifymail);
            setSupportActionBar(toolbar);
        }

        mResend.setEnabled(false);
        fAuth = FirebaseAuth.getInstance();

        countDownTimer = new CountDownTimer(60000,500){
            @Override
            public void onTick(long l) {
                txtView.setText(countStr+l/1000+" Seconds.");
                mResend.setEnabled(false);
            }

            @Override
            public void onFinish() {
                txtView.setText(finalStr);
                mResend.setEnabled(true);
            }
        };

        countDownTimer.start();

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fAuth.getCurrentUser()!=null){
                    fAuth.getCurrentUser().reload();
                    if(fAuth.getCurrentUser().isEmailVerified())
                        emailIsVerified();
                    else
                        Toast.makeText(getApplicationContext(), "Please verify your email first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mResend.setEnabled(false);
                if (fAuth.getCurrentUser()!=null){
                    fAuth.getCurrentUser().sendEmailVerification();
                    countDownTimer.start();
                    Toast.makeText(getApplicationContext(), "Verification email is sent to your email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.logout_menuitem){
            fAuth.signOut();
            item.setEnabled(false);

            Intent intent = new Intent(getApplicationContext(), ActivityAuthentication.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            finish();
            return true;
        }else
            return super.onOptionsItemSelected(item);
    }

    private void emailIsVerified() {
        Toast.makeText(getApplicationContext(), "Your Email is verified", Toast.LENGTH_SHORT).show();
    }
}