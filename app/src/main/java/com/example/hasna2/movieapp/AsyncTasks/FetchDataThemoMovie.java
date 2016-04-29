package com.example.hasna2.movieapp.AsyncTasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.example.hasna2.movieapp.BuildConfig;
import com.example.hasna2.movieapp.Data.Database;
import com.example.hasna2.movieapp.JSONParser;
import com.example.hasna2.movieapp.Models.MovieModule;
import com.example.hasna2.movieapp.R;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by hasna2 on 23-Apr-16.
 */


public abstract class FetchDataThemoMovie extends AsyncTask<String,Void , MovieModule[]> {

    Context context;
    View rootView;
    final String LOG_TAG = FetchDataThemoMovie.class.getSimpleName();
    Uri uri;
    String myUrl;
    String reason;
    public abstract void updateGrid(MovieModule[] result);
    public abstract void clearGrid(String reson);
    public FetchDataThemoMovie(Context context, View rootView) {
        this.context = context;
        this.rootView = rootView;
    }

    @Override
    protected void onPostExecute(MovieModule[] result) {

        if (result!=null&&result.length>0) {
            updateGrid(result);
            if (!reason.equals(context.getString(R.string.viewBy_favorites)))
                (new Database(context)).storeLastResponseIntoDB(result);
        } else {
            clearGrid(reason);

        }

    }



    @Override
    protected MovieModule[] doInBackground(String... params) {
        reason=params[0];
        ArrayList<MovieModule> al=null;

        if (params[0].equals(context.getString(R.string.viewBy_favorites))) {
            al = (new Database(context)).getFavoriteFromDB();
            return (al==null)?null:al.toArray(new MovieModule[al.size()]);
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        al = (new Database(context)).getLatestResponseFromDB();
        String JsonStr = null;
        try {

            final String themoviesdpURL = "http://api.themoviedb.org/3/movie";
            uri = Uri.parse(themoviesdpURL)
                    .buildUpon()
                    .appendPath(params[0])
                    .appendQueryParameter("api_key", BuildConfig.api_key)
                    .build();
            myUrl = uri.toString();
            Log.v(LOG_TAG, "Build URI: " + myUrl);
            URL url = new URL(myUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                Log.v(LOG_TAG, "NO INPUT STREAM");
                return (al==null)?null:al.toArray(new MovieModule[al.size()]);
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {

                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return (al==null)?null:al.toArray(new MovieModule[al.size()]);
            }
            JsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return (al==null)?null:al.toArray(new MovieModule[al.size()]);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
            Log.v(LOG_TAG, "JSON STRING: " + JsonStr);
        }


        try {
            al = (new JSONParser(JsonStr)).getMoviesArray();
            return al.toArray(new MovieModule[al.size()]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}

