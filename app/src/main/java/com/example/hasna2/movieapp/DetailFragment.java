package com.example.hasna2.movieapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hasna2.movieapp.Data.Database;
import com.example.hasna2.movieapp.Data.SaveAndGetImages;

/**
 * Created by hasna2 on 19-Apr-16.
 */
public class DetailFragment  extends Fragment {
    final String LOG_TAG = "fff";
    String base_URL = "http://image.tmdb.org/t/p/";
    String size[] = {"w92", "w154", "w185", "w342", "w500", "w780", "original"};
    MovieModule movie;

    public DetailFragment() { setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_share_){
            Toast toast = Toast.makeText(getActivity(), "jskh", Toast.LENGTH_SHORT);
            toast.show();
            startActivity(getShareIntent());
        }
        return super.onOptionsItemSelected(item);
    }

    public void onCreateOptionsMenu(Menu menu ,MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);

        MenuItem item = menu.findItem(R.id.action_share_);
        ShareActionProvider mShareActionProvider=(ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if(! (mShareActionProvider==null))
            mShareActionProvider.setShareIntent(getShareIntent());
        else
            Log.d(LOG_TAG, "Share Action Provider is null");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        movie =(MovieModule) intent.getSerializableExtra(Intent.EXTRA_TEXT);
        if (movie == null) Log.v(LOG_TAG, "movie is null :3 :3 ");
        init(rootView, movie);

        return rootView;

    }

    private void init(final View rootView, final MovieModule movie) {
        ArrayAdapter<String> in;
        new SaveAndGetImages(getContext(),"" + base_URL + size[3] + movie.poster_path ,(ImageView) rootView.findViewById(R.id.imageView))
                .getImage((String)movie.id);
        // Picasso.with(this.getContext()).load("" + base_URL + size[3] + movie.poster_path).into((ImageView) rootView.findViewById(R.id.imageView));
        ((TextView) rootView.findViewById(R.id.title)).setText(movie.title);
        ((TextView) rootView.findViewById(R.id.overview)).setText(movie.overview);
        ((TextView) rootView.findViewById(R.id.date)).setText(movie.release_date);
        ((TextView) rootView.findViewById(R.id.language)).setText(movie.original_language);
        ImageButton imageButton =((ImageButton)rootView.findViewById(R.id.favorite));
        manageImageButton(imageButton, movie);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFavorite(view, movie);
            }
        });
        FetchVideosAndReviewsTask fetchVideosAndReviewsTask = new FetchVideosAndReviewsTask(this.getActivity(),rootView);
        fetchVideosAndReviewsTask.execute(movie.id);
    }
    public void toggleFavorite (View view,MovieModule movie){
        Database database = new Database(getContext());
        String update="Error"  ;
        if(database.isFavorite(movie)){
            database.deleteFromFavorite(movie);
            update="removed from favorites";
        }
        else{
            database.saveToFavoriteDB(movie);
            update="added to favorites";
        }

        manageImageButton (view,movie);
        Toast toast = Toast.makeText(getActivity(), update, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void manageImageButton (View view,MovieModule movie){
        Database database = new Database(getContext());
        if(database.isFavorite(movie))
            ((ImageButton)view).setImageResource(R.drawable.fav_star);
        else
            ((ImageButton)view).setImageResource(R.drawable.un_fav_star);
    }

    public Intent getShareIntent (){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, movie.title + "\n" + movie.overview);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile((new SaveAndGetImages(getContext(),null,null).getOutputMediaFile(movie.id))));  //optional//use this when you want to send an image
        shareIntent.setType("image/jpeg");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
       // shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
       // startActivity(Intent.createChooser(shareIntent, "send"));
        return shareIntent;
    }

}




