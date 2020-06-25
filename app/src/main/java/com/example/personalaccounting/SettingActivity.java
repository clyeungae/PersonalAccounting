package com.example.personalaccounting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {

    private ImageButton budgetSettingButton;
    private ImageButton userSettingButton;
    private ImageButton feedbackButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        budgetSettingButton = findViewById(R.id.budgetSetting_button);
        userSettingButton = findViewById(R.id.userSetting_button);
        feedbackButton = findViewById(R.id.feedback_button);

        budgetSettingButton.setOnClickListener(new budgetSettingButtonOnClickListener());
        userSettingButton.setOnClickListener(new userSettingButtonOnClickListener());
        feedbackButton.setOnClickListener(new feedbackButtonOnClickListener());
    }

    private class feedbackButtonOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(SettingActivity.this, FeedbackActivity.class);
            startActivity(intent);
        }
    }

    private class budgetSettingButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(SettingActivity.this, BudgetSettingActivity.class);
            startActivity(intent);
        }
    }

    private class userSettingButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(SettingActivity.this, UserSettingActivity.class);
            startActivity(intent);
        }
    }


}