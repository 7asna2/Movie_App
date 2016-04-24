package com.example.hasna2.movieapp;

import java.io.Serializable;

/**
 * Created by hasna2 on 08-Apr-16.
 */

// @ToDo: seperate every class in a file and save them in one package

public class MovieModule implements Serializable{

    // Data Base Entries
    public final static String TABLE_NAME = "movies";
    public final static String MOVIE_ID = "movie_id";
    public final static String MOVIE_OVERVIEW="overview";
    public final static String MOVIE_POSTER_PATH="poster_path";
    public final static String MOVIE_RELEASE_DATE="release_date";
    public final static String MOVIE_TITLE="title";
    public final static String MOVIE_VOTE_COUNT ="vote_count";
    public final static String MOVIE_VOTE_AVERAGE="vote_average";
    public final static String MOVIE_ORIGINAL_LANGUAGE="original_language";

    public String id;
    public String title;
    public String overview;
    public String poster_path;
    public String original_language;
    public String release_date;
    public String original_title;
    public String vote_count;
    public String vote_average;

    boolean video;
    boolean adult;



}
