package com.example.hasna2.movieapp;
// @To do: http://api.themoviedb.org/3/search/movie?api_key=67c3e9a8357661792e5956106c3de3f6&query=%22chabby%22

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.hasna2.movieapp.Models.MovieModule;


public class MainActivity extends ActionBarActivity implements MovieListener {
    MoviesFragment moviesFragment;
    boolean twoPane ;
    Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.savedInstanceState=savedInstanceState;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        twoPane =(findViewById(R.id.layout_pane2)!=null);
        

        if (savedInstanceState == null) {
            moviesFragment = new MoviesFragment();
            moviesFragment.setMovieListener(this);
            FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.add(R.id.layout_pane1, moviesFragment).commit();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,SettingActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setSelectedMovie(MovieModule movieModule) {
//        twoPane =(findViewById(R.id.layout_pane2)!=null);
        Log.v("211","two pane:"+twoPane);

            if(twoPane){
                Bundle b = new Bundle();
                b.putSerializable("movie",movieModule);
                DetailFragment detailFragment = new DetailFragment();
                detailFragment.setArguments(b);
                FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
                fTrans.replace(R.id.layout_pane2, detailFragment).commit();

            }else{
                Intent intent = new Intent(this,DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT,movieModule);
                startActivity(intent);
            }
    }

    @Override
    public void setDefaultOnTablet(MovieModule movieModule) {

        if (twoPane && savedInstanceState == null) {

            Bundle b = new Bundle();
            b.putSerializable("movie", movieModule);
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(b);
            FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.layout_pane2, detailFragment).commit();
        }


    }
}
