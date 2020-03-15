package com.daevsoft.muvi.models;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.daevsoft.muvi.db.DbViewModelCallback;
import com.daevsoft.muvi.db.FavoriteAsyncCallback;
import com.daevsoft.muvi.db.FavoriteTvShowHelper;
import com.daevsoft.muvi.entities.TvShowEntity;
import com.daevsoft.muvi.ui.tvshows.FavTvShowAsync;

import java.util.ArrayList;

public class FavTvShowViewModel extends ViewModel {
    private MutableLiveData<ArrayList<TvShowEntity>> listMutableLiveData = new MutableLiveData<>();
    private FavoriteTvShowHelper TvShowHelper;
    private DbViewModelCallback viewModelCallback;

    public void loadListTvShow() {
        FavTvShowAsync asyc = new FavTvShowAsync(TvShowHelper, new FavoriteAsyncCallback<TvShowEntity>() {
            @Override
            public void onSuccess(ArrayList<TvShowEntity> TvShowEntities) {
                listMutableLiveData.postValue(TvShowEntities);
            }
        });
        asyc.execute();
    }

    public LiveData<ArrayList<TvShowEntity>> getTvShowData() {
        return listMutableLiveData;
    }

    public void setContext(Context context) {
        TvShowHelper = new FavoriteTvShowHelper(context);
        TvShowHelper.open();
    }

    public void setCallback(DbViewModelCallback viewModelCallback) {
        this.viewModelCallback = viewModelCallback;
    }

    public void TvShowDelete(TvShowEntity TvShow, @Nullable int position) {
        TvShowHelper.delete(TvShow);
        this.viewModelCallback.onDelete(position);
    }

    public void close() {
        TvShowHelper.close();
    }
}
