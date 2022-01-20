package com.example.project;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyNavigationTutorial extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    FirebaseAuth auth;
    CircleImageView circleImageView;
    GoogleApiClient client;

    LatLng latLngCurrent;
    GoogleMap mMap;
    Marker marker;
    String myDate,myCode;
    String myEmail;
    String myName;
    String myProfileImage;
    String mySharing;
    DatabaseReference reference;
    LocationRequest request;
    TextView textEmail;
    TextView textName;
    Toolbar toolbar;
    FirebaseUser user;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_my_navigation_tutorial);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar.setTitle((CharSequence) "GpsTracker");
        setSupportActionBar(this.toolbar);
        this.auth = FirebaseAuth.getInstance();

        this.user = this.auth.getCurrentUser();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, this.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        this.textName = (TextView) header.findViewById(R.id.nameTxt);
        this.textEmail = (TextView) header.findViewById(R.id.emailTxt);
        this.circleImageView = (CircleImageView) header.findViewById(R.id.imageView2);
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != 0) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 1000);
        }
        this.reference = FirebaseDatabase.getInstance().getReference().child("Users");
        this.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                try {
                    MyNavigationTutorial.this.myDate = dataSnapshot.child(MyNavigationTutorial.this.user.getUid()).child("date").getValue().toString();
                    MyNavigationTutorial.this.myCode = dataSnapshot.child(MyNavigationTutorial.this.user.getUid()).child("circlecode").getValue().toString();
                    MyNavigationTutorial.this.mySharing = dataSnapshot.child(MyNavigationTutorial.this.user.getUid()).child("issharing").getValue().toString();
                    MyNavigationTutorial.this.myEmail = dataSnapshot.child(MyNavigationTutorial.this.user.getUid()).child("email").getValue().toString();
                    MyNavigationTutorial.this.myName = dataSnapshot.child(MyNavigationTutorial.this.user.getUid()).child("name").getValue().toString();
                    MyNavigationTutorial.this.myProfileImage = dataSnapshot.child(MyNavigationTutorial.this.user.getUid()).child("profile_image").getValue().toString();
                    MyNavigationTutorial.this.textName.setText(MyNavigationTutorial.this.myName);
                    MyNavigationTutorial.this.textEmail.setText(MyNavigationTutorial.this.myEmail);
                    Picasso.get().load(MyNavigationTutorial.this.myProfileImage).placeholder((int) R.drawable.defaultimg).into((ImageView) MyNavigationTutorial.this.circleImageView);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Toast.makeText(MyNavigationTutorial.this.getApplicationContext(), "Could not connect to the network. Please try again later", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                Toast.makeText(MyNavigationTutorial.this.getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        if (!isServiceRunning(getApplicationContext(), HelpAlertService.class)) {
            try {
                startService(new Intent(this, HelpAlertService.class));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "An exception occurred", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen((int) GravityCompat.START)) {
            drawer.closeDrawer((int) GravityCompat.START);
            return;
        }
        super.onBackPressed();
        finish();
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.signout) {
            if (this.user != null) {
                this.auth.signOut();
                finish();
                stopService(new Intent(this, HelpAlertService.class));
                stopService(new Intent(this, LocationShareService.class));
                startActivity(new Intent(this, MainActivity.class));
            }
        } else if (id == R.id.joinCircle) {

            MyNavigationTutorial.this.startActivity(new Intent(MyNavigationTutorial.this, JoinCircleActivity.class));
        } else if (id == R.id.myCircle) {
            MyNavigationTutorial.this.startActivity(new Intent(MyNavigationTutorial.this, MyCircleActivity.class));
        } else if (id == R.id.inviteFriends) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT,"My GpsTracker app invite code is : "+ myCode);
            startActivity(i.createChooser(i,"Share using: "));
        } else if (id == R.id.joinedCircle) {
            MyNavigationTutorial.this.startActivity(new Intent(MyNavigationTutorial.this, JoinedCirclesActivity.class));
        }else if (id == R.id.sharelocation){
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT,"My location is : "+"https://www.google.com/maps/@"+latLngCurrent.latitude+","+latLngCurrent.longitude+",17z");
            startActivity(i.createChooser(i,"Share using: "));
        } else if (id == R.id.sendHelpAlert) {
            MyNavigationTutorial.this.startActivity(new Intent(MyNavigationTutorial.this, SendHelpAlertsActivity.class));
        } else if (id == R.id.alertCenter) {
            MyNavigationTutorial.this.startActivity(new Intent(MyNavigationTutorial.this, AlertCenterActivity.class));
        } else if (id == R.id.howtouse) {
            MyNavigationTutorial.this.startActivity(new Intent(MyNavigationTutorial.this, HowToUseActivity.class));
        }
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer((int) GravityCompat.START);
        return true;
    }

    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        this.client = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        this.client.connect();
    }

    public void onConnected(@Nullable Bundle bundle) {
        new LocationRequest();
        this.request = LocationRequest.create();
        this.request.setPriority(100);
        this.request.setInterval(7000);
        if (ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
            LocationServices.FusedLocationApi.requestLocationUpdates(this.client, this.request, (LocationListener) this);
        }
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    public void onConnectionSuspended(int i) {
    }

    public void onLocationChanged(Location location) {
        LocationServices.FusedLocationApi.removeLocationUpdates(this.client, (LocationListener) this);
        this.latLngCurrent = new LatLng(location.getLatitude(), location.getLongitude());
        //shareLocation();
        if (this.marker == null) {
            this.marker = this.mMap.addMarker(new MarkerOptions().position(this.latLngCurrent).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(240.0f)));
            this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(this.latLngCurrent, 15.0f));
            return;
        }
        this.marker.setPosition(this.latLngCurrent);
        this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(this.latLngCurrent, 15.0f));
    }

