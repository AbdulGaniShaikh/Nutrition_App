package com.miniproject.tournamentapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityMyTournament extends AppCompatActivity {


    AdapterTournament adapter;
    List<ModelTournament> list;
    RecyclerView recyclerView;

    LinearLayout ll;

    MaterialToolbar toolbar;

    String userId,name;
    String[] game;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tournament);

        initiateWithID();

        initiateFirebaseClasses();

        setUpToolbar();

        setUpRV();

        getData();

    }

    private void getData() {
        fStore.collection(Keys.USER_COLLECTION)
                .document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot dc) {
                        Map<String,Object> map = (Map<String, Object>) dc.get(Keys.USER_TOURNAMENTS);

                        if (map.size()>0){
                            recyclerView.setVisibility(View.VISIBLE);
                            ll.setVisibility(View.GONE);
                        }else{
                            recyclerView.setVisibility(View.GONE);
                            ll.setVisibility(View.VISIBLE);
                        }

                        for (Map.Entry<String, Object> m : map.entrySet()){
                            getTournamentData(m.getKey(),m.getValue().toString());
                        }
                    }
                });
    }

    private void initiateWithID() {
        recyclerView = findViewById(R.id.rv_mytour);
        ll = findViewById(R.id.noresult_mytour);

        toolbar = findViewById(R.id.toolbar_mytour);
        list = new ArrayList<>();

        recyclerView.setVisibility(View.GONE);
        ll.setVisibility(View.VISIBLE);

        game = getResources().getStringArray(R.array.gameSpinner);
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
                        name = dc.getString(Keys.USER_NAME);
                    }
                });
    }

    private void setUpRV(){

        adapter = new AdapterTournament(this,list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AdapterTournament.OnItemClickListener() {
            @Override
            public void onJoinClick(int position) {
                Intent intent = new Intent(getApplicationContext(),ActivityTournament.class);
                intent.putExtra("id",list.get(position).getId());
                startActivity(intent);
            }
        });
    }

    private void getTournamentData(String id,String name){
        fStore.collection(Keys.TOUR_COLLECTION)
                .document(id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot dc) {
                        Map<String ,Object> map = (Map<String, Object>) dc.get(Keys.TOUR_PARTICIPANTS);
                        String joined = +map.size()+"/"+dc.getString(Keys.TOUR_TOTALCAPACITY);
                        list.add(new ModelTournament(
                                dc.getString(Keys.TOUR_NAME),
                                dc.getString(Keys.TOUR_STARTD),
                                dc.getString(Keys.TOUR_STARTT),
                                joined,
                                dc.getString(Keys.TOUR_PRIZE),
                                game[dc.getLong(Keys.TOUR_GAME).intValue()],
                                dc.getString(Keys.TOUR_PARTICIPATION_TYPE),
                                id
                        ));
                        adapter.notifyItemInserted(list.size()-1);
                    }
                });
    }



    private void setUpToolbar(){
        if (getSupportActionBar()==null){
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
    
}