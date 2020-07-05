package com.jwhh.books;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import java.net.MalformedURLException;
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
        Intent intent = getIntent();
        String query = intent.getStringExtra("Query");
//        try {
            URL bookUrl = null;
            if(query==null || query.isEmpty()){
                bookUrl = ApiUtil.buildUrl("cooking");
            }else{
                try {
                    bookUrl = new URL(query);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

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

        ArrayList<String> recentLists = SpUtil.getQueryList(getApplicationContext());
        int itemNumber = recentLists.size();
        MenuItem recentMenu;
        Log.d("Joab checking errors", String.valueOf(recentLists.get(0)));
        //System.exit(1);
        //if(itemNumber>0){
            for(int i=0;i<itemNumber;i++){
                recentMenu = menu.add(Menu.NONE, i, Menu.NONE, recentLists.get(i));
            }
        //}
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_advanced_search:
                Intent intent = new Intent(this,SearchActivity.class);
                startActivity(intent);
                return true;
            default:
                int position = item.getItemId() + 1;
                String preferenceName = SpUtil.QUERY + String.valueOf(position);
                String query = SpUtil.getPreferenceString(getApplicationContext(),preferenceName);
                String[] prefParams = query.split("\\,");
                String[] queryParams = new String[4];
                for(int i=0;i<prefParams.length;i++){
                    queryParams[i] = prefParams[i];
                }
                URL bookUrl = ApiUtil.buildUrl(
                        (queryParams[0]==null)?"":queryParams[0],
                        (queryParams[1]==null)?"":queryParams[1],
                        (queryParams[2]==null)?"":queryParams[2],
                        (queryParams[3]==null)?"":queryParams[3]);

                return super.onOptionsItemSelected(item);
        }
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