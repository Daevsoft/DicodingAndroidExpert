package com.daevsoft.muvi.ui.tvshows;

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

import com.daevsoft.muvi.R;
import com.daevsoft.muvi.TvShowDetailActivity;
import com.daevsoft.muvi.adapters.FavTvShowListAdapter;
import com.daevsoft.muvi.adapters.TvShowListAdapter;
import com.daevsoft.muvi.db.DbViewModelCallback;
import com.daevsoft.muvi.entities.TvShowEntity;
import com.daevsoft.muvi.models.FavTvShowViewModel;

import java.util.ArrayList;

public class FavTvShowFragment extends Fragment {

    public static String EXTRA_STATE_LIST = "com.daevsoft.muvi.EXTRA_STATE_LIST";

    private ProgressBar progressBar;
    private RecyclerView rvFavoriteTvShow;

    private FavTvShowViewModel mViewModel;

    private FavTvShowListAdapter favTvShowListAdapter;

    public static FavTvShowFragment newInstance() {
        return new FavTvShowFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fav_tvshow_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.fav_tvshow_progressBar);
        rvFavoriteTvShow = view.findViewById(R.id.fav_tvshow_rvMovie);
        favTvShowListAdapter = new FavTvShowListAdapter(getContext(),
                new TvShowListAdapter.TvShowClickListener() {
                    @Override
                    public void OnTvShowClicked(TvShowEntity TvShowEntity) {
                        Intent intent = new Intent(getContext(), TvShowDetailActivity.class);
                        intent.putExtra(TvShowFragment.KEY_TVSHOW_SELECT, TvShowEntity);
                        startActivity(intent);
                    }
                });
        favTvShowListAdapter.setUnFavoriteClick(new FavTvShowListAdapter.FavTvShowClickListener() {
            @Override
            public void FavoriteTvShowClicked(TvShowEntity TvShow, int position) {
                final TvShowEntity TvShowEntity = TvShow;
                final int index = position;
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext())
                        .setTitle(getContext().getString(R.string.info))
                        .setIcon(R.drawable.ic_info_blue)
                        .setMessage(getContext().getString(R.string.sure_to_unfavorite))
                        .setPositiveButton(getContext().getString(R.string.unfavorite),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mViewModel.TvShowDelete(TvShowEntity, index);
                                    }
                                })
                        .setNegativeButton(getContext().getString(R.string.no_quit), null);
                AlertDialog alert = alertBuilder.create();
                alert.show();
            }
        });
        rvFavoriteTvShow.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFavoriteTvShow.setAdapter(favTvShowListAdapter);
        rvFavoriteTvShow.setHasFixedSize(true);

        mViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(FavTvShowViewModel.class);
        mViewModel.setContext(getContext());
        mViewModel.getTvShowData().observe(this, new Observer<ArrayList<TvShowEntity>>() {
            @Override
            public void onChanged(ArrayList<TvShowEntity> TvShowEntities) {
                favTvShowListAdapter.setTvShowlist(TvShowEntities);
                showLoading(false);
            }
        });
        mViewModel.setCallback(new DbViewModelCallback() {
            @Override
            public void onDelete(int position) {
                favTvShowListAdapter.deleteItem(position);
                rvFavoriteTvShow.smoothScrollToPosition(position);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        showLoading(true);
        mViewModel.loadListTvShow();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.close();
    }

    private void showLoading(boolean isShow) {
        if (isShow)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }
}