//    public void shareLocation() {
//        String myDate = new SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault()).format(new Date());
//        this.reference.child(this.user.getUid()).child("issharing").setValue("true");
//        this.reference.child(this.user.getUid()).child("date").setValue(myDate);
//        this.reference.child(this.user.getUid()).child("lat").setValue(String.valueOf(this.latLngCurrent.latitude));
//        this.reference.child(this.user.getUid()).child("lng").setValue(String.valueOf(this.latLngCurrent.longitude)).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (!task.isSuccessful()) {
//                    Toast.makeText(getApplicationContext(), "Could not share Location.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send :
                if (!isServiceRunning(getApplicationContext(), LocationShareService.class)) {
                    startService(new Intent(this, LocationShareService.class));
                    break;
                } else {
                    Toast.makeText(getApplicationContext(), "You are already sharing your location",Toast.LENGTH_SHORT).show();
                    break;
                }
            case R.id.action_stop /*2131558657*/:
                stopService(new Intent(this, LocationShareService.class));
                this.reference.child(this.user.getUid()).child("issharing").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MyNavigationTutorial.this.getApplicationContext(), "Location sharing is now stopped", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MyNavigationTutorial.this.getApplicationContext(), "Location sharing could not be stopped", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_navigation_tutorial, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000 && grantResults.length > 0 && grantResults[0] == 0) {
            Toast.makeText(getApplicationContext(), "Location permission granted. Thankyou.", Toast.LENGTH_SHORT).show();
            onConnected((Bundle) null);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void inviteMembers(View v) {
        MyNavigationTutorial.this.startActivity(new Intent(MyNavigationTutorial.this, InviteCodeActivity.class));
    }

    @SuppressLint("WrongConstant")
    public boolean isServiceRunning(Context c, Class<?> serviceClass) {
        for (ActivityManager.RunningServiceInfo runningServiceInfo : ((ActivityManager) c.getSystemService("activity")).getRunningServices(Integer.MAX_VALUE)) {
            if (runningServiceInfo.service.getClassName().equals(serviceClass.getName())) {
                return true;
            }
        }
        return false;
    }
}
