package com.example.project;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LocationShareService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    FirebaseAuth auth;
    GoogleApiClient client;
    LatLng latLngCurrent;
    NotificationCompat.Builder notification;
    DatabaseReference reference;
    LocationRequest request;
    public final int uniqueId = 654321;
    FirebaseUser user;

    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    public void onCreate() {
        super.onCreate();
        this.reference = FirebaseDatabase.getInstance().getReference().child("Users");
        this.auth = FirebaseAuth.getInstance();
        this.notification = new NotificationCompat.Builder(this);
        this.notification.setAutoCancel(false);
        this.notification.setOngoing(true);
        this.user = this.auth.getCurrentUser();
        this.client = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        this.client.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        new LocationRequest();
        this.request = LocationRequest.create();
        this.request.setPriority(100);
        this.request.setInterval(500);
        if (ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
            LocationServices.FusedLocationApi.requestLocationUpdates(this.client, this.request, (LocationListener) this);
            this.notification.setSmallIcon(R.drawable.loc3);
            this.notification.setTicker("Notification.");
            this.notification.setWhen(System.currentTimeMillis());
            this.notification.setContentTitle("Family Tracker App");
            this.notification.setContentText("You are sharing your location.!");
            this.notification.setDefaults(1);
            this.notification.setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MyNavigationTutorial.class), 134217728));
            ((NotificationManager) getSystemService("notification")).notify(654321, this.notification.build());
        }
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    public void onConnectionSuspended(int i) {
    }


    public void onLocationChanged(Location location) {
        this.latLngCurrent = new LatLng(location.getLatitude(), location.getLongitude());
        shareLocation();
    }

    public void shareLocation() {
        String myDate = new SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault()).format(new Date());
        this.reference.child(this.user.getUid()).child("issharing").setValue("true");
        this.reference.child(this.user.getUid()).child("date").setValue(myDate);
        this.reference.child(this.user.getUid()).child("lat").setValue(String.valueOf(this.latLngCurrent.latitude));
        this.reference.child(this.user.getUid()).child("lng").setValue(String.valueOf(this.latLngCurrent.longitude)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(LocationShareService.this.getApplicationContext(), "Could not share Location.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onDestroy() {
        LocationServices.FusedLocationApi.removeLocationUpdates(this.client, (LocationListener) this);
        this.client.disconnect();
        ((NotificationManager) getSystemService("notification")).cancel(654321);
    }
}
