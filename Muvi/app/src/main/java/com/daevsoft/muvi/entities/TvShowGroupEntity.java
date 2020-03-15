package com.daevsoft.muvi.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class TvShowGroupEntity implements Parcelable {
    public static final Parcelable.Creator<TvShowGroupEntity> CREATOR = new Parcelable.Creator<TvShowGroupEntity>() {
        @Override
        public TvShowGroupEntity createFromParcel(Parcel source) {
            return new TvShowGroupEntity(source);
        }

        @Override
        public TvShowGroupEntity[] newArray(int size) {
            return new TvShowGroupEntity[size];
        }
    };
    private List<TvShowEntity> listTvShow;
    private int groupName;

    public TvShowGroupEntity() {
    }

    public TvShowGroupEntity(List<TvShowEntity> listTvShow, int groupName) {
        this.listTvShow = listTvShow;
        this.groupName = groupName;
    }

    protected TvShowGroupEntity(Parcel in) {
        this.listTvShow = in.createTypedArrayList(TvShowEntity.CREATOR);
        this.groupName = in.readInt();
    }

    public List<TvShowEntity> getListTvShow() {
        return listTvShow;
    }

    public void setListTvShow(List<TvShowEntity> listTvShow) {
        this.listTvShow = listTvShow;
    }

    public int getgroupName() {
        return groupName;
    }

    public void setgroupName(int groupName) {
        this.groupName = groupName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.listTvShow);
        dest.writeInt(this.groupName);
    }
}
