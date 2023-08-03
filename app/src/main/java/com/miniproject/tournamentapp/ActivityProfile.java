package com.miniproject.tournamentapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActivityProfile extends AppCompatActivity {

    TextView mUsername,mDesp,mBuddies,mMatches,mEditProf,mLogout;
    AppCompatButton mRequestBtn;
    ShapeableImageView mAvatar;

//    AppCompatButton followBtn;

    LinearLayout bLL,tLL;

    String id,userId;

    RecyclerView recyclerView;
    AdapterProfileRecycler adapter;
    List<ModelInterests> list;

    MaterialToolbar toolbar;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        //bind all the views
        initiateWithID();

        //initiate firebase classes
        initiateFirebaseClasses();

        hideIfOtherUser();

        //Set up toolbar
        setupToolbar();

        //Setup RecyclerView
        setuRecyclerView();


        //get User Data from Firebase
        getDataFromFirebase();


        bLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityProfile.this,ActivityMyBuddies.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });

        tLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mEditProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ActivityEditProfile.class));
            }
        });

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), ActivityAuthentication.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                finish();
            }
        });

    }

    private void hideIfOtherUser() {

        if (!userId.equals(id)){
            mEditProf.setVisibility(View.GONE);
            mLogout.setVisibility(View.GONE);
        }else{
//            followBtn.setVisibility(View.GONE);
        }

    }

    private void setupToolbar(){
        if (getSupportActionBar()==null){
            toolbar = findViewById(R.id.toolbar_profile);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initiateFirebaseClasses() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();

        Bundle b = getIntent().getExtras();
        id = b.getString("userid");
    }

    private void initiateWithID() {
        mUsername = findViewById(R.id.username_profile);
        mDesp = findViewById(R.id.desp_profile);
        mBuddies = findViewById(R.id.buddies_profile);
        mMatches = findViewById(R.id.matches_profile);
        mEditProf = findViewById(R.id.editprof_profile);
        mLogout = findViewById(R.id.logout_profile);
        
//        followBtn = findViewById(R.id.follow_profile);

        bLL = findViewById(R.id.con_buddies_profile);
        tLL = findViewById(R.id.con_matches_profile);

        mAvatar = findViewById(R.id.avatar_profile);

        mRequestBtn = findViewById(R.id.request_profile);

        recyclerView =findViewById(R.id.recycler_profile);
    }

    private void setuRecyclerView() {

        list = new ArrayList<>();

        adapter = new AdapterProfileRecycler(ActivityProfile.this,list);
        recyclerView.setLayoutManager(new LinearLayoutManager(ActivityProfile.this,
                RecyclerView.HORIZONTAL,
                false));
        recyclerView.setAdapter(adapter);
    }

    private void getDataFromFirebase() {
        fStore.collection(Keys.USER_COLLECTION)
                .document(id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot dc, @Nullable FirebaseFirestoreException error) {
                        list.clear();
                        mUsername.setText(dc.getString(Keys.USER_NAME));
                        int l = dc.getLong(Keys.USER_AVATAR).intValue();
                        if(!dc.getString(Keys.USER_DESP).equals(""))
                            mDesp.setText(dc.getString(Keys.USER_DESP));
                        Map<String,Object> m = (Map<String, Object>) dc.get(Keys.USER_BUDDIES);
                        mBuddies.setText(""+m.size());
                        Map<String,Object> n = (Map<String, Object>) dc.get(Keys.USER_PARTICIPATIONS);
                        mMatches.setText(""+n.size());
                        mAvatar.setImageResource(Keys.getAvatar(l));

                        List<String> interests = (List<String>) dc.get(Keys.USER_INTERESTS);
                        for(String i : interests){
                            list.add(new ModelInterests(i,false));
                            adapter.notifyItemInserted(list.size()-1);
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        if (adapter!=null)
            adapter.notifyDataSetChanged();
        super.onResume();
    }
}