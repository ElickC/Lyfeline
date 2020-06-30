package com.example.lyfeline;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<HelpVics> victimsList = new ArrayList<>();


    public RecyclerViewAdapter(ArrayList<HelpVics> victims) {
        this.victimsList = victims;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item_recycler_view, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final @NonNull ViewHolder holder, final int position) {
        FirebaseFirestore mDb = FirebaseFirestore.getInstance();
        final DocumentReference vicRef = mDb
                .collection("HelpVics")
                .document(victimsList.get(position).vicLocation.getVictimUser().getUser_id());

        holder.name.setText(victimsList.get(position).vicLocation.getVictimUser().getFirstName());
        holder.message.setText(victimsList.get(position).getMessage());

        if(victimsList.get(position).isEmtOnTheWay()) {
            holder.status.setText("Assigned, On the way");
        }

        holder.btnOnTheWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                victimsList.get(position).setEmtOnTheWay(true);
                victimsList.get(position).setEmtHasArrived(false);
                victimsList.get(position).setEmtAssigned(FirebaseAuth.getInstance().getUid());
                holder.status.setText("Assigned, On the way");

                vicRef.set(victimsList.get(position)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "onBindViewHolder: successfully set Boolean value");
                        }
                    }
                });
            }
        });

        holder.btnArrived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                victimsList.get(position).setEmtHasArrived(true);
                victimsList.get(position).setEmtOnTheWay(false);
                vicRef.set(victimsList.get(position)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "onBindViewHolder: successfully set Boolean value");
                            holder.status.setText("EMT has Arrived");
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return victimsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, message, status;
        Button btnOnTheWay, btnArrived;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewName);
            message = itemView.findViewById(R.id.textViewMessage);
            status = itemView.findViewById(R.id.textViewStatus);
            btnOnTheWay = itemView.findViewById(R.id.buttonOnTheWay);
            btnArrived = itemView.findViewById(R.id.buttonArrived);
        }
    }
}
