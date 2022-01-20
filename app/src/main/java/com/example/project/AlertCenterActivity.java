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

public class AlertCenterActivity extends AppCompatActivity {

    FirebaseAuth auth;
    CreateUser createUser;
    RecyclerView.LayoutManager layoutManager;
    String memberUserId;
    ArrayList<CreateUser> myList;
    RecyclerView recyclerView;
    RecyclerView.Adapter recycleradapter;
    DatabaseReference reference;
    Toolbar toolbar;
    FirebaseUser user;
    DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_alert_center);
        this.recyclerView = (RecyclerView) findViewById(R.id.alertRecyclerView);
        this.layoutManager = new LinearLayoutManager(this);
        this.auth = FirebaseAuth.getInstance();
        this.user = this.auth.getCurrentUser();
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar.setTitle((CharSequence) "Help Center");
        setSupportActionBar(this.toolbar);
        this.reference = FirebaseDatabase.getInstance().getReference().child("Users").child(this.user.getUid()).child("HelpAlerts");
        this.userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        this.myList = new ArrayList<>();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.setHasFixedSize(true);
        this.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                myList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dss : dataSnapshot.getChildren()) {
                        memberUserId = dss.child("circlememberid").getValue().toString();
                        userReference.child(AlertCenterActivity.this.memberUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                                createUser = (CreateUser) dataSnapshot.getValue(CreateUser.class);
                                myList.add(createUser);
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    Toast.makeText(getApplicationContext(), "Showing alerts", Toast.LENGTH_SHORT).show();
//                    recycleradapter = new HelpAlertsAdapter(myList, getApplicationContext());
//                    recyclerView.setAdapter(recycleradapter);
//                    recycleradapter.notifyDataSetChanged();
                    return;
                }
               Toast.makeText(AlertCenterActivity.this.getApplicationContext(), "Alert list is empty", Toast.LENGTH_SHORT).show();
                recyclerView.setAdapter(null);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
                Toast.makeText(AlertCenterActivity.this.getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        recycleradapter = new HelpAlertsAdapter(myList,getApplicationContext());
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
