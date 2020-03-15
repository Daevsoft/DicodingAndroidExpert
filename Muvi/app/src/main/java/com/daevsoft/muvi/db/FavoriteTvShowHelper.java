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
import com.daevsoft.muvi.entities.TvShowEntity;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

import static android.provider.BaseColumns._ID;
import static com.daevsoft.muvi.db.DatabaseContract.ColumnFavTvShows.DESCRIPTION;
import static com.daevsoft.muvi.db.DatabaseContract.ColumnFavTvShows.GENRE;
import static com.daevsoft.muvi.db.DatabaseContract.ColumnFavTvShows.POSTER;
import static com.daevsoft.muvi.db.DatabaseContract.ColumnFavTvShows.RATING;
import static com.daevsoft.muvi.db.DatabaseContract.ColumnFavTvShows.RELEASE;
import static com.daevsoft.muvi.db.DatabaseContract.ColumnFavTvShows.TITLE;
import static com.daevsoft.muvi.db.DatabaseContract.ColumnFavTvShows.TVSHOW_ID;

public class FavoriteTvShowHelper {
    private static FavoriteTvShowHelper INSTANCE;
    private SQLiteDatabase sqlDatabase;
    private DatabaseHelper dbHelper;

    public FavoriteTvShowHelper(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public static FavoriteTvShowHelper getInstance(Context context) {
        if (INSTANCE == null)
            synchronized (SQLiteOpenHelper.class) {
                INSTANCE = new FavoriteTvShowHelper(context);
            }
        return INSTANCE;
    }

    public void open() throws SQLiteException {
        sqlDatabase = dbHelper.getWritableDatabase();
    }

    public void close() throws SQLiteException {
        dbHelper.close();

        if (sqlDatabase.isOpen())
            sqlDatabase.close();
    }

    private ContentValues parseContentValues(TvShowEntity entity) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        ContentValues values = new ContentValues();
        values.put(TVSHOW_ID, Integer.toString(entity.getId()));
        values.put(TITLE, entity.getTitle());
        values.put(DESCRIPTION, entity.getDescription());
        values.put(RATING, entity.getRating());
        values.put(GENRE, Arrays.toString(entity.getGenre()));
        values.put(RELEASE, sdf.format(entity.getRelease()));
        values.put(POSTER, entity.getPoster());
        return values;
    }

    public long insert(TvShowEntity entity, @Nullable View view) {
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
            return sqlDatabase.insert(DatabaseContract.TBL_NAME_FAV_TVSHOWS, null, values);
        }
    }

    public long delete(TvShowEntity entity) {
        return sqlDatabase.delete(DatabaseContract.TBL_NAME_FAV_TVSHOWS,
                TVSHOW_ID + "=?",
                new String[]{entity.getId().toString()});
    }

    private long update(ContentValues values, String TvShowId) {
        return sqlDatabase.update(DatabaseContract.TBL_NAME_FAV_TVSHOWS, values,
                TVSHOW_ID + "=?",
                new String[]{TvShowId});
    }

    public boolean exist(String TvShowId) {
        Cursor result = sqlDatabase.query(DatabaseContract.TBL_NAME_FAV_TVSHOWS, null,
                TVSHOW_ID + "=?",
                new String[]{TvShowId},
                null, null, null);
        return result.moveToNext();
    }

    public Cursor queryAll() {
        return sqlDatabase.query(DatabaseContract.TBL_NAME_FAV_TVSHOWS,
                null, null, null,
                null, null, _ID + " ASC");
    }
}
