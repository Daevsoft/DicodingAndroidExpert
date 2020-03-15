package com.daevsoft.muvi.models;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.daevsoft.muvi.BuildConfig;
import com.daevsoft.muvi.entities.TvShowEntity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class TvShowViewModel extends ViewModel {
    private MutableLiveData<ArrayList<TvShowEntity>> mTvData = new MutableLiveData<>();
    private DbMovieLoadCallback dbMovieLoadCallback;

    public void setMovieLoadCallback(DbMovieLoadCallback loadCallback) {
        this.dbMovieLoadCallback = loadCallback;
    }

    public void setTvShows(String lang, @Nullable String querySearch) {
        if (lang == null) lang = "en-US";
        final ArrayList<TvShowEntity> listTvShow = new ArrayList<>();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        AsyncHttpClient client = new AsyncHttpClient();
        String url;
        if (querySearch == null || TextUtils.isEmpty(querySearch.trim()))
            url = "https://api.themoviedb.org/3/discover/tv?api_key=" + BuildConfig.TMDB_API_KEY + "&language=" + lang + "&sort_by=popularity.desc";
        else
            url = "https://api.themoviedb.org/3/search/tv?api_key=" + BuildConfig.TMDB_API_KEY + "&language=" + lang + "&query=" + querySearch.trim();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                dbMovieLoadCallback.onPrepare();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray arrMovieTv = object.getJSONArray("results");
                    for (int i = 0; i < arrMovieTv.length(); i++) {
                        JSONObject objTvShow = arrMovieTv.getJSONObject(i);
                        String title = objTvShow.getString("name");
                        String poster = objTvShow.getString("poster_path");
                        String rating = objTvShow.getString("vote_average");
                        int id = objTvShow.getInt("id");
                        Date release = sdf.parse(objTvShow.getString("first_air_date"));

                        TvShowEntity tvShowEntity = new TvShowEntity();
                        tvShowEntity.setTitle(title);
                        tvShowEntity.setPoster(poster);
                        tvShowEntity.setRating(rating);
                        tvShowEntity.setId(id);
                        tvShowEntity.setRelease(release);
                        listTvShow.add(tvShowEntity);
                    }
                    mTvData.postValue(listTvShow);
                    dbMovieLoadCallback.onSuccess();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public LiveData<ArrayList<TvShowEntity>> getTvShowData() {
        return mTvData;
    }
}
