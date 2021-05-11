package com.example.myexcellentbooks;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;

public class Convertor extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // делаем полноэкранное
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.convertor_activity);
        init();

        final Gson gson = new Gson();

        Intent intent = getIntent();
        String position = intent.getStringExtra("ArrayNum");
        ArrayList<String> BookArray = gson.fromJson(SelectActivity.sharedPreferences.getString(position, ""), ArrayList.class);
        BookArray.remove(BookArray.size() - 1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.list_convertor, BookArray);
        listView.setAdapter(adapter);
    }
    public void init(){
        listView = (ListView) findViewById(R.id.list);
    }
}
