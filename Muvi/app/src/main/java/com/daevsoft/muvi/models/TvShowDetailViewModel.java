package com.daevsoft.muvi.models;

import android.util.Log;

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
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class TvShowDetailViewModel extends ViewModel {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private MutableLiveData<TvShowEntity> dataTvShow = new MutableLiveData<>();

    public void setDataTvShow(final int id, String lang) {
        String url = "https://api.themoviedb.org/3/tv/" + id + "?api_key=" + BuildConfig.TMDB_API_KEY + "&language=" + lang;
        Log.d("URL", "setDataTvShow: " + url);
        final AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try {
                    TvShowEntity tvShowEntity = new TvShowEntity();
                    JSONObject objTvShow = new JSONObject(response);
                    String title = objTvShow.getString("name");
                    String overview = objTvShow.getString("overview");
                    String rating = objTvShow.getString("vote_average");
                    JSONArray genreList = objTvShow.getJSONArray("genres");
                    String[] genres = new String[genreList.length()];
                    for (int i = 0; i < genreList.length(); i++)
                        genres[i] = genreList.getJSONObject(i).getString("name");
                    String poster = objTvShow.getString("poster_path");
                    Date date = dateFormat.parse(objTvShow.getString("first_air_date"));

                    tvShowEntity.setTitle(title);
                    tvShowEntity.setDescription(overview);
                    tvShowEntity.setRating(rating);
                    tvShowEntity.setGenre(genres);
                    tvShowEntity.setPoster(poster);
                    tvShowEntity.setRelease(date);
                    tvShowEntity.setId(id);

                    dataTvShow.postValue(tvShowEntity);

                } catch (Exception ex) {
                    Log.e(this.getClass().getSimpleName(), ex.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public LiveData<TvShowEntity> getDataTvShow() {
        return dataTvShow;
    }
}
