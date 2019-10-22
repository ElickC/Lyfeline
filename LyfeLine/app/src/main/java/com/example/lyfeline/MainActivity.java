package com.example.lyfeline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    Button buttonLogin, buttonCreateAcc;
    EditText emailId, passId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonCreateAcc = (Button) findViewById(R.id.buttonCreateAccount);
        emailId = (EditText) findViewById(R.id.editTextEmail);
        passId = (EditText) findViewById(R.id.editTextLast);
        mAuth = FirebaseAuth.getInstance();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( v == buttonLogin ) {
                    loginUser();
                }
            }
        });

        buttonCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( v == buttonCreateAcc ) {
                    registerUser();
                }
            }
        });
    }

    public void loginUser() {
        String email = emailId.getText().toString();
        if ( email.matches("") ) {
            Toast.makeText(this, "Email field is empty", Toast.LENGTH_LONG).show();
        }
        String password = passId.getText().toString();
        if ( password.matches("") ) {
            Toast.makeText(this, "Password field is empty", Toast.LENGTH_LONG).show();
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if ( task.isSuccessful() ) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(getApplicationContext(), "login successful", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Couldn't login", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void registerUser() {

        Intent registerAccount = new Intent(this, Main2Activity.class);
        startActivity(registerAccount);

        String email = emailId.getText().toString();
        if ( email.matches("") ) {
            Toast.makeText(this, "Email field is empty", Toast.LENGTH_LONG).show();
        }
        String password = passId.getText().toString();
        if ( password.matches("") ) {
            Toast.makeText(this, "Password field is empty", Toast.LENGTH_LONG).show();
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if ( task.isSuccessful() ) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(getApplicationContext(), "account registry complete", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Couldn't create account", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}


