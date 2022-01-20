package com.example.project;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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

public class MyCircleActivity extends AppCompatActivity {

    FirebaseAuth auth;
    ArrayList<String> circleuser_idList;
    CreateUser createUser;
    RecyclerView.LayoutManager layoutManager;
    String memberUserId;
    ArrayList<CreateUser> nameList;
    RecyclerView recyclerView;
    RecyclerView.Adapter recycleradapter;
    DatabaseReference reference;
    Toolbar toolbar;
    FirebaseUser user;
    DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_my_circle);
        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        this.layoutManager = new LinearLayoutManager(this);
        this.auth = FirebaseAuth.getInstance();
        this.user = this.auth.getCurrentUser();
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.setHasFixedSize(true);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar.setTitle((CharSequence) "My Circle");
        setSupportActionBar(this.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.nameList = new ArrayList<>();
        this.circleuser_idList = new ArrayList<>();
        this.usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        this.reference = FirebaseDatabase.getInstance().getReference().child("Users").child(this.user.getUid()).child("CircleMembers");
        this.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                nameList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dss : dataSnapshot.getChildren()) {
                        memberUserId = dss.child("circlememberid").getValue(String.class);
                        usersReference.child(memberUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull@NotNull DataSnapshot dataSnapshot) {
                                createUser =  dataSnapshot.getValue(CreateUser.class);
                                nameList.add(createUser);
                                recycleradapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    Toast.makeText(MyCircleActivity.this.getApplicationContext(), "Showing circle members", Toast.LENGTH_SHORT).show();
//                    MyCircleActivity.this.recycleradapter = new MembersAdapter(MyCircleActivity.this.nameList, MyCircleActivity.this.getApplicationContext());
//                    MyCircleActivity.this.recyclerView.setAdapter(MyCircleActivity.this.recycleradapter);
//                    MyCircleActivity.this.recycleradapter.notifyDataSetChanged();
                    return;
                }
                Toast.makeText(MyCircleActivity.this.getApplicationContext(), "List is empty.", Toast.LENGTH_SHORT).show();
                recyclerView.setAdapter( null);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        recycleradapter = new MembersAdapter(nameList,getApplicationContext());
        recyclerView.setAdapter(recycleradapter);
        recycleradapter.notifyDataSetChanged();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void refresh(View v) {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}
