package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SendHelpAlertsActivity extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference circlereference;
    int countValue = 5;
    String memberUserId;
    Thread myThread;
    TextView t1_CounterTxt;
    FirebaseUser user;
    ArrayList<String> userIDsList;
    DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_send_help_alerts);
        this.t1_CounterTxt = (TextView) findViewById(R.id.textView9);
        this.auth = FirebaseAuth.getInstance();
        this.userIDsList = new ArrayList<>();
        this.user = this.auth.getCurrentUser();
        this.circlereference = FirebaseDatabase.getInstance().getReference().child("Users").child(this.user.getUid()).child("CircleMembers");
        this.usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        this.myThread = new Thread(new ServerThread());
        this.myThread.start();
    }

    private class ServerThread implements Runnable {
        private ServerThread() {
        }

        public void run() {
            while (SendHelpAlertsActivity.this.countValue != 0) {
                try {
                    Thread.sleep(1000);
                    SendHelpAlertsActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            SendHelpAlertsActivity.this.t1_CounterTxt.setText(String.valueOf(SendHelpAlertsActivity.this.countValue));
                            SendHelpAlertsActivity.this.countValue--;
                        }
                    });
                } catch (Exception e) {
                    return;
                }
            }
            SendHelpAlertsActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    SendHelpAlertsActivity.this.circlereference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                            SendHelpAlertsActivity.this.userIDsList.clear();
                            for (DataSnapshot dss : dataSnapshot.getChildren()) {
                                SendHelpAlertsActivity.this.memberUserId = (String) dss.child("circlememberid").getValue(String.class);
                                SendHelpAlertsActivity.this.userIDsList.add(SendHelpAlertsActivity.this.memberUserId);
                            }
                            if (SendHelpAlertsActivity.this.userIDsList.isEmpty()) {
                                Toast.makeText(SendHelpAlertsActivity.this.getApplicationContext(), "No circle members. Please add some one to your circle.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            CircleJoin circleJoin = new CircleJoin(SendHelpAlertsActivity.this.user.getUid());
                            for (int i = 0; i < SendHelpAlertsActivity.this.userIDsList.size(); i++) {
                                SendHelpAlertsActivity.this.usersReference.child(SendHelpAlertsActivity.this.userIDsList.get(i).toString()).child("HelpAlerts").child(SendHelpAlertsActivity.this.user.getUid()).setValue(circleJoin).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SendHelpAlertsActivity.this.getApplicationContext(), "Alerts sent successfully.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(SendHelpAlertsActivity.this.getApplicationContext(), "Could not send alerts. Please try again later.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
                            Toast.makeText(SendHelpAlertsActivity.this.getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    public void setCancel(View v) {
        Toast.makeText(getApplicationContext(), "Alert cancelled.", Toast.LENGTH_SHORT).show();
        this.myThread.interrupt();
        startActivity(new Intent(this, MyNavigationTutorial.class));
        finish();
    }
}
