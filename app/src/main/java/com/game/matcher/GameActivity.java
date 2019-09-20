package com.game.matcher;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

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

    private int cardsFlipped = 0;
    private ArrayList<ImageButton> buttons;
    private ArrayList<Integer> cardsFlippedIndex;
    private ArrayList<CardPair> cardsPairs;
    private Drawable drawable;
    private ArrayList<String> ImagesURLs;
    private ArrayList<Integer> mapCardsToButtons;
    private ImageButton b0;
    private ImageButton b1;
    private ImageButton b2;
    private ImageButton b3;
    private ImageButton b4;
    private ImageButton b5;
    private ImageButton b6;
    private ImageButton b7;
    private ImageButton b8;
    private ImageButton b9;
    private boolean isDone;
    private int urlIndex;

    private GetJsonData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        shuffleCards(10);
        generateCardsFrontImage(10);
        setCards(10);
        for (CardPair p : cardsPairs) {
            Log.d("Pairs", Integer.toString(p.getFirstID()) + " and " + Integer.toString(p.getSecondID()));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Clear everything
        buttons.clear();
        cardsFlippedIndex.clear();
        cardsPairs.clear();
        ImagesURLs.clear();
        mapCardsToButtons.clear();

        if (data.getStatus() == AsyncTask.Status.RUNNING) {
            Log.d("Async", "running");
        }
        else if (data.getStatus() == AsyncTask.Status.FINISHED){
            Log.d("Async", "cancelled");
        }
    }

    private void shuffleCards(int numberOfCards){
        cardsPairs = new ArrayList<CardPair>();
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
                ID1 = -1;
                ID2 = -1;
                nRolls = 0;
            }
        }
    }

    private void generateCardsFrontImage(int numberOfCards){
        ImagesURLs = new ArrayList<String>();
        data = new GetJsonData();
        data.execute();
        isDone = false;
        while(!isDone){Log.d("isDone", "false");}
        for (String s : ImagesURLs) {
            Log.d("ImagesUrls", s);
        }
        Random rnd = new Random();
        for (int i = 0; i < numberOfCards / 2; ++i) {
            int index = rnd.nextInt(ImagesURLs.size());
            urlIndex = index;
            DownloadImageTask task = new DownloadImageTask();
            task.execute();
            isDone = false;
            while(!isDone){Log.d("isDone", "false");}
            cardsPairs.get(i).setFrontImage(drawable);
            ImagesURLs.remove(index);
        }
    }

    private void setCards(int numberOfCards) {
        buttons = new ArrayList<ImageButton>();
        cardsFlippedIndex = new ArrayList<Integer>();
        mapCardsToButtons = new ArrayList<Integer>() ;
        int index = -1;
        for (int i = 0; i < numberOfCards; ++i) {
            index = getMatchingID(i);
            if (index > -1) {
                mapCardsToButtons.add(index);
            }
        };
        setCardsFlipping();
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
        b0 = findViewById(R.id.Button0);
        b0.setImageResource(R.drawable.logo);
        b1 = findViewById(R.id.Button1);
        b1.setImageResource(R.drawable.logo);
        b2 = findViewById(R.id.Button2);
        b2.setImageResource(R.drawable.logo);
        b3 = findViewById(R.id.Button3);
        b3.setImageResource(R.drawable.logo);
        b4 = findViewById(R.id.Button4);
        b4.setImageResource(R.drawable.logo);
        b5 = findViewById(R.id.Button5);
        b5.setImageResource(R.drawable.logo);
        b6 = findViewById(R.id.Button6);
        b6.setImageResource(R.drawable.logo);
        b7 = findViewById(R.id.Button7);
        b7.setImageResource(R.drawable.logo);
        b8 = findViewById(R.id.Button8);
        b8.setImageResource(R.drawable.logo);
        b9 = findViewById(R.id.Button9);
        b9.setImageResource(R.drawable.logo);

        b0.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { ;
                cardsFlippedIndex.add(0);
                int index = mapCardsToButtons.get(0);
                updateCardsPairs(0, cardsPairs.get(index), b0, buttons.get(cardsPairs.get(index).getSecondID()));
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cardsFlippedIndex.add(1);
                int index = mapCardsToButtons.get(1);
                updateCardsPairs(1, cardsPairs.get(index), b1, buttons.get(cardsPairs.get(index).getSecondID()));
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cardsFlippedIndex.add(2);
                int index = mapCardsToButtons.get(2);
                updateCardsPairs(2, cardsPairs.get(index), b2, buttons.get(cardsPairs.get(index).getSecondID()));
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cardsFlippedIndex.add(3);
                int index = mapCardsToButtons.get(3);
                updateCardsPairs(3, cardsPairs.get(index), b3, buttons.get(cardsPairs.get(index).getSecondID()));
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cardsFlippedIndex.add(4);
                int index = mapCardsToButtons.get(4);
                updateCardsPairs(4, cardsPairs.get(index), b4, buttons.get(cardsPairs.get(index).getSecondID()));
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cardsFlippedIndex.add(5);
                int index = mapCardsToButtons.get(5);
                updateCardsPairs(5, cardsPairs.get(index), b5, buttons.get(cardsPairs.get(index).getSecondID()));
            }
        });
        b6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cardsFlippedIndex.add(6);
                int index = mapCardsToButtons.get(6);
                updateCardsPairs(6, cardsPairs.get(index), b6, buttons.get(cardsPairs.get(index).getSecondID()));
            }
        });
        b7.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cardsFlippedIndex.add(7);
                int index = mapCardsToButtons.get(7);
                updateCardsPairs(7, cardsPairs.get(index), b7, buttons.get(cardsPairs.get(index).getSecondID()));
            }
        });
        b8.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cardsFlippedIndex.add(8);
                int index = mapCardsToButtons.get(8);
                updateCardsPairs(8, cardsPairs.get(index), b8, buttons.get(cardsPairs.get(index).getSecondID()));
            }
        });
        b9.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cardsFlippedIndex.add(9);
                int index = mapCardsToButtons.get(9);
                updateCardsPairs(9, cardsPairs.get(index), b9, buttons.get(cardsPairs.get(index).getSecondID()));
            }
        });
        buttons.add(b0);
        buttons.add(b1);
        buttons.add(b2);
        buttons.add(b3);
        buttons.add(b4);
        buttons.add(b5);
        buttons.add(b6);
        buttons.add(b7);
        buttons.add(b8);
        buttons.add(b9);
    }

    private void updateCardsPairs(int iD, CardPair pair, ImageButton firstCard, ImageButton secondCard) {
        if (iD == pair.getFirstID()) {
            if (pair.getMode() == 0) {
                firstCard.setImageDrawable(pair.getFrontImage());
                firstCard.setScaleType(ImageView.ScaleType.FIT_CENTER);
                firstCard.setEnabled(false);
                pair.setMode(1);
            } else if (pair.getMode() == 2) {
                firstCard.setImageDrawable(pair.getFrontImage());
                firstCard.setScaleType(ImageView.ScaleType.FIT_CENTER);
                firstCard.setEnabled(false);
                secondCard.setEnabled(false);
                pair.setMode(3);;
            }
        }
        else {
            if (pair.getMode() == 0) {
                secondCard.setImageDrawable(pair.getFrontImage());
                secondCard.setScaleType(ImageView.ScaleType.FIT_CENTER);
                secondCard.setEnabled(false);
                pair.setMode(2);
            } else if (pair.getMode() == 1) {
                secondCard.setImageDrawable(pair.getFrontImage());
                secondCard.setScaleType(ImageView.ScaleType.FIT_CENTER);
                firstCard.setEnabled(false);
                secondCard.setEnabled(false);
                pair.setMode(3);
            }
        }
        ++cardsFlipped;
        if (cardsFlipped == 2) {
            if (pair.getMode() != 3) {
                flipCardsBack();
            } else {
                cardsFlipped = 0;
                cardsFlippedIndex.clear();
            }
        }
    }

    private void flipCardsBack() {
        CardPair pair0 =  cardsPairs.get(mapCardsToButtons.get(cardsFlippedIndex.get(0)));
        CardPair pair1 =  cardsPairs.get(mapCardsToButtons.get(cardsFlippedIndex.get(1)));
        buttons.get(cardsFlippedIndex.get(0)).setImageResource(R.drawable.logo);
        buttons.get(cardsFlippedIndex.get(0)).setScaleType(ImageView.ScaleType.FIT_XY);
        buttons.get(cardsFlippedIndex.get(0)).setEnabled(true);
        buttons.get(cardsFlippedIndex.get(1)).setImageResource(R.drawable.logo);
        buttons.get(cardsFlippedIndex.get(1)).setScaleType(ImageView.ScaleType.FIT_XY);
        buttons.get(cardsFlippedIndex.get(1)).setEnabled(true);
        pair0.setMode(0);
        pair1.setMode(0);
        cardsFlipped = 0;
        cardsFlippedIndex.clear();
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
            Log.d("waiting", "doInBackground");
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            Log.d("waiting", "onPostExecute");
            super.onPostExecute(v);
        }

        @Override
        protected void onCancelled() {
            handleOnCancelled();
        }

        private void getData() throws IOException, JSONException {
            JSONObject json = readJsonFromUrl("https://shopicruit.myshopify.com/admin/products.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6");
            try {
                products = json.getJSONArray("products");
                for (int i = 0; i < products.length(); ++i) {
                    ImagesURLs.add(products.getJSONObject(i).getJSONObject("image").getString("src"));
                }
                isDone = true;
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

        private void handleOnCancelled() {
            ImagesURLs.clear();
            Log.d("waiting", "onCancelled");
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Void> {
        Bitmap bitmap;

        @Override
        protected Void doInBackground(String... strings) {
            String url = ImagesURLs.get(urlIndex);
            bitmap = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(in);
                drawable = new BitmapDrawable(getResources(), bitmap);
                isDone = true;
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}