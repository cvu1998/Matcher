package com.game.matcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
    private ArrayList<Button> buttons;
    private ArrayList<Integer> cardsFlippedIndex;
    private ArrayList<CardPair> cardsPairs;
    private Drawable drawable;
    private ArrayList<String> ImagesURLs;
    private ArrayList<Integer> mapCardsToButtons;
    private boolean isDone;
    private int matched;
    private TableLayout cardsTable;
    private int urlIndex;
    private TextView victory;
    private ImageButton returnMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        cardsTable = findViewById(R.id.cardsTable);
        victory = findViewById(R.id.victory);
        returnMain = findViewById(R.id.returnMain);

        matched = 0;
        int numberOfCards = 36;

        shuffleCards(numberOfCards);
        generateCardsFrontImage(numberOfCards);
        setCards(numberOfCards);
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
            if (cardsID.size() > 0 && nRolls < 2) {
                int index = rnd.nextInt(cardsID.size());
                if (nRolls == 0) {
                    ID1 = cardsID.get(index);
                } else if (nRolls == 1) {
                    ID2 = cardsID.get(index);
                }
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
        new GetJsonData().execute();
        isDone = false;
        while(!isDone){Log.d("isDone", "false");}
        Random rnd = new Random();
        for (int i = 0; i < numberOfCards / 2; ++i) {
            urlIndex = rnd.nextInt(ImagesURLs.size());
            new DownloadImageTask().execute();
            isDone = false;
            while(!isDone){Log.d("isDone", "false");}
            cardsPairs.get(i).setFrontImage(drawable);
            ImagesURLs.remove(urlIndex);
        }
    }

    private void setCards(int numberOfCards) {
        buttons = new ArrayList<Button>();
        cardsFlippedIndex = new ArrayList<Integer>();
        mapCardsToButtons = new ArrayList<Integer>() ;
        int index = -1;
        for (int i = 0; i < numberOfCards; ++i) {
            index = getMatchingID(i);
            if (index > -1) {
                mapCardsToButtons.add(index);
            }
        };
        setCardsFlipping(numberOfCards);
    }

    private int getMatchingID(int index) {
        for (int i = 0; i < cardsPairs.size(); ++i) {
            if (cardsPairs.get(i).getFirstID() == index || cardsPairs.get(i).getSecondID() == index) {
                return i;
            }
        }
        return -1;
    }

    private void setCardsFlipping(int numberOfCards) {
        int z = 0;
        float scale = getResources().getDisplayMetrics().density;
        int height = 260;
        int padding= 20;
        int nRowNElements[] = new int[2];
        nRowNElements = findOptimalTableLayoutDisplay(numberOfCards);
        for (int i = 0; i < nRowNElements[0]; ++i) {
            TableRow tableRow = findViewById(R.id.row1);
            if (i == 1) {
                tableRow = findViewById(R.id.row2);
            }
            else if (i == 2) {
                tableRow = findViewById(R.id.row3);
            }
            else if (i == 3){
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
                        cardsFlippedIndex.add(buttonIndex);
                        int index = mapCardsToButtons.get(buttonIndex);
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
        nRowNElements [1] = nElementsInRow;
        return nRowNElements;
    }

    private void updateCardsPairs(int iD, CardPair pair, Button firstCard, Button secondCard) {
        if (iD == pair.getFirstID()) {
            if (pair.getMode() == 0) {
                firstCard.setBackground(pair.getFrontImage());
                firstCard.setEnabled(false);
                pair.setMode(1);
            } else if (pair.getMode() == 2) {
                firstCard.setBackground(pair.getFrontImage());
                firstCard.setEnabled(false);
                secondCard.setEnabled(false);
                pair.setMode(3);;
            }
        }
        else {
            if (pair.getMode() == 0) {
                secondCard.setBackground(pair.getFrontImage());
                secondCard.setEnabled(false);
                pair.setMode(2);
            } else if (pair.getMode() == 1) {
                secondCard.setBackground(pair.getFrontImage());
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
                ++matched;
                if (matched == cardsPairs.size())
                {
                    victory.setVisibility(View.VISIBLE);
                    returnMain.setVisibility(View.VISIBLE);
                    returnMain.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            goToMain();
                        }
                    });
                }
                cardsFlipped = 0;
                cardsFlippedIndex.clear();
            }
        }
    }

    private void flipCardsBack() {
        CardPair pair0 =  cardsPairs.get(mapCardsToButtons.get(cardsFlippedIndex.get(0)));
        CardPair pair1 =  cardsPairs.get(mapCardsToButtons.get(cardsFlippedIndex.get(1)));
        buttons.get(cardsFlippedIndex.get(0)).setBackgroundResource(R.drawable.logo);
        buttons.get(cardsFlippedIndex.get(0)).setEnabled(true);
        buttons.get(cardsFlippedIndex.get(1)).setBackgroundResource(R.drawable.logo);
        buttons.get(cardsFlippedIndex.get(1)).setEnabled(true);
        pair0.setMode(0);
        pair1.setMode(0);
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