package dev.narc.livesnap;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    Button backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutActivity.this,MainActivity.class));
            }
        });
    }

    public void credits(View view) {
        CreditsDialog alert = new CreditsDialog(this,"Contribution");


    }

    public static Intent getOpenFacebookIntent(Context context) {

        try {
            context.getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("fb://profile/100005426718334")); //Trys to make intent with FB's URI
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/rockreyad.bd")); //catches and opens a url to the desired page
        }
    }

    public void facebookMyProfile(View view) {
        Intent facebookIntent = getOpenFacebookIntent(this);
        startActivity(facebookIntent);
    }

    public void instaMyProfile(View view) {

        Uri uri = Uri.parse("http://instagram.com/_u/rockreyad");
        Intent i= new Intent(Intent.ACTION_VIEW,uri);
        i.setPackage("com.instagram.android");
        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {

            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/rockreyad")));
        }
    }

    public void livesnapsWeb(View view) {

        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://narc.dev/livesnap/")));
    }
}