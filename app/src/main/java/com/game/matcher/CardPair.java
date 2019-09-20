package com.game.matcher;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class CardPair {
    private Drawable frontImage;
    //mode: 0, none of the pair flipped
    //mode: 1, first card flipped
    //mode: 2, second card flipped
    //mode: 3, both flipped
    private int mode;
    private int firstID;
    private int secondID;

    public CardPair(Context context, int ID1, int ID2) {
        mode = 0;
        firstID = ID1;
        secondID = ID2;
    }

    public int getFirstID() {
        return firstID;
    }
    public int getSecondID() {
        return secondID;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getMode () {
        return mode;
    }

    public void setFrontImage(Drawable frontImage) {
        this.frontImage = frontImage;
    }

    public Drawable getFrontImage() {
        return frontImage;
    }
}
