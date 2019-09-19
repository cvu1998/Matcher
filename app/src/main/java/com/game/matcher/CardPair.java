package com.game.matcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class CardPair {
    private Drawable backImage;
    private Drawable frontImage;
    //mode: 0, none of the pair flipped
    //mode: 1, first card flipped
    //mode: 2, second card flipped
    //mode: 3, both flipped
    private int mode;
    private int firstID;
    private int secondID;

    public CardPair(Context context, int ID1, int ID2) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
        backImage = new BitmapDrawable(context.getResources(), bitmap);
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.tinder);
        frontImage = new BitmapDrawable(context.getResources(), b);
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

    public Drawable getBackImage() {
        return backImage;
    }

    public void setFrontImage(Drawable frontImage) {
        this.frontImage = frontImage;
    }

    public Drawable getFrontImage() {
        return frontImage;
    }
}
