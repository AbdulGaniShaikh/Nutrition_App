package com.miniproject.tournamentapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActivityHome extends AppCompatActivity {
    RecyclerView recyclerView;
    List<ModelTournament> list,filter;
    AdapterTournament adapter;

    RelativeLayout mNoData;
    SwipeRefreshLayout refreshLayout;

    EditText mSearch;
    ImageButton mFilter;

    MaterialToolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView mUserName;
    ShapeableImageView mAvatar;

    String userId,teamId;
    String[] game;
    List<String> games;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //initiate firebase classes
        initiateFirebaseClasses();

        //Set up toolbar
        setupToolbar();

        //bind all the views
        initiateWithID();

        //Setup NavigationDrawer
        setupNavigationView(savedInstanceState);

        //setup tab recycler view
        setupRecyclerView();

        //get data from firebase
        getDataFromDB();

        //add text change listener
        addTextChangeListener();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataFromDB();
                refreshLayout.setRefreshing(false);
            }
        });

        mFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //setup BottomSheet
                setUpBottomSheet();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.myteam_nav:
                        if (!teamId.trim().equals("")) {
                            intent = new Intent(getApplicationContext(), ActivityMyTeam.class);
                            intent.putExtra("id", teamId);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(), "Join a team first", Toast.LENGTH_SHORT).show();
                            intent = new Intent(getApplicationContext(),ActivityUser.class);
                            intent.putExtra("from",Keys.TOPIC_INTENT);
                            startActivity(intent);
                        }
                        break;
                    case R.id.createteam_nav:
                        if (teamId.trim().equals(""))
                            startActivity(new Intent(getApplicationContext(),ActivityCreateTeam.class));
                        else
                            Toast.makeText(getApplicationContext(), "You are already in a team", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.findteam_nav:
                        intent = new Intent(getApplicationContext(),ActivityUser.class);
                        intent.putExtra("from",Keys.TOPIC_INTENT);
                        startActivity(intent);
                        break;
                    case R.id.hosttournament_nav:
                        navigationView.setCheckedItem(R.id.hosttournament_nav);
                        startActivity(new Intent(getApplicationContext(),ActivityHostTournament.class));
                        break;
                    case R.id.mytournaments_nav:
                        startActivity(new Intent(getApplicationContext(),ActivityMyTournament.class));
                        break;
                    case R.id.myprofile_home:
                        intent = new Intent(getApplicationContext(),ActivityProfile.class);
                        intent.putExtra("userid",userId);
                        startActivity(intent);
                        break;
                    case R.id.players_home:
                        intent = new Intent(getApplicationContext(),ActivityUser.class);
                        intent.putExtra("from",Keys.USER_INTENT);
                        startActivity(intent);
                    default:
                        navigationView.setCheckedItem(R.id.browse_home);
                        break;
                }
                item.setChecked(true);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    private void getDataFromDB() {

        filter.clear();
        list.clear();
        adapter.notifyDataSetChanged();

        fStore.collection(Keys.USER_COLLECTION)
                .document(userId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot dc, @Nullable FirebaseFirestoreException error) {
                        mUserName.setText(dc.getString(Keys.USER_NAME));
                        int l = dc.getLong(Keys.USER_AVATAR).intValue();
                        mAvatar.setImageResource(Keys.getAvatar(l));
                        teamId = dc.getString(Keys.USER_TEAM);
                        games = (List<String>) dc.get(Keys.TOUR_GAME);
                    }
                });

        fStore.collection(Keys.TOUR_COLLECTION)
                .orderBy(Keys.TOUR_STARTD, Query.Direction.ASCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot dc : queryDocumentSnapshots.getDocuments()){
                            list.clear();
                            String gameName = game[dc.getLong(Keys.TOUR_GAME).intValue()];
                            if (!userId.equals(dc.getString(Keys.TOUR_HOST))) {
                                if (gameName.contains(gameName)) {
                                    Map<String, Object> map = (Map<String, Object>) dc.get(Keys.TOUR_PARTICIPANTS);
                                    String joined = +map.size() + "/" + dc.getString(Keys.TOUR_TOTALCAPACITY);
                                    list.add(new ModelTournament(
                                            dc.getString(Keys.TOUR_NAME),
                                            dc.getString(Keys.TOUR_STARTD),
                                            dc.getString(Keys.TOUR_STARTT),
                                            joined,
                                            dc.getString(Keys.TOUR_PRIZE),
                                            gameName,
                                            dc.getString(Keys.TOUR_PARTICIPATION_TYPE),
                                            dc.getId()
                                    ));
                                    filter.addAll(list);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                        if (list.size()>0){
                            recyclerView.setVisibility(View.VISIBLE);
                            mNoData.setVisibility(View.GONE);
                        }else{
                            recyclerView.setVisibility(View.GONE);
                            mNoData.setVisibility(View.VISIBLE);
                        }
                    }
                });

        /*fStore.collection(Keys.TOUR_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot dc : task.getResult()){
                                if (!userId.equals(dc.getString(Keys.TOUR_HOST))) {
                                    Map<String, Object> map = (Map<String, Object>) dc.get(Keys.TOUR_PARTICIPANTS);
                                    String joined = +map.size() + "/" + dc.getString(Keys.TOUR_TOTALCAPACITY);
                                    list.add(new ModelTournament(
                                            dc.getString(Keys.TOUR_NAME),
                                            dc.getString(Keys.TOUR_STARTD),
                                            dc.getString(Keys.TOUR_STARTT),
                                            joined,
                                            dc.getString(Keys.TOUR_PRIZE),
                                            game[dc.getLong(Keys.TOUR_GAME).intValue()],
                                            dc.getString(Keys.TOUR_PARTICIPATION_TYPE),
                                            dc.getId()
                                    ));
                                    adapter.notifyItemInserted(list.size() - 1);
                                }
                            }
                            if (list.size()>0){
                                recyclerView.setVisibility(View.VISIBLE);
                                mNoData.setVisibility(View.GONE);
                            }else{
                                recyclerView.setVisibility(View.GONE);
                                mNoData.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });*/

    }

    private void setupToolbar(){
        if (getSupportActionBar()==null){
            toolbar = findViewById(R.id.toolbar_home);
            setSupportActionBar(toolbar);
        }
    }

    private void addTextChangeListener() {
        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter.clear();
                if (charSequence.length()!=0) {
                    for (ModelTournament p : list) {

                        String s = p.getName().toLowerCase(Locale.ROOT);
                        String s1 = charSequence.toString().toLowerCase(Locale.ROOT);

                        if (s.contains(s1)) {
                            filter.add(p);
                        }
                    }
                }else{
                    filter.addAll(list);
                }

                if (list.size()>0){
                    recyclerView.setVisibility(View.VISIBLE);
                    mNoData.setVisibility(View.GONE);
                }else{
                    recyclerView.setVisibility(View.GONE);
                    mNoData.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                
            }
        });
    }

    private void initiateFirebaseClasses() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getUid();
    }

    private void initiateWithID() {
        navigationView = findViewById(R.id.navview_home);
        drawerLayout = findViewById(R.id.drawer_home);

        mNoData = findViewById(R.id.nodata_home);
        refreshLayout = findViewById(R.id.refresh_home);
        recyclerView =  findViewById(R.id.recycler_home);

        mSearch = findViewById(R.id.searchview_home);
        mFilter = findViewById(R.id.filter_home);

        list = new ArrayList<>();
        filter = new ArrayList<>();

        game = getResources().getStringArray(R.array.gameSpinner);
    }

    private void setupRecyclerView() {
        adapter = new AdapterTournament(mSearch.getContext(),filter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mSearch.getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new AdapterTournament.OnItemClickListener() {
            @Override
            public void onJoinClick(int position) {
                Intent intent = new Intent(getApplicationContext(),ActivityTournament.class);
                intent.putExtra("id",filter.get(position).getId());
                startActivity(intent);
            }
        });
    }



    private void setupNavigationView(Bundle savedInstanceState){
        navigationView.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.open_drawer,R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View headerView = navigationView.getHeaderView(0);
        mUserName = headerView.findViewById(R.id.username_header);
        mAvatar = headerView.findViewById(R.id.avatar_header);
        
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(),ActivityProfile.class);
                intent.putExtra("userid",userId);
                startActivity(intent);
            }
        });

        if(savedInstanceState==null) {
            navigationView.setCheckedItem(R.id.browse_home);
        }
    }

    private void setUpBottomSheet() {
        BottomSheetDialog bottomSheetDialog;
        bottomSheetDialog = new BottomSheetDialog(this,R.style.BottomSheetDialogTheme);

        bottomSheetDialog.setContentView(R.layout.item_filter);
        bottomSheetDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.browse_home);
    }
}