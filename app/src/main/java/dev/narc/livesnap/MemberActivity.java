package dev.narc.livesnap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class MemberActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    private DatabaseReference mRef;

    dev.narc.livesnap.TheGlowingLoader loaderAnimation;

    Button backBtn, getPack, licenseKeyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        backBtn = findViewById(R.id.backBtn);
        licenseKeyBtn = findViewById(R.id.licenseKeyBtn);
        getPack = findViewById(R.id.getPack);

        loaderAnimation = findViewById(R.id.loader);
        mAuth = FirebaseAuth.getInstance();

        licenseKeyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                licenseKeyCheck();
            }
        });
        getPack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MemberActivity.this, SupportActivity.class));
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MemberActivity.this, VerifyCheck.class));
            }
        });
    }

    private void licenseKeyCheck() {

        LicenseDialog alert = new LicenseDialog();
        alert.LicenseDialog(this, "Enter your license key");

        firebaseUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference("Users");


        Query query = mRef.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Check untill required data get
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    //get verify data
                    String getVerify = "" + dataSnapshot.child("Verify").getValue();
                    String approved = "true";

                    //Update the UI for verified user
                    if (approved.equals(getVerify)) {
                        loaderAnimation.setVisibility(View.VISIBLE);
                        updateUi();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateUi() {

        Toast.makeText(this, "Your account is activated with license", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent mainIntent = new Intent(MemberActivity.this, MainActivity.class);
                MemberActivity.this.startActivity(mainIntent);
                loaderAnimation.setVisibility(View.GONE);
                MemberActivity.this.finish();
            }
        }, 4 * 1000);

    }
}
