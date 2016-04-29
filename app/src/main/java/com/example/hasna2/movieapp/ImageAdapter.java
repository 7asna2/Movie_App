package com.example.hasna2.movieapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.hasna2.movieapp.Data.SaveAndGetImages;
import com.example.hasna2.movieapp.Models.MovieModule;

/**
 * Created by hasna2 on 25-Mar-16.
 */
public class ImageAdapter extends BaseAdapter {
    MovieModule[] movies;
    Context context;
    private final String LOG_TAG =ImageAdapter.class.getCanonicalName();
    private final String BASE_URL = "http://image.tmdb.org/t/p/";
    String size[] = {"w92", "w154", "w185", "w342", "w500", "w780", "original"};

    ImageAdapter(Context context, MovieModule[] movies) {
        this.movies = movies;
        this.context = context;

    }

    @Override
    public int getCount() {
        return movies.length;
    }

    @Override
    public Object getItem(int i) {
        return ""+BASE_URL + size[3] +movies[i].poster_path;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        Log.v(LOG_TAG, "getting item" + i);
        ImageHolder holder = new ImageHolder();
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.poster, null);
            holder.imageView = (ImageView) view.findViewById(R.id.poster_Element);
            view.setTag(holder);
        } else {
            holder = (ImageHolder) view.getTag();
        }
        new SaveAndGetImages(context,(String)getItem(i),holder.imageView).getImage(movies[i].id);
        return view;
    }
    class ImageHolder {
        ImageView imageView ;
    }

}

