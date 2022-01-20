package com.example.project;

import android.content.Context;
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

public class JoinedMembersAdapter extends RecyclerView.Adapter<JoinedMembersAdapter.JoinedMembersViewHolder> {
    Context c;
    ArrayList<CreateUser> nameList;

    JoinedMembersAdapter(ArrayList<CreateUser> nameList2, Context c2) {
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
    public JoinedMembersViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.joined_card_layout, parent, false);
        JoinedMembersViewHolder joinedMembersViewHolder = new JoinedMembersViewHolder(v,this.c, this.nameList);
        return joinedMembersViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull JoinedMembersViewHolder holder, int position) {
        CreateUser addCircle = this.nameList.get(position);
        Picasso.get().load(addCircle.profile_image).placeholder((int) R.drawable.defaultimg).into((ImageView) holder.i1);
        holder.name_txt.setText(addCircle.name);
    }

    public static class JoinedMembersViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        FirebaseAuth auth ;//= FirebaseAuth.getInstance();
        Context ctx;
        DatabaseReference currentReference ;//= FirebaseDatabase.getInstance().getReference().child("Users");
        CircleImageView i1;
        ArrayList<CreateUser> nameArrayList;
        TextView name_txt;
        DatabaseReference reference ;//= FirebaseDatabase.getInstance().getReference().child("Users").child(this.user.getUid()).child("JoinedCircles");
        FirebaseUser user;// = this.auth.getCurrentUser();
        View v;

        public JoinedMembersViewHolder(@NonNull @NotNull View itemView, Context ctx2, ArrayList<CreateUser> nameArrayList2) {
            super(itemView);
            itemView.setOnCreateContextMenuListener(this);
            //itemView.setOnClickListener(this);
            this.v = itemView;
            this.ctx = ctx2;
            this.nameArrayList = nameArrayList2;

            auth = FirebaseAuth.getInstance();
            user =auth.getCurrentUser();
            currentReference = FirebaseDatabase.getInstance().getReference().child("Users");
            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(this.user.getUid()).child("JoinedCircles");

            this.name_txt = (TextView) itemView.findViewById(R.id.item_title);
            this.i1 = (CircleImageView) itemView.findViewById(R.id.itemImage);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            final CreateUser addCircle = this.nameArrayList.get(getAdapterPosition());
            this.reference.child(addCircle.userid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        currentReference.child(addCircle.userid).child("CircleMembers").child(JoinedMembersViewHolder.this.user.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ctx, "Unjoined successfully", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
            return true;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v2, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add("UNJOIN").setOnMenuItemClickListener(this);
        }
    }
}
