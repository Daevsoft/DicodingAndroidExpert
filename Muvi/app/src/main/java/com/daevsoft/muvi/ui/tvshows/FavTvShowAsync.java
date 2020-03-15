package com.daevsoft.muvi.ui.tvshows;


import android.database.Cursor;
import android.os.AsyncTask;

import com.daevsoft.muvi.db.FavoriteAsyncCallback;
import com.daevsoft.muvi.db.FavoriteTvShowHelper;
import com.daevsoft.muvi.entities.TvShowEntity;
import com.daevsoft.muvi.mapper.TvShowMapper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class FavTvShowAsync extends AsyncTask<Void, Void, ArrayList<TvShowEntity>> {

    private WeakReference<FavoriteTvShowHelper> weakTvShowHelper;
    private WeakReference<FavoriteAsyncCallback<TvShowEntity>> weakTvShowCallback;

    public FavTvShowAsync(FavoriteTvShowHelper weakTvShowHelper, FavoriteAsyncCallback<TvShowEntity> weakTvShowCallback) {
        this.weakTvShowHelper = new WeakReference<>(weakTvShowHelper);
        this.weakTvShowCallback = new WeakReference<>(weakTvShowCallback);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(ArrayList<TvShowEntity> TvShowEntities) {
        super.onPostExecute(TvShowEntities);
        weakTvShowCallback.get().onSuccess(TvShowEntities);
    }

    @Override
    protected ArrayList<TvShowEntity> doInBackground(Void... voids) {
        Cursor cursor = weakTvShowHelper.get().queryAll();
        return TvShowMapper.toArrayList(cursor);
    }
}