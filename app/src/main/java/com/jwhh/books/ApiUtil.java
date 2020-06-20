package com.jwhh.books;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class ApiUtil {
    private ApiUtil(){}

    public static final String BASE_API_URL =
            "https://www.googleapis.com/books/v1/volumes";
    public static final String QUERY_PARAMETER_KEY = "q";
    public static final String KEY = "key";
    public static final String API_KEY = "AIzaSyCK9Z5fuQBLM0RXV58u1Wmkt9zznb0269c";

    public static URL buildUrl(String title) {

        URL url = null;
        Uri uri = Uri.parse(BASE_API_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAMETER_KEY, title)
                .appendQueryParameter(KEY, API_KEY)
                .build();
        try {
            url = new URL(uri.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return url;
    }

    public static String getJson(URL url) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            InputStream stream = connection.getInputStream();
            Scanner scanner = new Scanner(stream);
            scanner.useDelimiter("\\A");
            boolean hasData = scanner.hasNext();
            if (hasData) {
                return scanner.next();
            } else {
                return null;
            }
        }
        catch (Exception e){
            Log.d("Error", e.toString());
            return null;
        }
        finally {
            connection.disconnect();
        }
    }

    public static ArrayList<Book> getBooksFromJson(String json){
        final String ID = "ID";
        final String TITLE = "TITLE";
        final String SUB_TITLE = "subTitle";
        final String AUTHORS = "authors";
        final String PUBLISHER = "publisher";
        final String PUBLISHED_DATE = "publishedDate";
        final String ITEMS = "items";
        final  String VOLUMEINFO = "volumeInfo";

        ArrayList<Book> books = new ArrayList<Book>();
        try{
            JSONObject jsonBooks = new JSONObject(json);
            JSONArray arrayBooks = jsonBooks.getJSONArray(ITEMS);
            int numberOfBooks = arrayBooks.length();
            for(int i=0; i<numberOfBooks; i++){
                JSONObject bookJSON = arrayBooks.getJSONObject(i);
                JSONObject volumeInfoJSON = bookJSON.getJSONObject(VOLUMEINFO);
//                int authorNum = bookJSON.getJSONArray(AUTHORS).length();
//                String authors[] = new String[authorNum];
//                if(authorNum>0){
//                    for(int j = 0; j<authorNum; j++){
//                        authors[j] = bookJSON.getJSONArray(VOLUMEINFO).get(j).toString();
//                    }
//                }

                Book book = new Book(
                        (bookJSON.isNull(ID))?"BOOKID":bookJSON.getString(ID),
                        (volumeInfoJSON.isNull(TITLE))?"TITLE": volumeInfoJSON.getString(TITLE),
                        (volumeInfoJSON.isNull(SUB_TITLE))?"SUBTITLE":volumeInfoJSON.getString(SUB_TITLE),
                        //(volumeInfoJSON.isNull(AUTHORS)) ? null : authors,
                        volumeInfoJSON.getString(PUBLISHER),
                        volumeInfoJSON.getString(PUBLISHED_DATE)
                );
                books.add(book);
            }
        }
        catch (JSONException e){
            //e.printStackTrace();
            Log.d("Error", e.toString());
        }
        return books;
    }
}
