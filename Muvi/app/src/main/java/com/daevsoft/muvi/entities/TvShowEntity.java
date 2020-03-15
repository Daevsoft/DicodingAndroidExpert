package com.daevsoft.muvi.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class TvShowEntity implements Parcelable {
    public static final Creator<TvShowEntity> CREATOR = new Creator<TvShowEntity>() {
        @Override
        public TvShowEntity createFromParcel(Parcel source) {
            return new TvShowEntity(source);
        }

        @Override
        public TvShowEntity[] newArray(int size) {
            return new TvShowEntity[size];
        }
    };
    private int id;
    private String rating;
    private Date release;
    private String title;
    private String description;
    private String poster;
    private String[] genre;

    public TvShowEntity() {
    }

    protected TvShowEntity(Parcel in) {
        this.id = in.readInt();
        this.rating = in.readString();
        long tmpRelease = in.readLong();
        this.release = tmpRelease == -1 ? null : new Date(tmpRelease);
        this.title = in.readString();
        this.description = in.readString();
        this.poster = in.readString();
        this.genre = in.createStringArray();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String[] getGenre() {
        return genre;
    }

    public void setGenre(String[] genre) {
        this.genre = genre;
    }

    public Date getRelease() {
        return release;
    }

    public void setRelease(Date release) {
        this.release = release;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.rating);
        dest.writeLong(this.release != null ? this.release.getTime() : -1);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.poster);
        dest.writeStringArray(this.genre);
    }
}
