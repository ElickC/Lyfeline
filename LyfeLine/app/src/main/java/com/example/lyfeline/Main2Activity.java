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

public class Main2Activity extends AppCompatActivity {
    EditText emailId, passId, firstName, lastName;
    Button registerAccount;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        emailId = (EditText) findViewById(R.id.editTextEmail);
        passId = (EditText) findViewById(R.id.editTextPass);
        firstName = (EditText) findViewById(R.id.editTextFirst);
        lastName = (EditText) findViewById(R.id.editTextLast);
        registerAccount = (Button) findViewById(R.id.buttonRegister);
        mAuth = FirebaseAuth.getInstance();

        registerAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == registerAccount) {
                    registerUser();
                }
            }
        });
    }
    public void registerUser() {
        String email = emailId.getText().toString();
        String password = passId.getText().toString();
        String firstN = firstName.getText().toString();
        String lastN = lastName.getText().toString();

        if ( email.matches("") ) {
            Toast.makeText(this, "Email field is empty", Toast.LENGTH_LONG).show();
        }

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
