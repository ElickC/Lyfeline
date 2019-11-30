package com.example.lyfeline;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmtHomeFragment extends Fragment {

    private static final String TAG = "EmtHomeFragment";

    private ArrayList<HelpVics> victimsList = new ArrayList<>();
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private RecyclerView recyclerViewVictims;
    private RecyclerViewAdapter recyclerViewAdapter;


    public EmtHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_emt_home, container, false);

        recyclerViewVictims = view.findViewById(R.id.recyclerViewVictims);

        queryHelpVics();
        listenForRecyclerChanges();

        return view;
    }

    public void queryHelpVics() {

        CollectionReference vicHelpRef = mDb.collection("HelpVics");

        Query vicQuery = vicHelpRef;

        vicQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    victimsList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            Log.d(TAG, "queryHelpVics: adding victim to list");
                            HelpVics helpVic = document.toObject(HelpVics.class);
                            if(!helpVic.isEmtHasArrived()) {
                                victimsList.add(helpVic);
                            }
                        }
                    }
                    initRecyclerView();
                    recyclerViewAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    public void listenForRecyclerChanges() {
        final FirebaseFirestore mDb = FirebaseFirestore.getInstance();
        CollectionReference helpVicsRef = mDb.collection("HelpVics");
        helpVicsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                queryHelpVics();
            }
        });

    }

    public void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: setting up recyclerview");
        recyclerViewAdapter = new RecyclerViewAdapter(victimsList);
        recyclerViewVictims.setAdapter(recyclerViewAdapter);
        recyclerViewVictims.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

}
