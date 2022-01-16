package dev.narc.livesnap;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    Button signbtn,createAccount;
    EditText txtemail,txtpassword,txtrepassword;
    CheckBox checkBox;
    TextView checkBoxTv;
    dev.narc.livesnap.TheGlowingLoader loaderAnimation;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        signbtn = findViewById(R.id.signbtn);
        txtemail = findViewById(R.id.textEmailAddress);
        txtpassword = findViewById(R.id.textPassword);
        txtrepassword = findViewById(R.id.textRepeatPassword);
        createAccount = findViewById(R.id.createaccount);
        checkBox = findViewById(R.id.checkbox);
        checkBoxTv = findViewById(R.id.checkboxTv);
        loaderAnimation = findViewById(R.id.loader);

        mAuth = FirebaseAuth.getInstance();


        signbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,SignActivity.class);
                startActivity(intent);
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = txtemail.getText().toString().trim();
                String password = txtpassword.getText().toString().trim();
                String confrimPassword = txtrepassword.getText().toString().trim();


                if(TextUtils.isEmpty(email))
                {
                txtemail.setError("Email must be filled out!");
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    txtpassword.setError("Password is Required!");
                    return;
                }
                if(TextUtils.isEmpty(confrimPassword))
                {
                    txtrepassword.setError("This field can't empty!");
                    return;
                }
                if(password.length() <6)
                {
                    txtpassword.setError("Password must be more than 6 Characters");
                    return;
                }
                if(!(password.equals(confrimPassword)))
                {
                    txtrepassword.setError("Password doesn't match!");
                    return;
                }
                if(checkBox.isChecked())
                {
                    if(password.equals(confrimPassword))
                    {
                        loaderAnimation.setVisibility(View.VISIBLE);
                        createAccount(email,password);
                    }
                }else
                {
                    checkBoxTv.setText("Must have to agree to create account");
                }

            }
        });
    }

    private void createAccount(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    //Progressbar view Gone
                    loaderAnimation.setVisibility(View.GONE);
                    addUserToDatabase();

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // If sign in fails, display a message to the user.
                Toast.makeText(RegisterActivity.this, "Registration Error !!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addUserToDatabase() {

        firebaseUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        String userEmail = firebaseUser.getEmail();
        String userID = firebaseUser.getUid();
        String deviceName = Build.MANUFACTURER + " - " + Build.MODEL;

        LicenseKey licenseKey = new LicenseKey();
        String userKey = licenseKey.generate();
        String points = "50";

        String deviceid = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        //path to store user data In database under Users
        mRef = database.getReference("Users");
        //When user is registered store user info in firebase realtime database too.
        //using HashMap
        HashMap<Object, String> hashMap = new HashMap<>();

        //Put info hasMap
        hashMap.put("email", userEmail);
        hashMap.put("UserId", userID);
        hashMap.put("License", userKey);
        hashMap.put("DeviceId",deviceid);
        hashMap.put("DeviceName",deviceName);
        hashMap.put("LoginDeviceId",deviceid); //Login to Get this data
        hashMap.put("Verify","false"); // Default is not approved
        hashMap.put("Plan", "Trial"); //can edit from database admin
        hashMap.put("name", ""); // add Later is UpdateProfile
        hashMap.put("Address", ""); // add later in UpdateProfile
        hashMap.put("Phone", ""); // add later in UpdateProfile
        hashMap.put("points", points); // add later in UpdateProfile
        hashMap.put("uselivesnap", ""); // add later in UpdateProfile
        hashMap.put("currentSCUname", ""); // add later in UpdateProfile
        hashMap.put("previousSCUname", "null"); // add later in UpdateProfile
        hashMap.put("snapkit", "Fadyhotels"); // add later in UpdateProfile
        //put data within hashMap is database
        mRef.child(userID).setValue(hashMap);


        // Sign in success, update UI with the signed-in user's information
        Toast.makeText(RegisterActivity.this, "Registered Successfully !!", Toast.LENGTH_SHORT).show();
        updateUI();
        finish();
    }

    private void getVerifyCheck() {

    }

    private void updateUI() {
        Intent intent = new Intent(RegisterActivity.this,EditProfileActivity.class);
        startActivity(intent);
    }

    public void privacyPolicy(View view) {
        startActivity(new Intent(this,PrivacyPolicy.class));
    }
}