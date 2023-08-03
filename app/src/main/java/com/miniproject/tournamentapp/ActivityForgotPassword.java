package com.miniproject.tournamentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ActivityForgotPassword extends AppCompatActivity {

    TextInputEditText mEmail;
    TextInputLayout mTxtEmail;
    AppCompatButton mForgotBtn;

    MaterialToolbar toolbar;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //Set up toolbar
        setupToolbar();

        //bind all the views
        initiateWithID();

        //initiate firebase classes
        initiateFirebaseClasses();

        //add text change listener
        addTextChangeListner();

        mForgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = mEmail.getText().toString().trim();
                if(mail.equals("")){
                    mTxtEmail.setError("This field cannot be empty");
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
                    mTxtEmail.setError("Enter a valid email address");
                    return;
                }

                progressDialog = new ProgressDialog(mEmail.getContext(), R.style.Base_Theme_AppCompat_Light_Dialog_Alert);
                progressDialog.setTitle("Sending Reset Password Link");
                progressDialog.setMessage("Please wait...");
                progressDialog.show();

                fAuth.sendPasswordResetEmail(mail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(), "Reset password link has been sent to your email", Toast.LENGTH_LONG).show();
                                    finish();
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
            toolbar = findViewById(R.id.toolbar_forgotpassword);
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
    }

    private void initiateFirebaseClasses() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
    }

    private void initiateWithID() {
        mEmail = findViewById(R.id.forgotpassword_mail);
        mTxtEmail = findViewById(R.id.forgotpassword_txt_mail);
        
        mForgotBtn = findViewById(R.id.forgotpassword_btn);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    
}