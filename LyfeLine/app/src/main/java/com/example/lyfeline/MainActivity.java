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

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button buttonLogin, buttonCreateAcc;
    EditText emailId, passId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonCreateAcc = (Button) findViewById(R.id.buttonCreateAccount);

        buttonLogin.setOnClickListener(this);
        buttonCreateAcc.setOnClickListener(this);

        emailId = (EditText) findViewById(R.id.editTextEmail);
        passId = (EditText) findViewById(R.id.editTextLast);

        mAuth = FirebaseAuth.getInstance();

    }

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
    public void loginUser() {
        String email = emailId.getText().toString();
        String password = passId.getText().toString();
        if ( !email.matches("") && !password.matches("") ) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                String userID = user.getUid();

                            } else {
                                Toast.makeText(getApplicationContext(), "Please Try Again",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else {
            Toast.makeText(this, "Please Complete All Fields", Toast.LENGTH_LONG).show();
        }
    }

    public void registerUser() {
        Intent createAccount = new Intent(this, Main2Activity.class);
        startActivity(createAccount);
    }
}


