package com.daevsoft.muvi.models;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.daevsoft.muvi.db.DatabaseContract;
import com.daevsoft.muvi.db.DbViewModelCallback;
import com.daevsoft.muvi.db.FavoriteAsyncCallback;
import com.daevsoft.muvi.entities.MovieEntity;
import com.daevsoft.muvi.ui.movies.FavMovieAsync;

import java.util.ArrayList;

interface DataObserverCallback {
    void onChange();
}

public class FavMovieViewModel extends ViewModel {
    private MutableLiveData<ArrayList<MovieEntity>> listMutableLiveData = new MutableLiveData<>();
    private Context context;
    private DbViewModelCallback viewModelCallback;

    public void loadListMovie() {
        FavMovieAsync asyc = new FavMovieAsync(context, new FavoriteAsyncCallback<MovieEntity>() {
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
        HandlerThread thread = new HandlerThread("DataObserver");
        thread.start();
        Handler handler = new Handler(thread.getLooper());
        DataObserver dataObserver = new DataObserver(handler, context, new DataObserverCallback() {
            @Override
            public void onChange() {
                loadListMovie();
            }
        });
        context.getContentResolver().registerContentObserver(DatabaseContract.CONTENT_URI_FAV_MOVIE, true, dataObserver);
    }

    public void setCallback(DbViewModelCallback viewModelCallback) {
        this.viewModelCallback = viewModelCallback;
    }

    public void movieDelete(MovieEntity movie, int position) {
        Uri uriWithId = Uri.parse(DatabaseContract.CONTENT_URI_FAV_MOVIE + "/" + movie.getId());
        context.getContentResolver().delete(uriWithId, null, null);
        this.viewModelCallback.onDelete(position);
    }

    public static class DataObserver extends ContentObserver {

        final Context context;
        final DataObserverCallback dataObserverCallback;

        public DataObserver(Handler handler, Context context, DataObserverCallback dataObserverCallback) {
            super(handler);
            this.context = context;
            this.dataObserverCallback = dataObserverCallback;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            dataObserverCallback.onChange();
        }
    }
}