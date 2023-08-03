package com.miniproject.tournamentapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firestore.v1.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityMyBuddies extends AppCompatActivity {

    RecyclerView recyclerView;
    AdapterUser adapter;
    List<ModelUser> list;

    RecyclerView rvRequest;
    AdapterRequest adapterRequest;
    List<ModelRequest> listRequest;

    TextView t,title;

    LinearLayout ll;

    String userId,name;
    String fId;
    int from;
    String friendsMessage;

    MaterialToolbar toolbar;

    boolean b;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_buddies);

        initiateWithID();

        initiateFirebaseClasses();

        setUpToolbar();

        setUpRecyclerView();

        if (from==Keys.TOPIC_INTENT){
            getForumDataFromFirebase();
            toolbar.setTitle("Participants");
        }else
            getDataFromFirebase();
    }

    private void initiateWithID() {

        recyclerView = findViewById(R.id.rv_friends);
        ll = findViewById(R.id.noresult_friends);
        toolbar = findViewById(R.id.toolbar_buddies);
        t = findViewById(R.id.message_friends);
        title = findViewById(R.id.message_title);

        friendsMessage = " add someone they'll appear here.";

        Bundle b = getIntent().getExtras();
        fId = b.getString("id");
        from = b.getInt("from");

        list = new ArrayList<>();

    }

    private void initiateFirebaseClasses() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();
    }

    private void getDataFromFirebase() {
        fStore.collection(Keys.USER_COLLECTION)
                .document(fId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        try {
                            name = value.getString(Keys.USER_NAME);
                            t.setText("When "+name+friendsMessage);
                            if (!fId.equals(userId))
                                toolbar.setTitle(name+"'s Buddiis");
                            else
                                toolbar.setTitle("My Buddies");
                            Map<String,Object> map = (Map<String, Object>) value.get(Keys.USER_BUDDIES);
                            list.clear();

                            if (map.size()>0){
                                recyclerView.setVisibility(View.VISIBLE);
                                ll.setVisibility(View.GONE);
                            }else{
                                recyclerView.setVisibility(View.GONE);
                                ll.setVisibility(View.VISIBLE);
                                return;
                            }

                            for (Map.Entry<String,Object> entry: map.entrySet()) {
                                getUserData(entry.getKey(), (String) entry.getValue(),false);
                            }
                        }catch (Exception e){
                            Log.d("e",error.getMessage());
                        }
                    }
                });
    }

    private void getForumDataFromFirebase() {
        fStore.collection(Keys.TOUR_COLLECTION)
                .document(fId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        try {
                            name = value.getString(Keys.TOUR_NAME);
                            t.setText("When people join "+name+" they'll appear here.");
                            title.setText("No participants yet");

                            toolbar.setTitle(name + "'s Participants");
                            Map<String,Object> map = (Map<String, Object>) value.get(Keys.TOUR_PARTICIPANTS);
                            list.clear();

                            if (map.size()>0){
                                recyclerView.setVisibility(View.VISIBLE);
                                ll.setVisibility(View.GONE);
                            }else{
                                recyclerView.setVisibility(View.GONE);
                                ll.setVisibility(View.VISIBLE);
                                return;
                            }

                            for (Map.Entry<String,Object> entry: map.entrySet()) {
                                getUserData(entry.getKey(), (String) entry.getValue(),true);
                            }
                        }catch (Exception e){
                            Log.d("e",error.getMessage());
                        }
                    }
                });
    }

    private void getUserData(String id, String name,boolean b){
        fStore.collection(Keys.USER_COLLECTION)
                .document(id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot dc) {
                        int dp = dc.getLong(Keys.USER_AVATAR).intValue();
                        list.add(new ModelUser(name,id,dp,b));
                        adapter.notifyItemInserted(list.size()-1);
                    }
                });
    }

    private void setUpRecyclerView() {

        adapter = new AdapterUser(this,list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AdapterUser.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(ActivityMyBuddies.this, ActivityProfile.class);
                intent.putExtra("userid", list.get(position).getUserid());
                startActivity(intent);
            }

            @Override
            public void onDeleteUser(int position) {
                if (from == Keys.TOPIC_INTENT)
                    deleteUser(position);
                else
                    removeBuddy(position);
            }
        });

    }

    private void deleteUser(int i) {

        ModelUser u = list.get(i);

        Map<String, Object> map = new HashMap<>();
        map.put(Keys.TOUR_PARTICIPANTS+"."+u.getUserid(), FieldValue.delete());

        fStore.collection(Keys.TOUR_COLLECTION)
                .document(fId)
                .update(map);

        map.clear();

        map.put(Keys.USER_PARTICIPATIONS+"."+fId,FieldValue.delete());
        fStore.collection(Keys.USER_COLLECTION)
                .document(u.getUserid())
                .update(map);

    }

    private void removeBuddy(int i) {

        ModelUser u = list.get(i);

        Map<String, Object> map = new HashMap<>();
        map.put(Keys.USER_BUDDIES+"."+u.getUserid(), FieldValue.delete());

        fStore.collection(Keys.USER_COLLECTION)
                .document(fId)
                .update(map);

        map.clear();

        map.put(Keys.USER_BUDDIES+"."+fId,FieldValue.delete());
        fStore.collection(Keys.USER_COLLECTION)
                .document(u.getUserid())
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