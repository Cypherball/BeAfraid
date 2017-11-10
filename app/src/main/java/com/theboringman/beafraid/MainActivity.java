package com.theboringman.beafraid;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import java.util.Random;


public class MainActivity extends AppCompatActivity{
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private TextView activityState;
    private TextView Name;
    private ImageButton buttonSend;
    private boolean left = true;
    private boolean right = false;
    private long mDelay;
    private int mIndex;
    private CharSequence mText;
    private String[] playerMessages = {
            "Hey Selina!",
            "Yeah Ikr, I got your number from Erick.",
            "I'm developing a new a app in which I tell a story in the form of a textual " +
                    "conversation between two fictional people. \nThis will create a more " +
                    "immersive effect for the story and will also make the connection the reader " +
                    "has on those people more stronger than if they were to just read the story in the " +
                    "form of a book.",
            "Cool it is. But it will take a lot of time. I'm still trying to modify the code so " +
                    "that the texts will appear more natural and feel real. \nI also have to " +
                    "write the story for the app. Which also is going to take ages....",
            "Thanks Selina!"
    };
    private String[] botMessages = {"" +
            "Oh hey there Nitish! \nLong Time no talk!",
            "I just spoke to Erick yesterday. He seems to have achieved his life goal. \nSo " +
                    "anyways whats new with you??",
            "Oh That's cool! \nI can't wait to try out your new app!",
            "That's okay. I can wait till you complete your app. \nGood Luck.",
            "My pleasure."
    };
    private MediaPlayer keypress;
    private boolean isPlayer = true;
    private int playerInr = 0;
    private int botInr = 0;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSend = findViewById(R.id.send);
        Name = findViewById(R.id.name);
        listView = findViewById(R.id.msgview);
        chatText = findViewById(R.id.msg);
        activityState = findViewById(R.id.activity_state);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right_chat);
        listView.setAdapter(chatArrayAdapter);

        keypress= MediaPlayer.create(getApplicationContext(), R.raw.press);

        chatText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chatText.getText().toString().isEmpty() && playerInr<playerMessages.length && isPlayer)
                {//check if messages are available.
                    setCharacterDelay(150);
                    animateText(playerMessages[playerInr]); //load message of
                    // player from player's sequence array.
                }
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(playerInr<playerMessages.length && chatText.getText().toString().equals
                        (playerMessages[playerInr]) ) {
                    isPlayer = false;
                    playerInr++;
                    sendChatMessage();
                    if(botInr<botMessages.length) {     //Receive Message if available
                        waitForTyping();
                        waitForText();
                    }
                }
            }
        });

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

    private boolean sendChatMessage() {
        chatArrayAdapter.add(new ChatMessage(right, chatText.getText().toString()));
        chatText.setText("");
        return true;
    }

    private boolean receiveChatMessage() {
        if(botInr<botMessages.length) {       //check if messages are available.
            chatArrayAdapter.add(new ChatMessage(left, botMessages[botInr]));
            botInr++;
        }
        return true;
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
                mDelay = random.nextInt( 120 - 50) + 20;
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
        Runnable r0 = new Runnable() {
            @Override
            public void run(){
                receiveChatMessage();
                activityState.setText("Online");
                activityState.setTextColor(Color.parseColor("#bdbdbd"));
                isPlayer = true;
            }
        };
        Random random2 = new Random();
        int delay = random2.nextInt( 5000 - 2000) + 2000;
        Handler h0 = new Handler();
        h0.postDelayed(r0, delay);
    }
    public void waitForTyping(){
        Runnable r = new Runnable() {
            @Override
            public void run(){
                activityState.setText("Typing...");
                activityState.setTextColor(Color.parseColor("#25D366"));
            }
        };
        Handler h = new Handler();
        h.postDelayed(r, 1000);
    }
}

