package dev.narc.livesnap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference mRef;
    Button profileUpdate;
    private EditText userName, userAddress, userPhone;
    dev.narc.livesnap.TheGlowingLoader loaderAnimation;
    //Global Varibales to hold user data inside this activity
    String _FULNAME, _ADDRESS, _PHONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profileUpdate = findViewById(R.id.profileUpdate);

        userName = findViewById(R.id.userFullName);
        userAddress = findViewById(R.id.userAdd);
        userPhone = findViewById(R.id.userPhn);
        loaderAnimation = findViewById(R.id.loader);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference("Users");
        firebaseUser = mAuth.getCurrentUser();


        profileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserData();
            }
        });
    }

    public void sendUserData() {

        String name = userName.getText().toString().trim();
        String address = userAddress.getText().toString().trim();
        String phone = userPhone.getText().toString().trim();

        if(!name.isEmpty() && !address.isEmpty() && !phone.isEmpty())
        {
            loaderAnimation.setVisibility(View.VISIBLE);
            HashMap<String, Object> result = new HashMap<>();
            result.put("name",name);
            result.put("Address",address);
            result.put("Phone",phone);
            mRef.child(firebaseUser.getUid()).updateChildren(result)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditProfileActivity.this, "Data has been updated !", Toast.LENGTH_SHORT).show();
                            //Take user to set snapuser Name
                            updateUi();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }else if(name.isEmpty())
        {
            userName.setError("Please enter your full name !");
        }else if(address.isEmpty())
        {
            userAddress.setError("Please enter your address !");
        }else {
            userPhone.setError("Phone number can't be empty !");
        }

    }


    private void updateUi() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //TODO Change To SnapUserName
                final Intent mainIntent = new Intent(EditProfileActivity.this, MainActivity.class);
                EditProfileActivity.this.startActivity(mainIntent);
                loaderAnimation.setVisibility(View.GONE);
                EditProfileActivity.this.finish();
            }
        }, 3 * 1000);

    }
}