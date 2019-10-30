package com.example.lyfeline;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.maps.GoogleMap;


// Activity for home page. Handles user Login and directs Create Account to Main2Activity
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    // creating log
    private static final String TAG = "MainActivity";

    // error if user doesn't have correct version of google services
    private static final int ERROR_DIALOG_REQUEST = 9001;


    Button buttonLogin, buttonCreateAcc, buttonMap;
    EditText emailId, passId;
    private Boolean isVictim = false;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference dbRef;
    final String VICTIM_PATH = "Victims/";
    final String EMT_PATH = "Emt/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonMap = findViewById(R.id.buttonMap);

        if (isServicesOK()) {
            init();
        }


        buttonLogin = findViewById(R.id.buttonLogin);
        buttonCreateAcc = findViewById(R.id.buttonCreateAccount);

        buttonLogin.setOnClickListener(this);
        buttonCreateAcc.setOnClickListener(this);

        emailId = findViewById(R.id.editTextEmail);
        passId = findViewById(R.id.editTextLast);

        mAuth = FirebaseAuth.getInstance();

    }

    private void init(){
        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
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
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    // Overriding onClick to handle buttons clicked
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.buttonLogin:
                loginUser();
                break;
            case R.id.buttonCreateAccount:
                registerUser();
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
                                identifyUser();
                            }
                            else {
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
        Intent createAccount = new Intent(this, Main2Activity.class);
        startActivity(createAccount);
    }

    // After login, identify whether user is victim or EMT and act accordingly
    public void identifyUser() {
        //ValueEventListener triggered every time data in database changes
        ValueEventListener changeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseUser user = mAuth.getCurrentUser();
                String userID = user.getUid();
                //Update user class with modified data
                readData(dataSnapshot, userID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        mDatabase = FirebaseDatabase.getInstance();
        dbRef = mDatabase.getReference();
        dbRef.addValueEventListener(changeListener);

        // Start GUI activity depending on who user is
        if (isVictim) {
            Intent victimGui = new Intent(this, VictimGui.class);
            startActivity(victimGui);
        }
        else {
            Intent emtGui = new Intent(this, EmtGui.class);
            startActivity(emtGui);
        }

    }
    // Reads data into user class
    public void readData(DataSnapshot ds, String userID) {
        if (ds.hasChild(EMT_PATH  + userID)) {
            EmtUser user = new EmtUser();
            user.setFirstName(ds.child(EMT_PATH + userID).getValue(VictimUser.class).getFirstName());
            user.setLastName(ds.child(EMT_PATH + userID).getValue(VictimUser.class).getLastName());
            isVictim = false;
        }
        else if (ds.hasChild(VICTIM_PATH  + userID)) {
            VictimUser user = new VictimUser();
            user.setFirstName(ds.child(VICTIM_PATH + userID).getValue(VictimUser.class).getFirstName());
            user.setLastName(ds.child(VICTIM_PATH + userID).getValue(VictimUser.class).getLastName());
            user.setCity(ds.child(VICTIM_PATH + userID).getValue(VictimUser.class).getCity());
            user.setState(ds.child(VICTIM_PATH + userID).getValue(VictimUser.class).getState());
            user.setAddress(ds.child(VICTIM_PATH + userID).getValue(VictimUser.class).getAddress());
            isVictim = true;
        }
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}


