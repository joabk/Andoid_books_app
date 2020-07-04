package com.jwhh.books;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class BookListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private ProgressBar mProgress;
    private RecyclerView rv_books;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_list_activity);

        rv_books = (RecyclerView)findViewById(R.id.rv_books);
        LinearLayoutManager booksLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        rv_books.setLayoutManager(booksLayoutManager);
//        try {
            URL bookUrl = ApiUtil.buildUrl("cooking");
            new BookQueryTask().execute(bookUrl);
            //String jsonResults = ApiUtil.getJson(bookUrl);

//        } catch (IOException e) {
//            Log.d("Error", e.getMessage());
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)   {
        getMenuInflater().inflate(R.menu.book_list_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        try{
            URL bookURL = ApiUtil.buildUrl(query);
            new BookQueryTask().execute(bookURL);
        }catch (Exception e){
            Log.d("Error", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public class BookQueryTask extends AsyncTask<URL, Void, String>{
        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String result = null;
            try {
               result =  ApiUtil.getJson(searchUrl);
            } catch (IOException e) {
                Log.e("Error", e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            //tvResults.setText(result);
            TextView tvError = findViewById(R.id.tvError);
            mProgress.setVisibility(View.INVISIBLE);
            if(result == null) {
                tvError.setVisibility(View.VISIBLE);
                rv_books.setVisibility(View.INVISIBLE);
            }else{
                tvError.setVisibility(View.INVISIBLE);
                rv_books.setVisibility(View.VISIBLE);
            }

            ArrayList<Book> books = ApiUtil.getBooksFromJson(result);
//            String resultString = "";
//            for(Book book: books){
//                resultString = resultString + book.title + "\n" + book.publishedDate + "\n\n";
//            }
            BooksAdapter adapter = new BooksAdapter(books);
            rv_books.setAdapter(adapter);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgress = (ProgressBar) findViewById(R.id.pb_loading);
            mProgress.setVisibility(View.VISIBLE);
        }

    }
}