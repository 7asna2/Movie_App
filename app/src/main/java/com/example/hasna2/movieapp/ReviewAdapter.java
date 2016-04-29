package com.example.hasna2.movieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hasna2.movieapp.Models.Review;

/**
 * Created by hasna2 on 23-Apr-16.
 */
public class ReviewAdapter extends BaseAdapter {
    Review[] reviews;
    Context context;
    private final String LOG_TAG =ReviewAdapter.class.getSimpleName() ;

    ReviewAdapter(Context context, Review[] reviews) {
        this.reviews = reviews;
        this.context = context;

    }

    @Override
    public int getCount() {
        return reviews.length;
    }

    @Override
    public Object getItem(int i) {
        return reviews[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        Holder holder = new Holder();
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.review_list_item, null);
            holder.author = (TextView) view.findViewById(R.id.author);
            holder.content = (TextView) view.findViewById(R.id.content);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        holder.author.setText(((Review)getItem(i)).author);
        holder.content.setText(((Review)getItem(i)).content);
        return view;
    }
    class Holder {
        TextView author ;
        TextView content;
    }

}

