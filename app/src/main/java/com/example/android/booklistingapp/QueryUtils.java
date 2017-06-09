package com.example.android.booklistingapp;


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
 * Helper methods related to requesting and receiving book data from USGS.
 */
public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the google API and return a list of {@link BookList} objects.
     */
    public static List<BookList> fetchBookListData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link BookList}s
        List<BookList> booklist = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link BookList}s
        return booklist;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
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

    /**
     * Return a list of {@link BookList} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<BookList> extractFeatureFromJson(String bookJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding booklist to
        List<BookList> booklist = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(bookJSON);

            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or booklist).
            JSONArray booklistArray = baseJsonResponse.getJSONArray("items");

            // For each book in the booklistArray, create an {@link BookList} object
            for (int i = 0; i < booklistArray.length(); i++) {

                // Get a single book at position i within the list of booklist
                JSONObject currentBookList = booklistArray.getJSONObject(i);

                // For a given book, extract the JSONObject associated with the
                // key called volumeInfo, which represents a list of all properties
                // for that book.
                JSONObject volumeinfo = currentBookList.getJSONObject("volumeInfo");

                // Extract the value for the key called "title"
                String title = volumeinfo.getString("title");

                // Extract the value for the key called "subtitle"
                String subtitle = volumeinfo.optString("subtitle", "");

                // Extract the value for the key called "authors"
                String author = "";
                if (volumeinfo.has("authors")) {
                    JSONArray a = volumeinfo.getJSONArray("authors");

                    for (int g = 0; g < a.length(); g++) {

                        author += a.getString(g) + ", ";
                    }
                    author = author.substring(0, author.length() - 2);
                } else {
                    author = "";
                }
                // Extract the value for the key called "publisher"
                String editor = volumeinfo.optString("publisher", "");

                // For a given book, extract the JSONObject associated with the
                // key called volumeInfo, which represents a list of all properties
                // for that book.
                String image = "";
                if (volumeinfo.has("imageLinks")) {
                    JSONObject imagelinks = volumeinfo.getJSONObject("imageLinks");

                    // Extract the url for the key called "thumbnail"
                    image = imagelinks.optString("thumbnail", "");
                } else {
                    image = "NO_IMAGE";
                }
                // For a given book, extract the JSONObject associated with the
                // key called saleInfo, which represents a list of all properties
                // for that book.
                JSONObject saleinfo = currentBookList.getJSONObject("saleInfo");

                // Extract the value for the key called "buyLink"
                String url = saleinfo.optString("buyLink", "" + R.string.not_available);

                // Create a new {@link BookList} object with the magnitude, location, time,
                // and url from the JSON response.
                BookList book = new BookList(image, title, subtitle, author, editor, url);

                // Add the new {@link BookList} to the list of booklist.
                booklist.add(book);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }

        // Return the list of booklist
        return booklist;
    }

}
