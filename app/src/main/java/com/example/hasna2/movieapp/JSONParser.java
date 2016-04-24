package com.example.hasna2.movieapp;

import com.example.hasna2.movieapp.Models.MovieModule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hasna2 on 01-Apr-16.
 */
public class JSONParser {
    String JsonStr;
   // final String LOG_TAG=getClass().getSimpleName();
    final String Result = "results";
    private final String ID = "id";
    private final String OVERVIEW= "overview";
    private final String POSTER_PATH = "poster_path";
    private final String RELEASE_DATE = "release_date";
    private final String ORIGINAL_TITLE = "original_title";
    private final String TITLE="title";
    private final String ORIGINAL_LANGUAGE="original_language";
    private final String VOTE_COUNT="vote_count";
    private final String VIDEO="video";
    private final String ADULT="adult";
    private final String VOTE_AVERAGE="vote_average";


    public JSONParser (String JSONstr){
        this.JsonStr = JSONstr;
    }


    public ArrayList<MovieModule> getMoviesArray()
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.

        ArrayList<MovieModule> movieModulesArray = new ArrayList<>();
        JSONObject MoviesJson = new JSONObject(JsonStr);
        JSONArray MoviesArray = MoviesJson.getJSONArray(Result);


        for (int i = 0; i < MoviesArray.length(); i++) {
            // Get the JSON object representing the day
            JSONObject movie = MoviesArray.getJSONObject(i);
            MovieModule temp = new MovieModule();
            temp.id= movie.getString(ID);
            temp.original_title= movie.getString(ORIGINAL_TITLE);
            temp.overview= movie.getString(OVERVIEW);
            temp.poster_path= movie.getString(POSTER_PATH);
            temp.release_date=formatDate(movie.getString(RELEASE_DATE));
            temp.title = movie.getString(TITLE);
            temp.original_language=getLanguage(movie.getString(ORIGINAL_LANGUAGE));
            temp.adult = movie.getBoolean(ADULT);
            temp.video = movie.getBoolean(VIDEO);
            temp.vote_count=movie.getString(VOTE_COUNT);
            temp.vote_average=movie.getString(VOTE_AVERAGE);

            movieModulesArray.add(temp);

        }
        //for (String s : poster_URL)
        //    Log.v(LOG_TAG, "Movie Path: " + s);
        return movieModulesArray;
    }

    private String getLanguage (String lang){
        if(lang.equals("en")) return "English";
        if(lang.equals("fr")) return "French";
        if(lang.equals("ja")) return "Japanese";
        if(lang.equals("it")) return "Italian";
        if(lang.equals("ar")) return "Arabic";
        return lang;
    }

    private String formatDate (String date){
        String year =date.substring(0,4);
        String month=date.substring(5,7);
        String day=date.substring(8,10);
        int month_int=Integer.parseInt(month.trim());
        switch (month_int){
            case 1:month="January";break;
            case 2:month="February";break;
            case 3:month="March";break;
            case 4:month="April";break;
            case 5:month="May";break;
            case 6:month="June";break;
            case 7:month="July";break;
            case 8:month="August";break;
            case 9:month="September";break;
            case 10:month="October";break;
            case 11:month="November";break;
            case 12:month="December";break;
        }
        return (month+" "+year);
    }

}
