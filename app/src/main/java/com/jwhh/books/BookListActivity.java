package com.jwhh.books;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;

public class BookListActivity extends AppCompatActivity {
    private ProgressBar mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_list_activity);
//        try {
            URL bookUrl = ApiUtil.buildUrl("cooking");
            new BookQueryTask().execute(bookUrl);
            //String jsonResults = ApiUtil.getJson(bookUrl);

//        } catch (IOException e) {
//            Log.d("Error", e.getMessage());
//        }
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
            TextView tvResults = (TextView) findViewById(R.id.tvResponse);
            tvResults.setText(result);
            TextView tvError = findViewById(R.id.tvError);
            mProgress.setVisibility(View.INVISIBLE);
            if(result == null) {
                tvError.setVisibility(View.VISIBLE);
                tvResults.setVisibility(View.INVISIBLE);
            }else{
                tvError.setVisibility(View.INVISIBLE);
                tvResults.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgress = (ProgressBar) findViewById(R.id.pb_loading);
            mProgress.setVisibility(View.VISIBLE);
        }

    }
}