package com.theboringman.beafraid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class Story1Activity extends AppCompatActivity{
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private String playerName;
    private long mDelay;
    private int mIndex;
    private CharSequence mText;
    private SharedPreferences sharedPreferencesUser;
    private SharedPreferences sharedPreferencesGame;
    private int playerCount = 0;
    private int botCount = 0;
    private Random random;
    private int chapter = 1;
    //Messages
    private String[][] playerMessages; //current player message array
    private String[][] botMessages; //current bot message array
    private String[][] playerCh1;
    private String[][] botCh1;
    private String[][] playerCh2;
    private String[][] botCh2;
    private String[][] playerCh3;
    private String[][] botCh3;
    //Views
    private ListView listView;
    private EditText chatText;
    private TextView activityState;
    private TextView Name;
    private ImageButton buttonSend;
    //Booleans
    private boolean nextPlayer = false;
    private boolean nextBot = false;
    private boolean left = true;
    private boolean right = false;
    private boolean isPlayer = true;
    //Media
    private MediaPlayer keypress;
    private MediaPlayer receivedMessage;
    private MediaPlayer sentMessage;
    //Emojis
    String grinWithSmilingEyes;
    String cryLaughter;
    String smileOpenMouth;
    String smileOpenMouthSmilingEyes;
    String smileWithColdSweat;
    String smileTightClosedEys;
    String winkFace;
    String smile;
    String smileTongueOut;
    String relieveFace;
    String smileHeartEyes;
    String smirkFace;
    String unamusedFace;
    String tensedFace;
    String pensiveFace;
    String confoundedFace;
    String kissThrow;
    String kissFace;
    String getPrankedFace;
    String teaseFace;
    String dissapointedFace;
    String angryFace;
    String redAngryFace;
    String cryingFace;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story1);

        sharedPreferencesUser = getSharedPreferences("userInfo",MODE_PRIVATE);
        sharedPreferencesGame = getSharedPreferences("gameInfo",MODE_PRIVATE);

        playerName = sharedPreferencesUser.getString("firstName","");
        loadEmojis();
        loadMessages();
        setChapterMessages();

        buttonSend = findViewById(R.id.send);
        Name = findViewById(R.id.name);
        listView = findViewById(R.id.msgview);
        chatText = findViewById(R.id.msg);
        activityState = findViewById(R.id.activity_state);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right_chat);
        listView.setAdapter(chatArrayAdapter);

        keypress= MediaPlayer.create(getApplicationContext(), R.raw.keypress);
        receivedMessage = MediaPlayer.create(getApplicationContext(),R.raw.received_message);
        sentMessage = MediaPlayer.create(getApplicationContext(),R.raw.sent_message);

        random = new Random();

        chatText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chatText.getText().toString().isEmpty() && playerCount <playerMessages.length && isPlayer)
                {//check if messages are available.
                    setCharacterDelay(150);
                    animateText(playerMessages[playerCount][0]); //load message of
                    // player from player's sequence array.
                }
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(playerCount < playerMessages.length && chatText.getText().toString().equals(playerMessages[playerCount][0]) ) {
                    playerMessageSend();
                }
                if(!isPlayer && botCount < botMessages.length) {     //Receive Message if available
                        botMessageReceive();
                }
                if((playerCount < playerMessages.length && playerMessages[playerCount][1].contains("end")) || (botCount < botMessages.length && botMessages[botCount][1].contains("end"))){
                        chapter++;
                        sharedPreferencesGame.edit().putInt("chap",chapter).apply();
                        playerCount = 0;
                        botCount = 0;
                        setChapterMessages();
                }
            }});

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
    }

    //For animating the typewriter effect.
    private Handler mHandler = new Handler();
    private Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            keypress.start();
            chatText.setText(mText.subSequence(0, mIndex++));
            chatText.setSelection(chatText.getText().length());
            if(mIndex <= mText.length()) {
                Random random = new Random();
                mDelay = random.nextInt( 125 - 50) + 50;
                mHandler.postDelayed(characterAdder, mDelay);
            }
        }
    };
    public void animateText(CharSequence text) {
        mText = text;
        mIndex = 0;
        chatText.setText("");
        mHandler.removeCallbacks(characterAdder);
        mHandler.postDelayed(characterAdder, mDelay);

    }
    public void setCharacterDelay(long millis) {
        mDelay = millis;
    }

    public void waitForText(){
        final Handler h0 = new Handler();
        Runnable r0 = new Runnable() {
            @Override
            public void run(){
                receiveChatMessage();
                activityState.setText("Online");
                activityState.setTextColor(Color.parseColor("#bdbdbd"));
                checkForNextBot();
                if(nextBot) {
                    botCount++;
                    botMessageReceive();
                }
                else{
                    chatText.setHint("Click to compose");
                }
            }
        };
        int delay = random.nextInt( 6000 - 2000) + 2000;
        h0.postDelayed(r0, delay);
    }
    public void waitForTyping(){
        Handler h = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run(){
                activityState.setText("Typing.....");
                activityState.setTextColor(Color.parseColor("#25D366"));
            }
        };
        int delay = random.nextInt(1000 - 500) + 500;
        h.postDelayed(r, 1000);
    }

    private boolean sendChatMessage() {
        chatArrayAdapter.add(new ChatMessage(right, chatText.getText().toString()));
        chatText.setText("");
        sentMessage.start();
        return true;
    }
    private boolean receiveChatMessage() {
        if(botCount <botMessages.length) {       //check if messages are available.
            chatArrayAdapter.add(new ChatMessage(left, botMessages[botCount][0]));
            receivedMessage.start();
        }
        return true;
    }

    public void checkForNextPlayer(){
        if(playerCount<playerMessages.length && playerMessages[playerCount][1].contains("next")){
            nextPlayer = true;
        }
        else {
        nextPlayer = false;
        }
    }
    public void checkForNextBot(){
        if(botCount<botMessages.length && botMessages[botCount][1].contains("next")){
           nextBot = true;
           Log.i(TAG,botMessages[botCount][0]);
        } else{
            isPlayer = true;
            nextBot = false;
            botCount++;
            Log.i(TAG,"Nope");}

    }

    public void playerMessageSend(){
        sendChatMessage();
        checkForNextPlayer();
        playerCount++;
        if(!nextPlayer){
            isPlayer = false;
        }
    }
    public void botMessageReceive(){
            chatText.setHint("Wait for reply....");
            waitForTyping();
            waitForText();
    }

    public void loadMessages(){
       playerCh1 = new String[][]{
                {"Hey Selina "+smile,""},
                {"I was just wondering if you could explain this app to me....",""},
                {"Thanks, I knew I could count on you.",""},
                {"Wow, that sounds so cool!",""},
                {"He's probably having a drink, regretting the fact he killed people in his last adventure.",""},
                {"See ya",""}
        };
        botCh1 = new String[][] {
                {"Heyyy what up "+playerName+"??",""},
                {"Oh yeah sure I can " +smileOpenMouth,""},
                {"It's no problem.","next"},
                {"So Nitish's new app is going to be an Interactive Fiction and the story in it will " +
                        "be of the Horror genre.","next"},
                {"In it, you will be texting as one of the characters and chatting with another " +
                        "character whose replies will be automated.","next"},
                {"You will have the freedom to choose your replies during certain key events of the " +
                        "which will change the outcome of the story.","next"},
                {"Your choices will have consequences. \nThe fate of the other character is in your hands.",""},
                {"Ikr! Nitish is a genius "+smileHeartEyes,"next"},
                {"Anyways, I gotta go save Gotham now.....Where's Wayne when you need him right?",""},
                {cryLaughter+cryLaughter+"\nYeah.....","next"},
                {"See ya later",""}
        };
        playerCh2 = new String[][]{};
        botCh2 = new String[][]{};
        playerCh3 = new String[][]{};
        botCh3 = new String[][]{};
    }
    public void setChapterMessages(){
        chapter = sharedPreferencesGame.getInt("chap",1);
        switch(chapter){
            case 1:
                playerMessages = playerCh1;
                botMessages = botCh1;
                break;
            case 2:
                playerMessages = playerCh2;
                botMessages = botCh2;
                break;
            case 3:
                playerMessages = playerCh3;
                botMessages = botCh3;
                break;
            default:
                playerMessages = playerCh1;
                botMessages = botCh1;
                Toast.makeText(this,"Some error occurred.", Toast.LENGTH_LONG).show();
        }
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
    public void loadEmojis(){
        grinWithSmilingEyes = getEmojiByUnicode(0x1F601);
        cryLaughter = getEmojiByUnicode(0x1F602);
        smileOpenMouth = getEmojiByUnicode(0x1F603);
        smileOpenMouthSmilingEyes = getEmojiByUnicode(0x1F604);
        smileWithColdSweat = getEmojiByUnicode(0x1F605);
        smileTightClosedEys = getEmojiByUnicode(0x1F606);
        winkFace = getEmojiByUnicode(0x1F609);
        smile = getEmojiByUnicode(0x1F60A);
        smileTongueOut = getEmojiByUnicode(0x1F60B);
        relieveFace = getEmojiByUnicode(0x1F60C);
        smileHeartEyes = getEmojiByUnicode(0x1F60D);
        smirkFace = getEmojiByUnicode(0x1F60F);
        unamusedFace = getEmojiByUnicode(0x1F612);
        tensedFace = getEmojiByUnicode(0x1F613);
        pensiveFace = getEmojiByUnicode(0x1F614);
        confoundedFace = getEmojiByUnicode(0x1F616);
        kissThrow = getEmojiByUnicode(0x1F618);
        kissFace = getEmojiByUnicode(0x1F61A);
        getPrankedFace = getEmojiByUnicode(0x1F61C);  //Stuck out tongue and winking eye
        teaseFace = getEmojiByUnicode(0x1F61D); //Stuck out tongue with tightly closed eyes
        dissapointedFace = getEmojiByUnicode(0x1F61E);
        angryFace = getEmojiByUnicode(0x1F620);
        redAngryFace = getEmojiByUnicode(0x1F621);
        cryingFace = getEmojiByUnicode(0x1F622);
    }

    //Buttons Methods
    public void goToChatsScreen(View view){
        Intent intent = new Intent(this, ChatsActivity.class);
        startActivity(intent);
        finish();
    }
}

