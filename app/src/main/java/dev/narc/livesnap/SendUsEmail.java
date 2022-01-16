package dev.narc.livesnap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SendUsEmail extends AppCompatActivity {

    // define objects for edit text and button
    Button button,backBtn;
    EditText  sendto,subject, body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_us_email);
        // Getting instance of edittext and button
        sendto = findViewById(R.id.inputSend);
        subject = findViewById(R.id.inputSubject);
        body = findViewById(R.id.inputMessage);
        button = findViewById(R.id.sendBtn);
        backBtn = findViewById(R.id.backBtn);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // attach setOnClickListener to button
        // with Intent object define in it
        button.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view)
            {
                String emailsend = sendto.getText().toString();
                String emailsubject = subject.getText().toString();
                String emailbody = body.getText().toString();

                // define Intent object
                // with action attribute as ACTION_SEND
                Intent intent = new Intent(Intent.ACTION_SEND);

                // add three fiels to intent using putExtra function
                intent.putExtra(Intent.EXTRA_EMAIL,
                        new String[] { emailsend });
                intent.putExtra(Intent.EXTRA_SUBJECT, emailsubject);
                intent.putExtra(Intent.EXTRA_TEXT, emailbody);

                // set type of intent
                intent.setType("message/rfc822");

                // startActivity with intent with chooser
                // as Email client using createChooser function
                startActivity(
                        Intent
                                .createChooser(intent,
                                        "Choose an Email client :"));
            }
        });
    }
}