package com.example.pingpingv2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputFilter;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class GameOverActivity extends Activity {

    TextView score;
    EditText name;
    String yourName;
    Bundle endScore;
    String myScore;

    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    long userId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gv);
        endScore = getIntent().getExtras();
        score = (TextView) findViewById(R.id.Score);
        name = (EditText) findViewById(R.id.name);

        myScore = endScore.get("endScore").toString();
        score.setText("Score " + myScore);

        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.getWritableDatabase();

    }



    public void saveScore(View view){
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_NAME, name.getText().toString());
        cv.put(DatabaseHelper.COLUMN_SCORE, String.valueOf(myScore));
        db.insert(DatabaseHelper.TABLE, null, cv);

        db.close();
        Intent menu = new Intent(this, MainActivity.class);
        menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(menu);
        finish();
    }

    public void restart(View view){
        Intent game = new Intent(this, GameActivity.class);
        startActivity(game);
        finish();
    }

    public void onBackPressed() {
    }
}
