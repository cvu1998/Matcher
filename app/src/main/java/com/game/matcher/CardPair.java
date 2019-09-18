package com.game.matcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class CardPair {
    private Drawable backImage;
    private Drawable frontImage;
    private boolean openned;
    private int firstID;
    private int secondID;

    public CardPair(Context context, int ID1, int ID2) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.tinder);
        backImage = new BitmapDrawable(context.getResources(), bitmap);
        openned = false;
        firstID = ID1;
        secondID = ID2;
    }

    public int getFirstID() {
        return firstID;
    }
    public int getSecondID() {
        return secondID;
    }

    public void setOpenned(boolean bool) {
        openned = bool;
    }

    public boolean isOpenned() {
        return openned;
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
