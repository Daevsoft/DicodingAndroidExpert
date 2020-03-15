package com.daevsoft.muvi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "muvidb";
    public static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TABLE_MOVIES = String.format("CREATE TABLE %s"
                    + "(%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "%s INTEGER NOT NULL," + // movie id from api
                    "%s TEXT NOT NULL," + // title
                    "%s TEXT NOT NULL," + // description
                    "%s TEXT NOT NULL," + // poster
                    "%s TEXT NOT NULL," + // rating
                    "%s TEXT NOT NULL," + // release
                    "%s TEXT NOT NULL" + // genre
                    ")",
            DatabaseContract.TBL_NAME_FAV_MOVIES,
            DatabaseContract.ColumnFavMovies._ID,
            DatabaseContract.ColumnFavMovies.MOVIE_ID,
            DatabaseContract.ColumnFavMovies.TITLE,
            DatabaseContract.ColumnFavMovies.DESCRIPTION,
            DatabaseContract.ColumnFavMovies.POSTER,
            DatabaseContract.ColumnFavMovies.RATING,
            DatabaseContract.ColumnFavMovies.RELEASE,
            DatabaseContract.ColumnFavMovies.GENRE);

    private static final String SQL_CREATE_TABLE_TVSHOWS = String.format("CREATE TABLE %s"
                    + "(%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "%s INTEGER NOT NULL," + // tvshow id from api
                    "%s TEXT NOT NULL," + // title
                    "%s TEXT NOT NULL," + // description
                    "%s TEXT NOT NULL," + // poster
                    "%s TEXT NOT NULL," + // rating
                    "%s TEXT NOT NULL," + // release
                    "%s TEXT NOT NULL" + // genre
                    ")",
            DatabaseContract.TBL_NAME_FAV_TVSHOWS,
            DatabaseContract.ColumnFavTvShows._ID,
            DatabaseContract.ColumnFavTvShows.TVSHOW_ID,
            DatabaseContract.ColumnFavTvShows.TITLE,
            DatabaseContract.ColumnFavTvShows.DESCRIPTION,
            DatabaseContract.ColumnFavTvShows.POSTER,
            DatabaseContract.ColumnFavTvShows.RATING,
            DatabaseContract.ColumnFavTvShows.RELEASE,
            DatabaseContract.ColumnFavTvShows.GENRE);

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_MOVIES);
        db.execSQL(SQL_CREATE_TABLE_TVSHOWS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TBL_NAME_FAV_MOVIES);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TBL_NAME_FAV_TVSHOWS);
        onCreate(db);
    }
}