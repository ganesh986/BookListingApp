package com.example.android.booklistingapp;

/**
 * An {@link BookList} object contains information related to the single book.
 */

public class BookList {

    /**
     * Image of the book
     */
    private String mImage;

    /**
     * Title of the book
     */
    private String mTitle;

    /**
     * Subtext of the book
     */
    private String mSubtext;

    /**
     * Author of the book
     */
    private String mAuthor;

    /**
     * editor of the book
     */
    private String mEditor;

    /**
     * Website URL of the book
     */
    private String mUrl;

    /**
     * Constructs a new {@link BookList} object.
     *
     * @param image   url with title
     * @param title   string with title
     * @param subtext string with subtext
     * @param author  string with author of the book
     * @param editor  string with author of the book
     * @param url     is the website URL to buy the book
     */
    public BookList(String image, String title, String subtext, String author, String editor, String url) {
        mImage = image;
        mTitle = title;
        mSubtext = subtext;
        mAuthor = author;
        mEditor = editor;
        mUrl = url;
    }

    /**
     * Returns the url of the image.
     */
    public String getImage() {
        return mImage;
    }

    /**
     * Returns the title of the book.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the subtitle of the book.
     */
    public String getSubtext() {
        return mSubtext;
    }

    /**
     * Returns the author of the book..
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * Returns the editor of the book.
     */
    public String getEditor() {
        return mEditor;
    }

    /**
     * Returns the website URL to buy the book.
     */
    public String getUrl() {
        return mUrl;
    }
}
