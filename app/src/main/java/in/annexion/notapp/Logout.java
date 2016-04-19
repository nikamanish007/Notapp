package in.annexion.notapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by fanatic on 18/4/16.
 */
public class Logout extends AsyncTask {
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected Object doInBackground(Object[] params) {
        context=(Context) params[0];
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        editor=sharedPreferences.edit();
        if(isConnectingToInternet()) {
            return true;
        }
        else {
            return false;
        }
    }
    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if((Boolean) o) {
            new AlertDialog.Builder(context)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to log out?")
                    .setIcon(R.drawable.ic_logout)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            Toast.makeText(context, "LoggedOut", Toast.LENGTH_SHORT).show();
                            context.startActivity(new Intent(context, LoginActivity.class));
                            ((Activity)context).finish();
                            editor.putBoolean("isLoggedIn", false);
                            editor.commit();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }
        else{
            new AlertDialogManager().showAlertDialog(context, "Offline or Weak Connection!", "Please stay connected to Logout.", false);
        }
    }

    public boolean isConnectingToInternet(){
        boolean res=false;
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()&&hasActiveInternetConnection()) {
            res=true;
        }
        return res;
    }

    public boolean hasActiveInternetConnection() {
        try {
            HttpURLConnection urlc = (HttpURLConnection) (new URL("http://clients3.google.com/generate_204").openConnection());
            urlc.setRequestProperty("User-Agent", "Test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(5000);
            urlc.connect();
            Log.e("CD", "ResponseCode: " + urlc.getResponseCode());
            return (urlc.getResponseCode() == 204 || urlc.getResponseCode()==200);
        } catch (IOException e) {
            Log.e("ConnectionD", "Error checking internet connection", e);
        }
        return false;
    }
}
