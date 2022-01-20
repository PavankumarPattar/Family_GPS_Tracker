package com.example.project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

public class InviteCodeActivity extends AppCompatActivity {
    FirebaseAuth auth;
    String code = null;
    String date;
    ProgressDialog dialog;
    String email;
    StorageReference firebaseStorageReference;
    String issharing;
    String name;
    String password;
    DatabaseReference reference;
    Uri resultUri;
    TextView t4_code;
    TextView b3_done;
    Toolbar toolbar;
    FirebaseUser user;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_invite_code);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar.setTitle((CharSequence) "Invite Code");
        this.dialog = new ProgressDialog(this);
        this.b3_done = findViewById(R.id.button3);
        setSupportActionBar(this.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.auth = FirebaseAuth.getInstance();
        this.reference = FirebaseDatabase.getInstance().getReference().child("Users");
        this.firebaseStorageReference = FirebaseStorage.getInstance().getReference().child("Profile_images");
        this.t4_code = (TextView) findViewById(R.id.textView4);
        Intent intent = getIntent();
        if (intent != null) {
            this.name = intent.getStringExtra("name");
            this.email = intent.getStringExtra("email");
            this.password = intent.getStringExtra(EmailAuthProvider.PROVIDER_ID);
            this.date = intent.getStringExtra("date");
            this.issharing = intent.getStringExtra("issharing");
            this.code = intent.getStringExtra("code");
            this.resultUri = (Uri) intent.getParcelableExtra("imageUri");
        }
        if (this.code == null) {
            this.b3_done.setVisibility(View.VISIBLE);
            this.reference.addValueEventListener(new ValueEventListener() {
                public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                    InviteCodeActivity.this.user = InviteCodeActivity.this.auth.getCurrentUser();
                    InviteCodeActivity.this.t4_code.setText(dataSnapshot.child(InviteCodeActivity.this.user.getUid()).child("circlecode").getValue().toString());
                }

                public void onCancelled(@NotNull DatabaseError databaseError) {
                }
            });
            return;
        }
        this.t4_code.setText(this.code);
    }

//    public void sendCode(View v) {
//        Intent i = new Intent("android.intent.action.SEND");
//        i.setType("text/plain");
//        i.putExtra("android.intent.extra.TEXT", "Hello, My GPS Tracker Circle code is " + this.t4_code.getText().toString() + ". Please join my circle.");
//        startActivity(Intent.createChooser(i, "Share using:"));
//    }

    public void Register(View v) {
        this.dialog.setProgressStyle(0);
        this.dialog.setMessage("Creating new Profile. Please wait");
        this.dialog.setCancelable(false);
        this.dialog.show();
        this.auth.createUserWithEmailAndPassword(this.email, this.password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    InviteCodeActivity.this.user = InviteCodeActivity.this.auth.getCurrentUser();
                    InviteCodeActivity.this.reference.child(InviteCodeActivity.this.user.getUid()).setValue(new CreateUser(name, email, password, date, code, user.getUid(), "false", "na", "na", "defaultimage"))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                InviteCodeActivity.this.firebaseStorageReference.child(InviteCodeActivity.this.user.getUid() + ".jpg").putFile(InviteCodeActivity.this.resultUri).addOnCompleteListener((OnCompleteListener<UploadTask.TaskSnapshot>) new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            InviteCodeActivity.this.reference.child(InviteCodeActivity.this.user.getUid()).child("profile_image").setValue(task.getResult().getMetadata().getReference().getDownloadUrl().toString())//.getDownloadUrl()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        InviteCodeActivity.this.dialog.dismiss();
                                                        InviteCodeActivity.this.sendVerificationEmail();
                                                    }
                                                }
                                            });
                                            return;
                                        }
                                        Toast.makeText(InviteCodeActivity.this.getApplicationContext(), "Could not upload user image", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                    return;
                }
                Toast.makeText(InviteCodeActivity.this.getApplicationContext(), "Could not create account. Try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendVerificationEmail() {
        this.user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(InviteCodeActivity.this.getApplicationContext(), "Email sent for verification. Please check email.", Toast.LENGTH_SHORT).show();
                    InviteCodeActivity.this.finish();
                    InviteCodeActivity.this.auth.signOut();
                    InviteCodeActivity.this.startActivity(new Intent(InviteCodeActivity.this, MainActivity.class));
                    return;
                }
                InviteCodeActivity.this.overridePendingTransition(0, 0);
                InviteCodeActivity.this.finish();
                InviteCodeActivity.this.overridePendingTransition(0, 0);
                InviteCodeActivity.this.startActivity(InviteCodeActivity.this.getIntent());
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
