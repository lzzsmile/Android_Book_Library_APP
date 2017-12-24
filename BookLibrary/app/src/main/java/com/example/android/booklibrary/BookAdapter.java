package com.example.android.booklibrary;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by zhuangzhili on 2017-12-13.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    private static final String LOG_TAG = BookAdapter.class.getSimpleName();

    public BookAdapter(Activity context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Book currentBook = getItem(position);

        String title = currentBook.getTitle();
        TextView bookName = (TextView) listItemView.findViewById(R.id.book_name);
        bookName.setText(title);

        String authors = currentBook.getAuthors();
        TextView bookAuthor = (TextView) listItemView.findViewById(R.id.book_author);
        bookAuthor.setText(authors);


        ImageView bookImage = (ImageView) listItemView.findViewById(R.id.book_image);
        try {
            Picasso.with(getContext()).load(Uri.parse(currentBook.getImageUrl())).error(R.drawable.no_book_cover).into(bookImage);
        } catch (NullPointerException e) {
            Picasso.with(getContext()).load(R.drawable.no_book_cover).into(bookImage);
        }


        return listItemView;
    }

}
