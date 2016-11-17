package in.co.rubberduck.notapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by fanatic on 25/4/16.
 */
public class SyncPrefs extends AsyncTask
{
    static boolean updateClass,updateBranch,updateDprefs,updateFname,updateLname,updateEmail,updateNumber,updateDOB,updatePassword;
    static SharedPreferences sharedPreferences;
    static Intent intent;
    Context context;
    boolean isConnected;

    @Override
    protected Object doInBackground(Object[] params) {
        context=(Context) params[0];
        isConnected=new ConnectionDetector(context).isConnectingToInternet();
        if(isConnected) {
            sync();
            Log.e("SyncPrefs", "called");
        }
        MainActivity.synced=true;
        return null;
    }

    void sync()
    {
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        updateFname=sharedPreferences.getBoolean("updateFname",false); Log.e("updateFname=", "" + updateFname);
        updateLname=sharedPreferences.getBoolean("updateLname",false); Log.e("updateLname=",""+updateLname);
        updateEmail=sharedPreferences.getBoolean("updateEmail",false); Log.e("updateEmail=",""+updateEmail);
        updatePassword=sharedPreferences.getBoolean("updatePassword",false); Log.e("updatePassword=",""+updatePassword);
        updateNumber=sharedPreferences.getBoolean("updateNumber",false); Log.e("updateNumber=",""+updateNumber);
        updateDOB=sharedPreferences.getBoolean("updateDOB",false); Log.e("updateDOB=",""+updateDOB);
        updateClass=sharedPreferences.getBoolean("updateClass",false); Log.e("updateClass=",""+updateClass);
        updateBranch=sharedPreferences.getBoolean("updateBranch",false); Log.e("updateBranch=",""+updateBranch);
        updateDprefs=sharedPreferences.getBoolean("updateDprefs",false); Log.e("updateDPrefs=",""+updateDprefs);

        if (updateClass) {
            editor.putBoolean("updateClass",false);
            updateClass();
        }
        if (updateBranch) {
            editor.putBoolean("updateBranch",false);
            updateBranch();
        }
        if (updateDprefs) {
            editor.putBoolean("updateDprefs",false);
            updateDPrefs();
        }
        if (updateFname) {
            editor.putBoolean("updateFname",false);
            updateFName();
        }
        if (updateLname) {
            editor.putBoolean("updateLname",false);
            updateLName();
        }
        if (updateEmail) {
            editor.putBoolean("updateEmail",false);
            updateEMail();
        }
        if (updateNumber) {
            editor.putBoolean("updateNumber",false);
            updateNumber();
        }
        if (updateDOB) {
            editor.putBoolean("updateDOB",false);
            updateDOB();
        }
        if (updatePassword) {
            editor.putBoolean("updatePassword",false);
            updatePassword();
        }
        editor.commit();
        MainActivity.synced=true;
    }

    static public void updateClass()
    {
        String _class= sharedPreferences.getString("c_name", "b1");
        upload("c_name",_class);
    }
    static public void updateBranch()
    {
        String _branch= sharedPreferences.getString("d_name", "cse");
        upload("d_name",_branch);
    }
    static public void updateDPrefs()
    {
        Set<String> dprefs= sharedPreferences.getStringSet("prefs", new HashSet<>(Arrays.asList(new String[]{})));
        String _dprefs="21,22,23";
        Iterator iterator=dprefs.iterator();
        for (int i=0;i<dprefs.size();i++) {
            if(i==0)
                _dprefs="";
            _dprefs+=""+iterator.next();
            _dprefs+=",";
        }
        upload("prefs",_dprefs.substring(0,_dprefs.length()-1));
    }
    static public void updateFName()
    {
        String _fname= sharedPreferences.getString("f_name", "");
        try {
            _fname= URLEncoder.encode(_fname, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        upload("f_name",_fname);

    }
    static public void updateLName()
    {
        String _lname= sharedPreferences.getString("l_name", "");
        try {
            _lname= URLEncoder.encode(_lname,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        upload("l_name",_lname);

    }
    static public void updateEMail()
    {
        String _email= sharedPreferences.getString("email", "b1");
        try {
            _email= URLEncoder.encode(_email,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        upload("email",_email);
    }
    static public void updateNumber()
    {
        String _number= sharedPreferences.getString("phone", "");
        upload("phone",_number);

    }
    static public void updatePassword()
    {
        String _password= sharedPreferences.getString("pword", "");
        upload("pword",_password);
    }
    static public void updateDOB()
    {
        String _dob= sharedPreferences.getString("dob", "");
        upload("dob",_dob);

    }
    static private void upload(String key,String value)
    {
        String URL="http://wce.ac.in/notapp/sync.php?prn="+sharedPreferences.getString("PRN","")+"&key="+key+"&value="+value+"";
        new Send().execute(URL);
    }
    static class Send extends AsyncTask<String,Void,Void>
    {
        @Override
        protected Void doInBackground(String... params) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            Log.e("url", params[0]);
            HttpGet httpGet = new HttpGet(params[0]);
            try {
                httpClient.execute(httpGet);
            } catch (IOException e) {
                Log.e("SyncPrefs",""+e);
                e.printStackTrace();
            }
            return null;
        }
    }
}

