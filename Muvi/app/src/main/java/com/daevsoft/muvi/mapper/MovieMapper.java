package com.daevsoft.muvi.mapper;

import android.database.Cursor;

import com.daevsoft.muvi.db.DatabaseContract;
import com.daevsoft.muvi.entities.MovieEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MovieMapper {
    public static ArrayList<MovieEntity> toArrayList(Cursor movieCursor) {
        ArrayList<MovieEntity> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        try {
            while (movieCursor.moveToNext()) {
                int id = movieCursor.getInt(movieCursor.getColumnIndex(DatabaseContract.ColumnFavMovies.MOVIE_ID));
                String title = movieCursor.getString(movieCursor.getColumnIndex(DatabaseContract.ColumnFavMovies.TITLE));
                String description = movieCursor.getString(movieCursor.getColumnIndex(DatabaseContract.ColumnFavMovies.DESCRIPTION));
                String rating = movieCursor.getString(movieCursor.getColumnIndex(DatabaseContract.ColumnFavMovies.RATING));
                String genres = movieCursor.getString(movieCursor.getColumnIndex(DatabaseContract.ColumnFavMovies.GENRE));
                String poster = movieCursor.getString(movieCursor.getColumnIndex(DatabaseContract.ColumnFavMovies.POSTER));
                String release = movieCursor.getString(movieCursor.getColumnIndex(DatabaseContract.ColumnFavMovies.RELEASE));

                MovieEntity data = new MovieEntity();
                data.setId(id);
                data.setDescription(description);
                data.setRating(rating);
                data.setTitle(title);
                data.setGenre(genres.split(","));
                data.setPoster(poster);
                data.setRelease(sdf.parse(release));
                list.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
