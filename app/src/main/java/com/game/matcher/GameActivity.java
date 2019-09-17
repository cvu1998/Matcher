package com.game.matcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ImageButton;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    private ArrayList<ImageButton> buttons;
    private ArrayList<CardPair> cardsPairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void generateCards(int numberOfCards){

    }

    private void shuffleCards(int numberOfCards){

    }

    private void setCards(int numberOfCards) {
        buttons = new ArrayList<ImageButton>();
        cardsPairs = new ArrayList<CardPair>();
        for(int i = 0; i < numberOfCards; ++i) {
            ImageButton btn = new ImageButton(this);
            btn.setImageDrawable(cardsPairs.get(i).getBackImage());
            buttons.add(btn);
        }
    }
}
