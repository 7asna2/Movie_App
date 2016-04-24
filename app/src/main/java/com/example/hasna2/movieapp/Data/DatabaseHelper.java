package com.example.hasna2.movieapp.Data;

/**
 * Created by hasna2 on 15-Apr-16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.hasna2.movieapp.Models.MovieModule;
import com.example.hasna2.movieapp.Models.Review;
import com.example.hasna2.movieapp.Models.Video;

public class DatabaseHelper extends SQLiteOpenHelper {


    private static String DB_NAME="movie.db";
    public SQLiteDatabase myDataBase;
    private final Context myContext;

    /**
     * Constructor Takes and keeps a reference of the passed context in order to
     * access to the application assets and resources.
     *
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }



    /** To insert row in table. */

    public long insertRow(String table,  ContentValues contentArgs) {
        myDataBase = this.getWritableDatabase();
        long id = myDataBase.insert(table, null, contentArgs);
        myDataBase.close();
        return id;
    }

    /** To delete row in table */

    public  void deleteRow (String table , String whereClause){
       myDataBase = this.getWritableDatabase();
        String sql = "DELETE FROM "+table+" WHERE "+whereClause;
        myDataBase.execSQL(sql);

    }

    /** To update row in table. */
    public int updateRow(String table, ContentValues values,
                         String whereClause, String[] whereArgs) {
        Boolean resu = false;
        int noOfRows = 0;
        myDataBase.beginTransaction();
        try {
            noOfRows = myDataBase.update(table, values, whereClause, whereArgs);
            if (noOfRows > 0)
                resu = true;
            myDataBase.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("UPDATE", e.getMessage());
            if (noOfRows < 0)
                resu = false;
        } finally {
            myDataBase.endTransaction();
        }

        return noOfRows;
    }

    /** To select all rows in table. */
    public Cursor selectAllRaw(String tableName) {
        myDataBase = this.getWritableDatabase();
        Cursor cursor ;
        cursor =myDataBase.rawQuery("select * from "+tableName,null);
        if(cursor==null)Log.v("211","cursor null");
        return cursor;
    }

    /** To select specific row in table. */
    public Cursor selectRaw(String tableName, String wherecol) {
        myDataBase = this.getWritableDatabase();
        Cursor cursor;
        String sql = "SELECT * FROM " + tableName+" WHERE "+ wherecol;
        cursor = myDataBase.rawQuery(sql, null);
        if(cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    @Override
    public synchronized void close() {

        if (myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieModule.TABLE_NAME + " (" +
                MovieModule.MOVIE_ID + " TEXT PRIMARY KEY ," +
                MovieModule.MOVIE_TITLE + " TEXT NOT NULL," +
                MovieModule.MOVIE_OVERVIEW + " TEXT NOT NULL," +
                MovieModule.MOVIE_POSTER_PATH + " TEXT NOT NULL," +
                MovieModule.MOVIE_ORIGINAL_LANGUAGE + " TEXT NOT NULL," +
                MovieModule.MOVIE_VOTE_COUNT + " TEXT NOT NULL," +
                MovieModule.MOVIE_VOTE_AVERAGE+" TEXT NOT NULL," +
                MovieModule.MOVIE_RELEASE_DATE +" TEXT NOT NULL "+
                " );";
        final String SQL_CREATE_VIDEOS_TABLE = "CREATE TABLE " + Video.TABLE_NAME + " (" +
                Video.VIDEO_KEY + " TEXT PRIMARY KEY ," +
                Video.VIDEO_MOVIE_ID + " TEXT NOT NULL," +
                Video.VIDEO_NAME + " TEXT NOT NULL," +
                Video.VIDEO_SITE + " TEXT NOT NULL," +
                " FOREIGN KEY (" +  Video.VIDEO_MOVIE_ID + ") REFERENCES " +
                MovieModule.TABLE_NAME + " (" +MovieModule.MOVIE_ID + ")" +
                " );";
        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + Review.TABLE_NAME + " (" +
                Review.REVIEW_CONTENT + " TEXT PRIMARY KEY ," +
                Review.REVIEW_MOVIE_ID + " TEXT NOT NULL," +
                Review.REVIEW_AUTHOR+ " TEXT NOT NULL," +
                Video.VIDEO_SITE + " TEXT NOT NULL," +
                " FOREIGN KEY (" +  Review.REVIEW_MOVIE_ID + ") REFERENCES " +
                MovieModule.TABLE_NAME + " (" +MovieModule.MOVIE_ID + ") " +
                " );";
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + "favorites" + " ("+
                MovieModule.MOVIE_ID + " TEXT PRIMARY KEY "+" );";


        db.execSQL(SQL_CREATE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_VIDEOS_TABLE);
        db.execSQL(SQL_CREATE_REVIEWS_TABLE);
        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieModule.TABLE_NAME);
        onCreate(db);

    }
}