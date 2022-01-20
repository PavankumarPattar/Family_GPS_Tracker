package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.goodiebag.pinview.Pinview;
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

public class JoinCircleActivity extends AppCompatActivity {
    FirebaseAuth auth;
    DatabaseReference circleReference;
    DatabaseReference currentReference;
    String currentUserId;
    String current_userid;
    String joinUserId;
    DatabaseReference joinedReference;
    Pinview pinView;
    DatabaseReference reference;
    Toolbar toolbar;
    FirebaseUser user;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_join_circle);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar.setTitle((CharSequence) "Join a Circle");
        this.auth = FirebaseAuth.getInstance();
        this.user = this.auth.getCurrentUser();
        this.pinView = (Pinview) findViewById(R.id.mypinview);
        this.reference = FirebaseDatabase.getInstance().getReference().child("Users");
        this.currentReference = FirebaseDatabase.getInstance().getReference().child("Users").child(this.user.getUid());
        setSupportActionBar(this.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.currentReference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                JoinCircleActivity.this.current_userid = dataSnapshot.child("userid").getValue().toString();
            }

            public void onCancelled(@NotNull DatabaseError databaseError) {
                Toast.makeText(JoinCircleActivity.this.getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getCode(View v) {
        this.currentUserId = this.user.getUid();
        this.reference.orderByChild("circlecode").equalTo(this.pinView.getValue()).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    CreateUser createUser = null;
                    for (DataSnapshot childDss : dataSnapshot.getChildren()) {
                        createUser = (CreateUser) childDss.getValue(CreateUser.class);
                    }
                    joinUserId = createUser.userid;
                    circleReference = FirebaseDatabase.getInstance().getReference().child("Users").child(joinUserId).child("CircleMembers");
                    joinedReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("JoinedCircles");
                    CircleJoin circleJoin = new CircleJoin(current_userid);
                    final CircleJoin circleJoin1 = new CircleJoin(joinUserId);
                    circleReference.child(user.getUid()).setValue(circleJoin).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                joinedReference.child(joinUserId).setValue(circleJoin1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getApplicationContext(), "You have joined this circle successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(new Intent(JoinCircleActivity.this, MyNavigationTutorial.class));
                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "Could not join, try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    return;
                }
                Toast.makeText(JoinCircleActivity.this.getApplicationContext(), "Invalid circle code entered", Toast.LENGTH_SHORT).show();
            }

            public void onCancelled(@NotNull DatabaseError databaseError) {
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
