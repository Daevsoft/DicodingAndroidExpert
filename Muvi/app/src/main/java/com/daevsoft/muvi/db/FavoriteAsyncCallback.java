package com.daevsoft.muvi.db;

import java.util.ArrayList;

public interface FavoriteAsyncCallback<T> {
    void onSuccess(ArrayList<T> movieEntities);
}
