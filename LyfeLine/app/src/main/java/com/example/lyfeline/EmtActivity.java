package com.example.lyfeline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.lyfeline.services.EmtLocationService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EmtActivity extends AppCompatActivity {

    private static final String TAG = "EmtActivity";

    FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private EmtLocation mEmtPosition = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emt_gui);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        //TabItem tabHome = findViewById(R.id.tabHome);
        //TabItem tabMap = findViewById(R.id.tabMap);

        ViewPager viewPager = findViewById(R.id.viewPager);

        EmtPagerAdapter pageAdapter = new EmtPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        startLocationService();
        getEmtPosition();
    }

    private void startLocationService(){


        if(!isLocationServiceRunning()){
            Intent serviceIntent = new Intent(this, EmtLocationService.class);
//        this.startService(serviceIntent);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){

                EmtActivity.this.startForegroundService(serviceIntent);
            }else{
                startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.example.lyfeline.services.EmtLocationService".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }

    // Retrieve EmtPoistion info from DB
    private void getEmtPosition(){
        Log.d(TAG, "getEmtPosition: Begin" + mEmtPosition);
        DocumentReference locationRef = mDb
                .collection("EMTs_Location")
                .document(FirebaseAuth.getInstance().getUid());

        locationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                mEmtPosition = task.getResult().toObject(EmtLocation.class);

            }
        });
        Log.d(TAG, "getEmtPosition: End" + mEmtPosition);

    }
}
