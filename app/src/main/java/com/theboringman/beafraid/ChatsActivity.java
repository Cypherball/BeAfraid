package com.theboringman.beafraid;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

public class ChatsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    boolean isMale = false;    //Only used for checking a condition in firstTimeDialog()
    boolean isFemale = false;  //Only used for checking a condition in firstTimeDialog()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);

        firstTimeCheck();  //Check if first time opening app. If it is show firstTimeDialog Box.
    }

    //check if first time
    public void firstTimeCheck(){
        boolean isFirstTime = sharedPreferences.getBoolean("isFirstTime",true);
        if(isFirstTime){
            firstTimeDialog();
        }
    }
    //Alert Dialog for First Time Users.
    public void firstTimeDialog(){
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
            @Override public void onClick(View view) { //Check if male is checked
                if(male.isChecked()){
                    picutre.setImageResource(R.drawable.player); //set male profile picture
                    isMale = true;
                    isFemale = false;
                }
            }
        });
        female.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if(female.isChecked()){  //Check if female is checked
                    picutre.setImageResource(R.drawable.player_woman); //set female profile picture
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

    //Intents on Button Click
    public void goToStory1(View view){
        Intent intent = new Intent(this, Story1Activity.class);
        startActivity(intent);
    }
    public void goToSettings(View view){
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }
}
