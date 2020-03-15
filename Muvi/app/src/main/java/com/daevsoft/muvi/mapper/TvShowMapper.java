package com.daevsoft.muvi.mapper;


import android.database.Cursor;

import com.daevsoft.muvi.db.DatabaseContract;
import com.daevsoft.muvi.entities.TvShowEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TvShowMapper {
    public static ArrayList<TvShowEntity> toArrayList(Cursor TvShowCursor) {
        ArrayList<TvShowEntity> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        try {
            while (TvShowCursor.moveToNext()) {
                int id = TvShowCursor.getInt(TvShowCursor.getColumnIndex(DatabaseContract.ColumnFavTvShows.TVSHOW_ID));
                String title = TvShowCursor.getString(TvShowCursor.getColumnIndex(DatabaseContract.ColumnFavTvShows.TITLE));
                String description = TvShowCursor.getString(TvShowCursor.getColumnIndex(DatabaseContract.ColumnFavTvShows.DESCRIPTION));
                String rating = TvShowCursor.getString(TvShowCursor.getColumnIndex(DatabaseContract.ColumnFavTvShows.RATING));
                String genres = TvShowCursor.getString(TvShowCursor.getColumnIndex(DatabaseContract.ColumnFavTvShows.GENRE));
                String poster = TvShowCursor.getString(TvShowCursor.getColumnIndex(DatabaseContract.ColumnFavTvShows.POSTER));
                String release = TvShowCursor.getString(TvShowCursor.getColumnIndex(DatabaseContract.ColumnFavTvShows.RELEASE));

                TvShowEntity data = new TvShowEntity();
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
