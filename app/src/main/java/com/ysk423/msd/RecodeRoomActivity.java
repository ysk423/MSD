package com.ysk423.msd;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;

public class RecodeRoomActivity extends AppCompatActivity {

    private TextView textView;
    private EditText editTextPlayername, editTextScoreresult;
    private ScoreDatabaseHelper helper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recode_room);

        editTextPlayername = findViewById(R.id.edit_text_playername);
        editTextScoreresult = findViewById(R.id.edit_text_scoreresult);

        textView = findViewById(R.id.text_view);

        //起動時にScoreDB.dbを毎回削除
        //開発中のみの処理、デリーとボタン実装出来たら消す
        deleteDatabase("ScoreDB.db");

        Button insertButton = findViewById(R.id.button_insert);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(helper == null){
                    helper = new ScoreDatabaseHelper(getApplicationContext());
                }

                if(db == null){
                    db = helper.getWritableDatabase();
                }

                String key = editTextPlayername.getText().toString();
                String value = editTextScoreresult.getText().toString();

                // 点数は整数を想定
                insertData(db, key, Integer.valueOf(value));
            }
        });

        Button readButton = findViewById(R.id.button_read);
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readData();
            }
        });
    }


    private void readData(){
        if(helper == null){
            helper = new ScoreDatabaseHelper(getApplicationContext());
        }

        if(db == null){
            db = helper.getReadableDatabase();
        }
        Log.d("debug","**********Cursor");

        Cursor cursor = db.query(
                "scoredb",
                new String[] { "playername", "scoreresult" },
                null,
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();

        StringBuilder sbuilder = new StringBuilder();

        for (int i = 0; i < cursor.getCount(); i++) {
            sbuilder.append(cursor.getString(0));
            sbuilder.append(": ");
            sbuilder.append(cursor.getInt(1));
            sbuilder.append("\n");
            cursor.moveToNext();
        }

        // 忘れずに！
        cursor.close();

        Log.d("debug","**********"+sbuilder.toString());
        textView.setText(sbuilder.toString());
    }


    private void insertData(SQLiteDatabase db, String name, int score){

        ContentValues values = new ContentValues();
        values.put("playername", name);
        values.put("scoreresult", score);

        db.insert("scoredb", null, values);
    }


    public void tryAgain(View view) {
        startActivity(new Intent(getApplicationContext(), StartActivity.class));
    }

}

