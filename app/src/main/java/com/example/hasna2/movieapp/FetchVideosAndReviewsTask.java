package com.example.hasna2.movieapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by hasna2 on 09-Apr-16.
 */
public class FetchVideosAndReviewsTask extends AsyncTask<String,Void , String[]> {
    Context context;
    View rootView;
    final String LOG_TAG = "videos";
    Uri.Builder builder = new Uri.Builder();
    String myUrl;
    class Video {
        String Name;
        String key;
        String site;
    }
    class Review {
        String author;
        String content;
    }

    public FetchVideosAndReviewsTask(Context context,View rootView){
        this.context=context;
        this.rootView=rootView;
    }

    @Override
    protected void onPostExecute(String[] result) {
        final Video[] videosArray;
        Review [] ReviewsArray;
        ListView listView = (ListView) rootView.findViewById(R.id.videos_list);
        ListView reviewslistView = (ListView) rootView.findViewById(R.id.reviews_list);

        try {
            if (result[0] != null) {
                videosArray = (new VideoJSONParser(result[0])).getVideosIDs();
                ArrayAdapter<String> in = new ArrayAdapter<String>(context, R.layout.video, R.id.trialer_name, new ArrayList<String>());
                listView.setAdapter(in);
                in.clear();
                for (Video v : videosArray)
                    in.add(v.Name);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Uri uri = Uri.parse("https://www.youtube.com/watch?").buildUpon().appendQueryParameter("v", videosArray[i].key).build();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(uri);
                        context.startActivity(intent);
                    }
                });
            }
            if (result[1] != null) {
                 ReviewsArray = (new ReviewJSONParser(result[1])).getReviewsIDs();
                ArrayAdapter<String> reviewsAdapter = new ArrayAdapter<String>(context, R.layout.review_list_item, R.id.expandable_text, new ArrayList<String>());

                reviewslistView.setAdapter(reviewsAdapter);
                reviewsAdapter.clear();
                for (Review v : ReviewsArray)
                    reviewsAdapter.add(v.content);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            setListViewHeightBasedOnChildren(reviewslistView);
            setListViewHeightBasedOnChildren(listView);
        }

    }

    @Override
    protected String[] doInBackground(String... params) {
            String JSONString [ ]= new String[2];
        try {
            JSONString[0] = connect(getURL("videos",params[0])) ;
            JSONString[1] = connect(getURL("reviews",params[0])) ;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        return JSONString ;
    }


    class ReviewJSONParser {
        final String RESULT = "results";
        final String AUTHOR = "author";
        final String CONTENT = "content";
        String JSONstr;

        public ReviewJSONParser(String JSONstr) {
            this.JSONstr = JSONstr;
        }

        public Review[] getReviewsIDs() throws JSONException {
            ArrayList<Review> reviewsArrays = new ArrayList<>();
            JSONObject videosJson = new JSONObject(JSONstr);
            JSONArray JSONVideosArray = videosJson.getJSONArray(RESULT);

            for (int i = 0; i < JSONVideosArray.length(); i++) {
                JSONObject video = JSONVideosArray.getJSONObject(i);
                Review temp = new Review();
                temp.author = video.getString(AUTHOR);
                temp.content = video.getString(CONTENT);
                reviewsArrays.add(temp);
            }
            return reviewsArrays.toArray(new Review[reviewsArrays.size()]);
        }
    }

    class VideoJSONParser {
        final String RESULT = "results";
        final String SITE = "site";
        final String KEY = "key";
        final String YOUTUBE = "YouTube";
        final String NAME="name";
        String JSONstr;

        public VideoJSONParser(String JSONstr) {
            this.JSONstr = JSONstr;
        }

        public Video[] getVideosIDs() throws JSONException {
            ArrayList<Video> videosArray = new ArrayList<>();
            JSONObject videosJson = new JSONObject(JSONstr);
            JSONArray JSONVideosArray = videosJson.getJSONArray(RESULT);

            for (int i = 0; i < JSONVideosArray.length(); i++) {
                JSONObject video = JSONVideosArray.getJSONObject(i);
                Video temp = new Video();
                temp.Name=video.getString(NAME);
                temp.site=video.getString(SITE);
                temp.key=video.getString(KEY);
                if (temp.site.equals(YOUTUBE))
                    videosArray.add(temp);
            }
            return videosArray.toArray(new Video[videosArray.size()]);
        }
    }

    private URL getURL (String type,String ID) throws MalformedURLException {
        final String themoviesdpURL = "http://api.themoviedb.org/3/movie/";
        Uri uri;
        uri = Uri.parse(themoviesdpURL)
                .buildUpon()
                .appendPath(ID)
                .appendPath(type)
                //.appendPath("videos")
                .appendQueryParameter("api_key", BuildConfig.api_key)
                .build();
        myUrl = uri.toString();
        Log.v(LOG_TAG, "Buld URI: " + myUrl);


        return new URL(myUrl);
    }

    private String connect (URL url){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String JsonStr = null;

        try {
            //String api_key = "49c571e3be31260c5199658afa4dd2f5";

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                Log.v(LOG_TAG, "NO INPUTSTREAM");
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            Log.v(LOG_TAG, "WHats upp????");
            JsonStr = buffer.toString();
            //////////////////////////////////////////////////////////////////////////
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
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
        return JsonStr;
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



