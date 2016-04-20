package com.example.hasna2.movieapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.hasna2.movieapp.Data.Database;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by hasna2 on 25-Mar-16.
 */
public class MoviesFragment extends Fragment {
    MovieListener movieListener;
    final String LOG_TAG = "fra";
    View rootView;
    GridView gridView;
    ImageAdapter imageAdapter;
    MovieModule Movies [];

    public MoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView)rootView.findViewById(R.id.posters_Grid);
        gridView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /*
                Intent intent = new Intent(getActivity(),DetailActivity.class);
                Bundle b = new Bundle();
                intent.putExtra(Intent.EXTRA_TEXT,Movies[i]);
                startActivity(intent);
                */
                movieListener.setSelectedMovie(Movies[i]);
            }
        });


        return rootView;
    }
    public  void setMovieListener (MovieListener movieListener ){
        this.movieListener=movieListener;
    }

    @Override
    public void onStart() {
        super.onStart();
        FetchDataThemoMovie fetchDataThemoMovie = new FetchDataThemoMovie(this.getActivity(),rootView);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String view_by = prefs.getString(getString(R.string.viewBy_key), getString(R.string.viewBy_popular));
        Log.v(LOG_TAG , "the viewby = "+view_by);
        fetchDataThemoMovie.execute(view_by);
       // updateGrid();

    }



    public class FetchDataThemoMovie extends AsyncTask<String,Void , MovieModule[]> {
        Context context;
        View rootView;
        final String LOG_TAG = "LOG_TAG";
        Uri uri;
        String myUrl;

        public FetchDataThemoMovie(Context context , View rootView){
            this.context=context;
            this.rootView=rootView;
        }
        @Override
        protected void onPostExecute(MovieModule[] result) {

            if (result != null) {
                Log.v(LOG_TAG, "on post execute with "+result.length+"items");
                Movies =result ;
                updateGrid(result);
            }else {
                //Log.v(LOG_TAG, "on post execute and list is null :/");
                TextView textView = new TextView(getContext());
                textView.setText("No Internet Connection");
                textView.setVisibility(View.VISIBLE);
                textView.setTextColor(247);
            }

        }
        public void updateGrid (MovieModule[] result){
            imageAdapter = new ImageAdapter(getActivity(),result);
            gridView.setAdapter(imageAdapter);
            Database database = new Database(getContext());
            database.storeMoviesIntoDB(result);
        }

        @Override
        protected MovieModule[] doInBackground(String... params) {
            ArrayList<MovieModule> al ;

            if (params[0].equals(getString(R.string.viewBy_favorites)) ) {
                al = (new Database(getContext())).getFavoriteFromDB();
                return al.toArray(new MovieModule[al.size()]);
            }

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                al=(new Database(getContext())).getMoviesFromDB();

                String JsonStr = null;
                try {

                    final String themoviesdpURL = "http://api.themoviedb.org/3/movie";

                    //String api_key = "49c571e3be31260c5199658afa4dd2f5";
                    uri = Uri.parse(themoviesdpURL)
                            .buildUpon()
                            .appendPath(params[0])
                            .appendQueryParameter("api_key", BuildConfig.api_key)
                            .build();
                    myUrl = uri.toString();
                    Log.v(LOG_TAG, "Buld URI: " + myUrl);
                    URL url = new URL(myUrl);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        Log.v(LOG_TAG, "NO INPUTSTREAM");
                        return al.toArray(new MovieModule[al.size()]);
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {

                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return al.toArray(new MovieModule[al.size()]);
                    }
                    JsonStr = buffer.toString();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    // If the code didn't successfully get the weather data, there's no point in attemping
                    // to parse it.
                    return al.toArray(new MovieModule[al.size()]);
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
                //  Log.v(LOG_TAG, "ERROR getting json in array ");
            }
    }
}
