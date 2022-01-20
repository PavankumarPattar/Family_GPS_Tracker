package com.example.project;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.EmailAuthProvider;

public class RegisterPasswordActivity extends AppCompatActivity {
    Button b1_password;
    EditText e1_password;
    String email;
    Toolbar toolbar;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_register_password);
        this.e1_password = (EditText) findViewById(R.id.editTextPassword);
        this.toolbar = (Toolbar) findViewById(R.id.toolbarPassword);
        this.b1_password = (Button) findViewById(R.id.buttonPassword);
        Intent intent = getIntent();
        if (intent != null) {
            this.email = intent.getStringExtra("email");
        }
        this.b1_password.setEnabled(false);
        this.b1_password.setBackgroundColor(Color.parseColor("#faebd7"));
        this.toolbar.setTitle((CharSequence) "Password");
        setSupportActionBar(this.toolbar);
        this.e1_password.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (s.length() >= 8) {
                    RegisterPasswordActivity.this.b1_password.setEnabled(true);
                    RegisterPasswordActivity.this.b1_password.setBackgroundColor(Color.parseColor("#9C27B0"));
                    return;
                }
                RegisterPasswordActivity.this.b1_password.setEnabled(false);
                RegisterPasswordActivity.this.b1_password.setBackgroundColor(Color.parseColor("#faebd7"));
            }
        });
    }

    public void goToNameActivity(View v) {
        if (this.e1_password.getText().toString().length() >= 8) {
            Intent myIntent = new Intent(this, RegisterNameActivity.class);
            myIntent.putExtra("email", this.email);
            myIntent.putExtra(EmailAuthProvider.PROVIDER_ID, this.e1_password.getText().toString());
            startActivity(myIntent);
            finish();
        }
    }
}
