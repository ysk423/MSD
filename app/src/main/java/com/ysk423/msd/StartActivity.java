package com.ysk423.msd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private int startGameLevel = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Switch switch1 = (Switch) findViewById(R.id.hoSwitch);
        switch1.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (isChecked == true) {
             Toast.makeText(StartActivity.this, "HangOver mode ONにしました。", Toast.LENGTH_SHORT).show();
             startGameLevel = 3;
        } else {
            Toast.makeText(StartActivity.this, "HangOver mode OFFにしました。", Toast.LENGTH_SHORT).show();
            startGameLevel = 1;
        }
    }

    public void startGame(View view) {
        Intent intent = new Intent(getApplication(), MainActivity.class);
        intent.putExtra("GAME_LEVEL", startGameLevel);
        startActivity(intent);
    }

}
