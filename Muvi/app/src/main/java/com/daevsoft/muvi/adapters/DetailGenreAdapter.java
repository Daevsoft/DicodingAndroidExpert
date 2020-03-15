package com.daevsoft.muvi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daevsoft.muvi.R;

public class DetailGenreAdapter extends RecyclerView.Adapter<DetailGenreAdapter.GenreHolder> {

    private String[] arrSzGenres;
    private Context baseContext;

    public DetailGenreAdapter(String[] arrSzGenres, Context baseContext) {
        this.arrSzGenres = arrSzGenres;
        this.baseContext = baseContext;
    }

    @NonNull
    @Override
    public GenreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vGenre = LayoutInflater.from(baseContext).inflate(R.layout.detail_genre_item, parent, false);
        return new GenreHolder(vGenre);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreHolder holder, int position) {
        final String szGenre = this.arrSzGenres[position];
        holder.setGenre(szGenre);
        holder.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(baseContext, szGenre, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.arrSzGenres.length;
    }

    class GenreHolder extends RecyclerView.ViewHolder {
        Button btnGenre;

        GenreHolder(@NonNull final View itemView) {
            super(itemView);

            btnGenre = itemView.findViewById(R.id.genre_item_btnGenre);
        }

        void setGenre(String szGenre) {
            btnGenre.setText(szGenre);
        }

        void setClickListener(View.OnClickListener clickListener) {
            btnGenre.setOnClickListener(clickListener);
        }
    }
}
