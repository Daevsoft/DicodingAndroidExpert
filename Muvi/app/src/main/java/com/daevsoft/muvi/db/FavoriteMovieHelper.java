package com.daevsoft.muvi.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;

import androidx.annotation.Nullable;

import com.daevsoft.muvi.R;
import com.daevsoft.muvi.entities.MovieEntity;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

import static android.provider.BaseColumns._ID;
import static com.daevsoft.muvi.db.DatabaseContract.ColumnFavMovies.DESCRIPTION;
import static com.daevsoft.muvi.db.DatabaseContract.ColumnFavMovies.GENRE;
import static com.daevsoft.muvi.db.DatabaseContract.ColumnFavMovies.MOVIE_ID;
import static com.daevsoft.muvi.db.DatabaseContract.ColumnFavMovies.POSTER;
import static com.daevsoft.muvi.db.DatabaseContract.ColumnFavMovies.RATING;
import static com.daevsoft.muvi.db.DatabaseContract.ColumnFavMovies.RELEASE;
import static com.daevsoft.muvi.db.DatabaseContract.ColumnFavMovies.TITLE;

public class FavoriteMovieHelper {
    private static FavoriteMovieHelper INSTANCE;
    private SQLiteDatabase sqlDatabase;
    private DatabaseHelper dbHelper;

    public FavoriteMovieHelper(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public static FavoriteMovieHelper getInstance(Context context) {
        if (INSTANCE == null)
            synchronized (SQLiteOpenHelper.class) {
                INSTANCE = new FavoriteMovieHelper(context);
            }
        return INSTANCE;
    }

    public static ContentValues parseContentValues(MovieEntity entity) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        ContentValues values = new ContentValues();
        values.put(MOVIE_ID, Integer.toString(entity.getId()));
        values.put(TITLE, entity.getTitle());
        values.put(DESCRIPTION, entity.getDescription());
        values.put(RATING, entity.getRating());
        values.put(GENRE, Arrays.toString(entity.getGenre()));
        values.put(RELEASE, sdf.format(entity.getRelease()));
        values.put(POSTER, entity.getPoster());
        return values;
    }

    public void open() throws SQLiteException {
        sqlDatabase = dbHelper.getWritableDatabase();
    }

    public void close() throws SQLiteException {
        dbHelper.close();

        if (sqlDatabase != null)
            if (sqlDatabase.isOpen())
                sqlDatabase.close();
    }

    public long insert(MovieEntity entity, @Nullable View view) {
        ContentValues values = parseContentValues(entity);
        if (exist(entity.getId().toString())) {
            if (view != null)
                Snackbar.make(view,
                        view.getResources().getString(R.string.data_has_exist),
                        Snackbar.LENGTH_SHORT).show();
            return update(values, entity.getId().toString());
        } else {
            if (view != null)
                Snackbar.make(view,
                        view.getResources().getString(R.string.added_to_favorite),
                        Snackbar.LENGTH_SHORT).show();
            return sqlDatabase.insert(DatabaseContract.TBL_NAME_FAV_MOVIES, null, values);
        }
    }

    public long insert(ContentValues values, @Nullable View view) {
        return sqlDatabase.insert(DatabaseContract.TBL_NAME_FAV_MOVIES, null, values);
    }

    public long delete(MovieEntity entity) {
        return sqlDatabase.delete(DatabaseContract.TBL_NAME_FAV_MOVIES,
                MOVIE_ID + "=?",
                new String[]{entity.getId().toString()});
    }

    public long delete(String movieId) {
        return sqlDatabase.delete(DatabaseContract.TBL_NAME_FAV_MOVIES,
                MOVIE_ID + "=?",
                new String[]{movieId});
    }

    public long update(ContentValues values, String movieId) {
        return sqlDatabase.update(DatabaseContract.TBL_NAME_FAV_MOVIES, values,
                MOVIE_ID + "=?",
                new String[]{movieId});
    }

    public Cursor queryById(String movieId) {
        return sqlDatabase.query(DatabaseContract.TBL_NAME_FAV_MOVIES, null,
                MOVIE_ID + "=?",
                new String[]{movieId},
                null, null, null);
    }

    public boolean exist(String movieId) {
        return queryById(movieId).moveToNext();
    }

    public Cursor queryAll() {
        return sqlDatabase.query(DatabaseContract.TBL_NAME_FAV_MOVIES,
                null, null, null,
                null, null, _ID + " ASC");
    }
}
