package dev.narc.livesnap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class VerifyCheck extends AppCompatActivity {

    Button support;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_check);

        support = findViewById(R.id.goSupportBtn);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference("Users");

        validationUser();
        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VerifyCheck.this,SupportActivity.class));
            }
        });

    }

    @Override
    protected void onStart() {

        super.onStart();
        //Check there is user or not
        validationUser();
    }
    @Override
    protected  void onResume() {
        super.onResume();
        //validationUser();
    }
    private void validationUser() {

        if (firebaseUser != null) {

            readUserData();
        } else if (firebaseUser == null) {

            //TODO Progress Dialog
            finish();
            toSign();
            return;
        }

    }

    private void readUserData() {
        Query query = mRef.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Check untill required data get
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //get data
                    String getVerify = "" + dataSnapshot.child("Verify").getValue();

                    //Set Data
                    if(!getVerify.equals("review"))
                    {
                        //take to dashboard
                        startActivity(new Intent(VerifyCheck.this, MainActivity.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void toSign() {
        //Change the UI
        startActivity(new Intent(VerifyCheck.this, SignActivity.class));
    }

}