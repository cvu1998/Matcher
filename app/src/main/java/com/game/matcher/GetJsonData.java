package com.game.matcher;

import android.os.AsyncTask;
import android.util.Log;

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

public class GetJsonData extends AsyncTask<Void, Void, Void> {

    private JSONArray products;
    private ArrayList<String> ImagesURLs;
    private boolean isDone;

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
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    private void getData() throws IOException, JSONException {
        JSONObject json = readJsonFromUrl("https://shopicruit.myshopify.com/admin/products.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6");
        try {
            isDone = false;
            ImagesURLs = new ArrayList<String>();
            products = json.getJSONArray("products");
            for (int i = 0; i < products.length(); ++i) {
                ImagesURLs.add(products.getJSONObject(i).getJSONObject("image").getString("src"));
                if (isCancelled())
                    break;
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

    public ArrayList<String> getImagesURls() {
        while (!isDone) { }
        return ImagesURLs;
    }
}
