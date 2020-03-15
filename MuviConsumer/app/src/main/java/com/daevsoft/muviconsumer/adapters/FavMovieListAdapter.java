package com.daevsoft.muviconsumer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.daevsoft.muviconsumer.MainActivity;
import com.daevsoft.muviconsumer.R;
import com.daevsoft.muviconsumer.entities.MovieEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class FavMovieListAdapter extends RecyclerView.Adapter<FavMovieListAdapter.FavMovieHolder> {

    private ArrayList<MovieEntity> arrayList;
    private FavMovieClickListener favoriteClickListener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private Context context;

    public FavMovieListAdapter(Context context,
                               FavMovieClickListener favoriteClickListener) {
        this.arrayList = new ArrayList<>();
        this.favoriteClickListener = favoriteClickListener;
        this.context = context;
    }

    public void setUnFavoriteClick(FavMovieClickListener favoriteClickListener) {
        this.favoriteClickListener = favoriteClickListener;
    }

    public void setMovieList(ArrayList<MovieEntity> arrayList) {
        if (this.arrayList.size() > 0) this.arrayList.clear();
        this.arrayList.addAll(arrayList);
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.arrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, arrayList.size());
    }

    @NonNull
    @Override
    public FavMovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.fav_movie_list_item, parent, false);
        return new FavMovieHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FavMovieHolder holder, int position) {
        final MovieEntity movie = this.arrayList.get(position);
        final int index = position;
        holder.tvTitle.setText(movie.getTitle());
        holder.tvTitle.setText(movie.getTitle());
        holder.tvRate.setText(context.getResources().getString(R.string.rating, movie.getRating()));
        holder.tvRelease.setText(context.getResources().getString(R.string.release_date, dateFormat.format(movie.getRelease())));
        Glide.with(context)
                .load(MainActivity.IMAGE_URL + movie.getPoster())
                .apply(new RequestOptions().override(300, 350).fitCenter())
                .into(holder.imgPoster);
        holder.imgPoster.setContentDescription(movie.getTitle());
        holder.btnUnfavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteClickListener.FavoriteMovieClicked(movie, index);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteClickListener.onItemClick(movie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public interface FavMovieClickListener {
        void onItemClick(MovieEntity movie);

        void FavoriteMovieClicked(MovieEntity movie, int position);
    }

    public class FavMovieHolder extends RecyclerView.ViewHolder {
        private ImageView imgPoster;
        private TextView tvTitle, tvRate, tvRelease;
        private Button btnUnfavorite;

        public FavMovieHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.fav_movielist_imgPoster);
            tvTitle = itemView.findViewById(R.id.fav_movielist_tvTitle);
            tvRate = itemView.findViewById(R.id.fav_movielist_tvRate);
            tvRelease = itemView.findViewById(R.id.fav_movielist_tvRelease);
            btnUnfavorite = itemView.findViewById(R.id.fav_movielist_btnUnfavorite);
        }
    }
}