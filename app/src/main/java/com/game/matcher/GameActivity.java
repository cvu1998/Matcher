package com.game.matcher;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private ArrayList<Button> buttons;
    private ArrayList<CardPair> cardsPairs;
    private ArrayList<Integer> cardsFlippedIndex;
    private ArrayList<Integer> mapCardsToButtons;
    private ArrayList<String> ImagesURLs;

    private boolean started;
    private long pauseTime;
    private long resumeTime;

    private int numberOfCards;
    private int cardsFlipped;
    private int matched;
    private int urlIndex;
    private long startTime;
    final private String TAG = "GAME";

    private AsyncTask getJson;
    private AsyncTask getImages;
    private Handler handler;

    private TextView score;
    private TextView state;
    private TextView timer;
    private ImageButton returnMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        score = findViewById(R.id.score);
        state = findViewById(R.id.state);
        timer = findViewById(R.id.timer);
        returnMain = findViewById(R.id.returnMain);
        handler = new Handler();

        started = false;
        cardsFlipped = 0;
        matched = 0;
        numberOfCards = 2 * getIntent().getIntExtra("numberOfPairs",2);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        generateRandomPairs();
        generateCardsFrontImage();

        for(CardPair cp : cardsPairs) {
            Log.d(TAG, (cp.getFirstID()) + " " + (cp.getSecondID()));
        }

        startTime = System.currentTimeMillis();
        pauseTime = 0;
        resumeTime = 0;

        handler.postDelayed(timerRunnable, 0);
        handler.postDelayed(networkCheck, 0);
    }

    Runnable networkCheck = new Runnable() {

        @Override
        public void run() {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(GameActivity.this.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
                Toast.makeText(GameActivity.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(GameActivity.this, MainActivity.class));
            } else {
                handler.postDelayed(this, 1000);
            }
        }
    };

    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            int loadTime = 3;

            long millis = System.currentTimeMillis() - startTime - (resumeTime - pauseTime);
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            if (seconds > loadTime && !started) {
                started = true;
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else if (seconds > loadTime && started) {
                timer.setText("Time: " + String.format("%d:%02d", minutes, seconds - loadTime));
            } else {
                timer.setText("Start In: " +(loadTime - seconds));
            }

            handler.postDelayed(this, 500);
        }
    };

    @Override
    public void onRestart() {
        Log.d(TAG, "Resumed!");
        resumeTime = System.currentTimeMillis();

        if (matched != cardsPairs.size()) {
            handler.postDelayed(timerRunnable, 500);
            handler.postDelayed(networkCheck, 1000);
        }
        super.onRestart();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "Paused!");
        pauseTime = System.currentTimeMillis();

        if (!started) {
            Log.d(TAG, "Restarted!");
            if (getJson != null) {
                if (getJson.getStatus() == AsyncTask.Status.RUNNING) {
                    Log.d("onPause", "getJson cancelled!");
                    getJson.cancel(true);
                }
            }
            if (getImages != null) {
                if (getImages.getStatus() == AsyncTask.Status.RUNNING) {
                    Log.d(TAG, "getImages cancelled!");
                    getImages.cancel(true);
                }
            }

            startActivity(new Intent(this, UserInputActivity.class));
            finish();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Stopped!");
        handler.removeCallbacksAndMessages(null);
        super.onStop();
    }

    private void generateRandomPairs(){
        cardsPairs = new ArrayList<>();
        ArrayList<Integer> cardsID = new ArrayList<>();
        for(int i = 0; i < numberOfCards; ++i) {
            cardsID.add(i);
        }
        int nRolls = 0;
        int ID1 = -1;
        int ID2 = -1;
        Random rnd = new Random();
        while(cardsPairs.size() < (numberOfCards / 2)) {
            if (cardsID.size() > 0 && nRolls < 2) {
                int index = rnd.nextInt(cardsID.size());
                if (nRolls == 0) {
                    ID1 = cardsID.get(index);
                } else if (nRolls == 1) {
                    ID2 = cardsID.get(index);
                }
                cardsID.remove(index);
                ++nRolls;
            } else if (ID1 > 0 -1 && ID2 > -1) {
                cardsPairs.add(new CardPair(this, ID1, ID2));
                ID1 = -1;
                ID2 = -1;
                nRolls = 0;
            }
        }
    }

    private void generateCardsFrontImage(){
        ImagesURLs = new ArrayList<>();
        getJson = new GetJsonData().execute();
        setCards();
        setCardsFlipping();
    }

    private void setCards() {
        buttons = new ArrayList<>();
        cardsFlippedIndex = new ArrayList<>();
        mapCardsToButtons = new ArrayList<>() ;
        for (int i = 0; i < numberOfCards; ++i) {
            int index = getMatchingID(i);
            if (index > -1) {
                mapCardsToButtons.add(index);
            }
        }
    }

    private int getMatchingID(int index) {
        for (int i = 0; i < cardsPairs.size(); ++i) {
            if (cardsPairs.get(i).getFirstID() == index || cardsPairs.get(i).getSecondID() == index) {
                return i;
            }
        }
        return -1;
    }

    private void setCardsFlipping() {
        int height = 260;
        int padding= 20;
        int z = 0;
        float scale = getResources().getDisplayMetrics().density;
        int nRowNElements[] = findOptimalTableLayoutDisplay(numberOfCards);
        for (int i = 0; i < nRowNElements[0]; ++i) {
            TableRow tableRow = findViewById(R.id.row1);
            if (i == 1) {
                tableRow = findViewById(R.id.row2);
            } else if (i == 2) {
                tableRow = findViewById(R.id.row3);
            } else if (i == 3){
                tableRow = findViewById(R.id.row4);
            }
            for (int j = 0; j < nRowNElements[1]; ++j) {
                Button card = new Button(this);
                card.setBackgroundResource(R.drawable.logo);

                int adjustedPadding = (int) (padding * scale + 0.5f) / nRowNElements[0];
                int adjustedHeight = (int) (height * scale + 0.5f) / nRowNElements[0];

                TableRow.LayoutParams params = new TableRow.LayoutParams(0, adjustedHeight, 1);
                params.setMargins(adjustedPadding,adjustedPadding,adjustedPadding,adjustedPadding);
                card.setLayoutParams(params);
                card.setVisibility(View.VISIBLE);

                card.setTag(z);
                card.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        int buttonIndex = (Integer)v.getTag();
                        int index = mapCardsToButtons.get(buttonIndex);
                        cardsFlippedIndex.add(buttonIndex);
                        updateCardsPairs(buttonIndex, cardsPairs.get(index), buttons.get(cardsPairs.get(index).getFirstID()), buttons.get(cardsPairs.get(index).getSecondID()));
                    }
                });
                buttons.add(card);
                tableRow.addView(card);
                ++z;
            }
        }
    }

    private int[] findOptimalTableLayoutDisplay(int numberOfCards) {
        int nRowNElements[] = new int[2];
        int nElementsInRow = Integer.MAX_VALUE;
        int row = 0;
        for (int i = 1; i < 5; ++i) {
            if (numberOfCards % i == 0) {
                if ((numberOfCards / i) >= 3 && (numberOfCards / i) < nElementsInRow) {
                    row = i;
                    nElementsInRow = numberOfCards / i;
                }
            }
        }
        nRowNElements[0] = row;
        nRowNElements[1] = nElementsInRow;
        return nRowNElements;
    }

    private void updateCardsPairs(int iD, CardPair pair, Button firstCard, Button secondCard) {
        if (iD == pair.getFirstID()) {
            if (pair.getMode() == 0) {
                pair.setMode(1);
            } else if (pair.getMode() == 2) {
                pair.setMode(3);
            }
            firstCard.setBackground(pair.getFrontImage());
            firstCard.setEnabled(false);
        }
        else {
            if (pair.getMode() == 0) {
                pair.setMode(2);
            } else if (pair.getMode() == 1) {
                pair.setMode(3);
            }
            secondCard.setBackground(pair.getFrontImage());
            secondCard.setEnabled(false);
        }
        ++cardsFlipped;
        if (cardsFlipped == 2) {
            if (pair.getMode() != 3) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        flipCardsBack();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                }, 500);
            } else {

                ++matched;
                score.setText("Score: " + matched + " / " + (cardsPairs.size()));

                if (matched == cardsPairs.size()) {

                    state.setText("YOU WIN!");
                    state.setVisibility(View.VISIBLE);
                    handler.removeCallbacks(timerRunnable);

                    returnMain.setVisibility(View.VISIBLE);

                    returnMain.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            goToMain();
                        }
                    });
                } else {
                    state.setText("IT'S A MATCH!");
                    state.setVisibility(View.VISIBLE);
                }
                cardsFlipped = 0;
                cardsFlippedIndex.clear();
            }
        }
    }

    private void flipCardsBack() {
        for (int i = 0; i < cardsFlippedIndex.size(); ++i) {
            cardsPairs.get(mapCardsToButtons.get(cardsFlippedIndex.get(i))).setMode(0);
            buttons.get(cardsFlippedIndex.get(i)).setBackgroundResource(R.drawable.logo);
            buttons.get(cardsFlippedIndex.get(i)).setEnabled(true);
        }
        state.setVisibility(View.INVISIBLE);
        cardsFlipped = 0;
        cardsFlippedIndex.clear();
    }

    private void goToMain() {
        this.finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    private class GetJsonData extends AsyncTask<Void, Void, Void> {

        private JSONArray products;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                getData();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
        }

        @Override
        protected void onCancelled() { }

        private void getData() throws IOException, JSONException {
            JSONObject json = readJsonFromUrl("https://shopicruit.myshopify.com/admin/products.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6");
            try {
                products = json.getJSONArray("products");
                for (int i = 0; i < products.length(); ++i) {
                    ImagesURLs.add(products.getJSONObject(i).getJSONObject("image").getString("src"));
                }

                //Set front image for cards
                Random rnd = new Random();
                for (int i = 0; i < cardsPairs.size(); ++i) {
                    urlIndex = rnd.nextInt(ImagesURLs.size() - cardsPairs.size());
                    getImages = new DownloadImageTask(i).execute();
                }
            } catch (JSONException e) {

                e.printStackTrace();
            }
        }

        private String readAll(Reader rd) throws IOException {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        }

        public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
            InputStream is = new URL(url).openStream();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                JSONObject json = new JSONObject(jsonText);
                return json;
            } finally {
                is.close();
            }
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Void> {
        Bitmap bitmap;
        int index;

        public DownloadImageTask(int index) {
            this.index = index;
        }

        @Override
        protected Void doInBackground(String... strings) {
            if (!ImagesURLs.isEmpty() && !cardsPairs.isEmpty()) {
                String url = ImagesURLs.get(urlIndex);
                bitmap = null;
                try {
                    InputStream in = new java.net.URL(url).openStream();
                    bitmap = BitmapFactory.decodeStream(in);
                    Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                    cardsPairs.get(index).setFrontImage(drawable);
                    ImagesURLs.remove(urlIndex);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onCancelled() {
            index = 0;
            urlIndex = 0;
        }
    }
}