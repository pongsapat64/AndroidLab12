package com.example.androidlab12;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button bAddGundam, bShowImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bAddGundam = (Button) findViewById(R.id.bAddGundam);
        bAddGundam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddGundam();
            }
        });

        // Button: show foods
        //
        bShowImages = (Button) findViewById(R.id.bShowGundams);
        bShowImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGundam();
            }
        });

    }

    private void showAddGundam(){
        Intent intent = new Intent(MainActivity.this, AddGundam.class);
        MainActivity.this.startActivity(intent);
    }

    private void showGundam(){
        Intent intent = new Intent(MainActivity.this, ShowGundams.class);
        MainActivity.this.startActivity(intent);
    }
}