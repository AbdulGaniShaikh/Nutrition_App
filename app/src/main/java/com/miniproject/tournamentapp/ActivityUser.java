package com.miniproject.tournamentapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.List;
import java.util.Locale;

public class ActivityUser extends AppCompatActivity {

    AdapterUser adapterSearch;
    List<ModelUser> listSearch,filter;
    RecyclerView rvSearch;

    LinearLayout ll;

    int from;

    EditText searchBar;
    MaterialToolbar toolbar;

    String userId;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        initiateWithID();

        initiateFirebaseClasses();

        setUpToolbar();

        setUpSearchRV();

        getSearchData();


        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter.clear();
                for (ModelUser p : listSearch){

                    String s = p.getName().toLowerCase(Locale.ROOT);
                    String s1 = charSequence.toString().toLowerCase(Locale.ROOT);

                    if (s.contains(s1) && !p.getUserid().equals(userId)){
                        filter.add(p);
                    }
                }

                if (filter.size()>0){
                    rvSearch.setVisibility(View.VISIBLE);
                    ll.setVisibility(View.GONE);
                }else{
                    rvSearch.setVisibility(View.GONE);
                    ll.setVisibility(View.VISIBLE);
                }
                adapterSearch.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void initiateWithID() {
        rvSearch = findViewById(R.id.rv_search);
        searchBar = findViewById(R.id.edt_search);
        ll = findViewById(R.id.noresult_search);

        toolbar = findViewById(R.id.toolbar_search);
        listSearch = new ArrayList<>();
        filter = new ArrayList<>();

        rvSearch.setVisibility(View.GONE);
        ll.setVisibility(View.VISIBLE);

        Bundle b = getIntent().getExtras();
        from = b.getInt("from",Keys.USER_INTENT);
    }

    private void initiateFirebaseClasses() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();
    }

    private void setUpSearchRV(){

        adapterSearch = new AdapterUser(this,filter);
        rvSearch.setLayoutManager(new LinearLayoutManager(this));
        rvSearch.setAdapter(adapterSearch);

        adapterSearch.setOnItemClickListener(new AdapterUser.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                if (from==Keys.TOPIC_INTENT) {
                    Intent intent = new Intent(ActivityUser.this, ActivityMyTeam.class);
                    intent.putExtra("id", filter.get(position).getUserid());
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(ActivityUser.this, ActivityProfile.class);
                    intent.putExtra("userid", filter.get(position).getUserid());
                    startActivity(intent);
                }
            }

            @Override
            public void onDeleteUser(int position) {

            }
        });
    }

    private void getSearchData(){
        if (from==Keys.TOPIC_INTENT) {
            fStore.collection(Keys.TEAM_COLLECTION)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> l = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot dc : l) {
                                listSearch.add(new ModelUser(
                                        dc.getString(Keys.TEAM_NAME),
                                        dc.getId(),
                                        20,
                                        false
                                ));
                            }
                        }
                    });
        }else{
            fStore.collection(Keys.USER_COLLECTION)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> l = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot dc : l) {
                                listSearch.add(new ModelUser(
                                        dc.getString(Keys.USER_NAME),
                                        dc.getId(),
                                        dc.getLong(Keys.USER_AVATAR).intValue(),
                                        false
                                ));
                            }
                        }
                    });
        }
    }

    private void setUpToolbar(){
        if (getSupportActionBar()==null){
            setSupportActionBar(toolbar);
            if (from==Keys.TOPIC_INTENT)
                toolbar.setTitle("Search Teams");
            else
                toolbar.setTitle("Search Players");
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