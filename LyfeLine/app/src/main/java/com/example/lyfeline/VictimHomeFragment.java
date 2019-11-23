package com.example.lyfeline;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class VictimHomeFragment extends Fragment {
    Button buttonSendHelp;


    public VictimHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_victim_home, container, false);

        buttonSendHelp = view.findViewById(R.id.buttonSendHelp);
        buttonSendHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "help has been sent", Toast.LENGTH_LONG).show();

            }
        });


        return view;


    }



}
