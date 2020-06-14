package com.example.personalaccounting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {

    private ImageButton accountSettingButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        accountSettingButton = findViewById(R.id.accountSetting_button);


        accountSettingButton.setOnClickListener(new accountSettingButtonOnClickListener());

    }

    private class accountSettingButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(SettingActivity.this, UserSettingActivity.class);
            startActivity(intent);
        }
    }


}