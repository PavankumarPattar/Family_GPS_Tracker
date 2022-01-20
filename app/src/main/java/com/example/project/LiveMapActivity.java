package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class LiveMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    LatLng friendLatLng;
    String latitude;
    String longitude;
    ArrayList<String> mKeys;
    GoogleMap mMap;
    Marker marker;
    String myDate;
    String myImage;
    String myLat;
    String myLng;
    String myName;
    MarkerOptions myOptions;
    String name;
    String prevImage;
    String prevdate;
    DatabaseReference reference;
    Toolbar toolbar;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_live_map);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar22);
        this.myOptions = new MarkerOptions();
        Intent intent = getIntent();
        this.mKeys = new ArrayList<>();
        if (intent != null) {
            this.latitude = intent.getStringExtra("latitude");
            this.longitude = intent.getStringExtra("longitude");
            this.name = intent.getStringExtra("name");
            this.userid = intent.getStringExtra("userid");
            this.prevdate = intent.getStringExtra("date");
            this.prevImage = intent.getStringExtra("image");
        }
        this.toolbar.setTitle((CharSequence) this.name + "'s Location");
        setSupportActionBar(this.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.reference = FirebaseDatabase.getInstance().getReference().child("Users").child(this.userid);
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        this.reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NotNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildChanged(@NotNull DataSnapshot dataSnapshot, String s) {
                LiveMapActivity.this.reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        LiveMapActivity.this.myName = (String) dataSnapshot.child("name").getValue(String.class);
                        LiveMapActivity.this.myLat = (String) dataSnapshot.child("lat").getValue(String.class);
                        LiveMapActivity.this.myLng = (String) dataSnapshot.child("lng").getValue(String.class);
                        LiveMapActivity.this.myDate = (String) dataSnapshot.child("date").getValue(String.class);
                        LiveMapActivity.this.myImage = (String) dataSnapshot.child("profile_image").getValue(String.class);
                        LiveMapActivity.this.friendLatLng = new LatLng(Double.parseDouble(LiveMapActivity.this.myLat), Double.parseDouble(LiveMapActivity.this.myLng));
                        LiveMapActivity.this.myOptions.position(LiveMapActivity.this.friendLatLng);
                        LiveMapActivity.this.myOptions.snippet("Last seen: " + LiveMapActivity.this.myDate);
                        LiveMapActivity.this.myOptions.title(LiveMapActivity.this.myName);
                        if (LiveMapActivity.this.marker == null) {
                            LiveMapActivity.this.marker = LiveMapActivity.this.mMap.addMarker(LiveMapActivity.this.myOptions);
                            LiveMapActivity.this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LiveMapActivity.this.friendLatLng, 15.0f));
                            return;
                        }
                        LiveMapActivity.this.marker.setPosition(LiveMapActivity.this.friendLatLng);
                    }

                    public void onCancelled(@NotNull DatabaseError databaseError) {
                    }
                });
            }

            public void onChildRemoved(@NotNull DataSnapshot dataSnapshot) {
            }

            public void onChildMoved(@NotNull DataSnapshot dataSnapshot, String s) {
            }

            public void onCancelled(@NotNull DatabaseError databaseError) {
            }
        });
    }

    public void onMapReady(@NotNull GoogleMap googleMap) {
        this.mMap = googleMap;
        this.mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            public View getInfoWindow(@NotNull Marker marker) {
                return null;
            }

            public View getInfoContents(@NotNull Marker marker) {
                View row = LiveMapActivity.this.getLayoutInflater().inflate(R.layout.custom_snippet, (ViewGroup) null);
                TextView nameTxt = (TextView) row.findViewById(R.id.snippetName);
                TextView dateTxt = (TextView) row.findViewById(R.id.snippetDate);
                CircleImageView imageTxt = (CircleImageView) row.findViewById(R.id.snippetImage);
                if (LiveMapActivity.this.myName == null && LiveMapActivity.this.myDate == null) {
                    nameTxt.setText(LiveMapActivity.this.name);
                    dateTxt.setText(dateTxt.getText().toString() + LiveMapActivity.this.prevdate);
                    Picasso.get().load(LiveMapActivity.this.prevImage).placeholder((int) R.drawable.defaultimg).into((ImageView) imageTxt);
                } else {
                    nameTxt.setText(LiveMapActivity.this.myName);
                    dateTxt.setText(dateTxt.getText().toString() + LiveMapActivity.this.myDate);
                    Picasso.get().load(LiveMapActivity.this.myImage).placeholder((int) R.drawable.defaultimg).into((ImageView) imageTxt);
                }
                return row;
            }
        });
        this.friendLatLng = new LatLng(Double.parseDouble(this.latitude), Double.parseDouble(this.longitude));
        MarkerOptions optionsnew = new MarkerOptions();
        optionsnew.position(this.friendLatLng);
        optionsnew.title(this.name);
        optionsnew.icon(BitmapDescriptorFactory.defaultMarker(240.0f));
        if (this.marker == null) {
            this.marker = this.mMap.addMarker(optionsnew);
        } else {
            this.marker.setPosition(this.friendLatLng);
        }
        this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(this.friendLatLng, 15.0f));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
