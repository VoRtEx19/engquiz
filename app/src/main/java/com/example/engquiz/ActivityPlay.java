package com.example.engquiz;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.engquiz.fragments.Fragment1;
import com.example.engquiz.fragments.Fragment2;
import com.example.engquiz.fragments.Fragment3;
import com.example.engquiz.types.Type1;
import com.example.engquiz.types.Type2;
import com.example.engquiz.types.Type3;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Random;


public class ActivityPlay extends FragmentActivity implements View.OnClickListener, ResultActivity.onAnswerListener {
    TextView round_tv, score_tv;                                                                    //layout elements
    Button check;
    ArrayList<Type1> synonyms = new ArrayList<>();                                                  //question base
    ArrayList<Type1> antonyms = new ArrayList<>();
    ArrayList<Type2> similars = new ArrayList<Type2>();
    ArrayList<Type3> modals = new ArrayList<>();
    int round = 0, score = 0, addScore;
    static String[] str_s, str_a, str_m, str_d, gr = new String[3];                                               //helpers and game rule
    boolean result_correct, checked = false;
    long time;
    ResultActivity.onAnswerListener oal;
    String lowScore;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        round_tv = findViewById(R.id.round_tv);                                                     //linking with layout and setting button onclick listeners
        score_tv = findViewById(R.id.score_tv);
        check = findViewById(R.id.check);

        Intent i = getIntent();                                                                     //getting gameRule
        gr = i.getStringArrayExtra("gr");
        lowScore = i.getStringExtra("ls");
        if (questionBaseLength(gr) != 0) {
            load(gr);
        } else finish(-1);
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public void onClick(View v) {                                                                   //onClick, of course
        switch (v.getId()) {
            case R.id.check:
                if (!checked) {
                    check.setText(">");
                    oal.sendResult(result_correct);
                    addScore = 0;
                    if (result_correct) {
                        addScore += 100;
                        time = (System.currentTimeMillis() - time) / 100;
                        if (time > 20 && time < 120) {
                            addScore += (120 - time) / 2;
                        } else if (time <= 20) addScore += 50;
                        Log.i("add score", result_correct + " " + addScore);
                        score += addScore;
                        score_tv.setText(getResources().getString(R.string.score) + " " + score);
                    }
                } else {
                    if (result_correct) {
                        myHandler.sendEmptyMessage(0);
                    } else finish(0);
                }
                checked = !checked;
                break;
            case R.id.ok:
                dialog.dismiss();
                startRound();
        }
    }

    //---------------------------------------------------------------------------------------------- all connected with loading

    private int questionBaseLength(String[] gr) {                                                   //counting progress bar max value
        int l = 0;
        for (String s : gr) {
            if (!(s == null || s.isEmpty())) {
                if (s.equals("1")) {                                                                //getting resources for synonyms & antonyms
                    str_s = getResources().getStringArray(R.array.t1_s);
                    str_a = getResources().getStringArray(R.array.t1_a);
                    l += str_s.length + str_a.length;
                }
                if (s.equals("2")) {                                                                // getting resources for...
                    str_d = getResources().getStringArray(R.array.t2);
                    l += str_d.length;
                }
                if (s.equals("3")) {
                    str_m = getResources().getStringArray(R.array.t3);
                    l += str_m.length;
                }
            }
        }
        Log.i("loading", "progress bar length: " + l);
        return l;
    }

