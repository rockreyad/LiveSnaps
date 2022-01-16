package dev.narc.livesnap;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {


    String tempDeviceId;
    String deviceName;
    Button signbtn, signInBtn;
    TextView forgotpassword;
    EditText txtemail, txtpassword;

    dev.narc.livesnap.TheGlowingLoader loaderAnimation;

    private FirebaseAuthManager firebaseAuthManager;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    private DatabaseReference mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        tempDeviceId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        deviceName = Build.MANUFACTURER + " - " + Build.MODEL;

        signbtn = findViewById(R.id.signbtn);
        signInBtn = findViewById(R.id.signInbtn);

        txtemail = findViewById(R.id.textEmailAddress);
        txtpassword = findViewById(R.id.textPassword);
        forgotpassword = findViewById(R.id.forgotpassword);

        loaderAnimation = findViewById(R.id.loader);

        firebaseAuthManager = new FirebaseAuthManager();
        mAuth = FirebaseAuth.getInstance();


        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        signbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignActivity.class);
                startActivity(intent);
            }
        });
    }


    private void loginUser() {
        String email = txtemail.getText().toString().trim();
        String password = txtpassword.getText().toString().trim();

        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (!password.isEmpty()) {

                //TODO working with this
                loaderAnimation.setVisibility(View.VISIBLE);
                firebaseAuthManager.signInWithEmailAndPassword(email, password, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //Toast.makeText(LoginActivity.this, "Login Successfully!", Toast.LENGTH_SHORT).show();
                            updateUI();
                        } else {

                            Log.e("ERROR", task.getException().toString());
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            loaderAnimation.setVisibility(View.GONE);
                        }
                    }
                });
            } else {
                txtpassword.setError("Empty fields are not allowed");
            }
        } else if (email.isEmpty()) {
            txtemail.setError("Empty fields are not allowed");
        } else {
            txtemail.setError("Please enter Correct Email");
        }

    }

    private void updateUI() {
        firebaseUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference("Users");


        Query query = mRef.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Check untill required data get
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    //get verify data
                    String originalDeviceId = "" + dataSnapshot.child("DeviceId").getValue();
                    String loginDeviceId = "" + dataSnapshot.child("LoginDeviceId").getValue();

//                    String getVerify = "" + dataSnapshot.child("Verify").getValue();
//                    String approved = "true";

                    //Update the UI for verified user
//                    if (!approved.equals(getVerify)) {
//                        startActivity(new Intent(LoginActivity.this, VerifyCheck.class));
//                    }

                    //Set For Multiple Device
                    if (tempDeviceId.equals(originalDeviceId)) {
                        if (tempDeviceId.equals(loginDeviceId)) {
                            ToMainActivity();
                        } else {
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("LoginDeviceId", tempDeviceId);
                            result.put("DeviceName",deviceName);
                            mRef.child(firebaseUser.getUid()).updateChildren(result);
                            ToMainActivity();
                        }
                    } else {
                        if (!loginDeviceId.equals(originalDeviceId)) {
                            if (tempDeviceId.equals(loginDeviceId)) {
                                ToMainActivity();
                            }else
                            {
                                    HashMap<String, Object> result = new HashMap<>();
                                    result.put("LoginDeviceId", tempDeviceId);
                                    result.put("DeviceName",deviceName);
                                    // mRef.child(firebaseUser.getUid()).updateChildren(result).isSuccessful();
                                    mRef.child(firebaseUser.getUid()).updateChildren(result);
                                    ToMainActivity();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Registered Device is Logged in !", Toast.LENGTH_SHORT).show();
                            loaderAnimation.setVisibility(View.GONE);
                            startActivity(new Intent(LoginActivity.this, FraudActivity.class));
                        }
                    }

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ToMainActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(mainIntent);
                loaderAnimation.setVisibility(View.GONE);
                LoginActivity.this.finish();
            }
        }, 4 * 1000);

    }
}