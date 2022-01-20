package com.example.project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class RegisterEmailActivity extends AppCompatActivity {
    FirebaseAuth auth;
    Button b1_emailnext;
    ProgressDialog dialog;
    EditText e1_email;
    Toolbar toolbar;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_email);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.e1_email = (EditText) findViewById(R.id.editTextPass);
        this.auth = FirebaseAuth.getInstance();
        this.dialog = new ProgressDialog(this);
        this.b1_emailnext = (Button) findViewById(R.id.button);
        this.b1_emailnext.setEnabled(false);
        this.b1_emailnext.setBackgroundColor(Color.parseColor("#faebd7"));
        this.toolbar.setTitle((CharSequence) "Email Address");
        setSupportActionBar(this.toolbar);
        this.e1_email.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (!RegisterEmailActivity.this.e1_email.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+") || s.length() <= 0) {
                    RegisterEmailActivity.this.b1_emailnext.setEnabled(false);
                    RegisterEmailActivity.this.b1_emailnext.setBackgroundColor(Color.parseColor("#faebd7"));
                    return;
                }
                RegisterEmailActivity.this.b1_emailnext.setEnabled(true);
                RegisterEmailActivity.this.b1_emailnext.setBackgroundColor(Color.parseColor("#9C27B0"));
            }
        });
    }

    public void checkIfEmailPresent(View v) {
        this.dialog.setMessage("Please wait");
        this.dialog.show();
        this.auth.fetchSignInMethodsForEmail(this.e1_email.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                boolean check;
                RegisterEmailActivity.this.dialog.dismiss();
                if (!task.getResult().getSignInMethods().isEmpty()) {
                    check = true;
                } else {
                    check = false;
                }
                if (!check) {
                    Intent myIntent = new Intent(RegisterEmailActivity.this, RegisterPasswordActivity.class);
                    myIntent.putExtra("email", RegisterEmailActivity.this.e1_email.getText().toString());
                    RegisterEmailActivity.this.startActivity(myIntent);
                    RegisterEmailActivity.this.finish();
                    return;
                }
                Toast.makeText(RegisterEmailActivity.this.getApplicationContext(), "You already have an account. Please login.", Toast.LENGTH_SHORT).show();
                RegisterEmailActivity.this.startActivity(new Intent(RegisterEmailActivity.this, LoginEmailActivity.class));
                RegisterEmailActivity.this.finish();
            }
        });
    }
}
