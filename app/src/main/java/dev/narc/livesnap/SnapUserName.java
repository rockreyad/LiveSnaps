package dev.narc.livesnap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;

public class SnapUserName extends AppCompatActivity {


    private EditText snapName;
    private TextView crntSnpUsrTv;
    private LinearLayout authorizeLayout;
    Button confirmBtn;
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    dev.narc.livesnap.TheGlowingLoader loaderAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.snap_user_name);


        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference("Users");

        snapName = findViewById(R.id.snapUserName);
        confirmBtn = findViewById(R.id.confirm);
        crntSnpUsrTv = findViewById(R.id.crntSnpUsrTv);
        authorizeLayout = findViewById(R.id.authorizeLayout);
        loaderAnimation = findViewById(R.id.loader);


        //getUserData();
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeSnapUserName();
            }
        });

    }


    private void getUserData()
    {
        Query query = mRef.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Check untill required data get
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    //get verify data

                    String status = "" + dataSnapshot.child("Verify").getValue();
                    String previousSnap = "" + dataSnapshot.child("previousSCUname").getValue();
                    String  currentSnap = "" + dataSnapshot.child("currentSCUname").getValue();
                    String  getPoints = "" + dataSnapshot.child("points").getValue();
                    String getPlan = "" + dataSnapshot.child("plan").getValue();
                    String getUsages = "" + dataSnapshot.child("uselivesnap").getValue();
//                    int uselivesnap = Integer.parseInt(getUsages);
//                    int points = Integer.parseInt(getPoints);

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


    private void changeSnapUserName() {

        Query query = mRef.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Check untill required data get
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //get data
//                    String status = "" + dataSnapshot.child("Verify").getValue();
                    String previousSnap = "" + dataSnapshot.child("previousSCUname").getValue();
                    String  currentSnap = "" + dataSnapshot.child("currentSCUname").getValue();
                    String  getPoints = "" + dataSnapshot.child("points").getValue();
                    String getPlan = "" + dataSnapshot.child("plan").getValue();
                    String getUsages = "" + dataSnapshot.child("uselivesnap").getValue();

//                   int uselivesnap = Integer.parseInt(getUsages);
//                   int points = Integer.parseInt(getPoints);

                    String monthly = "Monthly", lifetime="Lifetime";
                    //Set Data
                    String username = snapName.getText().toString().trim();
                    //Rules for the snapUserName
                    if(!username.isEmpty())
                    {
                        if(username.length()<=15)
                        {
                            if(!previousSnap.equals(currentSnap))
                            {
                                loaderAnimation.setVisibility(View.VISIBLE);
                                //Cost 10Coin per Username Authorization
                                if( !previousSnap.equals("null"))
                                {
                                    if(!(getPlan.equals(monthly) || getPlan.equals(lifetime)))
                                    {
                                      /*  int cost = points - 10;
                                        points = cost;
                                        HashMap<String, Object> result = new HashMap<>();
                                        result.put("points", points);
                                        mRef.child(firebaseUser.getUid()).updateChildren(result)
                                                .isSuccessful();*/
                                    }
                                }
                                //push data to Database
                                String review = "review";
//                                uselivesnap++;
                                HashMap<String, Object> result = new HashMap<>();
                                //result.put("uselivesnap", uselivesnap);
                                result.put("previousSCUname", currentSnap);
                                result.put("currentSCUname", username);
                                result.put("Verify", review);
                                mRef.child(firebaseUser.getUid()).updateChildren(result)
                                        .isSuccessful();
                               // waitingApprovalScreen();
                                        /*
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(SnapUserName.this, "Data has been updated !", Toast.LENGTH_SHORT).show();
                                                //Take user to set snapuser Name
                                                waitingApprovalScreen();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SnapUserName.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                         */
                            }
                            else
                            {
                                snapName.setError("Snap username already authorized");
                            }
                        }else
                        {
                            snapName.setError("Snap username must be less than 15 characters");
                        }
                    }else
                    {
                        snapName.setError("Snap username can't be empty");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void waitingApprovalScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent mainIntent = new Intent(SnapUserName.this, VerifyCheck.class);
                SnapUserName.this.startActivity(mainIntent);
                loaderAnimation.setVisibility(View.GONE);
                SnapUserName.this.finish();
            }
        }, 5 * 1000);

    }

}