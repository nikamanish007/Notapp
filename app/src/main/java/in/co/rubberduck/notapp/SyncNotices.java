package in.co.rubberduck.notapp;

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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import in.co.rubberduck.notapp.R;

/**
 * Created by fanatic on 28/3/16.
 */
public class SyncNotices extends AsyncTask
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
    boolean isConnected;

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        MainActivity.swipeRefreshLayout_main.setRefreshing(false);
        if(isConnected)
            Toast.makeText(context,"You are up to date!",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context,"You are Offline",Toast.LENGTH_LONG).show();
    }

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
            public void clear() {}

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

        isConnected=new ConnectionDetector(context).isConnectingToInternet();
        if(isConnected) {
            Log.e("SyncNotices", "synced" + MainActivity.synced);
            for (int i = 0, v; i < selections.size(); i++) {
                v = Integer.parseInt(selections.get(i));
                Log.e("SyncNotices a", "v=" + (v-1));
                publishProgress(v-1);
            }
            Log.e("SyncNotices","synced="+MainActivity.synced);
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
        Integer v=(Integer)values[0];
        Log.e("SyncNotices b","v="+v);
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
            Log.e("SyncNotices","nbClicked-"+nbClicked);
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
                    Log.e("SyncNotices","cursor worked");
                } catch (Exception e) {
                    Log.e("SyncNotices","cursor fucked up");
                }
                cursor.moveToFirst();
                maxn_id=cursor.getInt(0);
                Log.e("SyncNotices"," max n_id in "+nbClicked+"-"+maxn_id);

                _class= URLEncoder.encode(_class, "utf-8");
                _branch=URLEncoder.encode(_branch,"utf-8");
                yourJsonStringUrl= "http://notapp.wce.ac.in/json/index.php?dept=" + nbClicked + "&class="+_class+"&branch="+_branch;
                yourJsonStringUrl=yourJsonStringUrl+"&n_id="+maxn_id;
                Log.e("SyncNotices","URL "+yourJsonStringUrl );

                JsonParser jParser = new JsonParser();
                JSONObject json = jParser.getJSONFromUrl(yourJsonStringUrl);
                dataJsonArr = json.getJSONArray("result");
                length = dataJsonArr.length();

                Log.e("SyncNotices", "Length json"+length+" "+dataJsonArr.length());

                n_id = new String[length];
                title = new String[length];
                uploadDate = new String[length];
                uploadedBy=new String[length];
                exp = new String[length];
                link = new String[length];
                noticeBoard=new String[length];
                md5=new String[length];
                Log.e("SyncNotices","coming in before for");

                for (int i = 0; i < length; i++)
                {
                    JSONObject c = dataJsonArr.getJSONObject(i);
                    Log.e("SyncNotices","After JObject");
                    n_id[i] = c.getString("n_id");
                    title[i] = c.getString("title");
                    uploadDate[i] = c.getString("uploadDate");
                    uploadedBy[i]=c.getString("uploadedBy");
                    exp[i] = c.getString("exp");
                    link[i] = c.getString("name");
                    noticeBoard[i]=""+ (Integer.parseInt(c.getString("noticeBoard")) - 1);
                    md5[i]=c.getString("md5");
                    Log.e("SyncNotices","After Read");
                    intent = new Intent(context, MainActivity.class);

                    notificationUtils = new NotificationUtils(context);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    notificationUtils.showNotificationMessage(""+title[i]+"$", uploadDate[i], uploadedBy[i], intent);

                    SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putBoolean("" + noticeBoard, true);
                    editor.commit();

                    Log.e("SyncNotices","Received-"+title[i]);

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
                Log.e("SyncNotices","Upto Date "+nbClicked);
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                Log.e("SyncNotices","Server Not found ");
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
            Log.e("SyncNotices", "Synced: "+nbClicked);
        }
    }
}
