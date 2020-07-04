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
import java.util.Arrays;
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
        final String ID = "id";
        final String TITLE = "title";
        final String SUB_TITLE = "subtitle";
        final String AUTHORS = "authors";
        final String PUBLISHER = "publisher";
        final String PUBLISHED_DATE = "publishedDate";
        final String ITEMS = "items";
        final String VOLUMEINFO = "volumeInfo";
        final String DESCRIPTION = "description";
        final String IMAGELINKS = "imageLinks";
        final String THUMBNAIL = "thumbnail";

        ArrayList<Book> books = new ArrayList<Book>();
        try{
            JSONObject jsonBooks = new JSONObject(json);
            JSONArray arrayBooks = jsonBooks.getJSONArray(ITEMS);
            int numberOfBooks = arrayBooks.length();

            for(int i=0; i<=numberOfBooks; i++){
                JSONObject bookJSON = arrayBooks.getJSONObject(i);
                JSONObject volumeInfoJSON = bookJSON.getJSONObject(VOLUMEINFO);
                JSONObject imageLinksJSON = volumeInfoJSON.getJSONObject(IMAGELINKS);

                //Log.d("Joab checking jsonBook", "Joab debug jsonBook" + bookJSON.getString(ID));
                //Log.d("Joab checking 22", "Joab debug" + volumeInfoJSON.getJSONArray(AUTHORS).length());
               int authorNum = volumeInfoJSON.getJSONArray(AUTHORS).length();
                String authors[] = new String[authorNum];
                if(authorNum>0){
                    for(int j = 0; j<authorNum; j++){
                        authors[j] = volumeInfoJSON.getJSONArray(AUTHORS).get(j).toString();
                        //Log.d("Joab checking jsonBook", "Joab debug jsonBook: " + volumeInfoJSON.getJSONArray(AUTHORS).get(j).toString());
                        //System.exit(1);
                    }
                }

                Book book = new Book(
                    bookJSON.getString(ID),
                    volumeInfoJSON.getString(TITLE),
                    //volumeInfoJSON.getString(SUB_TITLE),
                    (volumeInfoJSON.isNull(SUB_TITLE))?"SUB TITLE NOT AVAILABLE":volumeInfoJSON.getString(SUB_TITLE),
                    authors,
                    volumeInfoJSON.getString(PUBLISHER),
                    volumeInfoJSON.getString(PUBLISHED_DATE),
                    volumeInfoJSON.getString(DESCRIPTION),
                    imageLinksJSON.getString(THUMBNAIL)
                );
                String sub = (volumeInfoJSON.isNull(SUB_TITLE))?"SUB TITLE NOT AVAILABLE":volumeInfoJSON.getString(SUB_TITLE);
                books.add(book);
                //Log.d("Joab checking errors", Arrays.toString(authors));
                //Log.d("Joab checking jsonBook:", "Joab debug jsonBook" + sub);
                //System.exit(1);
            }
        }
        catch (JSONException e){
            //e.printStackTrace();
            Log.d("Error", e.toString());
        }
        //System.out.print("Books are"+books);
        //Log.d("Books are", String.valueOf(books));
        return books;
    }

}
