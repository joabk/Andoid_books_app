package com.jwhh.books;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {
    ArrayList<Book> books;
    BooksAdapter(ArrayList<Book> books){
        this.books = books;
    }
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemViewer = LayoutInflater.from(context)
                            .inflate(R.layout.book_list_item, parent, false);
        return new BookViewHolder(itemViewer);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.bind(book);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle;
        TextView tvAuthors;
        TextView tvDate;
        TextView tvPublisher;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = (TextView)itemView.findViewById(R.id.tvTitle);
            tvAuthors = (TextView)itemView.findViewById(R.id.tvAuthors);
            tvDate = (TextView)itemView.findViewById(R.id.tvPublishedDate);
            tvPublisher = (TextView)itemView.findViewById(R.id.tvPublisher);

        }

        public void bind(Book book){
            tvTitle.setText(book.title);
            String Authors = "";
            int i = 0;
            for(String author:book.authors){
                Authors+=author;
                i++;
                if(i<book.authors.length) Authors+=", ";
            }
            tvAuthors.setText(Authors);
            tvDate.setText(book.publishedDate);
            tvPublisher.setText(book.publisher);
        }
    }
}
