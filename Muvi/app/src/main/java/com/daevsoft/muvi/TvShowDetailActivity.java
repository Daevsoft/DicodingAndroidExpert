package com.daevsoft.muvi;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.daevsoft.muvi.db.FavoriteTvShowHelper;
import com.daevsoft.muvi.entities.TvShowEntity;
import com.daevsoft.muvi.models.TvShowDetailViewModel;
import com.daevsoft.muvi.ui.movies.MovieFragment;
import com.daevsoft.muvi.ui.tvshows.TvShowFragment;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TvShowDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TvShowEntity tvShowSelected;

    private ImageView imgPoster;
    private TextView tvTitle, tvRate, tvYear, tvOverview, tvGenre;
    private Button btnWatchNow;
    private Button btnWatchList;
    private Button btnPlayTrailer;
    private Burron btnFavorite;
    private ProgressBar pgbLoading;

    private boolean tvShowFavoriteExist;

    private FavoriteTvShowHelper favTvShowHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_show_detail);
        tvShowSelected = getIntent().getParcelableExtra(TvShowFragment.KEY_TVSHOW_SELECT);

        favTvShowHelper = FavoriteTvShowHelper.getInstance(this);
        favTvShowHelper.open();
        tvShowFavoriteExist = false;

        pgbLoading = findViewById(R.id.tvshow_detail_pgbLoading);
        tvTitle = findViewById(R.id.tvshow_detail_tvTitle);
        tvRate = findViewById(R.id.tvshow_detail_tvRating);
        tvYear = findViewById(R.id.tvshow_detail_tvYear);
        tvOverview = findViewById(R.id.tvshow_detail_tvOverview);
        tvGenre = findViewById(R.id.tvshow_detail_tvGenre);

        imgPoster = findViewById(R.id.tvshow_detail_imgPoster);
        btnWatchNow = findViewById(R.id.tvshow_detail_btnWatchNow);
        btnWatchList = findViewById(R.id.tvshow_detail_btnWatchList);
        btnPlayTrailer = findViewById(R.id.tvshow_detail_btnPlayTrailer);
        btnFavorite = findViewById(R.id.tvshow_detail_btnFavorite);

        btnWatchList.setOnClickListener(this);
        btnWatchNow.setOnClickListener(this);
        btnPlayTrailer.setOnClickListener(this);
        btnFavorite.setOnClickListener(this);

        TvShowDetailViewModel tvShowDetailViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(TvShowDetailViewModel.class);
        tvShowDetailViewModel.getDataTvShow().observe(this, new Observer<TvShowEntity>() {
            @Override
            public void onChanged(TvShowEntity tvShowEntity) {
                tvShowSelected = tvShowEntity;
                bindData();
                showLoading(false);
            }
        });
        showLoading(true);
        String language = Locale.getDefault().toString().replace("in_", "id-");
        tvShowDetailViewModel.setDataTvShow(tvShowSelected.getId(), language);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        checkTvShowIsFavorite();
    }

    private void checkTvShowIsFavorite() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvShowFavoriteExist = favTvShowHelper.exist(tvShowSelected.getId().toString());
                setButtonFavorite(!tvShowFavoriteExist);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        favTvShowHelper.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLoading(boolean isVisible) {
        if (isVisible)
            pgbLoading.setVisibility(View.VISIBLE);
        else
            pgbLoading.setVisibility(View.GONE);
    }

    private void bindData() {
        getSupportActionBar().setTitle(tvShowSelected.getTitle());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        tvOverview.setText(tvShowSelected.getDescription());
        tvTitle.setText(tvShowSelected.getTitle());
        tvRate.setText(getResources().getString(R.string.rating, tvShowSelected.getRating()));
        tvYear.setText(dateFormat.format(tvShowSelected.getRelease()));
        tvOverview.setText(tvShowSelected.getDescription());
        tvGenre.setText("");
        Glide.with(getBaseContext())
                .load(MovieFragment.IMAGE_URL + tvShowSelected.getPoster())
                .apply(new RequestOptions().override(300, 350).fitCenter())
                .into(imgPoster);
        String[] arrGenre = tvShowSelected.getGenre();
        for (String item : arrGenre) {
            String szGenre = tvGenre.getText().toString() + " ." + item;
            tvGenre.setText(szGenre);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tvshow_detail_btnWatchNow:
                Toast.makeText(getBaseContext(), getResources().getString(R.string.watch_now), Toast.LENGTH_SHORT).show();
                break;
            case R.id.tvshow_detail_btnWatchList:
                Toast.makeText(getBaseContext(), getResources().getString(R.string.watchlist), Toast.LENGTH_SHORT).show();
                break;
            case R.id.tvshow_detail_btnPlayTrailer:
                Toast.makeText(getBaseContext(), getResources().getString(R.string.play_trailer), Toast.LENGTH_SHORT).show();
                break;
            case R.id.tvshow_detail_btnFavorite:
                if (tvShowFavoriteExist) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.baseDialog))
                            .setTitle(getBaseContext().getString(R.string.info))
                            .setIcon(R.drawable.ic_info_blue)
                            .setMessage(getBaseContext().getString(R.string.sure_to_unfavorite))
                            .setPositiveButton(getBaseContext().getString(R.string.unfavorite),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            favTvShowHelper.delete(tvShowSelected);
                                            tvShowFavoriteExist = false;
                                            setButtonFavorite(true);
                                        }
                                    })
                            .setNegativeButton(getBaseContext().getString(R.string.no_quit), null);
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    long result = favTvShowHelper.insert(tvShowSelected, v);
                    tvShowFavoriteExist = true;
                    setButtonFavorite(false);
                }
            default:
                break;
        }
    }

    private void setButtonFavorite(boolean enable) {
        if (enable) {
            btnFavorite.setBackgroundColor(getColor(R.color.colorBlue));
            btnFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_white, 0, 0, 0);
            btnFavorite.setText(getString(R.string.add_favorite));
        } else {
            btnFavorite.setBackgroundColor(getColor(R.color.colorLowBlue));
            btnFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_white, 0, 0, 0);
            btnFavorite.setText(getString(R.string.unfavorite));
        }
    }
}
