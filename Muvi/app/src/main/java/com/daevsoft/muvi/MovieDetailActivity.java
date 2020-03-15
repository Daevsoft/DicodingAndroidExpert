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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.daevsoft.muvi.adapters.DetailGenreAdapter;
import com.daevsoft.muvi.db.FavoriteMovieHelper;
import com.daevsoft.muvi.entities.MovieEntity;
import com.daevsoft.muvi.models.MovieDetailViewModel;
import com.daevsoft.muvi.ui.movies.MovieFragment;
import com.daevsoft.muvi.widgets.MuviWidget;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MovieDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvRating, tvReleaseDate, tvOverview;
    private RecyclerView rvGenre;
    private ImageView imgPoster;
    private ProgressBar pgbLoading;
    private Button btnAddFavorite;

    private MovieEntity movieData;
    private boolean movieFavoriteExist;

    private FavoriteMovieHelper favMovieHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);
        movieData = getIntent().getParcelableExtra(MovieFragment.KEY_MOVIE_SELECT);

        favMovieHelper = FavoriteMovieHelper.getInstance(this);
        favMovieHelper.open();

        pgbLoading = findViewById(R.id.detail_movie_pgbLoading);
        tvTitle = findViewById(R.id.detail_tvTitle);
        tvRating = findViewById(R.id.detail_tvRating);
        tvReleaseDate = findViewById(R.id.detail_tvRelease);
        tvOverview = findViewById(R.id.detail_movie_overview);
        rvGenre = findViewById(R.id.detail_rvGenre);
        imgPoster = findViewById(R.id.detail_imgPoster);
        btnAddFavorite = findViewById(R.id.detail_btnAddFavorite);

        movieFavoriteExist = false;
        btnAddFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (movieFavoriteExist) {
                    showAlertToDelete();
                } else {
                    favMovieHelper.insert(movieData, v);
                    movieFavoriteExist = true;
                    setButtonFavorite(false);
                }
            }
        });

        MovieDetailViewModel movieDetailViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(MovieDetailViewModel.class);
        movieDetailViewModel.getDataMovie().observe(this, new Observer<MovieEntity>() {
            @Override
            public void onChanged(MovieEntity movieEntity) {
                movieData = movieEntity;
                bindData();
                showLoading(false);
            }
        });
        showLoading(true);
        String language = Locale.getDefault().toString().replace("in_", "id-");
        movieDetailViewModel.setDataMovie(movieData.getId(), language);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void showAlertToDelete() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.baseDialog))
                .setTitle(getBaseContext().getString(R.string.info))
                .setIcon(R.drawable.ic_info_blue)
                .setMessage(getBaseContext().getString(R.string.sure_to_unfavorite))
                .setPositiveButton(getBaseContext().getString(R.string.unfavorite),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                favMovieHelper.delete(movieData);
                                movieFavoriteExist = false;
                                setButtonFavorite(true);
                            }
                        })
                .setNegativeButton(getBaseContext().getString(R.string.no_quit), null);
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }
    private void checkMovieIsFavorite() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                movieFavoriteExist = favMovieHelper.exist(movieData.getId().toString());
                setButtonFavorite(!movieFavoriteExist);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home || item.getItemId() == R.id.up) {
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
        getSupportActionBar().setTitle(movieData.getTitle());
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        tvTitle.setText(movieData.getTitle());
        tvRating.setText(getResources().getString(R.string.rating, movieData.getRating()));
        tvReleaseDate.setText(getResources().getString(R.string.release_date, sdf.format(movieData.getRelease())));

        Glide.with(getBaseContext())
                .load(MovieFragment.IMAGE_URL + movieData.getPoster())
                .apply(new RequestOptions().override(300, 350).fitCenter())
                .into(imgPoster);
        tvOverview.setText(movieData.getDescription());

        String[] arrGenres = movieData.getGenre();
        if (arrGenres != null) {
            DetailGenreAdapter genreAdapter = new DetailGenreAdapter(arrGenres, this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            rvGenre.setAdapter(genreAdapter);
            rvGenre.setLayoutManager(linearLayoutManager);
        }

        checkMovieIsFavorite();
    }

    private void setButtonFavorite(boolean enable) {
        if (enable) {
            btnAddFavorite.setBackgroundColor(getColor(R.color.colorBlue));
            btnAddFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_white, 0, 0, 0);
            btnAddFavorite.setText(getString(R.string.add_favorite));
        } else {
            btnAddFavorite.setBackgroundColor(getColor(R.color.colorLowBlue));
            btnAddFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_white, 0, 0, 0);
            btnAddFavorite.setText(getString(R.string.unfavorite));//setCompoundDrawables(getResources().getDrawable(R.drawable.ic_close_white, getTheme()), null,null,null);
        }
    }
}
