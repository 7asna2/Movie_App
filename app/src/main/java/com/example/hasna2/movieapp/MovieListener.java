package com.example.hasna2.movieapp;

import com.example.hasna2.movieapp.Models.MovieModule;

/**
 * Created by hasna2 on 19-Apr-16.
 */
public interface MovieListener {

    public void setSelectedMovie (MovieModule movieModule);
    public void setDefaultOnTablet (MovieModule movieModule);
}
