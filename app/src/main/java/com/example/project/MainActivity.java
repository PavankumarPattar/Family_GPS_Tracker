package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_main);
        this.auth = FirebaseAuth.getInstance();
        this.user = this.auth.getCurrentUser();
        if (this.user == null) {
            setContentView((int) R.layout.activity_main);
            return;
        }
        startActivity(new Intent(this, MyNavigationTutorial.class));
        finish();
    }

    public void getStarted_click(View v) {
        startActivity(new Intent(this, RegisterEmailActivity.class));
        finish();
    }

    public void LoginUser(View v) {
        startActivity(new Intent(this, LoginEmailActivity.class));
        finish();
    }
}
