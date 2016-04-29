package com.example.hasna2.movieapp.Data;

import android.content.ContentResolver;
import android.net.Uri;

/**
 * Created by hasna2 on 23-Apr-16.
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY ="com.example.hasna2.movieapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES="movies";
    public static final String PATH_FAVORITES="favorites";
    public static final String PATH_RESPONSE="response";

    public static final class MOVIE_ENTRY {
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();


        public static Uri buildMovieWithId(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }


    }
    public static final class FAVORITES_ENTRY{
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String TABLE_NAME="favorites";
        public static Uri buildFavoriteWithID(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

    }

    public static final class RESPONSE_ENTRY{
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RESPONSE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RESPONSE;
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RESPONSE).build();

        public static final String TABLE_NAME=PATH_RESPONSE;

    }
}
