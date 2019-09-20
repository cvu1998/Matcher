package com.game.matcher;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;

public class DownloadImageTask extends AsyncTask<Void, Void, Void> {
    Bitmap bitmap;
    String url;
    boolean isDone;

    public DownloadImageTask(String link) {
        this.url = link;
    }

    @Override
    protected Void doInBackground(Void... params) {
        bitmap = null;
        isDone = false;
        try {
            InputStream in = new java.net.URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(in);
            isDone = true;
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {super.onPostExecute(aVoid);}

    @Override
    protected void onCancelled(){bitmap = null;}

    public Bitmap getBitmap() {
        while (!isDone) {
            //Log.d("waiting", "waiting");
        }
        return bitmap;
    }
}