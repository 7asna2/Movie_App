package com.example.hasna2.movieapp.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.hasna2.movieapp.Models.MovieModule;

/**
 * Created by hasna2 on 23-Apr-16.
 */
public class MoviesProvider extends ContentProvider {


    private static final UriMatcher sUriMatcher = buildUriMatcher();
    String LOG_TAG = ContentProvider.class.getSimpleName();
    private DatabaseHelper mOpenHelper;

    static final int MOVIES = 100;
    static final int FAVORITES = 101;
    static final int RESPONSE=102;
    static final int FAVORITE_WITH_ID= 103;
    static final int MOVIES_WITH_ID=104;


    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_MOVIES, MOVIES);
        matcher.addURI(authority, MovieContract.PATH_FAVORITES + "/#",FAVORITE_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/#", MOVIES_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_FAVORITES, FAVORITES);
        matcher.addURI(authority, MovieContract.PATH_RESPONSE, RESPONSE);
        return matcher;
    }
    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        Cursor retCursor=null;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                retCursor =mOpenHelper.selectAllRaw(MovieModule.TABLE_NAME);
                return retCursor;
            case FAVORITES:
                retCursor=mOpenHelper.selectAllRaw("favorites");
                return retCursor;
            case RESPONSE:
                retCursor=mOpenHelper.selectAllRaw(MovieContract.RESPONSE_ENTRY.TABLE_NAME);
                return retCursor;
            case FAVORITE_WITH_ID:
                retCursor=mOpenHelper.selectRaw(MovieContract.FAVORITES_ENTRY.TABLE_NAME,MovieModule.MOVIE_ID+" = '"+uri.getPathSegments().get(1)+"'");
                return retCursor;
            case MOVIES_WITH_ID:
                retCursor=mOpenHelper.selectRaw(MovieModule.TABLE_NAME,MovieModule.MOVIE_ID+" = '"+uri.getPathSegments().get(1)+"'");

        }
        return  retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES_WITH_ID:
                return MovieContract.MOVIE_ENTRY.CONTENT_ITEM_TYPE;
            case FAVORITE_WITH_ID:
                return MovieContract.FAVORITES_ENTRY.CONTENT_ITEM_TYPE;
            case MOVIES:
                return MovieContract.MOVIE_ENTRY.CONTENT_TYPE;
            case FAVORITES:
                return MovieContract.FAVORITES_ENTRY.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        long rowID=-1;
        switch (match) {
            case FAVORITES:
                Log.v(LOG_TAG,"inserting in favorites");
                rowID= mOpenHelper.insertRow("favorites", contentValues);
                return ContentUris.withAppendedId(uri,rowID);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return null;
    }



    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case MOVIES:
                    for (ContentValues cv : values){
                        long i=mOpenHelper.insertRow(MovieModule.TABLE_NAME, cv);
                        if(i!=-1)returnCount++;
                 }
                break;
            case RESPONSE:
                for (ContentValues cv : values){
                    long i=mOpenHelper.insertRow(MovieContract.RESPONSE_ENTRY.TABLE_NAME, cv);
                    if(i!=-1)returnCount++;
                }
                break;
            default:
                return super.bulkInsert(uri, values);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        Log.v(LOG_TAG,"number inserted"+returnCount);
        return returnCount;

}

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case FAVORITE_WITH_ID:
                mOpenHelper.delete("favorites",MovieModule.MOVIE_ID+" = '"+uri.getPathSegments().get(1)+"'");
                return 1;
            case RESPONSE:
                mOpenHelper.delete(MovieContract.RESPONSE_ENTRY.TABLE_NAME,null);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {

        return 0;
    }
}
