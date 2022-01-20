package com.example.project;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class HelpAlertService extends Service {
    FirebaseAuth auth;
    NotificationCompat.Builder notification;
    DatabaseReference reference;
    public final int uniqueId = 123456;
    FirebaseUser user;

    public void onCreate() {
        super.onCreate();
        this.notification = new NotificationCompat.Builder(this);
        this.notification.setAutoCancel(false);
        this.notification.setOngoing(true);
        this.auth = FirebaseAuth.getInstance();
        this.user = this.auth.getCurrentUser();
        this.reference = FirebaseDatabase.getInstance().getReference().child("Users").child(this.user.getUid()).child("HelpAlerts");
        this.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    HelpAlertService.this.notification.setSmallIcon(R.drawable.loc3);
                    HelpAlertService.this.notification.setTicker("Notification.");
                    HelpAlertService.this.notification.setWhen(System.currentTimeMillis());
                    HelpAlertService.this.notification.setContentTitle("Family Tracker App");
                    HelpAlertService.this.notification.setContentText("Your circle member needs your help. Please tap to open!");
                    HelpAlertService.this.notification.setSound(RingtoneManager.getDefaultUri(2));
                    HelpAlertService.this.notification.setContentIntent(PendingIntent.getActivity(HelpAlertService.this.getApplicationContext(), 0, new Intent(HelpAlertService.this.getApplicationContext(), AlertCenterActivity.class), 134217728));
                    ((NotificationManager) HelpAlertService.this.getSystemService("notification")).notify(123456, HelpAlertService.this.notification.build());
                    return;
                }
                ((NotificationManager) HelpAlertService.this.getSystemService("notification")).cancel(123456);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
            }
        });
    }

    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }
}
