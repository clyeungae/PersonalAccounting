package com.example.personalaccounting;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class UserSettingActivity extends AppCompatActivity {

    private Switch darkThemeSwitch;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);


        darkThemeSwitch =findViewById(R.id.userSetting_darkThemeSwitch);
        darkThemeSwitch.setOnCheckedChangeListener(new darkThemeSwitchOnCheckedChangeListener());
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
