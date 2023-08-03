package com.miniproject.tournamentapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityRequest extends AppCompatActivity {

    RecyclerView requestRv;
    AdapterRequest requestAdapter;
    List<ModelRequest> requestList;

    LinearLayout ll;

    String userId,userName;
    String tId,tName;

    MaterialToolbar toolbar;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        initiateWithID();

        initiateFirebaseClasses();

        setUpToolbar();

        setUpReqRV();

        getTeamData();

    }

    private void initiateWithID() {

        toolbar = findViewById(R.id.toolbar_requests);
        requestRv = findViewById(R.id.rv_requests);
        ll = findViewById(R.id.noresult_requests);

        Bundle b = getIntent().getExtras();
        tId = b.getString("id");

        requestList = new ArrayList<>();

    }

    private void initiateFirebaseClasses() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();
        fStore.collection(Keys.USER_COLLECTION)
                .document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot dc) {
                        userName = dc.getString(Keys.USER_NAME);
                    }
                });
    }

    private void setUpReqRV() {
        requestAdapter = new AdapterRequest(this,requestList);
        requestRv.setLayoutManager(new LinearLayoutManager(this));
        requestRv.setAdapter(requestAdapter);

        requestAdapter.setOnItemClickListener(new AdapterRequest.OnItemClickListener() {
            @Override
            public void onAcceptClick(int position) {
                acceptRequest(position);
            }

            @Override
            public void onRejectUser(int position) {
                rejectRequest(position);
            }
        });
    }

    private void getTeamData() {
        fStore.collection(Keys.TEAM_COLLECTION)
                .document(tId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        try {
                            Map<String, Object> map = (Map<String, Object>) value.get(Keys.TEAM_REQUESTS);
                            if(map.size()==0){
                                ll.setVisibility(View.VISIBLE);
                                requestRv.setVisibility(View.GONE);
                            }else {
                                ll.setVisibility(View.GONE);
                                requestRv.setVisibility(View.VISIBLE);
                                for (Map.Entry<String, Object> entry : map.entrySet())
                                    getRequestUserData(entry.getKey(), (String) entry.getValue());
                            }

                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getRequestUserData(String id, String name){
        fStore.collection(Keys.USER_COLLECTION)
                .document(id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot dc) {
                        int dp = dc.getLong(Keys.USER_AVATAR).intValue();
                        requestList.add(new ModelRequest(name,id,dp));
                        requestAdapter.notifyItemInserted(requestList.size()-1);
                    }
                });
    }

    private void rejectRequest(int i) {
    }

    private void acceptRequest(int i) {

        ModelRequest u = requestList.get(i);

        Map<String, Object> map = new HashMap<>();
        map.put(Keys.TEAM_PLAYERS+"."+u.getId(),u.getName());
        fStore.collection(Keys.TEAM_COLLECTION)
                .document(tId)
                .update(map);

        map.clear();
        map.put(Keys.USER_TEAM,tId);

        fStore.collection(Keys.USER_COLLECTION)
                .document(u.getId())
                .update(map);

    }

    private void setUpToolbar(){
        if (getSupportActionBar()==null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}