package com.miniproject.tournamentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivitySplash extends AppCompatActivity {

    Animation anim_fadein, anim_fadeout;
    ImageView imageView;
    TextView textView;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        anim_fadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_fadein);
        anim_fadeout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_fadeout);

        imageView = findViewById(R.id.icon_splash);
        textView = findViewById(R.id.text_splash);

        Thread myThread = new Thread(){
            @Override
            public void run() {
                imageView.setAnimation(anim_fadein);
                textView.setAnimation(anim_fadein);
                fAuth = FirebaseAuth.getInstance();
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent;
                FirebaseUser user = fAuth.getCurrentUser();
                if (user!=null){
                    user.reload();
                    if (user.isEmailVerified())
                        intent = new Intent(getApplicationContext(), ActivityHome.class);
                    else
                        intent = new Intent(getApplicationContext(), ActivityVerifyMail.class);
                }else
                    intent = new Intent(getApplicationContext(), ActivityAuthentication.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                finish();
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();

    }
}