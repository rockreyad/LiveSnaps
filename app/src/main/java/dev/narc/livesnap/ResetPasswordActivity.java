package dev.narc.livesnap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

   private Button logScreenbtn,sentBtn;
    private EditText txtemail;
    private String email;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        logScreenbtn = findViewById(R.id.logScreenbtn);
        sentBtn = findViewById(R.id.sentBtn);

        txtemail = findViewById(R.id.textEmailAddress);

        mAuth = FirebaseAuth.getInstance();

        sentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        logScreenbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPasswordActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void validateData() {
        email = txtemail.getText().toString().trim();
        if(!email.isEmpty())
        {
            forgetPassword();
        }else {
            txtemail.setError("Please enter registered email !!");
        }
    }

    private void forgetPassword() {
        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ResetPasswordActivity.this, "Reset password link has been sent, please check your email !", Toast.LENGTH_SHORT).show();
                        updateUI();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ResetPasswordActivity.this, "Error :"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        Intent intent = new Intent(ResetPasswordActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}