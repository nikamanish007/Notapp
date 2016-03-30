package in.annexion.notapp;

/**
 * Created by fanatic on 16/2/16.
 */
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmPushReceiver extends GcmListenerService {

    private static final String TAG = MyGcmPushReceiver.class.getSimpleName();

    private NotificationUtils notificationUtils;
    String [] titleArray;
    String [] intentArray;

    /**
     * Called when message is received.
     *
     * @param from   SenderID of the sender.
     * @param bundle Data bundle containing message data as key/value pairs.
     *               For Set of keys use data.keySet().
     */

    @Override
    public void onMessageReceived(String from, Bundle bundle) {

        titleArray=getResources().getStringArray(R.array.title);
        intentArray=getResources().getStringArray(R.array.intent);

        Log.e("onMessageReceived", "registration id =====  "+from);
        String title = bundle.getString("title");
        String uploadDate = bundle.getString("uploadDate");
        String uploadedBy = bundle.getString("name");
        String n_id = bundle.getString("n_id");
        String exp = bundle.getString("exp");
        String noticeBoard =""+ (Integer.parseInt(bundle.getString("dept")) - 1);
        String link = bundle.getString("link");
        String md5= bundle.getString("md5");

        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(""+noticeBoard,true);
        editor.commit();

        Log.e(TAG, "From: " + from);
        Log.e(TAG, "Title: " + title);
        Log.e(TAG, "uploadDate: " + uploadDate);

        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
        }

        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

        showNotificationMessage(getApplicationContext(), title, uploadDate, uploadedBy, resultIntent);

        NoticeDownloader noticeDownloader = new NoticeDownloader();
        noticeDownloader.insertIntoDB(getApplicationContext(), title, uploadDate, uploadedBy, n_id, exp, noticeBoard, link, md5);
        if(!(link.charAt(0)=='#'))
            noticeDownloader.downloadFile(link);
        if(MainActivity.active)
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String uploadDate, String name, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, uploadDate, name, intent);
    }

}