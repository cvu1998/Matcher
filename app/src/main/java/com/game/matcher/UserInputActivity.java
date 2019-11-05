package com.game.matcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class UserInputActivity extends AppCompatActivity {

    private ImageButton buttonReady;
    private TextInputEditText input;
    private TextView invalidInput;
    private int numberOfPair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_input);

        buttonReady = findViewById(R.id.buttonReady);
        input = findViewById(R.id.input);
        invalidInput = findViewById(R.id.invalid);

        buttonReady.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToGameActivity();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }

    //Function used to send the user to GradeActivty
    private void goToGameActivity() {
        if (isValidInput()) {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("numberOfPairs", numberOfPair);
            input.getText().clear();
            startActivity(intent);
        }
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
                        invalidInput.setText("Input should be a multiple of 2 or 3 or smaller or equal to 10");
                    }
                } else {
                    invalidInput.setText("Input must be smaller or equal to 20");
                }
            } else {
                invalidInput.setText("Input must be bigger than 1");
            }
        } else {
            invalidInput.setText("Input is not a positive integer");
        }
        invalidInput.setVisibility(View.VISIBLE);
        return false;
    }

    private boolean isOnlyDividableBy2() {
       if (numberOfPair * 2 % 3 > 0 && numberOfPair * 2 % 4 > 0) {
           return true;
       }
       return false;
    }
}
