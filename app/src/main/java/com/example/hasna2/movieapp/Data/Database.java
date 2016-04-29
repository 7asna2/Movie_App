package com.example.hasna2.movieapp.Data;

/**
 * Created by hasna2 on 15-Apr-16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.hasna2.movieapp.Models.MovieModule;

import java.util.ArrayList;


/* class that deals with the data using content provider */
public class Database {

    String LOG_TAG = Database.class.getSimpleName();
    Context context;
    Cursor cur;

    public Database(Context context) {
        this.context = context;
    }
    public ArrayList<MovieModule> getLatestResponseFromDB() {
        ArrayList<MovieModule> moviesArrayList = new ArrayList<>();
        Uri uri=MovieContract.RESPONSE_ENTRY.CONTENT_URI;
        cur = context.getContentResolver().query(uri,null,null,null,null);
        Log.v(LOG_TAG, "number of movies in DB :" + cur.getCount());
        String responseid;
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            moviesArrayList = new ArrayList<>();
            for (int i = 0; i < cur.getCount(); i++) {
                responseid = cur.getString(0);
                Uri uri2 = MovieContract.MOVIE_ENTRY.buildMovieWithId(responseid);
                Cursor cursor = context.getContentResolver().query(uri2, null, null, null, null);
                if (cursor.getPosition() >= 0) {
                    MovieModule movie = new MovieModule();
                    movie.id = (cursor.getString(0));
                    movie.title = (cursor.getString(1));
                    movie.overview = (cursor.getString(2));
                    movie.poster_path = (cursor.getString(3));
                    movie.original_language = (cursor.getString(4));
                    movie.vote_count = (cursor.getString(5));
                    movie.vote_average = (cursor.getString(6));
                    movie.release_date = (cursor.getString(7));
                    moviesArrayList.add(movie);
                }
                cur.moveToNext();
            }
        }
        return moviesArrayList;
    }

    public ArrayList<MovieModule> getFavoriteFromDB() {
        ArrayList<MovieModule> favoriteMoviesArrayList = null;
        Uri uri=MovieContract.FAVORITES_ENTRY.CONTENT_URI;
        cur = context.getContentResolver().query(uri,null,null,null,null);
        String favId;
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            favoriteMoviesArrayList = new ArrayList<>();
            for (int i = 0; i < cur.getCount(); i++) {
                favId = cur.getString(0);
                Uri uri2=MovieContract.MOVIE_ENTRY.buildMovieWithId(favId);
                Cursor cursor = context.getContentResolver().query(uri2,null,null,null,null);
                if (cursor.getPosition() >= 0) {
                    MovieModule movie = new MovieModule();
                    movie.id = (cursor.getString(0));
                    movie.title = (cursor.getString(1));
                    movie.overview = (cursor.getString(2));
                    movie.poster_path = (cursor.getString(3));
                    movie.original_language = (cursor.getString(4));
                    movie.vote_count = (cursor.getString(5));
                    movie.vote_average = (cursor.getString(6));
                    movie.release_date = (cursor.getString(7));
                    favoriteMoviesArrayList.add(movie);
                }
                cur.moveToNext();
            }
        }
        cur.close();
        return favoriteMoviesArrayList;
    }

    public boolean isFavorite(MovieModule movieModule) {
        Uri uri=MovieContract.FAVORITES_ENTRY.buildFavoriteWithID(movieModule.id);
        cur = context.getContentResolver().query(uri,null,null,null,null);
        if (cur.getCount() > 0)
                    return true;
        cur.close();
        return false;
    }

    public void saveToFavoriteDB(MovieModule movieModule) {
        ContentValues cv = new ContentValues();
        cv.put(MovieModule.MOVIE_ID, movieModule.id);
        Uri uri=context.getContentResolver().insert(MovieContract.FAVORITES_ENTRY.CONTENT_URI,cv);
    }

    public void deleteLatestResponse(){
        context.getContentResolver().delete(MovieContract.RESPONSE_ENTRY.CONTENT_URI,null,null);
    }

    public void storeLastResponseIntoDB(MovieModule movies[]) {
        deleteLatestResponse();
        ContentValues cv;
        ContentValues contentValues[] = new ContentValues[movies.length];
        ContentValues contentValues2[] = new ContentValues[movies.length];
        for (int i = 0; i < movies.length; i++) {
            MovieModule s = movies[i];
            cv = new ContentValues();
            contentValues2[i]=new ContentValues();
            contentValues2[i].put(MovieModule.MOVIE_ID, s.id);
            cv.put(MovieModule.MOVIE_ID, s.id);
            cv.put(MovieModule.MOVIE_TITLE, s.title);
            cv.put(MovieModule.MOVIE_OVERVIEW, s.overview);
            cv.put(MovieModule.MOVIE_POSTER_PATH, s.poster_path);
            cv.put(MovieModule.MOVIE_ORIGINAL_LANGUAGE, s.original_language);
            cv.put(MovieModule.MOVIE_VOTE_COUNT, s.vote_count);
            cv.put(MovieModule.MOVIE_VOTE_AVERAGE, s.vote_average);
            cv.put(MovieModule.MOVIE_RELEASE_DATE, s.release_date);
            contentValues[i] = cv;
            Log.e(LOG_TAG, "inserting in DB ");
        }
        context.getContentResolver().bulkInsert(MovieContract.MOVIE_ENTRY.CONTENT_URI, contentValues);
        int count =context.getContentResolver().bulkInsert(MovieContract.RESPONSE_ENTRY.CONTENT_URI, contentValues2);
        Log.e(LOG_TAG, count+"inserted movies ");
    }

    public void deleteFromFavorite(MovieModule movie) {
        Uri uri=MovieContract.FAVORITES_ENTRY.buildFavoriteWithID(movie.id);
        context.getContentResolver().delete(uri,null,null);
    }
}
