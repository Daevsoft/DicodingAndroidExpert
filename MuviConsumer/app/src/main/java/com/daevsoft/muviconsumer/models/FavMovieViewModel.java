package com.daevsoft.muviconsumer.models;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.daevsoft.muviconsumer.asyncs.FavMovieAsync;
import com.daevsoft.muviconsumer.db.DatabaseContract;
import com.daevsoft.muviconsumer.db.DbViewModelCallback;
import com.daevsoft.muviconsumer.entities.MovieEntity;

import java.util.ArrayList;

public class FavMovieViewModel extends ViewModel {
    private MutableLiveData<ArrayList<MovieEntity>> listMutableLiveData = new MutableLiveData<>();
    private Context context;
    private DbViewModelCallback viewModelCallback;

    public void loadListMovie() {
        FavMovieAsync asyc = new FavMovieAsync(context, new FavoriteAsyncCallback<MovieEntity>() {
            @Override
            public void onPreProcess() {
            }

            @Override
            public void onSuccess(ArrayList<MovieEntity> movieEntities) {
                listMutableLiveData.postValue(movieEntities);
            }
        });
        asyc.execute();
    }

    public LiveData<ArrayList<MovieEntity>> getMovieData() {
        return listMutableLiveData;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setCallback(DbViewModelCallback viewModelCallback) {
        this.viewModelCallback = viewModelCallback;
    }

    public void movieDelete(MovieEntity movie, int position) {
        Uri uriWithId = Uri.parse(DatabaseContract.CONTENT_URI_FAV_MOVIE + "/" + movie.getId());
        context.getContentResolver().delete(uriWithId, null, null);
        this.viewModelCallback.onDelete(position);
    }
}