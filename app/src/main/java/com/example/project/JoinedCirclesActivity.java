package com.example.project;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class JoinedCirclesActivity extends AppCompatActivity {

    FirebaseAuth auth;
    CreateUser createUser;
    DatabaseReference joinedReference;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<CreateUser> myList;
    RecyclerView recyclerView;
    RecyclerView.Adapter recycleradapter;
    Toolbar toolbar;
    FirebaseUser user;
    DatabaseReference usersReference;
    String circlememberid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_joined_circles);
        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerviewJoined);
        this.layoutManager = new LinearLayoutManager(this);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar.setTitle((CharSequence) "Joined Circles");
        setSupportActionBar(this.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.myList = new ArrayList<>();
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.setHasFixedSize(true);
        this.auth = FirebaseAuth.getInstance();
        this.user = this.auth.getCurrentUser();
        this.joinedReference = FirebaseDatabase.getInstance().getReference().child("Users").child(this.user.getUid()).child("JoinedCircles");
        this.usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        this.joinedReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                myList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dss : dataSnapshot.getChildren()) {
                        circlememberid = dss.child("circlememberid").getValue(String.class);
                        usersReference.child(circlememberid)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                                    createUser = (CreateUser) dataSnapshot.getValue(CreateUser.class);
                                    myList.add(createUser);
                                    recycleradapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
                                        Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        }
                    Toast.makeText(JoinedCirclesActivity.this.getApplicationContext(), "Showing joined circles", Toast.LENGTH_SHORT).show();
//                    JoinedCirclesActivity.this.recycleradapter = new JoinedMembersAdapter(JoinedCirclesActivity.this.myList, JoinedCirclesActivity.this.getApplicationContext());
//                    JoinedCirclesActivity.this.recyclerView.setAdapter(JoinedCirclesActivity.this.recycleradapter);
//                    JoinedCirclesActivity.this.recycleradapter.notifyDataSetChanged();
                    return;
                }
                Toast.makeText(JoinedCirclesActivity.this.getApplicationContext(), "You have not joined any circle yet!", Toast.LENGTH_SHORT).show();
                recyclerView.setAdapter( null);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
                Toast.makeText(JoinedCirclesActivity.this.getApplicationContext(), databaseError.toString(), Toast.LENGTH_LONG).show();
            }
        });

        recycleradapter = new JoinedMembersAdapter(myList,getApplicationContext());
        recyclerView.setAdapter(recycleradapter);
        recycleradapter.notifyDataSetChanged();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
