package com.example.lyfeline;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


/**
 * A simple {@link Fragment} subclass.
 */
public class VictimHomeFragment extends Fragment {
    private static final String TAG = "VictimHomeFragment";
    TextView textHelloUser;
    TextInputEditText textMessage;

    Button buttonSendHelp;
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();

    public VictimHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_victim_home, container, false);
        textHelloUser = view.findViewById(R.id.textViewHelloUser);
        textMessage = view.findViewById(R.id.textInputEditTextMessage);
        User user =  ((UserClient)(getActivity().getApplicationContext())).getUser();

        String firstName = user.firstName.substring(0, 1).toUpperCase() + user.firstName.substring(1);
        textHelloUser.setText("Hello " + firstName.trim() + "!");

        buttonSendHelp = view.findViewById(R.id.buttonSendHelp);
        buttonSendHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Victim attempting to send location to database for help");
                final String message = textMessage.getText().toString();

                CollectionReference vicRef = mDb.collection("Vics_Location");

                final DocumentReference vicHelpRef = mDb
                        .collection("HelpVics")
                        .document(FirebaseAuth.getInstance().getUid());

                Query vicQuery = vicRef.whereEqualTo("victimUser.user_id", FirebaseAuth.getInstance()
                        .getCurrentUser().getUid());

                vicQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    HelpVics helpVic = new HelpVics();
                                    helpVic.setMessage(message);
                                    VicLocation userLoc = document.toObject(VicLocation.class);
                                    helpVic.setVicLocation(userLoc);

                                    vicHelpRef.set(helpVic).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d(TAG, "onClick: Victim location successfully sent to database ");
                                            Toast.makeText(getContext(), "help has been sent", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
            }
        });


        return view;


    }



}
