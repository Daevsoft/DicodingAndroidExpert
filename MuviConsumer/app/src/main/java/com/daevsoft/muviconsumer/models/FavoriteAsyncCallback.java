package com.daevsoft.muviconsumer.models;

import java.util.ArrayList;

public interface FavoriteAsyncCallback<T> {
    void onPreProcess();

    void onSuccess(ArrayList<T> movieEntities);
}
