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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginPasswordActivity extends AppCompatActivity {
    FirebaseAuth auth;
    Button b1_password;
    ProgressDialog dialog;
    EditText e1_pass;
    String email;
    Toolbar toolbar;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_login_password);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar.setTitle((CharSequence) "Password");
        this.auth = FirebaseAuth.getInstance();
        this.dialog = new ProgressDialog(this);
        setSupportActionBar(this.toolbar);
        this.e1_pass = (EditText) findViewById(R.id.editTextPass);
        this.b1_password = (Button) findViewById(R.id.button);
        Intent intent = getIntent();
        if (intent != null) {
            this.email = intent.getStringExtra("email_login");
        }
        this.b1_password.setEnabled(false);
        this.b1_password.setBackgroundColor(Color.parseColor("#faebd7"));
        this.e1_pass.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (s.length() >= 8) {
                    LoginPasswordActivity.this.b1_password.setEnabled(true);
                    LoginPasswordActivity.this.b1_password.setBackgroundColor(Color.parseColor("#9C27B0"));
                    return;
                }
                LoginPasswordActivity.this.b1_password.setEnabled(false);
                LoginPasswordActivity.this.b1_password.setBackgroundColor(Color.parseColor("#faebd7"));
            }
        });
    }

    public void Login(View v) {
        this.dialog.setMessage("Please wait. Logging in.");
        this.dialog.show();
        if (this.e1_pass.getText().toString().length() >= 8) {
            this.auth.signInWithEmailAndPassword(this.email, this.e1_pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        LoginPasswordActivity.this.dialog.dismiss();
                        Toast.makeText(LoginPasswordActivity.this.getApplicationContext(), "Wrong username/password", Toast.LENGTH_SHORT).show();
                    } else if (Objects.requireNonNull(LoginPasswordActivity.this.auth.getCurrentUser()).isEmailVerified()) {
                        LoginPasswordActivity.this.dialog.dismiss();
                        LoginPasswordActivity.this.finish();
                        LoginPasswordActivity.this.startActivity(new Intent(LoginPasswordActivity.this, MyNavigationTutorial.class));
                    } else {
                        LoginPasswordActivity.this.dialog.dismiss();
                        LoginPasswordActivity.this.finish();
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(LoginPasswordActivity.this.getApplicationContext(), "This email is not verified yet. Please check your email", Toast.LENGTH_SHORT).show();
                        LoginPasswordActivity.this.startActivity(new Intent(LoginPasswordActivity.this, MainActivity.class));
                    }
                }
            });
        }
    }
}
