package com.miniproject.tournamentapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityTournament extends AppCompatActivity {

    TextView mName,mGame,mDate,mTime,mParticipation,mJoin,mJoined,mPrize,mDesp;
    ImageView mImageView;
    FloatingActionButton fab;

    String userId,name,tId,tName,type,team,hostId,url,teamName;
    boolean alreadyInTournament;

    boolean hasJoined;

    String[] game;

    MaterialToolbar toolbar;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament);

        //bind all the views
        initiateWithID();

        //Set up toolbar
        setupToolbar();

        //initiate Firebase Classes
        initiateFirebaseClasses();

        //get User Data from Firebase
        getDataFromFirebase();

        mJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasJoined){
                    leaveTour();
                }else{
                    joinTour();
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(url);
                Intent intent= new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });
        
    }

    private void joinTour() {
        if (type.equals(Keys.TEAM)){
            if (team.equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityTournament.this);
                builder.setMessage("You need to be in a team to participate in this tournament")
                        .setTitle("Cannot join the tourament")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }else{
                alreadyInTournament = true;
                fStore.collection(Keys.TEAM_COLLECTION)
                        .document(team)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot dc) {
                                alreadyInTournament = !dc.getString(Keys.TEAM_TOURNAMENTS).equals("");
                                teamName = dc.getString(Keys.TEAM_NAME);
                                if(alreadyInTournament){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityTournament.this);
                                    builder.setMessage("You can only join one tournament at time while in a team")
                                            .setTitle("Cannot join the tourament")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int i) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    builder.show();
                                }else{
                                    Map<String, Object> map = new HashMap<>();
                                    map.put(Keys.TEAM_TOURNAMENTS,tId);
                                    fStore.collection(Keys.TEAM_COLLECTION)
                                            .document(team)
                                            .update(map);

                                    map.clear();
                                    map.put(Keys.TOUR_PARTICIPANTS+"."+team,teamName);
                                    fStore.collection(Keys.TOUR_COLLECTION)
                                            .document(tId)
                                            .update(map);

                                }
                            }
                        });
            }
        }else {
            Map<String, Object> map = new HashMap<>();
            map.put(Keys.TOUR_PARTICIPANTS + "." + userId, name);

            fStore.collection(Keys.TOUR_COLLECTION)
                    .document(tId)
                    .update(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            map.clear();
            map.put(
                    Keys.USER_PARTICIPATIONS + "." + tId,
                    tName
            );
            fStore.collection(Keys.USER_COLLECTION)
                    .document(userId)
                    .update(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    private void leaveTour() {
        Map<String,Object> map = new HashMap<>();
        map.put(Keys.USER_PARTICIPATIONS+"."+tId, FieldValue.delete());

        fStore.collection(Keys.USER_COLLECTION)
                .document(userId)
                .update(map);

        map.clear();
        map.put(Keys.TOUR_PARTICIPANTS+"."+userId, FieldValue.delete());

        fStore.collection(Keys.TOUR_COLLECTION)
                .document(tId)
                .update(map);
    }

    private void getDataFromFirebase() {
        fStore.collection(Keys.TOUR_COLLECTION)
                .document(tId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot dc, @Nullable FirebaseFirestoreException error) {
                        try {
                            Map<String, Object> map = (Map<String, Object>) dc.get(Keys.TOUR_PARTICIPANTS);
                            String joined = +map.size() + "/" + dc.getString(Keys.TOUR_TOTALCAPACITY);

                            if (map.containsKey(userId)){
                                hasJoined = true;
                                mJoin.setText("Leave Tournament");
                            }else {
                                hasJoined = false;
                                mJoin.setText("Join Tournament");
                            }
                            hostId = dc.getString(Keys.TOUR_HOST);
                            if (userId.equals(hostId)) {
                                mJoin.setVisibility(View.GONE);
                                toolbar.getMenu().findItem(R.id.joineduser).setEnabled(true);
                            } else {
                                mJoin.setVisibility(View.VISIBLE);
                                toolbar.getMenu().findItem(R.id.joineduser).setEnabled(false);
                            }

                            tName = dc.getString(Keys.TOUR_NAME);
                            url = dc.getString(Keys.TOUR_DISCORD);
                            toolbar.setTitle(tName);

                            mName.setText("Host: "+dc.getString(Keys.TOUR_HOSTNAME));
                            mDesp.setText(dc.getString(Keys.TOUR_DESP));
                            mDate.setText(dc.getString(Keys.TOUR_STARTD));
                            mTime.setText(dc.getString(Keys.TOUR_STARTT));
                            mJoined.setText("Rs. "+joined);
                            mPrize.setText(dc.getString(Keys.TOUR_PRIZE));

                            mGame.setText(game[dc.getLong(Keys.TOUR_GAME).intValue()]);
                            mImageView.setImageResource(Keys.getGame(game[dc.getLong(Keys.TOUR_GAME).intValue()]));
                            mParticipation.setText(type=dc.getString(Keys.TOUR_PARTICIPATION_TYPE));

                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initiateWithID() {

        mImageView = findViewById(R.id.gameimg_tour);

        mName = findViewById(R.id.host_tour);
        mDesp = findViewById(R.id.desp_tour);
        mGame = findViewById(R.id.game_tour);
        mDate = findViewById(R.id.date_tour);
        mTime= findViewById(R.id.time_tour);
        mParticipation = findViewById(R.id.ptype_tour);
        mJoined = findViewById(R.id.joined_tour);
        mPrize = findViewById(R.id.prize_tour);

        mJoin = findViewById(R.id.join_tour);
        fab = findViewById(R.id.fab_tour);

        toolbar = findViewById(R.id.toolbar_tour);

        Bundle b = getIntent().getExtras();
        tId = b.getString("id");

        hasJoined = false;

        game = getResources().getStringArray(R.array.gameSpinner);
        
    }

    private void setupToolbar(){
        if (getSupportActionBar()==null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
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
                        team = dc.getString(Keys.USER_TEAM);
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.joineduser) {
            if (userId.equals(hostId)){
                Intent intent = new Intent(ActivityTournament.this,ActivityMyBuddies.class);
                intent.putExtra("id",tId);
                intent.putExtra("from",Keys.TOPIC_INTENT);
                startActivity(intent);
            }else{
                Toast.makeText(getApplicationContext(), "Only host can see the participants", Toast.LENGTH_SHORT).show();
            }
            return true;
        }else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tour, menu);
        return true;
    }
}