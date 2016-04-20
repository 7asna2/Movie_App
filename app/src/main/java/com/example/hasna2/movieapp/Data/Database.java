package com.example.hasna2.movieapp.Data;

/**
 * Created by hasna2 on 15-Apr-16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.hasna2.movieapp.MovieModule;

import java.util.ArrayList;



public class Database {
    String TAG ="hasnaa";
    Context context;
    DatabaseHelper db;
    Cursor cur;

public Database (Context context){
    this.context=context;
    db = new DatabaseHelper(context);
}


    //public ArrayList<MovieModule.Video>
    //@ToDo :get and set all data types
    /*INSERT INTO first_table_name [(column1, column2, ... columnN)]
   SELECT column1, column2, ...columnN
   FROM second_table_name
   [WHERE condition];
   */
    public ArrayList<MovieModule> getMoviesFromDB (){
        ArrayList<MovieModule> moviesArrayList = null;

            cur = db.selectAllRaw(MovieModule.TABLE_NAME);
            Log.v(TAG,"number of movies in DB :"+cur.getCount());
            if (cur.getCount() > 0) {
                cur.moveToFirst();
                moviesArrayList = new ArrayList<MovieModule>();
                for (int i = 0; i < cur.getCount(); i++) {
                    MovieModule movie = new MovieModule();
                    movie.id=(cur.getString(0));
                    movie.title=(cur.getString(1));
                    movie.overview=(cur.getString(2));
                    movie.poster_path=(cur.getString(3));
                    movie.original_language=(cur.getString(4));
                    movie.release_date=(cur.getString(5));
                    moviesArrayList.add(movie);
                    cur.moveToNext();
                }
            }
            cur.close();
            db.close();
        return moviesArrayList;
        }
    public ArrayList<MovieModule> getFavoriteFromDB (){
        ArrayList<MovieModule> favoriteMoviesArrayList = null;
        cur = db.selectAllRaw("favorites");
        String favId ;
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            favoriteMoviesArrayList = new ArrayList<MovieModule>();
            for (int i = 0; i < cur.getCount(); i++) {
                favId=cur.getString(0);
                Cursor cursor = db.selectRaw(MovieModule.TABLE_NAME , MovieModule.MOVIE_ID+" = '"+favId+"'");
                if(cursor.getPosition()>=0) {
                    MovieModule movie = new MovieModule();
                    movie.id = (cursor.getString(0));
                    movie.title = (cursor.getString(1));
                    movie.overview = (cursor.getString(2));
                    movie.poster_path = (cursor.getString(3));
                    movie.original_language = (cursor.getString(4));
                    movie.release_date = (cursor.getString(5));
                    favoriteMoviesArrayList.add(movie);
                }
                cur.moveToNext();
            }
        }
        cur.close();
        db.close();
        return favoriteMoviesArrayList ;
    }
    public boolean isFavorite (MovieModule movieModule){

        cur = db.selectAllRaw("favorites");
        String movieId=movieModule.id;
        if (cur.getCount() > 0) {
            cur.moveToFirst();
            for (int i = 0; i < cur.getCount(); i++) {
                if(movieId.equals(cur.getString(0)))
                    return true;
                cur.moveToNext();
            }
        }
        cur.close();
        db.close();
        return false;
    }
    public void saveToFavoriteDB (MovieModule movieModule){
        ContentValues cv=new ContentValues();
        cv.put(MovieModule.MOVIE_ID,movieModule.id);
        db.insertRow("favorites", cv);
    }
    public void storeMoviesIntoDB (MovieModule movies []) {
        ContentValues cv;
        //cur = db.selectAllRaw(MovieModule.TABLE_NAME);
        //Log.v(TAG,"number of movies in DB :"+cur.getCount());
        for (MovieModule s : movies) {
            cv = new ContentValues();
            cv.put(MovieModule.MOVIE_ID, s.id);
            cv.put(MovieModule.MOVIE_TITLE, s.title);
            cv.put(MovieModule.MOVIE_OVERVIEW, s.overview);
            cv.put(MovieModule.MOVIE_POSTER_PATH, s.poster_path);
            cv.put(MovieModule.MOVIE_ORIGINAL_LANGUAGE, s.original_language);
            cv.put(MovieModule.MOVIE_VOTE_COUNT ,s.vote_count );
            cv.put(MovieModule.MOVIE_VOTE_AVERAGE ,s.vote_average );
            cv.put(MovieModule.MOVIE_RELEASE_DATE, s.release_date);
            db.insertRow(MovieModule.TABLE_NAME, cv);
            Log.e(TAG,"inserting in DB ");
        }
    }
    public void deleteFromFavorite (MovieModule movie){
        db.deleteRow("favorites",MovieModule.MOVIE_ID+" = '"+movie.id+"'");
    }
    }

