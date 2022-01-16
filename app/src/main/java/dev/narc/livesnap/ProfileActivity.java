package dev.narc.livesnap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class ProfileActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private LinearLayout upgrade;

    private TextView name,plan,email,address,phone,authorize,usages,manager;

    Button backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference("Users");

        name = findViewById(R.id.userName);
        plan = findViewById(R.id.userPlan);
        email = findViewById(R.id.userEmail);
        address = findViewById(R.id.userAddress);
        phone = findViewById(R.id.userPhone);
        authorize = findViewById(R.id.userAuthorize);
        usages = findViewById(R.id.uselivesnap);
        manager = findViewById(R.id.userManager);

        upgrade = findViewById(R.id.upgradeContainer);
        backBtn = findViewById(R.id.backBtn);

        readUserdata();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProfileActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void readUserdata() {

        Query query = mRef.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Check untill required data get
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                    //get data
                    String getName = ""+dataSnapshot.child("name").getValue();
                    String getEmail = ""+dataSnapshot.child("email").getValue();
                    String getPlan = ""+dataSnapshot.child("Plan").getValue();
                    String getAddress = ""+dataSnapshot.child("Address").getValue();
                    String getPhone = ""+dataSnapshot.child("Phone").getValue();
                    String getUsages = ""+dataSnapshot.child("uselivesnap").getValue();
                    String getAuthorize = ""+dataSnapshot.child("Verify").getValue();
                    String getManager = ""+dataSnapshot.child("snapkit").getValue();

                    String legend = "Lifetime",tempStatusapproved="Approved",tempStatusrevewing="Revewing",tempStatusdeclined="Declined";
                    if(getPlan.equals(legend))
                    {
                        upgrade.setVisibility(View.GONE);
                    }

                    //Set Data
                    name.setText(getName);
                    plan.setText(getPlan);
                    email.setText(getEmail);
                    address.setText(getAddress);
                    phone.setText(getPhone);
                    usages.setText(getUsages);

                    manager.setText(getManager);
                    if(getAuthorize.equals("true"))
                    {
                        authorize.setText(tempStatusapproved);
                    }else if(getAuthorize.equals("review"))
                    {
                        authorize.setText(tempStatusrevewing);
                    }
                    else if(getAuthorize.equals("false"))
                    {
                        authorize.setText(tempStatusdeclined);
                    }
                    try {

                    }catch(Exception e)
                    {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}