package com.daevsoft.muvi.reminders;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.daevsoft.muvi.BuildConfig;
import com.daevsoft.muvi.R;
import com.daevsoft.muvi.entities.MovieEntity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class ReminderReceiver extends BroadcastReceiver {
    public static final int TYPE_DAILY_REMINDER = 200;
    public static final int TYPE_RELEASE_REMINDER = 201;

    private static final String EXTRA_TYPE = "com.daevsoft.muvi.reminder.EXTRA_TYPE_REMINDER";
    private static final String CHANNEL_ID = "muvi.channel.daily_id";
    private static final String CHANNEL_NAME = "MuviReminder";
    private static final String KEY_NOTIF_GROUP = "muvi.notif.group";
    private int iMaxNotif = 2;

    public static void setRemainder(Context context, int typeReminder) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        if (typeReminder == TYPE_DAILY_REMINDER)
            calendar.set(Calendar.HOUR_OF_DAY, 7);
        else
            calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra(EXTRA_TYPE, typeReminder);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, typeReminder, intent, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public static void cancelReminder(Context context, int typeReminder) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, typeReminder, intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            int type = intent.getIntExtra(EXTRA_TYPE, TYPE_DAILY_REMINDER);
            if (type == TYPE_DAILY_REMINDER)
                showDailyNotification(context);
            else
                showReleaseNotification(context);
        }
    }

    private void showDailyNotification(Context context) {
        long[] longVibration = new long[]{1000, 1000, 1000, 1000, 1000};

        NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri uriAlarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_live_tv_black_24dp)
                .setContentTitle(context.getString(R.string.notif_muvi_daily_title))
                .setContentText(context.getString(R.string.notif_muvi_daily_content))
                .setSound(uriAlarm)
                .setVibrate(longVibration)
                .setColor(ContextCompat.getColor(context, android.R.color.transparent));

        if (notifManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.enableVibration(true);
                channel.setVibrationPattern(longVibration);
                builder.setChannelId(CHANNEL_ID);
                notifManager.createNotificationChannel(channel);
            }
        }
        Notification notification = builder.build();

        if (notifManager != null)
            notifManager.notify(TYPE_DAILY_REMINDER, notification);
    }

    private void showReleaseNotification(final Context context) {
        final long[] longVibration = new long[]{1000, 1000, 1000, 1000, 1000};

        final NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        final Uri uriAlarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final Bitmap bitmapIconLarge = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_video_library_black_24dp);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Date dateToday = new Date();
        String today = dateFormat.format(dateToday);
        String url = "https://api.themoviedb.org/3/discover/movie?api_key=" + BuildConfig.TMDB_API_KEY +
                "&primary_release_date.gte=" + today +
                "&primary_release_date.lte=" + today;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray arrMovie = object.getJSONArray("results");
                    MovieEntity moviePrevious = new MovieEntity();

                    for (int i = 0; i < arrMovie.length(); i++) {
                        JSONObject objMovie = arrMovie.getJSONObject(i);
                        String title = objMovie.getString("title");
                        String poster = objMovie.getString("poster_path");
                        String rating = objMovie.getString("vote_average");
                        int id = objMovie.getInt("id");
                        Date release = dateFormat.parse(objMovie.getString("release_date"));

                        MovieEntity movieEntity = new MovieEntity();
                        movieEntity.setTitle(title);
                        movieEntity.setPoster(poster);
                        movieEntity.setRating(rating);
                        movieEntity.setId(id);
                        movieEntity.setRelease(release);

                        startReleaseNotification(context, moviePrevious, movieEntity, notifManager, i, longVibration, uriAlarm, bitmapIconLarge);
                        moviePrevious = movieEntity;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });


    }

    private void startReleaseNotification(Context context, MovieEntity moviePrevious, MovieEntity movie, NotificationManager notifManager, int count, long[] longVibration, Uri uriAlarm, Bitmap bitmapIconLarge) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_video_library_black_24dp)
                .setContentTitle(context.getString(R.string.notif_muvi_release_title))
                .setContentText(movie.getTitle())
                .setSound(uriAlarm)
                .setVibrate(longVibration)
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setGroup(KEY_NOTIF_GROUP)
                .setAutoCancel(true);

        if (count < iMaxNotif) {
            builder.setLargeIcon(bitmapIconLarge);
        } else {
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle()
                    .addLine(context.getString(R.string.new_movies) + movie.getTitle())
                    .addLine(context.getString(R.string.new_movies) + moviePrevious.getTitle())
                    .setBigContentTitle(context.getString(R.string.today_release))
                    .setSummaryText(context.getString(R.string.movies));
            builder.setStyle(inboxStyle)
                    .setGroupSummary(true);

        }

        if (notifManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.enableVibration(true);
                channel.setVibrationPattern(longVibration);
                builder.setChannelId(CHANNEL_ID);
                notifManager.createNotificationChannel(channel);
            }
        }

        Notification notification = builder.build();

        if (notifManager != null)
            notifManager.notify(TYPE_RELEASE_REMINDER, notification);
    }
}
