package dev.narc.livesnap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FraudActivity extends AppCompatActivity {


    Button signInBtn;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fraud);


        signInBtn = findViewById(R.id.signInbtn);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }


    @Override
    protected void onStart() {

        super.onStart();
        //Check there is user or not
        validationUser();
    }

    private void validationUser() {

        if (firebaseUser != null) {


        } else if (firebaseUser == null) {

            //TODO Progress Dialog
            finish();
            toSign();
            return;
        }

    }

    public final void signOut(){

        mAuth.signOut();
        toSign();
    }
    private void toSign() {
        //Change the UI
        startActivity(new Intent(FraudActivity.this, LoginActivity.class));
    }

}