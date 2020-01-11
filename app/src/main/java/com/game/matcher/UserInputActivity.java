package com.game.matcher;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class UserInputActivity extends AppCompatActivity {

    private ImageButton buttonReady;
    private TextInputEditText input;
    private Toast toast;
    private int numberOfPair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_input);

        buttonReady = findViewById(R.id.buttonReady);
        input = findViewById(R.id.input);

        buttonReady.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ConnectivityManager connectivityManager
                        = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo == null && !activeNetworkInfo.isConnected()) {
                    startActivity(new Intent(UserInputActivity.this, MainActivity.class));
                } else {
                    if (isValidInput()) {
                        Intent intent = new Intent(UserInputActivity.this, GameActivity.class);
                        intent.putExtra("numberOfPairs", numberOfPair);
                        input.getText().clear();
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private boolean isValidInput() {
        String stringInput = input.getText().toString();
        if (stringInput.matches("[0-9]+")) {
            numberOfPair = Integer.parseInt(stringInput);
            if (numberOfPair > 1) {
                if (numberOfPair < 21) {
                    if (!isOnlyDividableBy2()) {
                            return true;
                    } else if (numberOfPair < 11 && isOnlyDividableBy2()) {
                        return true;
                    } else {
                        toast = Toast.makeText(this, "Input should be a multiple of 2 or 3 or smaller or equal to 10", Toast.LENGTH_SHORT);
                    }
                } else {
                    toast = Toast.makeText(this, "Input must be smaller or equal to 20", Toast.LENGTH_SHORT);
                }
            } else {
                toast = Toast.makeText(this, "Input must be bigger than 1", Toast.LENGTH_SHORT);
            }
        } else {
            toast = Toast.makeText(this, "Input is not a positive integer", Toast.LENGTH_SHORT);
        }
        toast.show();
        return false;
    }

    private boolean isOnlyDividableBy2() {
       if (numberOfPair * 2 % 3 > 0 && numberOfPair * 2 % 4 > 0) {
           return true;
       }
       return false;
    }
}
