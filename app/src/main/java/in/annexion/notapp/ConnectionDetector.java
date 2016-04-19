package in.annexion.notapp;

/**
 * Created by sarang on 28/1/16.
 */
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectionDetector {

    private Context _context;
    boolean result;
    public ConnectionDetector(Context context){
        result=false;
        this._context = context;
    }

    /**
     * Checking for all possible internet providers
     * **/
    public boolean isConnectingToInternet(){
        boolean res=false;
        ConnectivityManager cm = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    public class ActiveInternetAsync extends AsyncTask<Void,Void,Boolean>
    {
        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean toPostExecute=false;
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://clients3.google.com/generate_204").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(5000);
                urlc.connect();
                toPostExecute = (urlc.getResponseCode() == 204 || urlc.getResponseCode()==200);
                Log.e("CD", "ResponseCode: " + urlc.getResponseCode());
            } catch (IOException e) {
                Log.e("ConnectionD", "Error checking internet connection", e);
            }
            return toPostExecute;
        }

        @Override
        protected void onPostExecute(Boolean toPostExecute) {
            result =toPostExecute;
        }

    }

}
