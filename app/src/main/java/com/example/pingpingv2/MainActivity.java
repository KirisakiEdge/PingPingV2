package com.example.pingpingv2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.database.sqlite.SQLiteDatabase;

public class MainActivity extends Activity{
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    ListView userList;
    SimpleCursorAdapter userAdapter;

    TextView score;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userList = (ListView)findViewById(R.id.list);

        databaseHelper = new DatabaseHelper(getApplicationContext());
    }


    @Override
    public void onResume() {
        super.onResume();
        db = databaseHelper.getReadableDatabase();

        //userCursor =  db.rawQuery("select * from "+ DatabaseHelper.TABLE, null);
        userCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " order by " + DatabaseHelper.COLUMN_SCORE  + " desc limit 5", null);

        String[] headers = new String[] {DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_SCORE};

        userAdapter = new SimpleCursorAdapter(this, R.layout.listview_style,
                userCursor, headers, new int[]{R.id.textView4, R.id.textView5}, 0);
        userList.setAdapter(userAdapter);

    }


    public void start(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        db.close();
        userCursor.close();
    }
}

