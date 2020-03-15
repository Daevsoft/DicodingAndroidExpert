package com.daevsoft.muvi.widgets;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.daevsoft.muvi.R;
import com.daevsoft.muvi.db.FavoriteMovieHelper;
import com.daevsoft.muvi.entities.MovieEntity;
import com.daevsoft.muvi.mapper.MovieMapper;
import com.daevsoft.muvi.ui.movies.MovieFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class StackMuviRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private List<MovieEntity> movieEntityList = new ArrayList<>();
    private FavoriteMovieHelper helper;
    private List<Bitmap> bitmapList = new ArrayList<>();
    private Context context;

    StackMuviRemoteViewsFactory(Context context) {
        this.context = context;
        helper = FavoriteMovieHelper.getInstance(context);
        helper.open();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        Cursor cursorMovies = helper.queryAll();
        movieEntityList = MovieMapper.toArrayList(cursorMovies);
        try {
            for (MovieEntity movie : movieEntityList) {
                Bitmap bitmap = Glide.with(context)
                        .asBitmap()
                        .load(MovieFragment.IMAGE_URL + movie.getPoster())
                        .error(R.drawable.img_failed)
                        .apply(new RequestOptions().override(180, 250).fitCenter())
                        .submit()
                        .get();
                bitmapList.add(bitmap);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return bitmapList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.muvi_widget_item);
        rv.setImageViewBitmap(R.id.widget_imageView, bitmapList.get(position));

        Bundle extra = new Bundle();
        Intent intent = new Intent();
        extra.putInt(MuviWidget.EXTRA_POSITION, position);
        extra.putParcelable(MuviWidget.EXTRA_MOVIE_ITEM, movieEntityList.get(position));
        intent.putExtras(extra);
        rv.setOnClickFillInIntent(R.id.widget_imageView, intent);
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
