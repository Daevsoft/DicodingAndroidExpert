package com.daevsoft.muvi.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.daevsoft.muvi.R;
import com.daevsoft.muvi.ui.movies.FavMovieFragment;
import com.daevsoft.muvi.ui.tvshows.FavTvShowFragment;

public class FavoritePagerAdapter extends FragmentPagerAdapter {

    private final Context context;
    private int[] TITLE_FRAGMENT = {
            R.string.my_movie,
            R.string.my_tv_show
    };

    public FavoritePagerAdapter(Context context, @NonNull FragmentManager fm) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return context.getString(TITLE_FRAGMENT[position]);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = FavMovieFragment.newInstance();
                break;
            case 1:
                fragment = FavTvShowFragment.newInstance();
                break;
            default:
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return TITLE_FRAGMENT.length;
    }
}
