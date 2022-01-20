package com.example.project;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HelpAlertsAdapter extends RecyclerView.Adapter<HelpAlertsAdapter.HelpAlertViewHolder> {
    Context c;
    ArrayList<CreateUser> nameList;

    HelpAlertsAdapter(ArrayList<CreateUser> nameList2, Context c2) {
        this.nameList = nameList2;
        this.c = c2;
    }

    @Override
    public int getItemCount() {
        return this.nameList.size();
    }

    @NonNull
    @NotNull
    @Override
    public HelpAlertViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_layout, parent, false);
        HelpAlertViewHolder helpAlertViewHolder = new HelpAlertViewHolder(v,this.c, this.nameList);
        return helpAlertViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HelpAlertViewHolder holder, int position) {
        CreateUser addCircle = this.nameList.get(position);
        holder.alertNameTxt.setText(addCircle.name);
        holder.alertDateTxt.setText(addCircle.date);
        Picasso.get().load(addCircle.profile_image).placeholder((int) R.drawable.defaultimg).into((ImageView) holder.alertImageView);
    }

    public static class HelpAlertViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {//, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        TextView alertDateTxt;
        CircleImageView alertImageView;
        TextView alertNameTxt;
        FirebaseAuth auth;// = FirebaseAuth.getInstance();
        Context ctx;
        DatabaseReference myReference;// = FirebaseDatabase.getInstance().getReference().child("Users").child(this.user.getUid()).child("HelpAlerts");
        ArrayList<CreateUser> nameArrayList;
        FirebaseUser user;// = this.auth.getCurrentUser();
        View v;

        public HelpAlertViewHolder(View itemView, Context ctx2, ArrayList<CreateUser> nameArrayList2) {
            super(itemView);
            this.v = itemView;
            itemView.setOnClickListener(this);

            auth = FirebaseAuth.getInstance();
            user =auth.getCurrentUser();
            itemView.setOnCreateContextMenuListener(this);
            this.nameArrayList = nameArrayList2;
            this.ctx = ctx2;
            this.alertImageView = (CircleImageView) itemView.findViewById(R.id.alertImage);
            this.alertNameTxt = (TextView) itemView.findViewById(R.id.alertName);
            this.alertDateTxt = (TextView) itemView.findViewById(R.id.alertDate);

            myReference = FirebaseDatabase.getInstance().getReference().child("Users").child(this.user.getUid()).child("HelpAlerts");
        }


        public void onClick(View v2) {
            CreateUser addCircle = this.nameArrayList.get(getAdapterPosition());
            String latitude_user = addCircle.lat;
            String longitude_user = addCircle.lng;
            if (!latitude_user.equals("na") || !longitude_user.equals("na")) {
                Intent mYIntent = new Intent(this.ctx, LiveMapActivity.class);
                mYIntent.putExtra("latitude", latitude_user);
                mYIntent.putExtra("longitude", longitude_user);
                mYIntent.putExtra("name", addCircle.name);
                mYIntent.putExtra("userid", addCircle.userid);
                mYIntent.putExtra("date", addCircle.date);
                mYIntent.putExtra("image", addCircle.profile_image);
                mYIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.ctx.startActivity(mYIntent);
                return;
            }
            Toast.makeText(this.ctx, "Could not get the location.Try again", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            this.myReference.child(this.nameArrayList.get(getAdapterPosition()).userid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(HelpAlertViewHolder.this.ctx, "Alert removed.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(HelpAlertViewHolder.this.ctx, "Could not remove it", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v2, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add("REMOVE").setOnMenuItemClickListener(this);
        }
    }
}
