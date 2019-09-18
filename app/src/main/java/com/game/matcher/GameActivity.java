package com.game.matcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private ArrayList<ImageButton> buttons;
    private ArrayList<CardPair> cardsPairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        generateCardsImage(10);
        shuffleCards(10);
        for (CardPair p : cardsPairs) {
            Log.d("Pairs", Integer.toString(p.getFirstID()) + " and " + Integer.toString(p.getSecondID()));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Clear everything
        //buttons.clear();
        cardsPairs.clear();
    }

    private void shuffleCards(int numberOfCards){
        ArrayList<Integer> cardsID = new ArrayList<Integer>();
        for(int i = 0; i < numberOfCards; ++i) {
            cardsID.add(i);
        }
        int nRolls = 0;
        int ID1 = -1;
        int ID2 = -1;
        Random rnd = new Random();
        while(cardsPairs.size() < (numberOfCards / 2)) {
            if (nRolls == 0) {
                int index = rnd.nextInt(cardsID.size());
                ID1 = cardsID.get(index);
                cardsID.remove(index);
                ++nRolls;
            } else if (nRolls == 1) {
                int index = rnd.nextInt(cardsID.size());
                ID2 = cardsID.get(index);
                cardsID.remove(index);
                ++nRolls;
            } else if (ID1 != -1 && ID2 != -1) {
                cardsPairs.add(new CardPair(this, ID1, ID2));
                nRolls = 0;
            }
        }
    }

    private void generateCardsImage(int numberOfCards){
        cardsPairs = new ArrayList<CardPair>();
    }

    private void setCards(int numberOfCards) {
        /*buttons = new ArrayList<ImageButton>();
        for(int i = 0; i < numberOfCards; ++i) {
            ImageButton btn = new ImageButton(this);
            btn.setImageDrawable(cardsPairs.get(i).getBackImage());
            buttons.add(btn);
        }*/
    }
}
