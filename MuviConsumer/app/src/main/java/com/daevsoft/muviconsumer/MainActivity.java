package com.daevsoft.muviconsumer;

import android.content.Context;
import android.content.DialogInterface;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daevsoft.muviconsumer.adapters.FavMovieListAdapter;
import com.daevsoft.muviconsumer.asyncs.FavMovieAsync;
import com.daevsoft.muviconsumer.db.DatabaseContract;
import com.daevsoft.muviconsumer.db.DbViewModelCallback;
import com.daevsoft.muviconsumer.entities.MovieEntity;
import com.daevsoft.muviconsumer.models.FavMovieViewModel;
import com.daevsoft.muviconsumer.models.FavoriteAsyncCallback;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FavoriteAsyncCallback<MovieEntity> {

    public static final String IMAGE_URL = "https://image.tmdb.org/t/p/w342";

    private RecyclerView rvMovies;
    private ProgressBar pgbLoading;

    private FavMovieViewModel mViewModel;

    private FavMovieListAdapter favMovieListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvMovies = findViewById(R.id.main_rvMovie);
        pgbLoading = findViewById(R.id.main_pgbLoading);

        HandlerThread handlerThread = new HandlerThread("DataObserver");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());

        DataObserver dataObserver = new DataObserver(handler, this);
        dataObserver.setFavMovieCallback(this);
        getContentResolver().registerContentObserver(DatabaseContract.CONTENT_URI_FAV_MOVIE, true, dataObserver);

        favMovieListAdapter = new FavMovieListAdapter(getBaseContext(),
                new FavMovieListAdapter.FavMovieClickListener() {
                    @Override
                    public void onItemClick(MovieEntity movie) {
                        Toast.makeText(getBaseContext(), movie.getTitle(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void FavoriteMovieClicked(MovieEntity movie, int position) {
                        final MovieEntity movieEntity = movie;
                        final int index = position;
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.baseDialog))
                                .setTitle(getBaseContext().getString(R.string.info))
                                .setIcon(R.drawable.ic_info_blue)
                                .setMessage(getBaseContext().getString(R.string.sure_to_unfavorite))
                                .setPositiveButton(getBaseContext().getString(R.string.unfavorite),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                mViewModel.movieDelete(movieEntity, index);
                                            }
                                        })
                                .setNegativeButton(getBaseContext().getString(R.string.no_quit), null);
                        AlertDialog alert = alertBuilder.create();
                        alert.show();
                    }
                });
        rvMovies.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        rvMovies.setAdapter(favMovieListAdapter);
        rvMovies.setHasFixedSize(true);

        mViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(FavMovieViewModel.class);
        mViewModel.setContext(getBaseContext());
        mViewModel.getMovieData().observe(this, new Observer<ArrayList<MovieEntity>>() {
            @Override
            public void onChanged(ArrayList<MovieEntity> movieEntities) {
                favMovieListAdapter.setMovieList(movieEntities);
                showLoading(false);
            }
        });
        mViewModel.setCallback(new DbViewModelCallback() {
            @Override
            public void onDelete(int position) {
                favMovieListAdapter.deleteItem(position);
                rvMovies.smoothScrollToPosition(position);
            }
        });
        mViewModel.loadListMovie();
    }

    private void showLoading(boolean isShow) {
        if (isShow)
            pgbLoading.setVisibility(View.VISIBLE);
        else
            pgbLoading.setVisibility(View.GONE);
    }

    @Override
    public void onPreProcess() {
        showLoading(true);
    }

    @Override
    public void onSuccess(ArrayList<MovieEntity> movieEntities) {
        favMovieListAdapter.setMovieList(movieEntities);
        showLoading(false);
    }

    public static class DataObserver extends ContentObserver {
        final Context context;
        private FavoriteAsyncCallback<MovieEntity> favMovieCallback;

        public DataObserver(Handler handler, Context context) {
            super(handler);
            this.context = context;
        }

        public void setFavMovieCallback(FavoriteAsyncCallback<MovieEntity> favMovieCallback) {
            this.favMovieCallback = favMovieCallback;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            new FavMovieAsync(context, favMovieCallback).execute();
        }
    }
}
