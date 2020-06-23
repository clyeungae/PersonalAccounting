package com.example.personalaccounting;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class UserSettingActivity extends AppCompatActivity {

    private DatabaseHelper myDB;

    private Switch darkThemeSwitch;
    private TextView languageTextView;
    private TextView startDateTextView;
    private TextView lastActiveTextView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);

        darkThemeSwitch = findViewById(R.id.userSetting_darkThemeSwitch);
        languageTextView = findViewById(R.id.userSetting_languageTextView);
        startDateTextView = findViewById(R.id.userSetting_startDateTextView);
        lastActiveTextView = findViewById(R.id.userSetting_lastActiveDateTextView);


        myDB = new DatabaseHelper(this);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        languageTextView.setText(Locale.getDefault().getDisplayLanguage());
        startDateTextView.setText(sdf.format(myDB.getUserStartDate().getTime()));
        lastActiveTextView.setText(sdf.format(myDB.getUserLastActiveDate().getTime()));
        darkThemeSwitch.setOnCheckedChangeListener(new darkThemeSwitchOnCheckedChangeListener());
        darkThemeSwitch.setTextOff(getResources().getString(R.string.general));
        darkThemeSwitch.setTextOn(getResources().getString(R.string.dark_mode));
    }

    private class darkThemeSwitchOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if(isChecked){

            }
            else{

            }
        }
    }
}
