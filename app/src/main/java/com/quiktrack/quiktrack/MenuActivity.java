package com.quiktrack.quiktrack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MenuActivity extends AppCompatActivity {

    private Button buttonAddTag;
    private Button buttonAddItem;
    private Button buttonCheckItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        buttonAddTag = (Button) findViewById(R.id.buttonAddTag);
        buttonAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, AddTagActivity.class);
                startActivity(intent);
            }
        });

        buttonAddItem = (Button) findViewById(R.id.buttonAddItem);
        buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, AddItemActivity.class);
                startActivity(intent);
            }
        });

        buttonCheckItem = (Button) findViewById(R.id.buttonCheckItem);
        buttonCheckItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, CheckItemActivity.class);
                startActivity(intent);
            }
        });

    }
}
