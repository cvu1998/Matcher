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

    public CardPair(Context context, Drawable image) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.tinder);
        backImage = new BitmapDrawable(context.getResources(), bitmap);;
        frontImage = image;
        openned = false;
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

    public Drawable getFrontImage() {
        return frontImage;
    }
}
