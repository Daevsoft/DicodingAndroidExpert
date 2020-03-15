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

public class TvShowListAdapter extends RecyclerView.Adapter<TvShowListAdapter.TvShowHolder> {

    private Context baseContext;
    private ArrayList<TvShowEntity> tvShowList;
    private TvShowClickListener clickListener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public TvShowListAdapter(Context baseContext) {
        this.baseContext = baseContext;
        this.tvShowList = new ArrayList<>();
    }

    public void setTvShowList(ArrayList<TvShowEntity> tvShowList) {
        this.tvShowList.clear();
        this.tvShowList.addAll(tvShowList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TvShowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(baseContext).inflate(R.layout.tvshow_list_item, parent, false);
        return new TvShowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TvShowHolder holder, int position) {
        final TvShowEntity tvShowEntity = tvShowList.get(position);

        holder.tvTitle.setText(tvShowEntity.getTitle());
        holder.tvRate.setText(baseContext.getResources().getString(R.string.rating, tvShowEntity.getRating()));
        holder.tvReleaseDate.setText(baseContext.getResources().getString(R.string.release_date,
                dateFormat.format(tvShowEntity.getRelease())));

        Glide.with(baseContext)
                .load(MovieFragment.IMAGE_URL + tvShowEntity.getPoster())
                .apply(new RequestOptions().override(300, 350).fitCenter())
                .into(holder.imgPoster);
        holder.imgPoster.setContentDescription(tvShowEntity.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.OnTvShowClicked(tvShowEntity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tvShowList.size();
    }

    public void setItemClickListener(TvShowClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface TvShowClickListener {
        void OnTvShowClicked(TvShowEntity tvShowEntity);
    }

    class TvShowHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvRate, tvReleaseDate;
        ImageView imgPoster;
        Button btnWishlist;

        TvShowHolder(@NonNull View v) {
            super(v);

            tvTitle = v.findViewById(R.id.tvlist_tvTitle);
            tvRate = v.findViewById(R.id.tvlist_tvRate);
            tvReleaseDate = v.findViewById(R.id.tvlist_tvRelease);
            imgPoster = v.findViewById(R.id.tvlist_imgPoster);
            btnWishlist = v.findViewById(R.id.tvlist_btnWishlist);
        }
    }
}
