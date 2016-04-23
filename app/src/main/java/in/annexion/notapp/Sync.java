package in.annexion.notapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by fanatic on 28/3/16.
 */
public class Sync extends AsyncTask
{
    static boolean updateClass,updateBranch,updateDprefs,updateFname,updateLname,updateEmail,updateNumber,updateDOB,updatePassword;
    static SharedPreferences sharedPreferences;
    static  NotificationUtils notificationUtils;
    static Intent intent;
    Context context;
    String intentArray[];
    String _class;
    String _branch;
    List<String> selections;
    private android.database.sqlite.SQLiteDatabase db;
    Cursor cursor;

    @Override
    protected Object doInBackground(Object[] params) {

        this.context=(Context) params[0];
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        intentArray=context.getResources().getStringArray(R.array.intent);
        _class=sharedPreferences.getString("c_name", "b1");
        _branch=sharedPreferences.getString("d_name", "cse");
        Set<String> set = sharedPreferences.getStringSet("prefs", new Set<String>() {
            @Override
            public boolean add(String object) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends String> collection) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public boolean contains(Object object) {
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> collection) {
                return false;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @NonNull
            @Override
            public Iterator<String> iterator() {
                return null;
            }

            @Override
            public boolean remove(Object object) {
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> collection) {
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> collection) {
                return false;
            }

            @Override
            public int size() {
                return 0;
            }

            @NonNull
            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @NonNull
            @Override
            public <T> T[] toArray(T[] array) {
                return null;
            }
        });
        selections=new ArrayList<>(set);
        cursor = new Cursor() {
            @Override
            public int getCount() {
                return 0;
            }

            @Override
            public int getPosition() {
                return 0;
            }

            @Override
            public boolean move(int offset) {
                return false;
            }

            @Override
            public boolean moveToPosition(int position) {
                return false;
            }

            @Override
            public boolean moveToFirst() {
                return false;
            }

            @Override
            public boolean moveToLast() {
                return false;
            }

            @Override
            public boolean moveToNext() {
                return false;
            }

            @Override
            public boolean moveToPrevious() {
                return false;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean isBeforeFirst() {
                return false;
            }

            @Override
            public boolean isAfterLast() {
                return false;
            }

            @Override
            public int getColumnIndex(String columnName) {
                return 0;
            }

            @Override
            public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
                return 0;
            }

            @Override
            public String getColumnName(int columnIndex) {
                return null;
            }

            @Override
            public String[] getColumnNames() {
                return new String[0];
            }

            @Override
            public int getColumnCount() {
                return 0;
            }

            @Override
            public byte[] getBlob(int columnIndex) {
                return new byte[0];
            }

            @Override
            public String getString(int columnIndex) {
                return null;
            }

            @Override
            public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {

            }

            @Override
            public short getShort(int columnIndex) {
                return 0;
            }

            @Override
            public int getInt(int columnIndex) {
                return 0;
            }

            @Override
            public long getLong(int columnIndex) {
                return 0;
            }

            @Override
            public float getFloat(int columnIndex) {
                return 0;
            }

            @Override
            public double getDouble(int columnIndex) {
                return 0;
            }

            @Override
            public int getType(int columnIndex) {
                return 0;
            }

            @Override
            public boolean isNull(int columnIndex) {
                return false;
            }

            @Override
            public void deactivate() {

            }

            @Override
            public boolean requery() {
                return false;
            }

            @Override
            public void close() {

            }

            @Override
            public boolean isClosed() {
                return false;
            }

            @Override
            public void registerContentObserver(ContentObserver observer) {

            }

            @Override
            public void unregisterContentObserver(ContentObserver observer) {

            }

            @Override
            public void registerDataSetObserver(DataSetObserver observer) {

            }

            @Override
            public void unregisterDataSetObserver(DataSetObserver observer) {

            }

            @Override
            public void setNotificationUri(ContentResolver cr, Uri uri) {

            }

            @Override
            public Uri getNotificationUri() {
                return null;
            }

            @Override
            public boolean getWantsAllOnMoveCalls() {
                return false;
            }

            @Override
            public void setExtras(Bundle extras) {

            }

            @Override
            public Bundle getExtras() {
                return null;
            }

            @Override
            public Bundle respond(Bundle extras) {
                return null;
            }
        };
        File file = new File(Environment.getExternalStorageDirectory().toString()+ "/Notapp/DB");
        if (!file.exists()) {
            file.mkdirs();
        }
        // db= SQLiteDatabase.openDatabase("" + Environment.getExternalStorageDirectory() + "/Notapp/DB/notapp.db", null, SQLiteDatabase.CREATE_IF_NECESSARY | SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING);
        db=SQLiteDatabase.openOrCreateDatabase("" + Environment.getExternalStorageDirectory() + "/Notapp/DB/notapp.db", null, null);
        db.enableWriteAheadLogging();

        db.execSQL("create table if not exists notices" +
                "(n_id int(11), " +                 //0
                "title varchar(50), " +             //1
                "uploadedBy varchar(30) , " +       //2
                "uploadDate varchar(15) , " +       //3
                "noticeBoard varchar(15) , " +      //4
                "link varchar(500) , " +            //5
                "md5 varchar(50) , " +              //6
                "isFav integer default 0 , " +      //7
                "isRead integer default 0 ," +      //8
                "isDone integer default 0)");

        if(new ConnectionDetector(context).isConnectingToInternet()) {
            sync();
            Log.e("Sync", "synced" + MainActivity.synced);
            for (int i = 0, v; i < selections.size(); i++) {
                v = Integer.parseInt(selections.get(i));
                Log.e("Sync a", "v=" + (v-1));
                publishProgress(v-1);
            }
            MainActivity.synced=true;
            Log.e("Sync","synced="+MainActivity.synced);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
        Integer v=(Integer)values[0];
        Log.e("Sync b","v="+v);
        new NoticesPuller(intentArray[v]).execute();
    }

    public class NoticesPuller extends AsyncTask<Void, Void, Void>
    {
        int maxn_id;
        String yourJsonStringUrl;
        String nbClicked;
        JSONArray dataJsonArr;

        NoticesPuller(String nbClicked)
        {
            this.nbClicked=nbClicked;
            yourJsonStringUrl = "http://notapp.wce.ac.in/json/index.php?dept=" + nbClicked + "&class="+_class+"&branch="+_branch;
            Log.e("Sync","nbClicked-"+nbClicked);
            dataJsonArr=null;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            int length;
            String[] n_id;
            String[] title;
            String[] uploadDate;
            String[] uploadedBy;
            String[] exp;
            String[] noticeBoard;
            String[] link;
            String[] md5;

            try {
                try {
                    cursor = db.rawQuery("select max(n_id) from notices where noticeBoard='" + nbClicked + "'", null);
                    Log.e("Sync","cursor worked");
                } catch (Exception e) {
                    Log.e("Sync","cursor fucked up");
                }
                cursor.moveToFirst();
                maxn_id=cursor.getInt(0);
                Log.e("Sync"," max n_id in "+nbClicked+"-"+maxn_id);

                _class= URLEncoder.encode(_class, "utf-8");
                _branch=URLEncoder.encode(_branch,"utf-8");
                yourJsonStringUrl= "http://notapp.wce.ac.in/json/index.php?dept=" + nbClicked + "&class="+_class+"&branch="+_branch;
                yourJsonStringUrl=yourJsonStringUrl+"&n_id="+maxn_id;
                Log.e("Sync","URL "+yourJsonStringUrl );

                JsonParser jParser = new JsonParser();
                JSONObject json = jParser.getJSONFromUrl(yourJsonStringUrl);
                dataJsonArr = json.getJSONArray("result");
                length = dataJsonArr.length();

                Log.e("Sync", "Length json"+length+" "+dataJsonArr.length());

                n_id = new String[length];
                title = new String[length];
                uploadDate = new String[length];
                uploadedBy=new String[length];
                exp = new String[length];
                link = new String[length];
                noticeBoard=new String[length];
                md5=new String[length];
                Log.e("Sync","coming in before for");

                for (int i = 0; i < length; i++)
                {
                    JSONObject c = dataJsonArr.getJSONObject(i);
                    Log.e("Sync","After JObject");
                    n_id[i] = c.getString("n_id");
                    title[i] = c.getString("title");
                    uploadDate[i] = c.getString("uploadDate");
                    uploadedBy[i]=c.getString("uploadedBy");
                    exp[i] = c.getString("exp");
                    link[i] = c.getString("name");
                    noticeBoard[i]=""+ (Integer.parseInt(c.getString("noticeBoard")) - 1);
                    md5[i]=c.getString("md5");
                    Log.e("Sync","After Read");
                    intent = new Intent(context, MainActivity.class);

                    notificationUtils = new NotificationUtils(context);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    notificationUtils.showNotificationMessage(""+title[i]+"$", uploadDate[i], uploadedBy[i], intent);

                    SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putBoolean("" + noticeBoard, true);
                    editor.commit();

                    Log.e("Sync","Received-"+title[i]);

                    Log.e("notices", "" + n_id[i] + "  " + title[i] + "  " + uploadDate[i] + "  " + exp[i] + "  " + link[i] + "  ");
                    NoticeDownloader noticeDownloader = new NoticeDownloader();
                    noticeDownloader.insertIntoDB(context, title[i], uploadDate[i], uploadedBy[i], ""+n_id[i], noticeBoard[i], link[i], md5[i]);
                    if(!(link[i].charAt(0)=='#'))
                        noticeDownloader.downloadFile(link[i]);
                }
                if(length>0) {
                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(pushNotification);
                }

            } catch (JSONException e) {
                Log.e("Sync","Upto Date "+nbClicked);
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                Log.e("Sync","Server Not found ");
                e.printStackTrace();
            }
            catch (Exception e) {
                Log.e("Server Not Found!",""+e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("Sync", "Synced: "+nbClicked);
        }
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
        String URL="http://notapp.wce.ac.in/sync.php?prn="+sharedPreferences.getString("PRN","")+"&key="+key+"&value="+value+"";
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
                Log.e("Sync:",""+e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
    void sync()
    {
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
    }

}
