package com.example.engquiz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class MenuActivity extends AppCompatActivity implements View.OnClickListener {
    SharedPreferences sp;                                                                           //to save the leaderBoard from mainactivity so it's free from any part of the app
    String[] gr = new String[3];                                                                    //gamerule (in settings activity user chooses questions they would like to see)
    String[] nicks = new String[7];                                                                 //for the leaderboard
    String[] scores = new String[7];
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        load();                                                                                     //loading data from shared prefs

        Button play = findViewById(R.id.play);
        Button settings = findViewById(R.id.settings);
        Button leaderBoard = findViewById(R.id.leaderBoard);
        Button exit = findViewById(R.id.exit);

        play.setOnClickListener(this);                                                              //setting onClickListener for all buttons
        settings.setOnClickListener(this);
        leaderBoard.setOnClickListener(this);
        exit.setOnClickListener(this);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {                                                                   //overrode onclick
        Intent i;
        switch (v.getId()) {
            case R.id.play:                                                                         //starts activity play and puts chosen gamerule
                i = new Intent(MenuActivity.this, ActivityPlay.class);
                if (gr[0]==null && gr[1]==null && gr[2]==null) gr[0]="1";
                i.putExtra("gr", gr);
                i.putExtra("ls", scores[6]);
                startActivityForResult(i, 0);
                break;
            case R.id.settings:                                                                     //starts activity settings (puts already saved custom settings and gets new settings)
                i = new Intent(MenuActivity.this, ActivitySettings.class);
                i.putExtra("gameRule", gr);
                startActivityForResult(i, 1);
                break;
            case R.id.leaderBoard:                                                                  //gives a table for the leaderboard
                i = new Intent(this, ActivityLeaderBoard.class);
                i.putExtra("n", nicks);
                i.putExtra("s", scores);
                startActivity(i);
                break;
            case R.id.exit:                                                                         //finishes the activity
                dialog = new Dialog(MenuActivity.this);
                dialog.setContentView(R.layout.quit_dialog_view);
                Button y = dialog.findViewById(R.id.yes), n = dialog.findViewById(R.id.no);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                y.setOnClickListener(this);
                n.setOnClickListener(this);
                break;
            case R.id.yes:
                dialog.dismiss();
                this.finish();
                break;
            case R.id.no:
                dialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {       //gets gamerule settings from activity_settings
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                assert data != null;
                gr = data.getStringArrayExtra("settings");
                Log.d("settings_received", Arrays.toString(gr));
            }
            if (requestCode == 0 && data != null) {
                String nickResult = data.getStringExtra("n"), scoreResult = data.getStringExtra("s");
                int place = 6;
                for (int i = 5; i >= 0; i--)
                    if (Integer.parseInt(scoreResult) >= Integer.parseInt(scores[i])) place--;
                String tmpn, tmps;
                for (int i = place; i < 7; i++) {
                    tmps = scores[i];
                    tmpn = nicks[i];
                    scores[i] = scoreResult;
                    nicks[i] = nickResult;
                    scoreResult = tmps;
                    nickResult = tmpn;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {                                                                    //saves current lb and gr before the activity is closed
        super.onDestroy();
        save();
    }

    private void save() {                                                                           //method save()
        sp = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        for (int i = 0; i < 7; i++) {                                                               //puts the whole leaderboard
            String key_n = "n" + i;
            String key_s = "s" + i;
            ed.putString(key_n, nicks[i]);
            ed.putString(key_s, scores[i]);
        }
        Log.i("settings", Arrays.toString(gr));
        for (int i = 0; i < gr.length; i++) ed.putString("gr" + (i + 1), gr[i]);
        ed.apply();
        Log.i("SharedPrefs", "saved");
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
    }

    private void load() {                                                                           //method load()
        sp = getPreferences(MODE_PRIVATE);
        for (int i = 0; i < 7; i++) {                                                               //gets the lb
            String key_n = "n" + i;
            String key_s = "s" + i;
            nicks[i] = sp.getString(key_n, "---");
            scores[i] = sp.getString(key_s, "0");
        }
        for (int i = 0; i<3; i++){                                                                  // gets the gr
            gr[i]=sp.getString("gr"+(i+1), null);
        }
        Log.d("settings_loaded", Arrays.toString(gr));
    }
}