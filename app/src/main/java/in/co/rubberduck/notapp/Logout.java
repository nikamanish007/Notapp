package in.co.rubberduck.notapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
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
    File file;
    @Override
    protected Object doInBackground(Object[] params) {
        context=(Context) params[0];
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        editor=sharedPreferences.edit();
        if(isConnectingToInternet()) {
            try{
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet("http://notapp.wce.ac.in/json/logout.php?PRN=" + params[1]);
                HttpResponse httpResponse = null;
                httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                Log.e("Logout","Logged Out");
            }
            catch (Exception e){
                Log.e("Logout","io");
            }
            return true;
        }
        else {
            Log.e("Logout","Logged Out");
            return false;
        }
    }
    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if(!(Boolean)o){
            new AlertDialogManager().showAlertDialog(context, "Offline or Weak Connection!", "Please stay connected to Logout.", false);
            Log.e("Logout","Offline");
        }
        else {
            context.startActivity(new Intent(context,MainActivity.class));
            ((Activity)context).finish();
            Toast.makeText(context,"Successfully Logged Out!",Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.clear().commit();
            file=new File(""+ Environment.getExternalStorageDirectory()+"/Notapp");
            if(file.exists()) {
                try {
                    deleteRecursive(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void deleteRecursive(File fileOrDirectory) throws Exception {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);
        fileOrDirectory.delete();
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
