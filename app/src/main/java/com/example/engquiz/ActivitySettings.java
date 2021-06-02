package com.example.engquiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class ActivitySettings extends AppCompatActivity implements View.OnClickListener {
    Button leave;
    CheckBox t1, t2, t3;                                                                    //button and checkboxes
    String[] gr = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        t1 = findViewById(R.id.type1);
        t2 = findViewById(R.id.type2);
        t3 = findViewById(R.id.type3);

        Intent i = getIntent();                                                                     //getting the gr
        gr = i.getStringArrayExtra("gameRule");
        t1.setChecked(!(gr[0] == null || gr[0].isEmpty()));                  //setting checkboxes with custom savings
        t2.setChecked(!(gr[1] == null || gr[1].isEmpty()));
        t3.setChecked(!(gr[2] == null || gr[2].isEmpty()));

        Button leave = (Button) findViewById(R.id.save);
        leave.setOnClickListener(this);                                                             //sets leave onClick
    }

    @Override
    public void onClick(View v) {
        int isOneChecked = 0;                                                                       //checks if at least one box is checked before leaving

        if (t1.isChecked()) {
            gr[0] = "1";
            isOneChecked++;
        } else gr[0]=null;
        if (t2.isChecked()) {
            gr[1] = "2";
            isOneChecked++;
        } else gr[1]=null;
        if (t3.isChecked()) {
            gr[2] = "3";
            isOneChecked++;
        } else gr[2]=null;
        if (isOneChecked != 0) {

            Log.d("settings_sent", Arrays.toString(gr));

            Intent intent = new Intent();                                                           //sends settings back to menu_activity
            intent.putExtra("settings", gr);
            setResult(RESULT_OK, intent);

            finish();
        } else {
            Log.i("settings", "not chosen");
            Toast.makeText(this, "You must check at least one box", Toast.LENGTH_LONG).show();
        }
    }


}
