package dev.narc.livesnap;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UserDetails extends AppCompatActivity {

    private TextView name;
    private TextView email;
    private TextView status;
    private TextView plan;
    private TextView license;

    DatabaseReference mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_detail);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        status = findViewById(R.id.status);
        plan = findViewById(R.id.plan);
        license = findViewById(R.id.license);

        Intent intent=getIntent();
        String key = intent.getStringExtra("key");
        mRef = FirebaseDatabase.getInstance().getReference("Users");
        Query query = mRef.orderByKey().equalTo(key);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for(DataSnapshot ds:snapshot.getChildren())
                    {
                        name.setText(ds.child("name").getValue(String.class));
                        email.setText(ds.child("email").getValue(String.class));
                        status.setText(ds.child("Verify").getValue(String.class));
                        plan.setText(ds.child("Plan").getValue(String.class));
                        license.setText(ds.child("License").getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query.addListenerForSingleValueEvent(eventListener);
    }
}