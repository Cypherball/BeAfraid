package com.theboringman.beafraid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    private boolean isMale;
    private String Name;
    private ImageView profilePic;
    private TextView nameHolder;
    private SharedPreferences sharedPreferencesUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferencesUser = getSharedPreferences("userInfo",MODE_PRIVATE);

        isMale = sharedPreferencesUser.getBoolean("gender",true);
        Name = sharedPreferencesUser.getString("fullName","Error");

        profilePic = findViewById(R.id.settings_profilePic);
        if(isMale){
            profilePic.setImageResource(R.drawable.player);
        }else{
            profilePic.setImageResource(R.drawable.player_woman);
        }

        nameHolder = findViewById(R.id.settings_name);
        nameHolder.setText(Name);
    }

    public void goBack(View view){
        Intent intent = new Intent(this, ChatsActivity.class);
        startActivity(intent);
        finish();
    }
}
