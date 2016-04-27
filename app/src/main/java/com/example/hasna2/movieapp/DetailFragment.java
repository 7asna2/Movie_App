package com.example.hasna2.movieapp;

import android.content.ContentValues;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hasna2.movieapp.AsyncTasks.FetchVideosAndReviewsTask;
import com.example.hasna2.movieapp.Data.Database;
import com.example.hasna2.movieapp.Data.SaveAndGetImages;
import com.example.hasna2.movieapp.Models.MovieModule;
import com.example.hasna2.movieapp.Models.Review;
import com.example.hasna2.movieapp.Models.Video;

import java.util.ArrayList;

/**
 * Created by hasna2 on 19-Apr-16.
 */
public class DetailFragment  extends Fragment {
    View rootView;
    final String LOG_TAG = "fff";
    String base_URL = "http://image.tmdb.org/t/p/";
    String size[] = {"w92", "w154", "w185", "w342", "w500", "w780", "original"};
    MovieModule movie;
    String shareTrailer;
    ShareActionProvider mShareActionProvider;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share_) {
            Toast toast = Toast.makeText(getActivity(), "jskh", Toast.LENGTH_SHORT);
            toast.show();
           // startActivity(getShareIntent());

        }
        return super.onOptionsItemSelected(item);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);

        MenuItem item = menu.findItem(R.id.action_share_);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if (!(mShareActionProvider == null))
            mShareActionProvider.setShareIntent(getShareIntent());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            movie = (MovieModule) intent.getSerializableExtra(Intent.EXTRA_TEXT);
            if (movie == null) Log.v(LOG_TAG, "movie is null :3 :3 ");
        }
        init(rootView, movie);

        return rootView;

    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        movie = (MovieModule) args.getSerializable("movie");




    }

    private void init(final View rootView, final MovieModule movie) {
        new SaveAndGetImages(getContext(), "" + base_URL + size[2] + movie.poster_path, (ImageView) rootView.findViewById(R.id.imageView))
                .getImage(movie.id);
        ((TextView) rootView.findViewById(R.id.title)).setText(movie.title);
        ((TextView) rootView.findViewById(R.id.overview)).setText(movie.overview);
        ((TextView) rootView.findViewById(R.id.date)).setText(movie.release_date);
        ((TextView) rootView.findViewById(R.id.language)).setText(movie.original_language);
        ((RatingBar) rootView.findViewById(R.id.rating)).setRating(Float.parseFloat(movie.vote_average) / 2);
        ImageButton imageButton = ((ImageButton) rootView.findViewById(R.id.favorite));
        manageImageButton(imageButton, movie);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFavorite(view, movie);
            }
        });
        FetchVideosAndReviewsTask fetchVideosAndReviewsTask = new FetchVideosAndReviewsTask(this.getActivity(), rootView) {
            @Override
            public void updateVideoList(Video[] videos) {
                updateVideos(videos);
            }

            @Override
            public void updateReviewList(Review[] reviews) {
                updateReviews(reviews);
            }
        };
        fetchVideosAndReviewsTask.execute(movie.id);
    }

    public void toggleFavorite(View view, MovieModule movie) {
        Database database = new Database(getContext());
        String update;
        if (database.isFavorite(movie)) {
            database.deleteFromFavorite(movie);
            update = "removed from favorites";
        } else {
            ContentValues cv=new ContentValues();
            cv.put(MovieModule.MOVIE_ID,movie.id);
//            Uri uri=getContext().getContentResolver().insert(MovieContract.FAVORITES_ENTRY.CONTENT_URI,cv);
            database.saveToFavoriteDB(movie);
            update = "added to favorites";
        }

        manageImageButton(view, movie);
        Toast toast = Toast.makeText(getActivity(), update, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void manageImageButton(View view, MovieModule movie) {
        Database database = new Database(getContext());
        if (database.isFavorite(movie))
            ((ImageButton) view).setImageResource(R.drawable.fav_star);
        else
            ((ImageButton) view).setImageResource(R.drawable.un_fav_star);
    }

    public Intent getShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        String linkAndText =  movie.title + "\n" + movie.overview+"\n";
        if(shareTrailer != null)linkAndText+=shareTrailer;
        shareIntent.putExtra(Intent.EXTRA_TEXT,linkAndText
        );
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile((new SaveAndGetImages(getContext(), null, null).getOutputMediaFile(movie.id))));  //optional//use this when you want to send an image
        shareIntent.setType("*/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return shareIntent;
    }

    public void updateVideos(final Video videos[]) {
        final String YOUTUBE_BASE="https://www.youtube.com/watch?";
        shareTrailer=YOUTUBE_BASE+"v="+videos[0].key;
        if (!(mShareActionProvider == null))
            mShareActionProvider.setShareIntent(getShareIntent());
        ListView videosListView = (ListView) rootView.findViewById(R.id.videos_list);
        ArrayAdapter<String> in = new ArrayAdapter<>(getContext(), R.layout.video, R.id.trialer_name, new ArrayList<String>());
        videosListView.setAdapter(in);
        in.clear();
        for (Video v : videos)
            in.add(v.Name);
        videosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Uri uri = Uri.parse(YOUTUBE_BASE).buildUpon().appendQueryParameter("v", videos[i].key).build();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                getContext().startActivity(intent);
            }
        });
        setListViewHeightBasedOnChildren(videosListView);
    }

    public void updateReviews(Review reviews[]) {
        ListView reviewsListView = (ListView) rootView.findViewById(R.id.reviews_list);
        ReviewAdapter reviewsAdapter=new ReviewAdapter(getContext(),reviews);
        reviewsListView.setAdapter(reviewsAdapter);
        setListViewHeightBasedOnChildren(reviewsListView);
    }
    private static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = ListView.MeasureSpec.makeMeasureSpec(listView.getWidth(), ListView.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ListView.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, ListView.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
    }


}






