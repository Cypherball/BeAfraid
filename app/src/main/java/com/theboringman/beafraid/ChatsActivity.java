package com.theboringman.beafraid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class ChatsActivity extends AppCompatActivity {

    private boolean isFirstTime = true;
    private SharedPreferences sharedPreferences;
    boolean isMale = false;
    boolean isFemale = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);

        isFirstTime = sharedPreferences.getBoolean("isFirstTime",true);
        if(isFirstTime){
           FirstTimeDialog();
        }
    }
    public void goToStory1(View view){
        Intent intent = new Intent(this, Story1Activity.class);
        startActivity(intent);
    }

    public void goToSettings(View view){
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }

//Alert Dialog for First Time Users.
    public void FirstTimeDialog(){
        LayoutInflater factory = LayoutInflater.from(this);
        final View firstDialog = factory.inflate(R.layout.first_time_dialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(firstDialog);
        dialog.setCancelable(false);

        final EditText firstName = firstDialog.findViewById(R.id.dialog_firstName);
        final EditText lastName = firstDialog.findViewById(R.id.dialog_lastName);

        final ImageView picutre = firstDialog.findViewById(R.id.dialog_picture);

        ImageButton okayButton = firstDialog.findViewById(R.id.dialog_okay);

        final RadioButton male = firstDialog.findViewById(R.id.dialog_male);
        final RadioButton female = firstDialog.findViewById(R.id.dialog_female);
        male.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if(male.isChecked()){
                    picutre.setImageResource(R.drawable.player);
                    isMale = true;
                    isFemale = false;
                }
            }
        });
        female.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if(female.isChecked()){
                    picutre.setImageResource(R.drawable.player_woman);
                    isMale = false;
                    isFemale = true;
                }
            }
        });

        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Fill all fields!",Toast.LENGTH_SHORT).show();
                }
                else if(!isMale && !isFemale){
                    Toast.makeText(getApplicationContext(),"Select your gender.",Toast.LENGTH_SHORT).show();
                }
                else{
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(isMale){
                        editor.putBoolean("gender",isMale);
                    }else{
                        editor.putBoolean("gender",!isMale);
                    }
                    String fullName = firstName.getText().toString() + " " + lastName.getText().toString();
                    editor.putString("firstName",firstName.getText().toString());
                    editor.putString("fullName", fullName);
                    editor.putBoolean("isFirstTime",false);
                    editor.apply();
                   dialog.dismiss();
                }
            }
        });

        dialog.show();
    }
}
