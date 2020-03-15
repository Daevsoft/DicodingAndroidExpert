package com.daevsoft.muvi.ui.movies;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.daevsoft.muvi.db.DatabaseContract;
import com.daevsoft.muvi.db.FavoriteAsyncCallback;
import com.daevsoft.muvi.entities.MovieEntity;
import com.daevsoft.muvi.mapper.MovieMapper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class FavMovieAsync extends AsyncTask<Void, Void, ArrayList<MovieEntity>> {

    private WeakReference<Context> contextWeakReference;
    private WeakReference<FavoriteAsyncCallback<MovieEntity>> weakMovieCallback;

    public FavMovieAsync(Context context, FavoriteAsyncCallback<MovieEntity> weakMovieCallback) {
        this.contextWeakReference = new WeakReference<>(context);
        this.weakMovieCallback = new WeakReference<>(weakMovieCallback);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(ArrayList<MovieEntity> movieEntities) {
        super.onPostExecute(movieEntities);
        weakMovieCallback.get().onSuccess(movieEntities);
    }

    @Override
    protected ArrayList<MovieEntity> doInBackground(Void... voids) {
        Cursor cursor = contextWeakReference.get().getContentResolver().query(DatabaseContract.CONTENT_URI_FAV_MOVIE, null, null, null);
        return MovieMapper.toArrayList(cursor);
    }
}