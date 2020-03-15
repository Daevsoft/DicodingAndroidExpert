package com.daevsoft.muvi.adapters;


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
import com.daevsoft.muvi.R;
import com.daevsoft.muvi.entities.TvShowEntity;
import com.daevsoft.muvi.ui.movies.MovieFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class FavTvShowListAdapter extends RecyclerView.Adapter<FavTvShowListAdapter.FavTvShowHolder> {

    private ArrayList<TvShowEntity> arrayList;
    private TvShowListAdapter.TvShowClickListener itemClickListener;
    private FavTvShowClickListener unfavoriteClickListener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private Context context;

    public FavTvShowListAdapter(Context context,
                                TvShowListAdapter.TvShowClickListener itemClickListener) {
        this.arrayList = new ArrayList<>();
        this.itemClickListener = itemClickListener;
        this.context = context;
    }

    public void setUnFavoriteClick(FavTvShowClickListener unfavoriteClickListener) {
        this.unfavoriteClickListener = unfavoriteClickListener;
    }

    public void setTvShowlist(ArrayList<TvShowEntity> arrayList) {
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
    public FavTvShowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.fav_tvshow_list_item, parent, false);
        return new FavTvShowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FavTvShowHolder holder, int position) {
        final TvShowEntity TvShow = this.arrayList.get(position);
        final int index = position;
        holder.tvTitle.setText(TvShow.getTitle());
        holder.tvRate.setText(context.getResources().getString(R.string.rating, TvShow.getRating()));
        holder.tvRelease.setText(context.getResources().getString(R.string.release_date, dateFormat.format(TvShow.getRelease())));
        Glide.with(context)
                .load(MovieFragment.IMAGE_URL + TvShow.getPoster())
                .apply(new RequestOptions().override(300, 350).fitCenter())
                .into(holder.imgPoster);
        holder.imgPoster.setContentDescription(TvShow.getTitle());
        holder.btnUnfavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unfavoriteClickListener.FavoriteTvShowClicked(TvShow, index);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.OnTvShowClicked(TvShow);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public interface FavTvShowClickListener {
        void FavoriteTvShowClicked(TvShowEntity TvShow, int position);
    }

    public class FavTvShowHolder extends RecyclerView.ViewHolder {
        private ImageView imgPoster;
        private TextView tvTitle, tvRate, tvRelease;
        private Button btnUnfavorite;

        public FavTvShowHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.fav_tvshowlist_imgPoster);
            tvTitle = itemView.findViewById(R.id.fav_tvshowlist_tvTitle);
            tvRate = itemView.findViewById(R.id.fav_tvshowlist_tvRate);
            tvRelease = itemView.findViewById(R.id.fav_tvshowlist_tvRelease);
            btnUnfavorite = itemView.findViewById(R.id.fav_tvshowlist_btnUnfavorite);
        }
    }
}