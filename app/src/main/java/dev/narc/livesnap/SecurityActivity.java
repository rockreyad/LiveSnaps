package dev.narc.livesnap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SecurityActivity extends AppCompatActivity {

    private EditText cpass,npass,crnpass;
    private Button backBtn,passUpdate;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        cpass = findViewById(R.id.crntPass);
        npass = findViewById(R.id.newPass);
        crnpass = findViewById(R.id.confrimPass);

        backBtn = findViewById(R.id.backBtn);
        passUpdate = findViewById(R.id.passUpdateBtn);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        passUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateUserSecurity();

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SecurityActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void validateUserSecurity() {

        String oldPassword = cpass.getText().toString().trim();
        String newPassword = npass.getText().toString().trim();
        String confrimPassword = crnpass.getText().toString().trim();

        //Validate userinfo
        if(!oldPassword.isEmpty())
        {
            if(!newPassword.isEmpty() && !oldPassword.equals(newPassword))
            {
                if(!confrimPassword.isEmpty() && newPassword.equals(confrimPassword))
                {
                    updatePassword(oldPassword,newPassword);
                }
                else if(confrimPassword.isEmpty())
                {
                    crnpass.setError("Enter Confrim Password !");
                }
                else{
                    crnpass.setError("Password doesn't match !");
                }
            }
            else if(newPassword.isEmpty())
            {
                npass.setError("Enter New Password");
            }
            else if(newPassword.length()<6)
            {
                npass.setError("Password must be more than 6 character !");
            }

        }else{
            cpass.setError("Enter Current Password !");
        }


    }

    private void updatePassword(String oldPassword, String newPassword) {

        AuthCredential authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(),oldPassword);
        firebaseUser.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Successfull authenticated , begin update
                        firebaseUser.updatePassword(newPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(SecurityActivity.this, "Password has been Updated..", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //failled updating password
                                        Toast.makeText(SecurityActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Authentication Failled, show reason
                        Toast.makeText(SecurityActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}