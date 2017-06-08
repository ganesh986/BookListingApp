package com.example.android.booklistingapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class BookListLoader extends AsyncTaskLoader<List<BookList>> {
    /** Tag for log messages */
    private static final String LOG_TAG = BookListLoader.class.getName();

    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link BookListLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public BookListLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<BookList> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<BookList> book = QueryUtils.fetchBookListData(mUrl);
        return book;
    }
}