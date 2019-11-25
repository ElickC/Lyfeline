package com.example.lyfeline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.GeoApiContext;

// Activity for creating an account
public class Main2Activity extends AppCompatActivity {
    // creating log
    private static final String TAG = "Main2Activity";

    // constants
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    // variables
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private EmtLocation mEmtLocation;
    private VicLocation mVicLocation;
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();

    TextInputEditText emtEmail, emtPass, victimEmail, victimPass;
    EditText victimFirstName, victimLastName;
    EditText emtFirstName, emtLastName, emtCode;
    Button buttonVictimRegister, buttonEmtRegister, buttonEmt, buttonVictim;


    private FirebaseAuth mAuth;
    final String EMT_CODE = "123";
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private boolean isEmt = false, isVic = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emt_or_victim);

        //Buttons
        buttonEmt = findViewById(R.id.buttonEMT);
        buttonVictim = findViewById(R.id.buttonVictim);
        buttonVictimRegister = findViewById(R.id.buttonRegisterVictim);
        buttonEmtRegister = findViewById(R.id.buttonRegisterEMT);

        mAuth = FirebaseAuth.getInstance();


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    // Function to identify which button was clicked
    public void onClickResolve(View v) {
        switch (v.getId()) {
            case R.id.buttonEMT:
                setContentView(R.layout.emt_create_account);
                break;
            case R.id.buttonVictim:
                setContentView(R.layout.victim_create_account);
                break;
            case R.id.buttonRegisterVictim:
                registerVictim();
                break;
            case R.id.buttonRegisterEMT:
                registerEMT();
                break;
        }
    }
     // TODO add a new activity to handle further registration details
    // Handles victim registration
    public void registerVictim() {
        victimEmail = findViewById(R.id.textInputEditTextEmailVictim);
        victimPass =  findViewById(R.id.textInputEditTextPasswordVictim);
        victimFirstName = findViewById(R.id.editTextFirst);
        victimLastName = findViewById(R.id.editTextLast);

        final String email = victimEmail.getText().toString();
        String password = victimPass.getText().toString();
        final String firstN = victimFirstName.getText().toString();
        final String lastN = victimLastName.getText().toString();


        if (!email.matches("") && !password.matches("") && !firstN.matches("") &&
                !lastN.matches("")) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmailAndPassword: Victim Successful ");
                                String userID = FirebaseAuth.getInstance().getUid();
                                isVic = true;
                               //getVicDetails();
                                createNewVictim(userID, email, firstN, lastN);

                            } else {
                                Log.d(TAG, "createUserWithEmailAndPassword: Victim Failure ");
                                try {
                                    throw task.getException();
                                }
                                catch (FirebaseAuthWeakPasswordException e) {
                                    showToast("Password is Weak");
                                }
                                catch (FirebaseAuthInvalidCredentialsException e) {
                                    showToast("Email Does Not Exist");
                                }
                                catch (FirebaseAuthUserCollisionException e) {
                                    showToast("Email Already Registered");
                                }
                                catch(Exception e) {
                                    showToast("Please Try Again");
                                }
                            }
                        }
                    });

        } else {
            showToast("Please Complete All Fields");
        }

    }

    // Creates new VictimUser class, writes user data to database and starts victimGUI
    public void createNewVictim(String userID, String email, String firstName, String lastName) {
        getLocationPermission();
        VictimUser victim = new VictimUser(userID, email, firstName, lastName);

        DocumentReference vicRef = mDb
                .collection("VicUser")
                .document(FirebaseAuth.getInstance().getUid());

        vicRef.set(victim).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "createNewVictim: \ninserted vic user into database");
                getVicDetails();
            }
        });

        Intent victimGui = new Intent(this, VictimGui.class);
        startActivity(victimGui);
    }

    // Handles emt registration
    public void registerEMT() {
        emtEmail = findViewById(R.id.textInputEditTextEmailEMT);
        emtPass = findViewById(R.id.textInputEditTextPasswordEMT);
        emtFirstName = findViewById(R.id.editTextFirstEMT);
        emtLastName = findViewById(R.id.editTextLastEMT);
        emtCode = findViewById(R.id.editTextCodeEMT);

        final String email = emtEmail.getText().toString();
        String password = emtPass.getText().toString();
        final String firstN = emtFirstName.getText().toString();
        final String lastN = emtLastName.getText().toString();
        final String code = emtCode.getText().toString();


        if (!email.matches("") && !password.matches("") && !firstN.matches("") &&
                !lastN.matches("") && code.matches(EMT_CODE)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmailAndPassword: EMT Successful ");
                                String userID = FirebaseAuth.getInstance().getUid();
                                isEmt = true;
                                createNewEMT(userID, email, firstN, lastN);
                            } else {
                                Log.d(TAG, "createUserWithEmailAndPassword: EMT Failure ");
                                try {
                                    throw task.getException();
                                }
                                catch (FirebaseAuthWeakPasswordException e) {
                                    showToast("Password is Weak");
                                }
                                catch (FirebaseAuthInvalidCredentialsException e) {
                                    showToast("Email Does Not Exist");
                                }
                                catch (FirebaseAuthUserCollisionException e) {
                                    showToast("Email Already Registered");
                                }
                                catch(Exception e) {
                                    showToast("Please Try Again");
                                }
                            }
                        }
                    });

        } else {
            if (!code.matches("")) {
                showToast("Please Provide a Valid EMT Code");
            }
            else {
                showToast("Please Fill in All Fields");
            }
        }
    }

    // Creates new EmtUser class, writes user data to database and starts EmtGUI
    public void createNewEMT(String userID, String email, String firstName, String lastName) {
        getLocationPermission();
        EmtUser emt = new EmtUser(userID, email, firstName, lastName);

        DocumentReference emtRef = mDb
                .collection("EmtUser")
                .document(FirebaseAuth.getInstance().getUid());

        emtRef.set(emt).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "createNewEMT: \ninserted emt user into database");
                getEmtDetails();
            }
        });

        Intent emtGui = new Intent(this, EmtGui.class);
        startActivity(emtGui);
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void saveUserLocation(){



        if (mEmtLocation != null){
            DocumentReference locationRef = mDb
                    .collection("EMTs_Location")
                    .document(FirebaseAuth.getInstance().getUid());

            locationRef.set(mEmtLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(TAG, "saveUserLocation: \ninserted emt location into database" +
                            "/n lattitude : " + mEmtLocation.getGeo_point().getLatitude() +
                            "/n longitude : " + mEmtLocation.getGeo_point().getLongitude());

                }
            });
        }

        else if (mVicLocation != null){
            DocumentReference locationRef = mDb
                    .collection("Vics_Location")
                    .document(FirebaseAuth.getInstance().getUid());

            locationRef.set(mVicLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(TAG, "saveUserLocation: \ninserted victim location into database" +
                            "/n lattitude : " + mVicLocation.getGeo_point().getLatitude() +
                            "/n longitude : " + mVicLocation.getGeo_point().getLongitude());
                }
            });
        }

    }

    private void getEmtDetails(){
        if(mEmtLocation == null){
            mEmtLocation = new EmtLocation();

            DocumentReference emtRef = mDb
                    .collection("EmtUser")
                    .document(FirebaseAuth.getInstance().getUid());

            emtRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Log.d(TAG, "getEmtDetails: successfully set the user details");

                    EmtUser emtUser = task.getResult().toObject(EmtUser.class);
                    mEmtLocation.setEmtUser(emtUser);
                    ((UserClient)(getApplicationContext())).setUser(emtUser);
                    getDeviceLocation();
                }
            });
        }
        else {
            getDeviceLocation();
        }
    }

    private void getVicDetails(){
        if(mVicLocation == null){
            mVicLocation = new VicLocation();

            DocumentReference vicRef = mDb
                    .collection("VicUser")
                    .document(FirebaseAuth.getInstance().getUid());

            vicRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Log.d(TAG, "getVicDetails: successfully set the user details");

                    VictimUser vicUser = task.getResult().toObject(VictimUser.class);
                    mVicLocation.setVictimUser(vicUser);
                    ((UserClient)(getApplicationContext())).setUser(vicUser);
                    getDeviceLocation();
                }
            });
        }
        else {
            getDeviceLocation();
        }
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionGranted){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location");
                            Location currentLocation = (Location) task.getResult();

                            GeoPoint geoPoint = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());

                            if (isEmt) {
                                mEmtLocation.setGeo_point(geoPoint);
                                mEmtLocation.setTimestamp(null);
                                Log.d(TAG, "userClient: emt");
                            }
                            else if (isVic){
                                mVicLocation.setGeo_point(geoPoint);
                                mVicLocation.setTimestamp(null);
                                Log.d(TAG, "userClient: vic");
                            }

                            saveUserLocation();

                        }
                        else{
                            Log.d(TAG, "getDeviceLocation: current location is null");
                            Toast.makeText(getApplicationContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    public void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            }
            else{
                ActivityCompat.requestPermissions(this, permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else{
            ActivityCompat.requestPermissions(this, permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

}
