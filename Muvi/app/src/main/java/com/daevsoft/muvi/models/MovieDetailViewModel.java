package com.daevsoft.muvi.models;

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
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class MovieDetailViewModel extends ViewModel {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private MutableLiveData<MovieEntity> dataMovie = new MutableLiveData<>();

    public void setDataMovie(final int id, String lang) {
        final String url = "https://api.themoviedb.org/3/movie/" + id + "?api_key=" + BuildConfig.TMDB_API_KEY + "&language=" + lang;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try {
                    MovieEntity movieEntity = new MovieEntity();
                    JSONObject objMovie = new JSONObject(response);
                    String title = objMovie.getString("title");
                    String overview = objMovie.getString("overview");
                    String rating = objMovie.getString("vote_average");
                    JSONArray genreList = objMovie.getJSONArray("genres");
                    String[] genres = new String[genreList.length()];
                    for (int i = 0; i < genreList.length(); i++)
                        genres[i] = genreList.getJSONObject(i).getString("name");

                    String poster = objMovie.getString("poster_path");
                    Date date = dateFormat.parse(objMovie.getString("release_date"));

                    movieEntity.setId(id);
                    movieEntity.setTitle(title);
                    movieEntity.setDescription(overview);
                    movieEntity.setRating(rating);
                    movieEntity.setGenre(genres);
                    movieEntity.setPoster(poster);
                    movieEntity.setRelease(date);

                    dataMovie.postValue(movieEntity);
                } catch (Exception ex) {
                    Log.e(this.getClass().getSimpleName(), ex.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public LiveData<MovieEntity> getDataMovie() {
        return dataMovie;
    }
}
