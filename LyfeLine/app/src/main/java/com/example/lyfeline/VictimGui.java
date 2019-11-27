package com.example.lyfeline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.lyfeline.services.VicLocationService;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class VictimGui extends AppCompatActivity {
    Toolbar toolbar;
    private static final String TAG = "VictimGui";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_victim_gui);
        Log.d(TAG, "onCreate: started victim gui");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        //TabItem tabHome = findViewById(R.id.tabHome);
        //TabItem tabMap = findViewById(R.id.tabMap);

        ViewPager viewPager = findViewById(R.id.viewPager);

        VictimPagerAdapter pageAdapter = new VictimPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        startLocationService();
    }

    private void startLocationService(){


        if(!isLocationServiceRunning()){
            Intent serviceIntent = new Intent(this, VicLocationService.class);
//        this.startService(serviceIntent);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){

                VictimGui.this.startForegroundService(serviceIntent);
            }else{
                startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.example.lyfeline.services.VicLocationService".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }
}
