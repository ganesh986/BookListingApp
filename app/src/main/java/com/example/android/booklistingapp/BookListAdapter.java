package com.example.android.booklistingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;

/**
 * An {@link BookListAdapter} knows how to create a list item layout for each book
 * in the data source (a list of {@link BookList} objects).
 * <p>
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class BookListAdapter extends ArrayAdapter<BookList> {

    /**
     * Constructs a new {@link BookListAdapter}.
     *
     * @param context of the app
     * @param book    is the list of book, which is the data source of the adapter
     */
    public BookListAdapter(Context context, List<BookList> book) {
        super(context, 0, book);
    }

    /**
     * Returns a list item view that displays information about the book at the given position
     * in the list of earthquakes.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = listItemView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        // Find the book at the given position in the list of books
        BookList currentBookList = getItem(position);

        ImageView booksView = (ImageView) listItemView.findViewById(R.id.image);
//        Picasso.with(getContext()).load(currentBookList.getImage()).into(booksView);
        Picasso.with(getContext()).load(currentBookList.getImage()).placeholder(getContext().getDrawable(R.drawable.no_image_available)).error(getContext().getDrawable(R.drawable.no_image_available)).into(booksView);

        // Find the TextView in the item.xml layout with the title.
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title);
        titleTextView.setText(currentBookList.getTitle());

        // Find the TextView in the item.xml layout with the subtitle.
        TextView subtitleTextView = (TextView) listItemView.findViewById(R.id.subtext);
        subtitleTextView.setText(currentBookList.getSubtext());

        // Find the TextView in the item.xml layout with the author.
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author);
        authorTextView.setText(currentBookList.getAuthor());

        // Find the TextView in the item.xml layout with the editor.
        TextView editorTextView = (TextView) listItemView.findViewById(R.id.editor);
        editorTextView.setText(currentBookList.getEditor());


        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }
}
