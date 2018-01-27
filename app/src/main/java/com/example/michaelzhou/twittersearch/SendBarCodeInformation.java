package com.example.michaelzhou.twittersearch;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

@SuppressLint("NewApi")
public class SendBarCodeInformation extends AsyncTask<String, Void, ArrayList<String>> {
    @Override

    protected ArrayList<String> doInBackground (String... params) {
        String content = params[0];
        String input = params[1];

        System.out.println("Hello World!");
        String lat = Double.toString(38.897676);
        String lon = Double.toString(-77.036530);
        String max_range = Integer.toString(10);
        String postBody = "https://api.twitter.com/1.1/search/tweets.json?q=&geocode=" + lat + "," + lon + "," + max_range + "mi&count=100";

        String access_token = get_bearer();

        HttpURLConnection urlConnection = null;
        URL url;
        try {
            url = new URL(postBody);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", "Bearer " + access_token);
            urlConnection.setRequestProperty("Content-Length", String.valueOf(postBody.getBytes().length));
            urlConnection.setFixedLengthStreamingMode(postBody.getBytes().length);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);

            OutputStream output = urlConnection.getOutputStream();
            output.write(postBody.getBytes("UTF-8"));
            output.flush();
            output.close();

            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                JSONObject raw_data = new JSONObject(result.toString());
                JSONArray statuses = raw_data.getJSONArray("statuses");

                ArrayList<String> tweets = new ArrayList<>();

                for (int i = 0; i < statuses.length(); i++) {
                    JSONObject status = statuses.getJSONObject(i);
                    tweets.add(status.getString("text"));
                }
                is.close();
                result.close();
                return tweets;
            } else {
                Log.e("Twitter HTTP Error", urlConnection.getResponseCode() + urlConnection.getResponseMessage());
                InputStream is = urlConnection.getErrorStream();
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                Log.e("Twitter Feed Error", result.toString());
                is.close();
                result.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String get_bearer() {
        String authString = "2uSBRzRORaM2qBguhwPug7LSe:T42SCO6DZ084AKvcnbC5N7cdlQ60UW7SuSfsU4jCSheX6jpQVh";
        String basicAuth = "Basic " + Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);

        String postBody = "grant_type=client_credentials";

        HttpURLConnection urlConnection = null;
        URL url;
        try {
            url = new URL("https://api.twitter.com/oauth2/token");
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestProperty("Authorization", basicAuth);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            urlConnection.setRequestProperty("Content-Length", String.valueOf(postBody.getBytes().length));
            urlConnection.setRequestProperty("Accept-Encoding", "gzip");
            urlConnection.setFixedLengthStreamingMode(postBody.getBytes().length);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);

            OutputStream output = urlConnection.getOutputStream();
            output.write(postBody.getBytes("UTF-8"));
            output.flush();
            output.close();

            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                JSONObject j = new JSONObject(result.toString());
                is.close();
                result.close();
                return j.getString("access_token");
            } else {
                Log.e("Twitter HTTP Error", urlConnection.getResponseCode() + urlConnection.getResponseMessage());
                InputStream is = urlConnection.getErrorStream();
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                Log.e("Twitter Auth Error", result.toString());
                is.close();
                result.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
