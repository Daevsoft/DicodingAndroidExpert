package com.daevsoft.muvi.models;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.daevsoft.muvi.BuildConfig;
import com.daevsoft.muvi.entities.MovieEntity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class MovieViewModel extends ViewModel {
    private MutableLiveData<ArrayList<MovieEntity>> mMovieData = new MutableLiveData<>();
    private DbMovieLoadCallback dbMovieLoadCallback;

    public void setMovieLoadCallback(DbMovieLoadCallback loadCallback) {
        this.dbMovieLoadCallback = loadCallback;
    }

    public void setMovies(String lang, String querySearch) {
        if (lang == null) lang = "en-US";
        final ArrayList<MovieEntity> listMovie = new ArrayList<>();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        AsyncHttpClient client = new AsyncHttpClient();
        String url;
        if (querySearch == null || TextUtils.isEmpty(querySearch.trim()))
            url = "https://api.themoviedb.org/3/discover/movie?api_key=" + BuildConfig.TMDB_API_KEY + "&language=" + lang + "&sort_by=popularity.desc";
        else
            url = "https://api.themoviedb.org/3/search/movie?api_key=" + BuildConfig.TMDB_API_KEY + "&language=" + lang + "&query=" + querySearch.trim();
        Log.d("", "setMovies: " + url);
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
                        JSONObject objMovieOrTv = arrMovieTv.getJSONObject(i);
                        String title = objMovieOrTv.getString("title");
                        String poster = objMovieOrTv.getString("poster_path");
                        String rating = objMovieOrTv.getString("vote_average");
                        int id = objMovieOrTv.getInt("id");
                        Date release = sdf.parse(objMovieOrTv.getString("release_date"));

                        MovieEntity movieEntity = new MovieEntity();
                        movieEntity.setTitle(title);
                        movieEntity.setPoster(poster);
                        movieEntity.setRating(rating);
                        movieEntity.setId(id);
                        movieEntity.setRelease(release);
                        listMovie.add(movieEntity);
                    }
                    mMovieData.postValue(listMovie);
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

    public LiveData<ArrayList<MovieEntity>> getMovieData() {
        return mMovieData;
    }
}
