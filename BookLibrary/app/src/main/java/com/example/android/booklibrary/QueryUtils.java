package com.example.android.booklibrary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuangzhili on 2017-12-13.
 */

public final class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    public static List<Book> extractBooks (String requestUrl) {
        URL ReqUrl = createUrl(requestUrl);

        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(ReqUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        List<Book> books = extractFeatureFromJSON(jsonResponse);
        return books;
    }

    private static List<Book> extractFeatureFromJSON(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        List<Book> books = new ArrayList<>();

        try {

            JSONObject bookInfo = new JSONObject(jsonResponse);
            JSONArray bookItems = bookInfo.getJSONArray("items");
            for (int j = 0; j < bookItems.length(); j++) {
                JSONObject item = bookItems.getJSONObject(j);
                JSONObject volume = item.getJSONObject("volumeInfo");
                String title = volume.getString("title");
                String author = getAuthor(volume);
                String imageUrl = getImageUrl(volume);
                String infoUrl = volume.getString("infoLink");
                Book book = new Book(title, author, imageUrl, infoUrl);
                books.add(book);
            }
            return books;

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the Book JSON results", e);
        }

        return null;
    }

    private static String getAuthor(JSONObject volume) {
        try {
            final JSONArray authors = volume.getJSONArray("authors");
            int numAuthors = authors.length();
            final String[] authorStrings = new String[numAuthors];
            for (int i = 0; i < numAuthors; ++i) {
                authorStrings[i] = authors.getString(i);
            }
            return "by " + TextUtils.join(", ", authorStrings);
        } catch (JSONException e) {
            return "by ";
        }
    }

    private static String getImageUrl(JSONObject volume) {
        try {
            final JSONObject image = volume.getJSONObject("imageLinks");
            String imageUrl = image.getString("smallThumbnail");
            return imageUrl;
        } catch (JSONException e) {
            return null;
        }
    }

    private static String getInfoUrl(JSONObject volume) {
        try {
            String infoUrl = volume.getString("infoLink");
            return infoUrl;
        } catch (JSONException e) {
            return null;
        }
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code" + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


}
