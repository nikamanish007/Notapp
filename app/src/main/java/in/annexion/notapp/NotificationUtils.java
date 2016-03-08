package in.annexion.notapp;

/**
 * Created by fanatic on 16/2/16.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import java.util.Random;

public class NotificationUtils {

    private Context mContext;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    public void showNotificationMessage(final String title, final String uploadDate, final String name, Intent intent) {
        // Check for empty push message
        if (TextUtils.isEmpty(name))
            return;

        // notification icon
        final int icon = R.mipmap.ic_launcher;

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 0, intent,0);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);

        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + mContext.getPackageName() + "/raw/tonn");

        showSmallNotification(mBuilder, icon, title, uploadDate , name, resultPendingIntent, alarmSound);

    }

    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title, String uploadDate, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {

        Notification notification;
        notification = mBuilder.setSmallIcon(icon)
                .setTicker(title)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(uploadDate)
                .build();

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(m, notification);
    }

}
