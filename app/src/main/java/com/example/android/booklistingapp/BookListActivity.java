package com.example.android.booklistingapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<BookList>> {

    private final int LOADER_ID = 1;
    private final String BOOK_API_URL = "https://www.googleapis.com/books/v1/volumes";
    LoaderManager loaderManager;
    Button search;
    ListView listView;
    EditText searchEdiText;
    ProgressBar progressBar;
    private BookListAdapter bookDataAdapter;
    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        listView = (ListView) findViewById(R.id.list);
        search = (Button) findViewById(R.id.search_button);
        searchEdiText = (EditText) findViewById(R.id.search_edtx);
        progressBar = (ProgressBar) findViewById(R.id.loading_indicator);

        progressBar.setVisibility(View.GONE);

        loaderManager = getLoaderManager();
        loaderManager.initLoader(LOADER_ID, null, BookListActivity.this);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        listView.setEmptyView(mEmptyStateTextView);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyBoard();

                bookDataAdapter.clear();
                loaderManager.restartLoader(LOADER_ID, null, BookListActivity.this);
            }
        });

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected earthquake.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current book that was clicked on
                BookList currentBook = bookDataAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri bookUri = Uri.parse(currentBook.getUrl());
                if (URLUtil.isValidUrl(String.valueOf(bookUri))) {
                    // Create a new intent to view the earthquake URI
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                    // Send the intent to launch a new activity
                    startActivity(websiteIntent);
                } else {
                    Toast.makeText(BookListActivity.this, R.string.not_available,
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<BookList>> onCreateLoader(int id, Bundle args) {

        Uri baseUri = Uri.parse(BOOK_API_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        String bookName = searchEdiText.getText().toString();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String maxResult = "40";

        uriBuilder.appendQueryParameter("q", bookName);
        uriBuilder.appendQueryParameter("maxResults", maxResult);

        progressBar.setVisibility(View.VISIBLE);

        return new BookListLoader(this, uriBuilder.toString());
    }


    @Override
    public void onLoadFinished(Loader<List<BookList>> loader, List<BookList> data) {

        bookDataAdapter = new BookListAdapter(this, data);
        listView.setAdapter(bookDataAdapter);

        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void onLoaderReset(Loader<List<BookList>> loader) {
        bookDataAdapter = new BookListAdapter(this, new ArrayList<BookList>() {
        });

    }


    private void hideKeyBoard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
