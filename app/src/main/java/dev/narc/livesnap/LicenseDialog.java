package dev.narc.livesnap;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LicenseDialog {

    String userLicense, licenseKey;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    private DatabaseReference mRef;


    public void LicenseDialog(Activity activity, String msg) {


        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialogbox_otp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        mAuth = FirebaseAuth.getInstance();

        TextView text = dialog.findViewById(R.id.txt_file_path);
        text.setText(msg);
        EditText license = dialog.findViewById(R.id.inputLicense);

        getServerLicense();

        Button dialogBtn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        dialogBtn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    Toast.makeText(getApplicationContext(),"Cancel" ,Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });


        Button dialogBtn_okay = (Button) dialog.findViewById(R.id.btn_okay);
        dialogBtn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                licenseKey = license.getText().toString().trim();

                if (!(licenseKey.isEmpty())) {
                    if (licenseKey.length() == 10) {
                        if (licenseKey.equals(userLicense)) {

                            //Activating the user with license
                            String approval = "true";
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("Verify", approval);
                            mRef.child(firebaseUser.getUid()).updateChildren(result)
                                    .isSuccessful();

                            dialog.cancel();
                        } else {
                            license.setError("License key not found !");
                        }

                    } else {
                        license.setError("License key is 10 characters !");
                    }
                } else {
                    license.setError("Please enter license !");
                }
            }
        });
        dialog.show();
    }

    private void getServerLicense() {

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
                    userLicense = "" + dataSnapshot.child("License").getValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


}
