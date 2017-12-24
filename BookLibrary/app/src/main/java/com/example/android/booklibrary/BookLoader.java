package com.example.android.booklibrary;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by zhuangzhili on 2017-12-13.
 */

public class BookLoader extends AsyncTaskLoader<List<Book>> {

    private static final String LOG_TAG = BookLoader.class.getName();

    private String Url;

    public BookLoader(Context context, String url) {
        super(context);
        Url = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "Start onStartLoading");
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        Log.i(LOG_TAG, "Start loadInBackground");
        if (Url == null) {
            return null;
        }

        List<Book> books = QueryUtils.extractBooks(Url);
        return books;
    }

}
