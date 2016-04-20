package com.example.hasna2.movieapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity implements MovieListener {
    boolean twoPane ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        twoPane = findViewById(R.id.layout_pane2)==null?false:true;
        if (savedInstanceState == null) {
            MoviesFragment moviesFragment = new MoviesFragment();
            moviesFragment.setMovieListener(this);
            FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.add(R.id.container, moviesFragment).commit();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
            if(twoPane){
                DetailFragment detailFragment = new DetailFragment();
                Bundle b = new Bundle();
                b.putSerializable(Intent.EXTRA_TEXT,movieModule);
                detailFragment.setArguments(b);
                FragmentTransaction fTrans = getSupportFragmentManager().beginTransaction();
                fTrans.replace(R.id.layout_pane2, detailFragment).commit();

            }else{
                Intent intent = new Intent(this,DetailActivity.class);
                Bundle b = new Bundle();
                intent.putExtra(Intent.EXTRA_TEXT,movieModule);
                startActivity(intent);
            }
    }

    /**
     * A placeholder fragment containing a simple view.
     */

}
