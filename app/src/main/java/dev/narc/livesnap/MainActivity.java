package dev.narc.livesnap;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    Button signOutBtn;
    dev.narc.livesnap.TheGlowingLoader loaderAnimation;
    private TextView userName;
    private TextView userPoints;
    private LinearLayout pointsLayout;

    String tempDeviceId;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    private DatabaseReference mRef;

    private BroadcastReceiver MyReceiver = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyReceiver = new MyReceiver();
        broadcastIntent();

        tempDeviceId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);


        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference("Users");

        userName = findViewById(R.id.txtusername);
        signOutBtn = findViewById(R.id.signOutBtn);
        userPoints = findViewById(R.id.pointsResult);
        pointsLayout = findViewById(R.id.pointsContainer);

        loaderAnimation = findViewById(R.id.loader);

        //Check there is user or not
        validationUser();

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loaderAnimation.setVisibility(View.VISIBLE);
                mAuth.signOut();
                toLogin();
            }

        });

    }

    public void broadcastIntent() {
        registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }


    private void toLogin() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(mainIntent);
                loaderAnimation.setVisibility(View.GONE);
                MainActivity.this.finish();

            }
        }, 2 * 1000);

    }

    @Override
    protected void onStart() {

        super.onStart();
        //Check there is user or not
        validationUser();
    }
    @Override
    protected  void onResume() {
        super.onResume();
        //validationUser();
    }
    private void validationUser() {

        if (firebaseUser != null) {

            readUserData();
        } else if (firebaseUser == null) {

            //TODO Progress Dialog
            finish();
            toSign();
            return;
        }

    }

//    @Override
//    protected  void onPause()
//    {
//        super.onPause();
//        unregisterReceiver(MyReceiver);
//        //validationUser();
//    }


    private void readUserData() {
        Query query = mRef.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Check untill required data get
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //get data
                    String getName = "" + dataSnapshot.child("name").getValue();
                    String getPoints = "" + dataSnapshot.child("points").getValue();
                    String getPlan = ""+dataSnapshot.child("Plan").getValue();
                    String originalDeviceId = "" + dataSnapshot.child("DeviceId").getValue();
                    String loginDeviceId = "" + dataSnapshot.child("LoginDeviceId").getValue();
                    int points = Integer.parseInt(getPoints);

                    String monthly = "Monthly", lifetime="Lifetime";
                    //Set Data
                    userName.setText(getName);
                    userPoints.setText(getPoints);

                    if(getPlan.equals(monthly))
                    {
                        userPoints.setText(monthly);
                    }else if(getPlan.equals(lifetime))
                    {
                        userPoints.setText(lifetime);
                    }
                    if(points==0)
                    {
                        if(!(getPlan.equals(monthly) || getPlan.equals(lifetime)))
                        {
                            Integer.toString(points);
                            String buy = "BUY";
                            pointsLayout.setBackgroundResource(R.drawable.background_card);
                            userPoints.setText(buy);
                            userPoints.setTextColor(Color.WHITE);
                        }
                    }

                    //Check Multiple Device Capture
                    if (tempDeviceId.equals(originalDeviceId)) {
                        if (!(tempDeviceId.equals(loginDeviceId))) {
                            loaderAnimation.setVisibility(View.VISIBLE);
                            HashMap<String, Object> result = new HashMap<>();
                            result.put("LoginDeviceId", tempDeviceId);
                            //mRef.child(firebaseUser.getUid()).updateChildren(result).isSuccessful();
                            mRef.child(firebaseUser.getUid()).updateChildren(result);
                            loaderAnimation.setVisibility(View.GONE);
                        }
                    } else { if (loginDeviceId.equals(originalDeviceId) && !(tempDeviceId.equals(loginDeviceId))) {
                            Toast.makeText(MainActivity.this, "Registered Device is Logged in !", Toast.LENGTH_SHORT).show();
                            toFraudActivity();
                            //startActivity(new Intent(MainActivity.this, FraudActivity.class));
                        }else if (!(tempDeviceId.equals(loginDeviceId))) {
                            Toast.makeText(MainActivity.this, "DeviceId has been Changed !", Toast.LENGTH_SHORT).show();
                            toFraudActivity();
                            //startActivity(new Intent(MainActivity.this, FraudActivity.class));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void toFraudActivity() {
        loaderAnimation.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent mainIntent = new Intent(MainActivity.this, FraudActivity.class);
                MainActivity.this.startActivity(mainIntent);
                loaderAnimation.setVisibility(View.GONE);
                MainActivity.this.finish();
            }
        }, 5 * 1000);
    }


    private void toSign() {
        //Change the UI
        startActivity(new Intent(MainActivity.this, SignActivity.class));
    }

    public void snaptool(View view) {
        Intent intent = new Intent(MainActivity.this, SnapActivity.class);
        startActivity(intent);
    }

    public void profileBtn(View view) {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    public void updateProfileBtn(View view) {
        Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
        startActivity(intent);
    }

    public void passupdate(View view) {
        Intent intent = new Intent(MainActivity.this, SecurityActivity.class);
        startActivity(intent);
    }

    public void support(View view) {
        Intent intent = new Intent(MainActivity.this, SupportActivity.class);
        startActivity(intent);
    }

    public void unLinkDevice(View view) {
        Intent intent = new Intent(MainActivity.this, UnlinkDevice.class);
        startActivity(intent);
    }

    public void aboutApp(View view) {

        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    public void newFeatures(View view) {

        Toast.makeText(this, "SnapChat Anitban is Coming Soon...", Toast.LENGTH_SHORT).show();
    }

    public void managerAccess(View view) {

        Intent intent = new Intent(MainActivity.this, ManagerActivity.class);
        startActivity(intent);
    }

    public void snapUser(View view) {
        Intent intent = new Intent(MainActivity.this, SnapUserName.class);
        startActivity(intent);
    }

    public void pointsBuy(View view) {
        Intent intent = new Intent(MainActivity.this, PriceScreen.class);
        startActivity(intent);
    }

}