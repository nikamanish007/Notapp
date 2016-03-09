package in.annexion.notapp;

/**
 * Created by sarang on 27/1/16.
 */
import android.content.Context;
import android.content.DialogInterface;

public class AlertDialogManager {
    /**
     * Function to display simple Alert Dialog
     * @param context - application context
     * @param title - alert dialog title
     * @param message - alert message
     * @param status - success/failure (used to set icon)
     *               - pass null if you don't want icon
     * */

    boolean isForLogin=false;
    public void showAlertDialog(final Context context, String title, String message,
                                Boolean status) {
        int iconId;
        if(status)
            iconId=android.R.drawable.ic_popup_sync;
        else
            iconId=R.drawable.ic_signal_wifi_off;

        if(title.equals("Offline or Weak Connection!"))
            isForLogin=true;


        new android.support.v7.app.AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(iconId)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (isForLogin) {
                        }
                        else
                        {

                        }
                    }
                }).show();
    }
}