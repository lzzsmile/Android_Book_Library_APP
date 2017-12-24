package com.example.android.booklibrary;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.media.session.MediaSession;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.v7.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String DEFAULT_QUERY = "android";

    private static final String API_BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    private static final String API_BASE_END = "&fields=kind,items(volumeInfo(title,authors,imageLinks(smallThumbnail),infoLink))";

    private String Url;

    private BookAdapter adapter;

    private static final int BOOK_LOADER_ID = 1;

    private TextView emptyTextView;

    private ProgressBar pb;

    private boolean UserInfoLoaderInitialize = false;

//    private ConnectivityManager cm;
//
//    private NetworkInfo activeNetwork;
//
//    private LoaderManager loaderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView bookListView = (ListView) findViewById(R.id.book_list);
        adapter = new BookAdapter(this, new ArrayList<Book>());
        bookListView.setAdapter(adapter);
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Book currentBook = adapter.getItem(position);
                try {
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentBook.getInfoUrl()));
                    startActivity(websiteIntent);
                } catch (NullPointerException e) {
                    Toast.makeText(MainActivity.this, "No corresponding link!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        emptyTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyTextView);

        pb = (ProgressBar) findViewById(R.id.progress_bar);
        pb.setVisibility(View.INVISIBLE);
        emptyTextView.setText(R.string.insert_key);

        final EditText searchKey = (EditText) findViewById(R.id.query_keyword);
        Button search = (Button) findViewById(R.id.search_submit);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pb.setVisibility(View.VISIBLE);
                emptyTextView.setText("");
                String query = searchKey.getText().toString();
                fetchBooks(query);
            }
        });
    }


    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        Log.i(LOG_TAG, "Start onCreateLoader");
        Loader<List<Book>> loader = new BookLoader(this, Url);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        Log.i(LOG_TAG, "Start onLoadFinished");

        pb.setVisibility(View.INVISIBLE);

        emptyTextView.setText(R.string.no_books);
        adapter.clear();
        if (books != null && !books.isEmpty()) {
            adapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        Log.i(LOG_TAG, "Start onLoaderReset");
        adapter.clear();
    }

    private void fetchBooks(String query) {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        Url = API_BASE_URL + query + API_BASE_END;
        Log.i(LOG_TAG, Url);

        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            LoaderManager loaderManager = getLoaderManager();
            if (!UserInfoLoaderInitialize) {
                Log.i(LOG_TAG, "Start initLoader");
                loaderManager.initLoader(BOOK_LOADER_ID, null, MainActivity.this);
                UserInfoLoaderInitialize = true;
            } else {
                loaderManager.restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
            }

        } else {
            pb = (ProgressBar) findViewById(R.id.progress_bar);
            pb.setVisibility(View.INVISIBLE);
            emptyTextView.setText(R.string.no_internet);
        }


    }

}
