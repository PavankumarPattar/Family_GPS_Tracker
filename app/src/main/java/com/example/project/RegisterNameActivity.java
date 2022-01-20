package com.example.project;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.EmailAuthProvider;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Date;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterNameActivity extends AppCompatActivity {
    Button b1;
    CircleImageView circleImageView;
    EditText e1;
    String email;
    String password;
    Uri resultUri;
    Toolbar toolbar;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_register_name);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar.setTitle((CharSequence) "Your Profile");
        setSupportActionBar(this.toolbar);
        this.e1 = (EditText) findViewById(R.id.editTextPass);
        this.b1 = (Button) findViewById(R.id.button);
        this.b1.setEnabled(false);
        this.b1.setBackgroundColor(Color.parseColor("#faebd7"));
        this.circleImageView = (CircleImageView) findViewById(R.id.profile_image);
        Intent intent = getIntent();
        if (intent != null) {
            this.email = intent.getStringExtra("email");
            this.password = intent.getStringExtra(EmailAuthProvider.PROVIDER_ID);
        }
        this.e1.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    RegisterNameActivity.this.b1.setEnabled(true);
                    RegisterNameActivity.this.b1.setBackgroundColor(Color.parseColor("#9C27B0"));
                    return;
                }
                RegisterNameActivity.this.b1.setEnabled(false);
                RegisterNameActivity.this.b1.setBackgroundColor(Color.parseColor("#faebd7"));
            }
        });
    }

    public void generateCode(View v) {
        if (this.e1.getText().toString().length() > 0) {
            new Date();
            String code = String.valueOf(100000 + new Random().nextInt(900000));
            if (this.resultUri != null) {
                Intent myIntent = new Intent(this, InviteCodeActivity.class);
                myIntent.putExtra("name", this.e1.getText().toString());
                myIntent.putExtra("email", this.email);
                myIntent.putExtra(EmailAuthProvider.PROVIDER_ID, this.password);
                myIntent.putExtra("date", "na");
                myIntent.putExtra("issharing", "false");
                myIntent.putExtra("code", code);
                myIntent.putExtra("imageUri", this.resultUri);
                startActivity(myIntent);
                finish();
                return;
            }
            Toast.makeText(getApplicationContext(), "You must choose your profile picture.", Toast.LENGTH_SHORT).show();
        }
    }

    public void openGallery(View v) {
        startActivityForResult(new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 12);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12 && resultCode == -1 && data != null) {
            CropImage.activity(data.getData()).start(this);
        }
        if (requestCode == 203) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == -1) {
                this.resultUri = result.getUri();
                this.circleImageView.setImageURI(this.resultUri);
            } else if (resultCode == 204) {
                result.getError();
            }
        }
    }
}
