package dev.narc.livesnap;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.snapchat.kit.sdk.SnapCreative;
import com.snapchat.kit.sdk.creative.api.SnapCreativeKitApi;
import com.snapchat.kit.sdk.creative.exceptions.SnapMediaSizeException;
import com.snapchat.kit.sdk.creative.exceptions.SnapVideoLengthException;
import com.snapchat.kit.sdk.creative.media.SnapMediaFactory;
import com.snapchat.kit.sdk.creative.models.SnapContent;
import com.snapchat.kit.sdk.creative.models.SnapLiveCameraContent;
import com.snapchat.kit.sdk.creative.models.SnapPhotoContent;
import com.snapchat.kit.sdk.creative.models.SnapVideoContent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class SnapActivity extends AppCompatActivity {

    String tempDeviceId;


    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    private BroadcastReceiver MyReceiver = null;

    Button backBtn, clearSnapBtn, share_button;
    ImageView snap_container;


    public enum SnapState {
        NO_SNAP("Clear Snap"),
        IMAGE("Select Image"),
        VIDEO("Select Video");

        private String mOptionText;

        SnapState(String optionText) {
            mOptionText = optionText;
        }

        public String getOptionText() {
            return mOptionText;
        }

        public int getRequestCode() {
            return ordinal();
        }
    }

    private static final String SNAP_NAME = "snap";

    private SnapState mSnapState = SnapState.NO_SNAP;
    private File mSnapFile;

    private ImageView mPreviewImage;
    private VideoView mPreviewVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap);


        MyReceiver = new MyReceiver();
        broadcastIntent();

        tempDeviceId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);


        snap_container = findViewById(R.id.snap_container);
        share_button = findViewById(R.id.share_button);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();


        final SnapCreativeKitApi snapCreativeKitApi = SnapCreative.getApi(this);
        final SnapMediaFactory snapMediaFactory = SnapCreative.getMediaFactory(this);

        mSnapFile = new File(getCacheDir(), SNAP_NAME);

        mPreviewImage = findViewById(R.id.image_preview);
        mPreviewVideo = findViewById(R.id.video_preview);

        mPreviewVideo.setOnPreparedListener(mp -> mp.setLooping(true));

        snap_container.setOnClickListener(view -> openMediaSelectDialog());

        share_button.setOnClickListener(view -> {
            try {

                final SnapContent content;
                switch (mSnapState) {
                    case IMAGE:
                        //pointsCheck(1,0,0);
                        content = new SnapPhotoContent(snapMediaFactory.getSnapPhotoFromFile(mSnapFile));
                        break;
                    case VIDEO:
                        //  pointsCheck(0,3,0);
                        content = new SnapVideoContent(snapMediaFactory.getSnapVideoFromFile(mSnapFile));
                        break;
                    case NO_SNAP:
                    default:
                        content = new SnapLiveCameraContent();
                }
                snapCreativeKitApi.send(content);
            } catch (SnapMediaSizeException | SnapVideoLengthException e) {
                Toast.makeText(view.getContext(), "Media too large to share", Toast.LENGTH_SHORT).show();
            }
        });

        clearSnapBtn = findViewById(R.id.clearSnapBtn);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(SnapActivity.this, MainActivity.class);
            startActivity(intent);
        });

        clearSnapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
    }

    public void broadcastIntent() {
        registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(MyReceiver);
//        validationUser();
//    }

    @Override
    protected void onStart() {

        super.onStart();
        //Check there is user or not
        validationUser();
    }

    private void validationUser() {


        if (firebaseUser != null) {

            //TODO Progress Dialog
            getVerifyCheck();

        } else {
            toSignin();
        }

    }

    private void toSignin() {
        startActivity(new Intent(SnapActivity.this, LoginActivity.class));
    }

    private void pointsCheck(int pointsImage,int pointsVideo,int pointsUName)
    {
        int totalCostPoints = pointsImage+pointsVideo+pointsUName;
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference("Users");

        Query query = mRef.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Check untill required data get
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    String getPoints = "" + dataSnapshot.child("points").getValue();
                    String getPlan = "" + dataSnapshot.child("plan").getValue();
                    String getUsages = "" + dataSnapshot.child("uselivesnap").getValue();
                    int uselivesnap = Integer.parseInt(getUsages);
                    int points = Integer.parseInt(getPoints);
                    uselivesnap++;


                    String monthly = "Monthly", lifetime = "Lifetime";
                    if(!(getPlan.equals(monthly) || getPlan.equals(lifetime)))
                    {
                        int sendPoints = points - totalCostPoints;
                        points = sendPoints;
                    }

                    HashMap<String, Object> result = new HashMap<>();
                    result.put("points", points);
                    result.put("uselivesnap", uselivesnap);
                    mRef.child(firebaseUser.getUid()).updateChildren(result);

                    //Update the UI for verified user

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SnapActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getVerifyCheck() {
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

                    String getVerify = "" + dataSnapshot.child("Verify").getValue();
                    String approved = "true";
                    String getPoints = "" + dataSnapshot.child("points").getValue();
                    int points = Integer.parseInt(getPoints);

                    if(points ==0 && points <1)
                    {
                        share_button.setVisibility(View.GONE);
                        priceScreen();
                    }

                    //Update the UI for verified user

                    if (!approved.equals(getVerify)) {
                        startActivity(new Intent(SnapActivity.this, VerifyCheck.class));
                    } else if (!tempDeviceId.equals(loginDeviceId)) {
                        startActivity(new Intent(SnapActivity.this, FraudActivity.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SnapActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void priceScreen()
    {
        startActivity(new Intent(SnapActivity.this, PriceScreen.class));
    }
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        if (mSnapState == SnapState.VIDEO) {
//            mPreviewVideo.start();
//        }
//    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, @Nullable Intent intent) {
        if (requestCode == SnapState.IMAGE.getRequestCode()) {
            handleImageSelect(intent);
        } else if (requestCode == SnapState.VIDEO.getRequestCode()) {
            handleVideoSelect(intent);
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    private void openMediaSelectDialog() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item);

        for (SnapState state : SnapState.values()) {
            adapter.add(state.getOptionText());
        }


        new AlertDialog.Builder(this)
                .setAdapter(adapter, (dialog, which) -> {
                    switch (SnapState.values()[which]) {
                        case IMAGE:
                            selectMediaFromGallery("image/*", SnapState.IMAGE.getRequestCode());
                            break;
                        case VIDEO:
                            selectMediaFromGallery("video/*", SnapState.VIDEO.getRequestCode());
                            break;
                        case NO_SNAP:
                            reset();
                    }
                })
                .show();

    }

    private void selectMediaFromGallery(String mimeType, int resultCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mimeType);

        startActivityForResult(intent, resultCode);
    }

    private void handleImageSelect(@Nullable Intent intent) {
        if (saveContentLocally(intent)) {
            reset();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(mSnapFile));
                mPreviewImage.setImageBitmap(bitmap);
                mSnapState = SnapState.IMAGE;
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("Saved the image file, but it doesn't exist!");
            }
        }
    }

    private void handleVideoSelect(@Nullable Intent intent) {
        if (saveContentLocally(intent)) {
            reset();
            mPreviewVideo.setVideoURI(Uri.fromFile(mSnapFile));
            mPreviewVideo.start();
            mPreviewVideo.setVisibility(View.VISIBLE);
            mSnapState = SnapState.VIDEO;
        }
    }

    private void reset() {
        mPreviewImage.setImageDrawable(null);
        mPreviewVideo.setVisibility(View.GONE);
        mPreviewVideo.setVideoURI(null);
        mSnapState = SnapState.NO_SNAP;
    }

    /**
     * Saves the file from the ACTION_PICK Intent locally to {@link #mSnapFile} to be accessed by our FileProvider
     */
    private boolean saveContentLocally(@Nullable Intent intent) {
        if (intent == null || intent.getData() == null) {
            return false;
        }
        InputStream inputStream;

        try {
            inputStream = getContentResolver().openInputStream(intent.getData());
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Could not open file", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (inputStream == null) {
            Toast.makeText(this, "File does not exist", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            copyFile(inputStream, mSnapFile);
        } catch (IOException e) {
            Toast.makeText(this, "Failed save file locally", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private static void copyFile(InputStream inputStream, File file) throws IOException {
        byte[] buffer = new byte[1024];
        int length;

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        }
    }
}