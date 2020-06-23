package com.example.personalaccounting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FeedbackActivity extends AppCompatActivity {

    EditText subject;
    EditText feedback;
    Button submitButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        subject = findViewById(R.id.feedback_email_subject);
        feedback = findViewById(R.id.feedback_email_content);
        submitButton = findViewById(R.id.feedback_submit);

        submitButton.setOnClickListener(new submitButtonOnClickListener());

    }

    private class submitButtonOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if(feedback.getText().toString().length() > 0){
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");

                String emailSubject = getResources().getString(R.string.defaultEmailSubject);
                if(subject.getText().toString().length() > 0)
                    emailSubject = subject.getText().toString();

                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"katerdeluna@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
                emailIntent.putExtra(Intent.EXTRA_TEXT, feedback.getText().toString());

                try{
                    startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.SendEmailMessage)));
                    finish();
                }
                catch (android.content.ActivityNotFoundException e) {
                    Toast.makeText(FeedbackActivity.this,
                            getResources().getString(R.string.EmailClientNotFoundMessage), Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(FeedbackActivity.this, getResources().getString(R.string.EmptyFeedbackMessage), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
