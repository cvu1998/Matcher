package com.game.matcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ImageButton buttonPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonPlay = (ImageButton) findViewById(R.id.buttonPlay);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToUserInputActivity();
            }
        });
    }

    //Function used to send the user to GradeActivty
    private void goToUserInputActivity() {
            Intent intent = new Intent(this, UserInputActivity.class);
            startActivity(intent);
    }
}
