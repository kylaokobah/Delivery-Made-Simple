package com.example.dmsimpledriver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class profile extends AppCompatActivity {
    Button logout, view_requests_bt;

    FirebaseAuth mAuth;
    FirebaseUser User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        logout = findViewById(R.id.logout_bt);
        view_requests_bt = findViewById(R.id.view_requests_bt);
        mAuth = FirebaseAuth.getInstance();

        User = mAuth.getCurrentUser();
        view_requests_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =  new Intent (getApplicationContext(), Dashboard.class);
                startActivity(i);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent i =  new Intent (getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
    }
}