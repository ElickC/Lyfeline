package com.example.lyfeline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.lyfeline.util.Constants.ERROR_DIALOG_REQUEST;


// Activity for home page. Handles user Login and directs Create Account to Main2Activity
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    // creating log
    private static final String TAG = "MainActivity";

    Button buttonLogin, buttonCreateAcc;
    TextInputEditText emailId, passId;
    private Boolean isVictim = false;
    private FirebaseAuth mAuth;


    // constants
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    // variables
    private Boolean mLocationPermissionGranted = false;

    // used to find last known location of device


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isServicesOK();
        getLocationPermission();

        buttonLogin = findViewById(R.id.buttonLogin);
        buttonCreateAcc = findViewById(R.id.buttonCreateAccount);

        buttonLogin.setOnClickListener(this);
        buttonCreateAcc.setOnClickListener(this);

        emailId = findViewById(R.id.textInputEditTextEmailEMT);
        passId = findViewById(R.id.textInputEditTextPasswordEMT);

        mAuth = FirebaseAuth.getInstance();


    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            // everything is okay and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            // an error occurred but we can resolve it
            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            showToast("You can't make map requests");
        }
        return false;
    }
      

    // Overriding onClick to handle buttons clicked
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.buttonLogin:
                Log.d(TAG, "onClick: Login button pressed, checking for location permissions");
                if(mLocationPermissionGranted) {
                    Log.d(TAG, "onClick: Location permissions granted, proceeding to Login user");
                    loginUser();
                }
                else {
                    Log.d(TAG, "onClick: Location permissions  not granted, getting permission");
                    getLocationPermission();
                }
                break;
            case R.id.buttonCreateAccount:
                Log.d(TAG, "onClick: Register button pressed, checking for location permissions");
                if(mLocationPermissionGranted) {
                    Log.d(TAG, "onClick: Location permissions granted, proceeding to register user");
                    registerUser();
                }
                else {
                    Log.d(TAG, "onClick: Location permissions  not granted, getting permission");
                    getLocationPermission();
                }
                break;
        }
    }

    //User login process
    public void loginUser() {
        String email = emailId.getText().toString();
        String password = passId.getText().toString();
        if ( !email.matches("") && !password.matches("") ) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInWithEmailAndPassword: success");
                                identifyUser();
                            }
                            else {
                                Log.d(TAG, "signInWithEmailAndPassword: failure");
                                try {
                                    throw task.getException();
                                }
                                catch (FirebaseAuthInvalidCredentialsException e) {
                                    showToast("Invalid Password");
                                }
                                catch (FirebaseAuthInvalidUserException e) {
                                    showToast("Invalid Email");
                                }
                                catch(Exception e) {
                                    showToast("Please Try Again");
                                }
                            }
                        }
                    });
        }
        else {
            showToast("Fill in All Fields");
        }
    }

    // If creating an account go to Main2Activity
    public void registerUser() {
        Log.d(TAG, "registerUser: starting new activity for registering user");

        Intent createAccount = new Intent(this, Main2Activity.class);
        startActivity(createAccount);
    }

    // After login, identify whether user is victim or EMT and act accordingly
    public void identifyUser() {
        Log.d(TAG, "identifyUser: Attempting to identify user as victim or emt");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference loginUserRef = db.collection("LoginUser");

        Query loginUserQuery = loginUserRef
                .whereEqualTo("user_id", FirebaseAuth.getInstance()
                        .getCurrentUser().getUid());

        loginUserQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            Log.d(TAG, "identifyUser: user found in database");
                            LoginUsers user =  document.toObject(LoginUsers.class);
                            isVictim = user.isVictim();
                            openGui();
                        }
                        else {
                            Log.d(TAG, "identifyUser: user not found in database");
                        }
                    }
                }
                else {
                    Log.d(TAG, "identifyUser: Could not query database");
                }
            }
        });


    }


    public void openGui() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Start GUI activity depending on who user is
        if (isVictim) {
            Log.d(TAG, "openGui: Creating singleton object for Victim user");
            CollectionReference vicRef = db.collection("VicUser");
            Query vicQuery = vicRef.whereEqualTo("user_id", FirebaseAuth.getInstance()
                    .getCurrentUser().getUid());
            vicQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                Log.d(TAG, "identifyUser: user found in database");
                                VictimUser user =  document.toObject(VictimUser.class);
                                ((UserClient)(getApplicationContext())).setUser(user);
                                Log.d(TAG, "openGui: user is Victim, creating intent for VictimGui");
                                Intent victimGui = new Intent(getApplicationContext(), VictimGui.class);
                                startActivity(victimGui);
                            }
                        }
                    }
                }
            });

        }
        else {
            Log.d(TAG, "openGui: Creating singleton object for EMT user");
            CollectionReference emtRef = db.collection("EmtUser");
            Query emtQuery = emtRef.whereEqualTo("user_id", FirebaseAuth.getInstance()
                    .getCurrentUser().getUid());
            emtQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                Log.d(TAG, "identifyUser: user found in database");
                                EmtUser user =  document.toObject(EmtUser.class);
                                ((UserClient)(getApplicationContext())).setUser(user);
                                Log.d(TAG, "openGui: user is EMT, creating intent for EmtGui");
                                Intent emtGui = new Intent(getApplicationContext(), EmtActivity.class);
                                startActivity(emtGui);
                            }
                        }
                    }
                }
            });



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

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}


