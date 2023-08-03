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
import android.widget.TextView;
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


public class ActivityLogin extends AppCompatActivity {

    TextInputEditText mEmail,mPassword;
    TextInputLayout mTxtEmail,mTxtPassword;
    TextView mForgotP;
    AppCompatButton mLoginBtn;

    MaterialToolbar toolbar;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Set up toolbar
        setupToolbar();

        //bind all the views
        initiateWithID();

        //initiate firebase classes
        initiateFirebaseClasses();

        //add text change listener
        addTextChangeListner();

        mForgotP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ActivityForgotPassword.class));
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailStr,passwordStr;

                emailStr = mEmail.getText().toString().trim();
                passwordStr = mPassword.getText().toString().trim();

                if (emailStr.equals("")) {
                    mTxtEmail.setError("This field cannot be empty");
                    return;
                }
                if (passwordStr.length()<8) {
                    mTxtPassword.setError("Password length should be 8 characters atleast");
                    return;
                }

                progressDialog = new ProgressDialog(mEmail.getContext(), R.style.Base_Theme_AppCompat_Light_Dialog_Alert);
                progressDialog.setTitle("Sign In");
                progressDialog.setMessage("Please wait...");
                progressDialog.show();

                fAuth.signInWithEmailAndPassword(emailStr,passwordStr)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Intent intent;
                                    FirebaseUser user = fAuth.getCurrentUser();
                                    user.reload();
                                    if (user.isEmailVerified())
                                        intent = new Intent(getApplicationContext(), ActivityHome.class);
                                    else
                                        intent = new Intent(getApplicationContext(), ActivityVerifyMail.class);

                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);

                                    finish();
                                }else {
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
            toolbar = findViewById(R.id.toolbar_login);
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
    }

    private void initiateFirebaseClasses() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
    }

    private void initiateWithID() {
        mEmail = findViewById(R.id.login_mail);
        mPassword = findViewById(R.id.login_password);

        mTxtEmail = findViewById(R.id.login_txt_mail);
        mTxtPassword = findViewById(R.id.login_txt_password);

        mForgotP = findViewById(R.id.login_forgotpassword);

        mLoginBtn = findViewById(R.id.login_btn);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}