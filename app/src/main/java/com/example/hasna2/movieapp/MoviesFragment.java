package com.example.hasna2.movieapp;


import android.app.Activity;
import android.content.SharedPreferences;
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

import com.example.hasna2.movieapp.AsyncTasks.FetchDataThemoMovie;
import com.example.hasna2.movieapp.Data.Database;
import com.example.hasna2.movieapp.Models.MovieModule;

/**
 * Created by hasna2 on 25-Mar-16.
 */
public class MoviesFragment extends Fragment {
    MovieListener movieListener;
    final String LOG_TAG = "fra";
    View rootView;
    GridView gridView;
    TextView textView;
    String viewBy;
    FetchDataThemoMovie fetchDataThemoMovie;
    ImageAdapter imageAdapter;
    MovieModule Movies [];

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        mCallbacks = (TaskCallbacks) activity;
        movieListener=(MovieListener)activity;
    }
    @Override
    public void onDetach() {
        super.onDetach();
//        mCallbacks = null;
        movieListener=null;
    }

    public MoviesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView)rootView.findViewById(R.id.posters_Grid);
        textView =(TextView)rootView.findViewById(R.id.no_internet);
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
        fetchDataThemoMovie = new FetchDataThemoMovie(this.getActivity(), rootView) {

            @Override
            public void updateGrid(MovieModule[] result) {
                update(result);
            }

            @Override
            public void clearGrid(String reason) {
                clear(reason);

            }
        };
        viewBy=getViewby();
        fetchDataThemoMovie.execute(viewBy);

        return rootView;
    }
    public  void setMovieListener (MovieListener movieListener ){
        this.movieListener=movieListener;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!viewBy.equals(getViewby())){
            viewBy=getViewby();
            (new FetchDataThemoMovie(getContext(), rootView) {
                @Override
                public void updateGrid(MovieModule[] result) {
                    update(result);
                }

                @Override
                public void clearGrid(String reason) {
                    clear(reason);
                }
            }).execute(viewBy);
        }

    }
    public void clear(String reason) {
        if(null!=gridView)
            gridView.setVisibility(View.GONE);
        Log.v(LOG_TAG, "on post execute and list is null :/");
        if(reason.equals(getContext().getString(R.string.viewBy_favorites)))
            textView.setText("No Favorites Stored");
        else
            textView.setText("No Internet Connection");
        textView.setVisibility(View.VISIBLE);

    }
    public void update(MovieModule[] result) {
        textView.setVisibility(View.GONE);
        gridView.setVisibility(View.VISIBLE);
        Movies=result;
        imageAdapter = new ImageAdapter(getActivity(), result);
        gridView.setAdapter(imageAdapter);
        (new Database(getContext())).storeMoviesIntoDB(result);
//        ContentValues contentValues[] = (new Database(getContext())).storeMoviesIntoDB(result);
//        int count =getContext().getContentResolver().bulkInsert(MovieContract.MOVIE_ENTRY.CONTENT_URI, contentValues);
//        Log.v(LOG_TAG,"num of inserted data"+count);
        movieListener.setDefaultOnTablet(result[0]);
    }
    public String getViewby(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String view_by = prefs.getString(getString(R.string.viewBy_key), getString(R.string.viewBy_popular));
        return view_by;
    }



    }