    private void load(String[] gr) {
        ObjectMapper om = new ObjectMapper();
        int progress = 0;
        for (String s : gr) {
            if (!(s == null || s.isEmpty())) {

                if (s.equals("1")) {                                                            //adding synonyms and antonyms
                    for (String str_ : str_s) {
                        try {
                            synonyms.add(om.readValue(str_, Type1.class));
                            Log.i("Loading", "progress" + ++progress);
                        } catch (JsonMappingException e) {
                            e.printStackTrace();
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }

                    for (String value : str_a) {
                        try {
                            antonyms.add(om.readValue(value, Type1.class));
                            Log.i("Loading", "progress" + ++progress);
                        } catch (JsonMappingException e) {
                            e.printStackTrace();
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (s.equals("2")) {                                                                // adding some questions
                    for (String value : str_d) {
                        try {
                            similars.add(om.readValue(value, Type2.class));
                            Log.i("Loading", "progress" + ++progress);
                        } catch (JsonMappingException e) {
                            e.printStackTrace();
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (s.equals("3")) {
                    for (String value : str_m) {
                        try {
                            modals.add(om.readValue(value, Type3.class));
                            Log.i("Loading", "progress" + ++progress);
                        } catch (JsonMappingException e) {
                            e.printStackTrace();
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        myHandler.sendEmptyMessage(0);
    }


    @SuppressLint("HandlerLeak")
    Handler myHandler = new Handler() {                                                             //removes loading

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
                Log.i("Loading", "fragments removed");
                if (round == 0) {
                    dialog = new Dialog(ActivityPlay.this);
                    dialog.setContentView(R.layout.ready_dialog_view);
                    Button ok = dialog.findViewById(R.id.ok);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    ok.setOnClickListener(ActivityPlay.this::onClick);
                } else if (round < 50) {
                    startRound();
                } else finish(1);
            }
        }
    };

    public int getBaseLength(String[] str) {                                                        // counts the whole base
        int l = 0;
        for (String s : gr) {
            if (!(s == null || s.isEmpty())) {
                if (s.equals("1")) {
                    l += synonyms.size() + antonyms.size();                                         //type1
                }
                if (s.equals("2")) {
                    l += similars.size();
                }
                if (s.equals("3")) {                                                                //type3
                    l += modals.size();
                }
            }
        }
        Log.i("play", "full size left " + l);
        return l;
    }

    public int getBaseLength(String s) {                                                            //counts for exact type
        int l = 0;
        switch (s) {
            case "1":
                l += synonyms.size() + antonyms.size();
                break;
            case "2":
                l += similars.size();
                break;
            case "3":
                l += modals.size();
                break;
            default:
                break;
        }
        Log.i("play", "this question type " + s + " left " + l);
        return l;
    }

//--------------------------------------------------------------------------------------------------the actual game

    @Override
    public void sendResult(boolean isCorrect) {
        result_correct = isCorrect;
        check.setEnabled(true);
    }


    @SuppressLint("SetTextI18n")
    public void startRound() {
        if (getBaseLength(gr) == 0) {                                                               // check if no questions left and if true finishes the game with result -1
            Log.i("play", "no questions left");
            finish(-1);
        } else {
            check.setText("check");
            check.setEnabled(false);
            String thisRoundType;
            do {
                thisRoundType = gr[(int) (Math.random() * gr.length)];
            } while (thisRoundType == null || thisRoundType.isEmpty() || getBaseLength(thisRoundType) == 0);
            round_tv.setText(getString(R.string.round) + ++round);
            Log.d("round", "N" + round + ", type " + thisRoundType);
            addScore = 0;
            switch (thisRoundType) {
                case "1":
                    play1();
                    break;
                case "2":
                    play2();
                    break;
                case "3":
                    play3();
                    break;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void play1() {
        int SorA;
        if (synonyms.size() == 0 || antonyms.size() == 0) {                                         //checks whether synonyms or antonyms is empty and decides which one to pick
            if (synonyms.size() == 0) {
                SorA = 1;
            } else SorA = 0;
        } else SorA = (int) (Math.random() * 2);
        Log.i("type1", SorA == 0 ? "synonym" : "antonym");

        Random rand = new Random();
        Type1 randomElement;                                                                        //getting questions
        if (SorA == 0) {
            randomElement = synonyms.remove(rand.nextInt(synonyms.size()));
        } else {
            randomElement = antonyms.remove(rand.nextInt(antonyms.size()));
        }
        Fragment1 f1 = new Fragment1(getString(SorA == 0 ? R.string.synonym : R.string.antonym) + " " + randomElement.getQ() + "?", randomElement.getA(), randomElement.getRa());
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.place_holder, f1);
        ft.commit();
        try {
            oal = f1;
        } catch (ClassCastException e) {
            throw new ClassCastException(f1.toString() + " must implement onSomeEventListener");
        }
        time = System.currentTimeMillis();
        check.setOnClickListener(this);
    }

    public void play2() {
        Random rand = new Random();
        Type2 randomElement = similars.remove(rand.nextInt(similars.size()));
        Fragment2 f2 = new Fragment2(String.format(getString(R.string.question2), randomElement.getQ()), randomElement.getA(), randomElement.getRa());
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.place_holder, f2);
        ft.commit();
        try {
            oal = f2;
        } catch (ClassCastException e) {
            throw new ClassCastException(f2.toString() + " must implement onSomeEventListener");
        }
        time = System.currentTimeMillis();
        check.setOnClickListener(this);
    }

    public void play3() {
        Random rand = new Random();
        Type3 randomElement = modals.remove(rand.nextInt(modals.size()));
        Fragment3 f3 = new Fragment3(String.format(getString(R.string.question3), randomElement.getQ()), randomElement.getA(), randomElement.getRa());
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.place_holder, f3);
        ft.commit();
        try {
            oal = f3;
        } catch (ClassCastException e) {
            throw new ClassCastException(f3.toString() + " must implement onSomeEventListener");
        }
        time = System.currentTimeMillis();
        check.setOnClickListener(this);
    }

//--------------------------------------------------------------------------------------------------

    public void finish(int msg) {
        Intent i = new Intent(ActivityPlay.this, ResultActivity.class);
        i.putExtra("r", msg);
        i.putExtra("s", score);
        i.putExtra("ls", lowScore);
        Log.i("ActivityPlay", lowScore);
        startActivityForResult(i, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {       //gets gamerule settings from activity_settings
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 2) {
            Intent intent = new Intent();
            if (data != null) {
                String n = data.getStringExtra("n"), s = data.getStringExtra("s");
                intent.putExtra("s", s);
                intent.putExtra("n", n);
                setResult(RESULT_OK, intent);
            }
        } else setResult(RESULT_CANCELED);
        this.finish();
    }
}