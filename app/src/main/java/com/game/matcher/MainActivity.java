package com.game.matcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ImageButton buttonPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonPlay = findViewById(R.id.buttonPlay);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    goToUserInputActivity();
                } else {
                    Toast.makeText(MainActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Function used to send the user to GradeActivty
    private void goToUserInputActivity() {
            startActivity(new Intent(this, UserInputActivity.class));
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
