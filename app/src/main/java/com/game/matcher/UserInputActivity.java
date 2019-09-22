package com.game.matcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

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

        buttonReady = (ImageButton) findViewById(R.id.buttonReady);
        input = findViewById(R.id.input);
        invalidInput = findViewById(R.id.invalid);

        buttonReady.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToGameActivity();
            }
        });
    }

    //Function used to send the user to GradeActivty
    private void goToGameActivity() {
        if (isValidInput()) {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("numberOfPairs", numberOfPair);
            startActivity(intent);
        }
    }

    private boolean isValidInput() {
        CharSequence charSequence = input.getText();
        String stringInput = charSequence.toString();
        if (stringInput.length() < 3 && stringInput.matches("[0-9]+")) {
            numberOfPair = Integer.parseInt(stringInput);
            if (numberOfPair > 1) {
                if (!isOnlyDividableBy2() && numberOfPair < 25) {
                    return true;
                } else if (isOnlyDividableBy2() && numberOfPair < 11) {
                    return true;
                } else {
                    invalidInput.setText("Number of pairs too large (ideally number is multiple of 2 or 3)");
                    invalidInput.setVisibility(View.VISIBLE);
                    return false;
                }
            } else {
                invalidInput.setText("Input is too small");
            }
        }
        invalidInput.setText("Input contains too many characters (max 2) or is not an integer");
        invalidInput.setVisibility(View.VISIBLE);
        return false;
    }

    private boolean isOnlyDividableBy2() {
       if (numberOfPair % 3 > 0 && numberOfPair % 4 > 0) {
           return true;
       }
       return false;
    }
}
