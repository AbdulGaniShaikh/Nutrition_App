package com.miniproject.tournamentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.ProgressDialog;
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
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ActivityHostTournament extends AppCompatActivity {

    TextView mStartD,mEndD,mStartT,mEndT;
    TextInputEditText mName,mDesp,mCapacity,mPrize,mDiscord;
    TextInputLayout mTxtName,mTxtDesp,mTxtGame,mTxtCapacity,mTxtPrize,mTxtDiscord;
    MaterialAutoCompleteTextView mGame;
    AppCompatButton mCreateBtn;
    RadioGroup radioGroup;

    CoordinatorLayout mCL;

    MaterialToolbar toolbar;

    String userID,userName,startDate,endDate,startTime,endTime;
    int gameInt;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_tournament);

        //Set up toolbar
        setupToolbar();

        //bind all the views
        initiateWithID();

        //initiate firebase classes
        initiateFirebaseClasses();

        //set default time to textview
        setDefaultValues();

        //add Constraints to input fields;
        addTextChangeListner();

        mStartD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker("Start");
            }
        });

        mEndD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker("End");
            }
        });
        mStartT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker("Start");
            }
        });

        mEndT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker("End");
            }
        });

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    createTournament();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void createTournament() throws ParseException {

        String nameStr,despStr,prizeStr,capacityStr,discordStr;
        String participationtype = Keys.SOLO;

        nameStr = mName.getText().toString().trim();
        despStr = mDesp.getText().toString().trim();
        prizeStr = mPrize.getText().toString().trim();
        capacityStr = mCapacity.getText().toString().trim();
        discordStr  = mDiscord.getText().toString().trim();

        if (nameStr.equals("")) {
            mTxtName.setError("This field cannot be empty");
            return;
        }
        if (prizeStr.equals("")) {
            mTxtPrize.setError("This field cannot be empty");
            return;
        }
        if (capacityStr.equals("")){
            mTxtCapacity.setError("This field cannot be empty");
            return;
        }
        if (despStr.equals("")) {
            mTxtDesp.setError("This field cannot be empty");
            return;
        }
        if (discordStr.equals("") || !discordStr.contains("https://discord.")) {
            mTxtDiscord.setError("Enter valid discord invitation link");
            return;
        }

        if (radioGroup.getCheckedRadioButtonId()==R.id.team_host){
            participationtype = Keys.TEAM;
        }

        Date date = new SimpleDateFormat("dd, MMM yyyy hh:mm a").parse(startDate+" "+startTime);

        Map<String,Object> map = new HashMap<>();
        map.put(Keys.TOUR_NAME,nameStr);
        map.put(Keys.TOUR_DISCORD,discordStr);
        map.put(Keys.TOUR_DESP,despStr);
        map.put(Keys.TOUR_TOTALCAPACITY,capacityStr);
        map.put(Keys.TOUR_PRIZE,prizeStr);
        map.put(Keys.TOUR_HOST,userID);
        map.put(Keys.TOUR_HOSTNAME,userName);
        map.put(Keys.TOUR_PARTICIPATION_TYPE,participationtype);
        map.put(Keys.TOUR_PARTICIPANTS,new HashMap<String,Object>());
        map.put(Keys.TOUR_GAME,gameInt);
        map.put(Keys.TOUR_TIMESTAMP,new Timestamp(date));
        map.put(Keys.TOUR_STARTD,startDate);
        map.put(Keys.TOUR_STARTT,startTime);
        map.put(Keys.TOUR_ENDD,endDate);
        map.put(Keys.TOUR_ENDT,endTime);

        progressDialog = new ProgressDialog(mTxtPrize.getContext(), R.style.Base_Theme_AppCompat_Light_Dialog_Alert);
        progressDialog.setTitle("Creating Forum");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();


        fStore.collection(Keys.TOUR_COLLECTION)
                .add(map)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()){
                            saveToUserDB(task.getResult().getId(),nameStr);
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void saveToUserDB(String id,String name){
        Map<String, Object> map = new HashMap<>();
        map.put(Keys.USER_TOURNAMENTS+"."+id,name);
        fStore.collection(Keys.USER_COLLECTION)
                .document(userID)
                .update(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Tournament successfully created", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setDefaultValues() {
        Date sdate,edate;

        Calendar calendar = Calendar.getInstance();
        sdate = calendar.getTime();

        startDate = new SimpleDateFormat("dd, MMM yyyy").format(sdate);
        startTime = new SimpleDateFormat("hh:mm a").format(sdate);
        mStartD.setText(startDate);
        mStartT.setText(startTime);

        edate = new Date(sdate.getTime()+604800000L);
        endDate = new SimpleDateFormat("dd, MMM yyyy").format(edate);
        endTime = new SimpleDateFormat("hh:mm a").format(edate);
        mEndD.setText(endDate);
        mEndT.setText(endTime);

        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(),
                R.layout.item_gamespinner,
                getResources().getStringArray(R.array.gameSpinner));

        mGame.setAdapter(arrayAdapter);

    }

    private void datePicker(String s) {


        CalendarConstraints.Builder calendarConstraints = new CalendarConstraints.Builder();
        calendarConstraints.setValidator(DateValidatorPointForward.now());

        MaterialDatePicker.Builder dateBuilder = MaterialDatePicker.Builder.datePicker();
        dateBuilder.setTitleText("Select "+s+" Date");
        dateBuilder.setCalendarConstraints(calendarConstraints.build());

        MaterialDatePicker datePicker = dateBuilder.build();
        datePicker.show(getSupportFragmentManager(),"DATE_PICKER");
        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                if (s.equals("Start")) {
                    startDate = datePicker.getHeaderText();
                    mStartD.setText(startDate);
                }
                else if (s.equals("End")) {
                    endDate = datePicker.getHeaderText();
                    mEndD.setText(endDate);
                }
            }
        });
    }

    private void timePicker(String s){

        Calendar calendar = Calendar.getInstance();
        Date d = calendar.getTime();

        MaterialTimePicker.Builder timeBuilder =  new MaterialTimePicker.Builder();
        timeBuilder.setTimeFormat(TimeFormat.CLOCK_12H);
        timeBuilder.setHour(d.getHours());
        timeBuilder.setMinute(d.getMinutes());
        timeBuilder.setTitleText("Select Tournament "+s+" Time");
        MaterialTimePicker timePicker = timeBuilder.build();
        timePicker.show(getSupportFragmentManager(),"TIME_PICKER");
        timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stime = String.format("%02d", timePicker.getHour())+":"+ String.format("%02d", timePicker.getMinute());
                SimpleDateFormat _24Hour = new SimpleDateFormat("HH:mm");
                SimpleDateFormat _12Hour = new SimpleDateFormat("hh:mm a");
                Date _24HourDt = null;
                try {
                    _24HourDt = _24Hour.parse(stime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                stime = _12Hour.format(_24HourDt);

                if (s.equals("Start")) {
                    startTime = stime;
                    mStartT.setText(startTime);
                }
                else if (s.equals("End")) {
                    endTime = stime;
                    mEndT.setText(endTime);
                }
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
        mName = findViewById(R.id.name_host);
        mDesp = findViewById(R.id.desp_host);
        mGame = findViewById(R.id.game_host);
        mCapacity = findViewById(R.id.capacity_host);
        mPrize = findViewById(R.id.prize_host);
        mDiscord = findViewById(R.id.discord_host);

        mTxtName = findViewById(R.id.name_txt_host);
        mTxtDesp = findViewById(R.id.desp_host_txt);
        mTxtGame = findViewById(R.id.game_host_txt);
        mTxtCapacity = findViewById(R.id.capacity_host_txt);
        mTxtPrize = findViewById(R.id.prize_host_txt);
        mTxtDiscord = findViewById(R.id.discord_txt_host);

        mStartD = findViewById(R.id.startd_host);
        mStartT = findViewById(R.id.startt_host);
        mEndD = findViewById(R.id.endd_host);
        mEndT = findViewById(R.id.endt_host);

        mCL = findViewById(R.id.hosttournament);

        radioGroup = findViewById(R.id.radiogroup_host);

        mCreateBtn = findViewById(R.id.create_host);
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
        mDiscord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String discordStr = mDiscord.getText().toString().trim();
                if (discordStr.length()>0 && discordStr.contains("https://discord.gg")){
                    mTxtDiscord.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mPrize.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mPrize.getText().toString().trim().length()>0){
                    mTxtPrize.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mCapacity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mCapacity.getText().toString().trim().length()>0){
                    mTxtCapacity.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mGame.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), i +"", Toast.LENGTH_SHORT).show();
                gameInt = i;
            }
        });
    }

    private void setupToolbar(){
        if (getSupportActionBar()==null){
            toolbar = findViewById(R.id.toolbar_host);
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