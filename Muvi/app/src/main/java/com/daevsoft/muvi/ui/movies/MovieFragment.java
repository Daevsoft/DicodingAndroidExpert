package com.daevsoft.muvi.ui.movies;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daevsoft.muvi.MainActivity;
import com.daevsoft.muvi.MovieDetailActivity;
import com.daevsoft.muvi.R;
import com.daevsoft.muvi.adapters.MovieListAdapter;
import com.daevsoft.muvi.entities.MovieEntity;
import com.daevsoft.muvi.models.DbMovieLoadCallback;
import com.daevsoft.muvi.models.MovieViewModel;

import java.util.ArrayList;
import java.util.Locale;

public class MovieFragment extends Fragment implements SearchView.OnQueryTextListener {
    public static final String KEY_MOVIE_SELECT = "com.daevsoft.muvi.selected_movie";
    public static final String IMAGE_URL = "https://image.tmdb.org/t/p/w342";
    private static final String TAG = "Movie Log";
    private ProgressBar pgbLoading;
    private MovieViewModel movieViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        pgbLoading = view.findViewById(R.id.main_pgbLoading);
        final RecyclerView rvMovie = view.findViewById(R.id.main_rvMovie);
        final MovieListAdapter movieListAdapter = new MovieListAdapter(getActivity());
        movieListAdapter.setOnClickListener(new MovieListAdapter.MovieClickListener() {
            @Override
            public void onClick(MovieEntity movieEntity) {
                Intent intent = new Intent(getContext(), MovieDetailActivity.class);
                intent.putExtra(MovieFragment.KEY_MOVIE_SELECT, movieEntity);
                startActivity(intent);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvMovie.setLayoutManager(linearLayoutManager);
        rvMovie.setAdapter(movieListAdapter);
        rvMovie.setHasFixedSize(true);

        movieViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(MovieViewModel.class);
        movieViewModel.setMovieLoadCallback(new DbMovieLoadCallback() {
            @Override
            public void onPrepare() {
                showLoading(true);
            }

            @Override
            public void onSuccess() {
                showLoading(false);
            }
        });
        movieViewModel.getMovieData().observe(this, new Observer<ArrayList<MovieEntity>>() {
            @Override
            public void onChanged(ArrayList<MovieEntity> movieEntities) {
                movieListAdapter.setEntityList(movieEntities);
                movieListAdapter.notifyDataSetChanged();
                showLoading(false);
            }
        });
        showLoading(true);
        showMovieData(null);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setSearchView();
    }

    private void setSearchView() {
        Fragment fragment = getParentFragment();
        if (fragment != null) {
            MainActivity activity = (MainActivity) fragment.getActivity();
            if (activity != null) {
                activity.setSearchViewHint(R.string.search_movie);
                activity.setSearchViewQueryTextCallback(this);
            }
        }
    }

    private void showLoading(boolean isVisible) {
        if (isVisible)
            pgbLoading.setVisibility(View.VISIBLE);
        else
            pgbLoading.setVisibility(View.GONE);
    }

    private void showMovieData(String querySearch) {
        String language = Locale.getDefault().toString().replace('_', '-');
        movieViewModel.setMovies(language, querySearch);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        showMovieData(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
