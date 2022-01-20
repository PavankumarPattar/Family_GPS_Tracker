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

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MembersViewHolder> {
    Context c;
    ArrayList<CreateUser> nameList;

    MembersAdapter(ArrayList<CreateUser> nameList2, Context c2) {
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
    public MembersViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout,parent,false);
        MembersViewHolder membersViewHolder = new MembersViewHolder(v,c,nameList);

        return membersViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MembersViewHolder holder, int position) {
        CreateUser addCircle = this.nameList.get(position);
        holder.name_txt.setText(addCircle.name);
        Picasso.get().load(addCircle.profile_image).placeholder((int) R.drawable.defaultimg).into((ImageView) holder.circleImageView);
        if (addCircle.issharing.equals("false")) {
            holder.i1.setImageResource(R.drawable.redoffline1);
        } else if (addCircle.issharing.equals("true")) {
            holder.i1.setImageResource(R.drawable.green);
        }
    }

    public static class MembersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        CircleImageView circleImageView;
        Context ctx;
        ImageView i1;
        FirebaseAuth mAuth ;//= FirebaseAuth.getInstance();
        DatabaseReference mJoinedRef ;//= FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference mReference ;//= FirebaseDatabase.getInstance().getReference().child("Users").child(this.mUser.getUid()).child("CircleMembers");
        FirebaseUser mUser ;//= this.mAuth.getCurrentUser();
        ArrayList<CreateUser> nameArrayList;
        TextView name_txt;
        View v;

        public MembersViewHolder(View itemView, Context ctx2, ArrayList<CreateUser> nameArrayList2) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
            this.nameArrayList = nameArrayList2;
            this.v = itemView;

            mAuth = FirebaseAuth.getInstance();
            mUser =mAuth.getCurrentUser();
            mReference = FirebaseDatabase.getInstance().getReference().child("Users").child(this.mUser.getUid()).child("CircleMembers");
            mJoinedRef = FirebaseDatabase.getInstance().getReference().child("Users");

            this.ctx = ctx2;
            this.name_txt = (TextView) itemView.findViewById(R.id.item_title);
            this.i1 = (ImageView) itemView.findViewById(R.id.item_image);
            this.circleImageView = (CircleImageView) itemView.findViewById(R.id.item_imageprofile);
        }


        @Override
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
            Toast.makeText(this.ctx, "This circle member is not sharing location.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            final CreateUser addCircle = this.nameArrayList.get(getAdapterPosition());
            this.mReference.child(addCircle.userid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        MembersViewHolder.this.mJoinedRef.child(addCircle.userid).child("JoinedCircles").child(MembersViewHolder.this.mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MembersViewHolder.this.ctx, "User removed from circle.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v2, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add("Remove").setOnMenuItemClickListener(this);
        }
    }
}
