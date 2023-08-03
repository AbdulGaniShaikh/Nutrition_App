package com.miniproject.tournamentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ActivityCreateTeam extends AppCompatActivity {
    
    TextInputEditText mName,mDesp;
    TextInputLayout mTxtName,mTxtDesp;
    AppCompatButton mCreateBtn;

    MaterialToolbar toolbar;

    String userID,userName;
    int gameInt;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

        //Set up toolbar
        setupToolbar();

        //bind all the views
        initiateWithID();

        //initiate firebase classes
        initiateFirebaseClasses();

        //add Constraints to input fields;
        addTextChangeListner();

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTeam();
            }
        });
        
    }

    private void initiateFirebaseClasses() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();
        fStore.collection(Keys.USER_COLLECTION)
                .document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot dc) {
                        userName = dc.getString(Keys.USER_NAME);
                    }
                });
    }

    private void initiateWithID() {
        mName = findViewById(R.id.name_createteam);
        mDesp = findViewById(R.id.desp_createteam);

        mTxtName = findViewById(R.id.name_txt_createteam);
        mTxtDesp = findViewById(R.id.desp_createteam_txt);

        mCreateBtn = findViewById(R.id.create_createteam);
    }

    private void createTeam(){
        String name,desp;
        name = mName.getText().toString().trim();
        desp = mDesp.getText().toString().trim();

        if (name.equals("")){
            mTxtName.setError("Enter Game Name");
            return;
        }
        if (desp.equals("")){
            mTxtDesp.setError("Enter description first");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCreateTeam.this);
        builder.setMessage("Are you sure you want to create this team?")
                .setTitle("Confirm Creation!")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Map<String, Object> map = new HashMap<>();
                        map.put(Keys.TEAM_NAME,name);
                        map.put(Keys.TEAM_DESP,desp);
                        map.put(Keys.TEAM_LEADER,userID);

                        Map<String, Object> map1 = new HashMap<>();
                        map1.put(userID,userName);

                        map.put(Keys.TEAM_PLAYERS,map1);
                        map.put(Keys.TEAM_REQUESTS,new HashMap<String, Object>());
                        map.put(Keys.TEAM_TOURNAMENTS,"");

                        progressDialog = new ProgressDialog(mName.getContext(), R.style.Base_Theme_AppCompat_Light_Dialog_Alert);
                        progressDialog.setTitle("Creating Team");
                        progressDialog.setMessage("Please wait...");
                        progressDialog.show();


                        fStore.collection(Keys.TEAM_COLLECTION)
                                .add(map)
                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()){
                                            saveToUserDB(task.getResult().getId(),userName);
                                            finish();
                                        }else{
                                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                        progressDialog.dismiss();
                                    }
                                });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private void saveToUserDB(String id,String name){
        Map<String, Object> map = new HashMap<>();
        map.put(Keys.USER_TEAM,id);
        fStore.collection(Keys.USER_COLLECTION)
                .document(userID)
                .update(map);
    }

    private void addTextChangeListner() {
        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mName.getText().toString().trim().length()>0){
                    mTxtName.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mDesp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mDesp.getText().toString().trim().length()>0){
                    mTxtDesp.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void setupToolbar(){
        if (getSupportActionBar()==null){
            toolbar = findViewById(R.id.toolbar_createteam);
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