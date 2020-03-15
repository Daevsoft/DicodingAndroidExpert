package com.daevsoft.muvi.ui.tvshows;


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
import com.daevsoft.muvi.R;
import com.daevsoft.muvi.TvShowDetailActivity;
import com.daevsoft.muvi.adapters.TvShowListAdapter;
import com.daevsoft.muvi.entities.TvShowEntity;
import com.daevsoft.muvi.models.DbMovieLoadCallback;
import com.daevsoft.muvi.models.TvShowViewModel;

import java.util.ArrayList;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class TvShowFragment extends Fragment implements SearchView.OnQueryTextListener {
    public static final String KEY_TVSHOW_SELECT = "com.daevsoft.muvi.selected_tvshow";
    private TvShowViewModel tvShowViewModel;
    private ProgressBar pgbLoading;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tv_show, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        pgbLoading = view.findViewById(R.id.tv_show_pgbLoading);
        RecyclerView rvTvShow = view.findViewById(R.id.tv_show_rvTvShow);
        final TvShowListAdapter tvShowAdapter = new TvShowListAdapter(getContext());
        tvShowAdapter.setItemClickListener(new TvShowListAdapter.TvShowClickListener() {
            @Override
            public void OnTvShowClicked(TvShowEntity tvShowEntity) {
                Intent intentDetail = new Intent(getContext(), TvShowDetailActivity.class);
                intentDetail.putExtra(TvShowFragment.KEY_TVSHOW_SELECT, tvShowEntity);
                startActivity(intentDetail);
            }
        });
        rvTvShow.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTvShow.setAdapter(tvShowAdapter);
        rvTvShow.setHasFixedSize(true);

        tvShowViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(TvShowViewModel.class);
        tvShowViewModel.setMovieLoadCallback(new DbMovieLoadCallback() {
            @Override
            public void onPrepare() {
                showLoading(true);
            }

            @Override
            public void onSuccess() {
                showLoading(false);
            }
        });
        tvShowViewModel.getTvShowData().observe(this, new Observer<ArrayList<TvShowEntity>>() {
            @Override
            public void onChanged(ArrayList<TvShowEntity> tvShowEntities) {
                tvShowAdapter.setTvShowList(tvShowEntities);
                tvShowAdapter.notifyDataSetChanged();
            }
        });
        showTvShowData(null);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setSearchView();
    }

    private void showTvShowData(@Nullable String querySearch) {
        String language = Locale.getDefault().toString().replace('_', '-');
        tvShowViewModel.setTvShows(language, querySearch);
    }

    private void showLoading(boolean isVisible) {
        if (isVisible)
            pgbLoading.setVisibility(View.VISIBLE);
        else
            pgbLoading.setVisibility(View.GONE);
    }

    private void setSearchView() {
        Fragment fragment = getParentFragment();
        if (fragment != null) {
            MainActivity activity = (MainActivity) fragment.getActivity();
            if (activity != null) {
                activity.setSearchViewHint(R.string.search_tvshow);
                activity.setSearchViewQueryTextCallback(this);
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        showTvShowData(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
