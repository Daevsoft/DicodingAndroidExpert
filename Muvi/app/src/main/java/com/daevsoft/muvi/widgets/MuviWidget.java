package com.daevsoft.muvi.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.daevsoft.muvi.MovieDetailActivity;
import com.daevsoft.muvi.R;
import com.daevsoft.muvi.entities.MovieEntity;
import com.daevsoft.muvi.ui.movies.MovieFragment;

/**
 * Implementation of App Widget functionality.
 */
public class MuviWidget extends AppWidgetProvider {

    public static final String EXTRA_POSITION = "com.daevsoft.muvi.widget.EXTRA_POSITION";
    public static final String EXTRA_MOVIE_ITEM = "com.daevsoft.muvi.widget.EXTRA_MOVIE_ITEM";
    private static final String MUVI_CLICK_ACTION = "com.daevsoft.muvi.widget.MUVI_CLICK_ACTION";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Intent intent = new Intent(context, MuviWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.muvi_widget);
        views.setRemoteAdapter(R.id.widget_stackView, intent);
        views.setEmptyView(R.id.widget_stackView, R.id.empty_view);

        Intent toastIntent = new Intent(context, MuviWidget.class);
        toastIntent.setAction(MUVI_CLICK_ACTION);
        toastIntent.setData(Uri.parse(toastIntent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_stackView, pendingIntent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction() != null) {
            if (intent.getAction().equals(MUVI_CLICK_ACTION)) {
                MovieEntity movie = intent.getParcelableExtra(MuviWidget.EXTRA_MOVIE_ITEM);
                Intent intentDetail = new Intent(context, MovieDetailActivity.class);
                intentDetail.putExtra(MovieFragment.KEY_MOVIE_SELECT, movie);
                context.startActivity(intentDetail);
            }
        }
    }
}

