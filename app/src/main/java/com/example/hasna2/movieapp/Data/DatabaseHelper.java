package com.example.hasna2.movieapp.Data;

/**
 * Created by hasna2 on 15-Apr-16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.hasna2.movieapp.Models.MovieModule;

public class DatabaseHelper extends SQLiteOpenHelper {


    private static String DB_NAME="movie.db";
    public SQLiteDatabase myDataBase;
    private final Context myContext;

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

    public  void delete (String table , String whereClause){
         myDataBase = this.getWritableDatabase();
         myDataBase.delete(table,whereClause,null);

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

        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + "favorites" + " ("+
                MovieModule.MOVIE_ID + " TEXT PRIMARY KEY "+" );";

        final String SQL_CREATE_RESPONSE_TABLE = "CREATE TABLE " + MovieContract.RESPONSE_ENTRY.TABLE_NAME + " ("+
                MovieModule.MOVIE_ID + " TEXT PRIMARY KEY "+" );";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_RESPONSE_TABLE);
        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieModule.TABLE_NAME);
        onCreate(db);

    }

}