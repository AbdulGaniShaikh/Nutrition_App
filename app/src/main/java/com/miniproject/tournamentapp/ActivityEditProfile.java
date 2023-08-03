package com.miniproject.tournamentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityEditProfile extends AppCompatActivity {

    EditText desp;

    ShapeableImageView mAvatar;
    TextView btn;
    ImageButton left,right;
    String userId;

    int avatar;

    RecyclerView recyclerView;
    AdapterProfileRecycler adapter;
    List<ModelInterests> list;

    MaterialToolbar toolbar;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        setupToolbar();

        initiateWithID();

        initiateFirebaseClasses();

        setuRecyclerView();

        getDataFromFirebase();

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (avatar==0)
                    avatar=15;
                else
                    avatar--;

                mAvatar.setImageResource(Keys.getAvatar(avatar));
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(avatar>=15)
                    avatar=0;
                else
                    avatar++;

                mAvatar.setImageResource(Keys.getAvatar(avatar));
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String ,Object> map = new HashMap<>();

                map.put(Keys.USER_DESP,desp.getText().toString().trim());
                map.put(Keys.USER_AVATAR,avatar);

                List<String> interests = new ArrayList<>();

                for (ModelInterests mi: list){
                    if (mi.isSelected())
                        interests.add(mi.getGameName());
                }

                map.put(Keys.USER_INTERESTS,interests);

                fStore.collection(Keys.USER_COLLECTION)
                        .document(userId)
                        .update(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
//                                    Intent intent = new Intent(getApplicationContext(),ActivityProfile.class);
//                                    intent.putExtra("userid",userId);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(intent);
                                    finish();
                                }else
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void setupToolbar(){
        if (getSupportActionBar()==null){
            toolbar = findViewById(R.id.toolbar_settings);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initiateWithID() {
        mAvatar = findViewById(R.id.avatar_editprof);
        desp = findViewById(R.id.desp_settings);

        left = findViewById(R.id.left_settings);
        right = findViewById(R.id.right_settings);
        btn = findViewById(R.id.save_settings);

        recyclerView =findViewById(R.id.rv_settings);
        list = new ArrayList<>();
    }

    private void initiateFirebaseClasses() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();
    }

    private void setuRecyclerView() {

        for (String s :getResources().getStringArray(R.array.gameSpinner)){
            list.add(new ModelInterests(s,false));
        }

        adapter = new AdapterProfileRecycler(ActivityEditProfile.this,list);
        recyclerView.setLayoutManager(new LinearLayoutManager(ActivityEditProfile.this,
                RecyclerView.HORIZONTAL,
                false));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AdapterProfileRecycler.OnItemClickListener() {
            @Override
            public void onClick(int i) {
                ModelInterests s = list.get(i);
                list.set(i,new ModelInterests(s.getGameName(),!s.isSelected()));
                adapter.notifyItemChanged(i);
            }
        });
    }

    private void getDataFromFirebase() {
        fStore.collection(Keys.USER_COLLECTION)
                .document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot dc) {
                        int l = dc.getLong(Keys.USER_AVATAR).intValue();
                        if(!dc.getString(Keys.USER_DESP).equals(""))
                            desp.setText(dc.getString(Keys.USER_DESP));

                        mAvatar.setImageResource(Keys.getAvatar(l));

                        List<String> interests = (List<String>) dc.get(Keys.USER_INTERESTS);
                        for (int i = 0; i < list.size(); i++) {
                            ModelInterests m = list.get(i);
                            if (interests.contains(m.getGameName())){
                                list.set(i,new ModelInterests(m.getGameName(),true));
                                adapter.notifyItemChanged(i);
                            }
                        }
                    }
                });
    }
}