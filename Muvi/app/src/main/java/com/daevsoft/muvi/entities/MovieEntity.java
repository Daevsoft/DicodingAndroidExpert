package com.daevsoft.muvi.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class MovieEntity implements Parcelable {
    public static final Creator<MovieEntity> CREATOR = new Creator<MovieEntity>() {
        @Override
        public MovieEntity createFromParcel(Parcel source) {
            return new MovieEntity(source);
        }

        @Override
        public MovieEntity[] newArray(int size) {
            return new MovieEntity[size];
        }
    };
    private int id;
    private String title;
    private String rating;
    private String poster;
    private String description;
    private Date release;
    private String[] genre;

    public MovieEntity() {
    }

    protected MovieEntity(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.rating = in.readString();
        this.poster = in.readString();
        this.description = in.readString();
        long tmpRelease = in.readLong();
        this.release = tmpRelease == -1 ? null : new Date(tmpRelease);
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

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getRelease() {
        return release;
    }

    public void setRelease(Date release) {
        this.release = release;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String[] getGenre() {
        return genre;
    }

    public void setGenre(String[] genre) {
        this.genre = genre;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.rating);
        dest.writeString(this.poster);
        dest.writeString(this.description);
        dest.writeLong(this.release != null ? this.release.getTime() : -1);
        dest.writeStringArray(this.genre);
    }
}
