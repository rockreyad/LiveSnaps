package dev.narc.livesnap;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SupportActivity extends AppCompatActivity {

    Button backBtn,livechatBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        backBtn = findViewById(R.id.backBtn);
        livechatBtn = findViewById(R.id.livechatBtn);

        livechatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLivechatBtn();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setLivechatBtn() {
        boolean installed = appInstalledOrNot("com.whatsapp") || appInstalledOrNot("com.aero");
        if (installed){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=\"+\"+8801784061111"));
            startActivity(intent);
        }else {
            Toast.makeText(SupportActivity.this, "Whats app not installed on your device", Toast.LENGTH_SHORT).show();
        }
    }

    //Create method appInstalledOrNot
    private boolean appInstalledOrNot(String url){
        PackageManager packageManager =getPackageManager();
        boolean app_installed;
        try {
            packageManager.getPackageInfo(url,PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }catch (PackageManager.NameNotFoundException e){
            app_installed = false;
        }
        return app_installed;
    }

    public void openEmail(View view) {
startActivity(new Intent(SupportActivity.this,SendUsEmail.class));
    }

}