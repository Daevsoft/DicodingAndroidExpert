package com.daevsoft.muvi.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.daevsoft.muvi.R;
import com.daevsoft.muvi.entities.MovieEntity;
import com.daevsoft.muvi.ui.movies.MovieFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieHolder> {
    private Activity activity;
    private ArrayList<MovieEntity> entityList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    private MovieClickListener clickListener;

    public MovieListAdapter(Activity activity) {
        this.activity = activity;
        entityList = new ArrayList<>();
    }

    public void setEntityList(ArrayList<MovieEntity> entityList) {
        this.entityList.clear();
        this.entityList.addAll(entityList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.movie_list_item, parent, false);
        return new MovieHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
        final MovieEntity data = this.entityList.get(position);
        holder.tvTitle.setText(data.getTitle());
        holder.tvRate.setText(activity.getResources().getString(R.string.rating, data.getRating()));
        holder.tvReleaseDate.setText(activity.getResources().getString(R.string.release_date, dateFormat.format(data.getRelease())));
        Glide.with(activity)
                .load(MovieFragment.IMAGE_URL + data.getPoster())
                .apply(new RequestOptions().override(300, 350).fitCenter())
                .into(holder.imgPoster);
        holder.imgPoster.setContentDescription(data.getTitle());
        holder.btnWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity.getBaseContext(), data.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(data);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.entityList.size();
    }

    public void setOnClickListener(MovieClickListener listener) {
        this.clickListener = listener;
    }

    public interface MovieClickListener {
        void onClick(MovieEntity movieEntity);
    }

    class MovieHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvRate, tvReleaseDate;
        ImageView imgPoster;
        Button btnWishlist;

        MovieHolder(@NonNull View v) {
            super(v);

            tvTitle = v.findViewById(R.id.movielist_tvTitle);
            tvRate = v.findViewById(R.id.movielist_tvRate);
            tvReleaseDate = v.findViewById(R.id.movielist_tvRelease);
            imgPoster = v.findViewById(R.id.movielist_imgPoster);
            btnWishlist = v.findViewById(R.id.movielist_btnWishlist);
        }
    }
}
