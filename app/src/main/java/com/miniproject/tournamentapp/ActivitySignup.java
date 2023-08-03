package com.miniproject.tournamentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ActivitySignup extends AppCompatActivity {

    TextInputEditText mEmail,mName,mPassword,mCPassword;
    TextInputLayout mTxtEmail,mTxtName,mTxtPassword,mTxtCPassword;
    AppCompatButton mSignupBtn;

    MaterialToolbar toolbar;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Set up toolbar
        setupToolbar();

        //bind all the views
        initiateWithID();

        //initiate firebase classes
        initiateFirebaseClasses();

        //add text change listener
        addTextChangeListner();

        mSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameStr,emailStr,passwordStr,cpasswordStr;
                nameStr = mName.getText().toString().trim();
                emailStr = mEmail.getText().toString().trim();
                passwordStr = mPassword.getText().toString().trim();
                cpasswordStr = mCPassword.getText().toString().trim();

                if (nameStr.equals("")) {
                    mTxtName.setError("This field cannot be empty");
                    return;
                }
                if (emailStr.equals("")) {
                    mTxtEmail.setError("This field cannot be empty");
                    return;
                }
                if (passwordStr.length()<8) {
                    mTxtPassword.setError("Password length should be 8 characters atleast");
                    return;
                }
                if (!passwordStr.equals(cpasswordStr)){
                    mTxtCPassword.setError("Password Doesn't Match");
                    return;
                }

                progressDialog = new ProgressDialog(mTxtEmail.getContext(), R.style.Base_Theme_AppCompat_Light_Dialog_Alert);
                progressDialog.setTitle("Registering");
                progressDialog.setMessage("Please wait...");
                progressDialog.show();

                fAuth.createUserWithEmailAndPassword(emailStr,passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //send verification email to user
                            sendVerificationMail();
                            //save user data to firebase
                            saveUserToDatabase(nameStr,emailStr);
                            
                            Intent intent = new Intent(getApplicationContext(), ActivityVerifyMail.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        }else{
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });

            }
        });

    }

    private void setupToolbar(){
        if (getSupportActionBar()==null){
            toolbar = findViewById(R.id.toolbar_signup);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void addTextChangeListner() {
        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mEmail.getText().toString().trim().length()>0){
                    mTxtEmail.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mPassword.getText().toString().trim().length()>8){
                    mTxtPassword.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mCPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mCPassword.getText().toString().trim().equals(mPassword.getText().toString().trim())){
                    mTxtCPassword.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void sendVerificationMail() {
        FirebaseUser fUser = fAuth.getCurrentUser();
        fUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Verification Email has been sent to you", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserToDatabase(String nameStr, String emailStr) {

        Map<String,Object> userdata = new HashMap<>();
        userdata.put(Keys.USER_NAME,nameStr);
        userdata.put(Keys.USER_DESP,"");
        userdata.put(Keys.USER_AVATAR,0);
        userdata.put(Keys.USER_EMAIL,emailStr);

        Map<String,Object> map = new HashMap<>();
        userdata.put(Keys.USER_BUDDIES, map);
        userdata.put(Keys.USER_TOURNAMENTS, map);
        userdata.put(Keys.USER_PARTICIPATIONS,map);
        userdata.put(Keys.USER_REQUESTEDTEAM,map);

        userdata.put(Keys.USER_REQUESTS,map);
        userdata.put(Keys.USER_REQUESTEDPLAYERS,map);
        userdata.put(Keys.USER_INTERESTS, map);
        userdata.put(Keys.USER_TEAM, "");

        userdata.put(Keys.USER_INTERESTS,Arrays.asList(getResources().getStringArray(R.array.gameSpinner)));

        fStore.collection(Keys.USER_COLLECTION)
                .document(fAuth.getUid())
                .set(userdata)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Data Added Successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initiateFirebaseClasses() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
    }

    private void initiateWithID() {
        mEmail = findViewById(R.id.signup_mail);
        mName = findViewById(R.id.signup_name);
        mPassword = findViewById(R.id.signup_password);
        mCPassword = findViewById(R.id.signup_cpassword);

        mTxtEmail = findViewById(R.id.signup_txt_mail);
        mTxtName = findViewById(R.id.signup_txt_name);
        mTxtPassword = findViewById(R.id.signup_txt_password);
        mTxtCPassword = findViewById(R.id.signup_txt_cpassword);

        mSignupBtn = findViewById(R.id.signup_btn);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}