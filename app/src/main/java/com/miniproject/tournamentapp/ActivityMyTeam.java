package com.miniproject.tournamentapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
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

import me.fahmisdk6.avatarview.AvatarView;

public class ActivityMyTeam extends AppCompatActivity {

    RecyclerView teamRV;
    AdapterUser teamAdapter;
    List<ModelUser> teamList;

    RecyclerView requestRv;
    AdapterRequest requestAdapter;
    List<ModelRequest> requestList;

    AvatarView mDP;
    TextView mName,mDesp,mParticipation,message;
    
    int status;

    AppCompatButton mBtn;

    String userId,userName,userTeam;

    String tId,tName,ownerId;
    boolean isOwner=false;

    MaterialToolbar toolbar;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_team);

        initiateWithID();

        initiateFirebaseClasses();

        setUpToolbar();

        setUpReqRV();

        setUpTeamRV();

        getTeamData();

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status==Keys.NO_STATUS)
                    sendRequest();
                else if(status==Keys.STATUS_REQUESTED)
                    cancelFriendRequest();
                else if (status==Keys.STATUS_FRIEND) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMyTeam.this);
                    builder.setMessage("Are you sure you want to leave this team?")
                            .setTitle("Confirm Leave!")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    unFollow();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    builder.show();
                }

            }
        });

    }

    private void hideIfOtherUser() {

    }

    private void sendRequest(){
        if (teamList.size()>5){
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMyTeam.this);
            builder.setMessage("The team already have 5 members")
                    .setTitle("Team member limit exceeded")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
            return;
        }
        if (!userTeam.equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMyTeam.this);
            builder.setMessage("You can't join multiple teams at same leave current team to join this team")
                    .setTitle("Already in a team")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
            return;
        }
        Map<String ,Object> map = new HashMap<>();
        map.put(Keys.TEAM_REQUESTS+"."+userId,userName);

        fStore.collection(Keys.TEAM_COLLECTION)
                .document(tId)
                .update(map);

        map.clear();
        map.put(Keys.USER_REQUESTEDTEAM+"."+tId, tName);
        fStore.collection(Keys.USER_COLLECTION)
                .document(userId)
                .update(map);

        status = Keys.STATUS_FRIEND;
    }
    private void cancelFriendRequest(){
        Map<String,Object> map = new HashMap<>();
        map.put(Keys.USER_REQUESTEDTEAM+"."+tId, FieldValue.delete());

        fStore.collection(Keys.USER_COLLECTION)
                .document(userId)
                .update(map);

        map.clear();
        map.put(Keys.TEAM_REQUESTS+"."+userId, FieldValue.delete());

        fStore.collection(Keys.TOUR_COLLECTION)
                .document(tId)
                .update(map);
        status = Keys.NO_STATUS;
    }
    private void unFollow(){
        Map<String, Object> map = new HashMap<>();
        map.put(Keys.USER_TEAM, "");

        fStore.collection(Keys.USER_COLLECTION)
                .document(userId)
                .update(map);

        map.clear();
        map.put(Keys.TEAM_PLAYERS + "." + userId, FieldValue.delete());

        fStore.collection(Keys.TEAM_COLLECTION)
                .document(tId)
                .update(map);
        status = Keys.NO_STATUS;

    }

    private void initiateWithID() {

        mName = findViewById(R.id.name_team);
        mDesp = findViewById(R.id.desp_team);
        mParticipation = findViewById(R.id.part_team);
        message = findViewById(R.id.message_team);

        teamRV = findViewById(R.id.members_team);
        requestRv = findViewById(R.id.request_team);

        mDP = findViewById(R.id.dp_team);
        mBtn = findViewById(R.id.btn_team);
        toolbar = findViewById(R.id.toolbar_team);

        Bundle b = getIntent().getExtras();
        tId = b.getString("id");

        teamList = new ArrayList<>();
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
                        userName = dc.getString(Keys.USER_NAME).trim();
                        userTeam = dc.getString(Keys.USER_TEAM).trim();

                        if (!userTeam.equals(tId)){

                        }

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
                            teamList.clear();
                            requestList.clear();
                            String s= value.getString(Keys.TEAM_NAME);

                            mName.setText(s);
                            mDP.bind(s,null);
                            mDesp.setText(value.getString(Keys.TEAM_DESP));
                            ownerId = value.getString(Keys.TEAM_LEADER);
                            s = value.getString(Keys.TEAM_TOURNAMENTS);
                            if (s.equals(""))
                                mParticipation.setText(value.getString(Keys.TEAM_NAME)+" have not participated in tournament for now");
                            else
                                mParticipation.setText(s);

                            Map<String, Object> team = (Map<String, Object>) value.get(Keys.TEAM_PLAYERS);
                            Map<String, Object> req = (Map<String, Object>) value.get(Keys.TEAM_REQUESTS);

                            if (req.size()==0) {
                                message.setVisibility(View.GONE);
                                requestRv.setVisibility(View.GONE);
                            } else {
                                message.setVisibility(View.VISIBLE);
                                requestRv.setVisibility(View.VISIBLE);
                            }

                            if (team.containsKey(userId)) {
                                status = Keys.STATUS_FRIEND;
                                mBtn.setText("Leave Team");
                                if(isOwner = ownerId.equals(userId))
                                    mBtn.setVisibility(View.GONE);
                            } else if (req.containsKey(userId)) {
                                status = Keys.STATUS_REQUESTED;
                                mBtn.setText("Cancel Request");
                                message.setVisibility(View.GONE);
                                requestRv.setVisibility(View.GONE);
                            } else {
                                status = Keys.NO_STATUS;
                                mBtn.setText("Request Join");
                                message.setVisibility(View.GONE);
                                requestRv.setVisibility(View.GONE);
                            }

                            requestList.clear();
                            teamList.clear();
                            for (Map.Entry<String, Object> m : team.entrySet()){
                                getTeamUserData(m.getKey(),m.getValue().toString(),isOwner);
                            }
                            for (Map.Entry<String, Object> m : req.entrySet()){
                                getRequestUserData(m.getKey(),m.getValue().toString());
                            }

                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void rejectRequest(int i) {
        ModelRequest m = requestList.get(i);

        Map<String,Object> map = new HashMap<>();
        map.put(Keys.USER_REQUESTEDTEAM+"."+tId, FieldValue.delete());

        fStore.collection(Keys.USER_COLLECTION)
                .document(m.getId())
                .update(map);

        map.clear();
        map.put(Keys.TEAM_REQUESTS+"."+m.getId(), FieldValue.delete());

        fStore.collection(Keys.TOUR_COLLECTION)
                .document(tId)
                .update(map);
    }

    private void acceptRequest(int i) {

        ModelRequest u = requestList.get(i);

        Map<String, Object> map = new HashMap<>();
        map.put(Keys.TEAM_PLAYERS+"."+u.getId(),u.getName());
        map.put(Keys.TEAM_REQUESTS+"."+u.getId(),FieldValue.delete());
        fStore.collection(Keys.TEAM_COLLECTION)
                .document(tId)
                .update(map);


        map.clear();

        map.put(Keys.USER_TEAM,tId);
        fStore.collection(Keys.USER_COLLECTION)
                .document(u.getId())
                .update(map);

    }

    private void setUpTeamRV() {

        teamAdapter = new AdapterUser(this,teamList);
        teamRV.setLayoutManager(new LinearLayoutManager(this));
        teamRV.setAdapter(teamAdapter);

        teamAdapter.setOnItemClickListener(new AdapterUser.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(ActivityMyTeam.this, ActivityProfile.class);
                intent.putExtra("userid", teamList.get(position).getUserid());
                startActivity(intent);
            }

            @Override
            public void onDeleteUser(int position) {
                deleteUser(position);
            }
        });

    }

    private void deleteUser(int i) {

        ModelUser u = teamList.get(i);

        Map<String, Object> map = new HashMap<>();
        map.put(Keys.TEAM_PLAYERS+"."+u.getUserid(), FieldValue.delete());

        fStore.collection(Keys.TEAM_COLLECTION)
                .document(tId)
                .update(map);

        map.clear();

        map.put(Keys.USER_TEAM,"");
        fStore.collection(Keys.USER_COLLECTION)
                .document(u.getUserid())
                .update(map);

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

    private void getTeamUserData(String id, String name,boolean isOwner){
        fStore.collection(Keys.USER_COLLECTION)
                .document(id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot dc) {
                        int dp = dc.getLong(Keys.USER_AVATAR).intValue();
                        if (!id.equals(ownerId))
                            teamList.add(new ModelUser(name,id,dp,isOwner));
                        else
                            teamList.add(new ModelUser(name,id,dp,false));
                        teamAdapter.notifyItemInserted(teamList.size()-1);
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