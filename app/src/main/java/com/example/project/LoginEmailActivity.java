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

public class LoginEmailActivity extends AppCompatActivity {
    FirebaseAuth auth;
    Button b1_emailnext;
    ProgressDialog dialog;
    EditText e1_email;
    Toolbar toolbar;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_login_email);
        this.e1_email = (EditText) findViewById(R.id.editTextPass);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar.setTitle((CharSequence) "Sign In");
        setSupportActionBar(this.toolbar);
        this.dialog = new ProgressDialog(this);
        this.auth = FirebaseAuth.getInstance();
        this.b1_emailnext = (Button) findViewById(R.id.button);
        this.b1_emailnext.setEnabled(false);
        this.b1_emailnext.setBackgroundColor(Color.parseColor("#faebd7"));
        this.e1_email.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (!LoginEmailActivity.this.e1_email.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+") || s.length() <= 0) {
                    LoginEmailActivity.this.b1_emailnext.setEnabled(false);
                    LoginEmailActivity.this.b1_emailnext.setBackgroundColor(Color.parseColor("#faebd7"));
                    return;
                }
                LoginEmailActivity.this.b1_emailnext.setEnabled(true);
                LoginEmailActivity.this.b1_emailnext.setBackgroundColor(Color.parseColor("#9C27B0"));
            }
        });
    }

    public void checkEmail(View v) {
        this.dialog.setMessage("Please wait!");
        this.dialog.show();
        this.auth.fetchSignInMethodsForEmail(this.e1_email.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                boolean check;
                if (!task.getResult().getSignInMethods().isEmpty()) {
                    check = true;
                } else {
                    check = false;
                }
                if (!check) {
                    LoginEmailActivity.this.dialog.dismiss();
                    Toast.makeText(LoginEmailActivity.this.getApplicationContext(), "This email does not exist. Please create an account first", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(LoginEmailActivity.this, RegisterEmailActivity.class);
                    startActivity(myIntent);
                    return;
                }
                LoginEmailActivity.this.dialog.dismiss();
                Intent myIntent = new Intent(LoginEmailActivity.this, LoginPasswordActivity.class);
                myIntent.putExtra("email_login", LoginEmailActivity.this.e1_email.getText().toString());
                LoginEmailActivity.this.startActivity(myIntent);
                LoginEmailActivity.this.finish();
            }
        });
    }
}
