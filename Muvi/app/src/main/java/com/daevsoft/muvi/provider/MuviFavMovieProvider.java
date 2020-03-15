package com.daevsoft.muvi.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.daevsoft.muvi.db.FavoriteMovieHelper;

import static com.daevsoft.muvi.db.DatabaseContract.CONTENT_URI_FAV_MOVIE;
import static com.daevsoft.muvi.db.DatabaseContract.TBL_NAME_FAV_MOVIES;

public class MuviFavMovieProvider extends ContentProvider {
    private static final int MOVIE = 1;
    private static final int MOVIE_ID = 2;

    private FavoriteMovieHelper favMovieHelper;
    private UriMatcher uriMatcher;
    private Context context;

    public MuviFavMovieProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        long size = 0;
        if (uriMatcher.match(uri) == MOVIE_ID) {
            String id = uri.getLastPathSegment();
            if (id != null)
                size = favMovieHelper.delete(id);
        }
        return (int) size;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = 0;
        if (uriMatcher.match(uri) == MOVIE) {
            id = favMovieHelper.insert(values, null);
        }
        if (context != null)
            context.getContentResolver().notifyChange(CONTENT_URI_FAV_MOVIE, null);
        return Uri.parse(CONTENT_URI_FAV_MOVIE + "/" + id);
    }

    @Override
    public boolean onCreate() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CONTENT_URI_FAV_MOVIE.getAuthority(), TBL_NAME_FAV_MOVIES, MOVIE);
        uriMatcher.addURI(CONTENT_URI_FAV_MOVIE.getAuthority(), TBL_NAME_FAV_MOVIES + "/#", MOVIE_ID);

        context = getContext();
        favMovieHelper = FavoriteMovieHelper.getInstance(context);
        favMovieHelper.open();

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor result = null;
        switch (uriMatcher.match(uri)) {
            case MOVIE:
                result = favMovieHelper.queryAll();
                break;
            case MOVIE_ID:
                result = favMovieHelper.queryById(uri.getLastPathSegment());
                break;
            default:
                break;
        }
        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        long id = 0;
        if (uriMatcher.match(uri) == MOVIE_ID) {
            id = favMovieHelper.update(values, uri.getLastPathSegment());
        }
        return (int) id;
    }
}
