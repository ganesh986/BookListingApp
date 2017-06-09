package com.example.android.booklistingapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BookListLoader extends AsyncTaskLoader<List<BookList>> {
    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = BookListLoader.class.getName();

    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link BookListLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public BookListLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<BookList> loadInBackground() {

        List<BookList> books = new ArrayList<>();

        try {
            //making url
            URL obj = new URL(mUrl);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            //reading and adding to stringBuilder
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Perform the network request, parse the response, and extract a list of earthquakes.
            List<BookList> book = QueryUtils.fetchBookListData(mUrl);
            return book;
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return books;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}