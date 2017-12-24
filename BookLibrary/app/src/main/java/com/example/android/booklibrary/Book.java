package com.example.android.booklibrary;

/**
 * Created by zhuangzhili on 2017-12-13.
 */

public class Book {
    private String title;
    private String authors;
    private String imageUrl;
    private String infoUrl;

    public Book (String t, String a, String i, String in) {
        title = t;
        authors = a;
        imageUrl = i;
        infoUrl = in;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    @Override
    public String toString() {
        return "Title: " + title + "; Authors: " + authors +
                "; ImageUrl: " + imageUrl + "; InfoUrl: " + infoUrl;
    }
}
