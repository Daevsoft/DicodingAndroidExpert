package com.daevsoft.muvi.widgets;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class MuviWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackMuviRemoteViewsFactory(getApplicationContext());
    }
}
