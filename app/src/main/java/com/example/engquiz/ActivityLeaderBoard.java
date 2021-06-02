package com.example.engquiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityLeaderBoard extends AppCompatActivity {
    TextView nick1,nick2,nick3,nick4,nick5,nick6,nick7,score1,score2,score3,score4,score5,score6,score7;
    Button leave2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        Intent i = getIntent();                                                                     //gets nicks and scores from menu activity
        String[] nicks = i.getStringArrayExtra("n");
        String[] scores = i.getStringArrayExtra("s");
        Log.d("ActivityRecord","records received");

        putLeaderBoard(nicks, scores);                                                              //goes to method putLeaderBoard()

        leave2 = findViewById(R.id.leave2);
        leave2.setOnClickListener(v -> {
            finish();
        });
    }

    private void putLeaderBoard(String[] nicks, String[] scores){                                   // method putLeaderBoard()
        nick1=findViewById(R.id.nick1);                                                             //setting nicks
        nick1.setText(nicks[0]);

        nick2=findViewById(R.id.nick2);
        nick2.setText(nicks[1]);

        nick3=findViewById(R.id.nick3);
        nick3.setText(nicks[2]);

        nick4=findViewById(R.id.nick4);
        nick4.setText(nicks[3]);

        nick5=findViewById(R.id.nick5);
        nick5.setText(nicks[4]);

        nick6=findViewById(R.id.nick6);
        nick6.setText(nicks[5]);

        nick7=findViewById(R.id.nick7);
        nick7.setText(nicks[6]);

        score1=findViewById(R.id.score1);                                                           //setting scores
        score1.setText(scores[0]);

        score2=findViewById(R.id.score2);
        score2.setText(scores[1]);

        score3=findViewById(R.id.score3);
        score3.setText(scores[2]);

        score4=findViewById(R.id.score4);
        score4.setText(scores[3]);

        score5=findViewById(R.id.score5);
        score5.setText(scores[4]);

        score6=findViewById(R.id.score6);
        score6.setText(scores[5]);

        score7=findViewById(R.id.score7);
        score7.setText(scores[6]);
    }
}
