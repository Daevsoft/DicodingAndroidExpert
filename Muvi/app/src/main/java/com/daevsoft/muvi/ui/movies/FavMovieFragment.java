package com.daevsoft.muvi.ui.movies;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daevsoft.muvi.MovieDetailActivity;
import com.daevsoft.muvi.R;
import com.daevsoft.muvi.adapters.FavMovieListAdapter;
import com.daevsoft.muvi.adapters.MovieListAdapter;
import com.daevsoft.muvi.db.DbViewModelCallback;
import com.daevsoft.muvi.entities.MovieEntity;
import com.daevsoft.muvi.models.FavMovieViewModel;
import com.daevsoft.muvi.widgets.MuviWidget;

import java.util.ArrayList;


public class FavMovieFragment extends Fragment {

    public static String EXTRA_STATE_LIST = "com.daevsoft.muvi.EXTRA_STATE_LIST";

    private ProgressBar progressBar;
    private RecyclerView rvFavoriteMovie;

    private FavMovieViewModel mViewModel;

    private FavMovieListAdapter favMovieListAdapter;

    public static FavMovieFragment newInstance() {
        return new FavMovieFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fav_movie_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        showLoading(true);
        mViewModel.loadListMovie();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.fav_movie_progressBar);
        rvFavoriteMovie = view.findViewById(R.id.fav_movie_rvMovie);

        favMovieListAdapter = new FavMovieListAdapter(getContext(),
                new MovieListAdapter.MovieClickListener() {
                    @Override
                    public void onClick(MovieEntity movieEntity) {
                        Intent intent = new Intent(getContext(), MovieDetailActivity.class);
                        intent.putExtra(MovieFragment.KEY_MOVIE_SELECT, movieEntity);
                        startActivity(intent);
                    }
                });
        favMovieListAdapter.setUnFavoriteClick(new FavMovieListAdapter.FavMovieClickListener() {
            @Override
            public void FavoriteMovieClicked(MovieEntity movie, int position) {
                final MovieEntity movieEntity = movie;
                final int index = position;
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext())
                        .setTitle(getContext().getString(R.string.info))
                        .setIcon(R.drawable.ic_info_blue)
                        .setMessage(getContext().getString(R.string.sure_to_unfavorite))
                        .setPositiveButton(getContext().getString(R.string.unfavorite),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mViewModel.movieDelete(movieEntity, index);
                                    }
                                })
                        .setNegativeButton(getContext().getString(R.string.no_quit), null);
                AlertDialog alert = alertBuilder.create();
                alert.show();
            }
        });
        rvFavoriteMovie.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFavoriteMovie.setAdapter(favMovieListAdapter);
        rvFavoriteMovie.setHasFixedSize(true);

        mViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(FavMovieViewModel.class);
        mViewModel.setContext(getContext());
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
                rvFavoriteMovie.smoothScrollToPosition(position);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void showLoading(boolean isShow) {
        if (isShow)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }
}
