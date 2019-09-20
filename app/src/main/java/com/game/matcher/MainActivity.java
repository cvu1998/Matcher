package com.game.matcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageButton buttonPlay;
    ArrayList<String> runningactivities = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonPlay = (ImageButton) findViewById(R.id.buttonPlay);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToGameActivity();
            }
        });
    }

    //Function used to send the user to GradeActivty
    private void goToGameActivity() {
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
    }
}
