package com.example.engquiz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {
    public TextView result_tv, score_tv;
    public EditText nickname_ed;
    public Button button;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        result_tv = findViewById(R.id.result);
        score_tv = findViewById(R.id.yourScore);
        nickname_ed = findViewById(R.id.nickname);
        button = findViewById(R.id.proceed);

        Intent i = getIntent();                                                                     //getting msg
        int msg = i.getIntExtra("r", 2);
        switch (msg) {
            case -1:
                result_tv.setTextColor(getResources().getColor(R.color.green));
                result_tv.setText("Lucky! No questions left");
                break;
            case 0:
                result_tv.setTextColor(getResources().getColor(R.color.red_1));
                result_tv.setText("Defeat");
                break;
            case 1:
                result_tv.setTextColor(getResources().getColor(R.color.green));
                result_tv.setText("Victory");
                break;
            default:
                Log.e("Error", "*hysterically throws an Exception");
                this.finish();
        }
        int score = i.getIntExtra("s", 0);
        int lowScore = Integer.parseInt(i.getStringExtra("ls"));
        score_tv.setText(getResources().getString(R.string.yourScore) + " " + score + (score > lowScore ? "\nNew Record!" : ""));
        if (score > lowScore) {
            nickname_ed.setVisibility(View.VISIBLE);
            nickname_ed.setEnabled(true);
        }

        button.setOnClickListener(v -> {
            Log.d("button", "pressed");
            if (score > lowScore) {
                if (nickname_ed.getText().toString().trim().length() != 0) {
                    Log.d("nick", "nick");
                    Intent intent = new Intent();
                    intent.putExtra("s", String.valueOf(score));
                    intent.putExtra("n", String.valueOf(nickname_ed.getText()));
                    setResult(RESULT_OK, intent);
                    this.finish();
                } else Log.d("nick", "null");
            } else {
                Log.i("not record", String.valueOf(score));
                setResult(RESULT_CANCELED);
                this.finish();
            }
        });
    }

    public interface onAnswerListener {
        void sendResult(boolean isDone);
    }
}