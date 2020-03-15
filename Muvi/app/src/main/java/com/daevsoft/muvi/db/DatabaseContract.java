package com.daevsoft.muvi.db;

import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseContract {
    public static final String TBL_NAME_FAV_MOVIES = "favmovies";
    public static final String TBL_NAME_FAV_TVSHOWS = "favtvshows";
    private static final String SCHEME = "content";
    private static final String AUTHORITY = "com.daevsoft.muvi";
    public static final Uri CONTENT_URI_FAV_MOVIE = new Uri.Builder()
            .scheme(SCHEME)
            .authority(AUTHORITY)
            .appendPath(TBL_NAME_FAV_MOVIES)
            .build();
    public static final Uri CONTENT_URI_FAV_TVSHOW = new Uri.Builder()
            .scheme(SCHEME)
            .authority(AUTHORITY)
            .appendPath(TBL_NAME_FAV_TVSHOWS)
            .build();

    public class ColumnFavMovies implements BaseColumns {
        public static final String MOVIE_ID = "movie_id";
        public static final String TITLE = "title";
        public static final String RATING = "rating";
        public static final String DESCRIPTION = "description";
        public static final String POSTER = "poster";
        public static final String RELEASE = "release";
        public static final String GENRE = "genre";
    }

    public class ColumnFavTvShows implements BaseColumns {
        public static final String TVSHOW_ID = "tvshow_id";
        public static final String TITLE = "title";
        public static final String RATING = "rating";
        public static final String DESCRIPTION = "description";
        public static final String POSTER = "poster";
        public static final String RELEASE = "release";
        public static final String GENRE = "genre";
    }
}
