package in.annexion.notapp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class NoticesActivity extends AppCompatActivity implements NoticeAdapter.ClickListener
{
    ArrayList<NoticeInfo> noticeList;
    Intent intent;
    android.app.AlertDialog alertDialog;
    RecyclerView recyclerView_Notices;
    Cursor cursor = new Cursor() {
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
    NoticeAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    String nbClicked;
    String title;
    SQLiteDatabase db;
    AppBarLayout appBarLayout;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    CollapsingToolbarLayout collapsingToolbarLayout;

    int width;
    int height;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices);

        intent=getIntent();
        nbClicked=intent.getStringExtra("nb");
        title=intent.getStringExtra("title" );

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    Log.e("MainActivity","Refreshed no Broadcast.");
                    refresh();
                }
            }
        };

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(title);

        appBarLayout=(AppBarLayout)findViewById(R.id.app_bar);
        collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
        recyclerView_Notices=(RecyclerView)findViewById(R.id.recyclerView_Notices);
        recyclerView_Notices.setHasFixedSize(true);
        context=getBaseContext();

        /*Point p = new Point();
        getWindowManager().getDefaultDisplay().getSize(p);
        collapsingToolbarLayout.getLayoutParams().height = (p.x)/2;
        collapsingToolbarLayout.requestLayout();*/

        File file = new File(Environment.getExternalStorageDirectory().toString()+ "/Notapp/DB");
        if (!file.exists()) {
            file.mkdirs();
        }

        db=SQLiteDatabase.openDatabase(""+Environment.getExternalStorageDirectory()+"/Notapp/DB/notapp.db",null,SQLiteDatabase.CREATE_IF_NECESSARY | SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING);

        noticeList= new ArrayList<>();

        registerForContextMenu(recyclerView_Notices);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        fetchFromDB();

        recyclerView_Notices.setMinimumHeight(height - 180);

        layoutManager=new LinearLayoutManager(this);
        recyclerView_Notices.setLayoutManager(layoutManager);

        recyclerView_Notices.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING)
                    appBarLayout.setExpanded(false, true);
            }
        });


        adapter= new NoticeAdapter(noticeList,getBaseContext(),this);
        recyclerView_Notices.setAdapter(adapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void itemClicked(View view, int position)
    {
        NoticeInfo item=noticeList.get(position);
        String title,link,message,md5;
        title=item.title;
        link=item.link;
        message=item.message;
        md5=item.md5;
        int nID=item.n_id;

        Log.e("intent na", title + "  " + link);

        db.execSQL("update notices set isRead=1 where n_id=" + nID);

        Intent intent=new Intent(getBaseContext(), MainNoticeActivity.class);
        intent.putExtra("title",title);
        intent.putExtra("nb", nbClicked);
        intent.putExtra("link", link);
        intent.putExtra("message", message);
        intent.putExtra("md5",md5);
        intent.putExtra("n_id", nID);
        startActivity(intent);

    }

    @Override
    public void itemTouched(View view,MotionEvent event )
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            view.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));
        else
            view.setBackgroundColor(getResources().getColor(R.color.white));
    }

    @Override
    public void delete(int pos)
    {
        NoticeInfo item=noticeList.get(pos);
        int nID=item.n_id;
        Cursor cursor2;

        cursor2= db.rawQuery("select * from notices where n_id=" + nID, null);
        Log.e("Delete", "select link from notices where nid=" + nID);
        cursor2.moveToFirst();
        String gibberish=cursor2.getString(6);
        Log.e("Delete", "fh " + nID);
        db.execSQL("delete from notices where n_id=" + nID);
        Log.e("Delete", "delete * from notices where n_id=" + nID);

        File file=new File(Environment.getExternalStorageDirectory().toString()+"/Notapp/"+gibberish +".pdf");

        if(file.exists())
            file.delete();

        Toast.makeText(getBaseContext(),""+gibberish+" deleted.",Toast.LENGTH_LONG).show();

        recyclerView_Notices.removeViewAt(pos);
        adapter.notifyDataSetChanged();
        refresh();
    }

    @Override
    public void addToFav(int pos)
    {
        NoticeInfo item=noticeList.get(pos);
        int nID=item.n_id;
        db.execSQL("update notices set isFav=1 where n_id=" + nID);
        refresh();
    }

    @Override
    public void removeFromFav(int pos)
    {
        NoticeInfo item=noticeList.get(pos);
        int nID=item.n_id;
        db.execSQL("update notices set isFav=0 where n_id="+nID);
        refresh();
    }

    public  void fetchFromDB()
    {
        Log.e("ffdb", "coming in ffdb");

        File file = new File(Environment.getExternalStorageDirectory().toString()+ "/Notapp/DB");
        if (!file.exists()) {
            file.mkdirs();
        }

        Log.e("ffdb", "db path:" + Environment.getExternalStorageDirectory() + "/Notapp/DB/notapp.db");

        try {

            try {
                cursor = db.rawQuery("select * from notices where noticeBoard= '" + nbClicked + "' order by n_id desc", null);
            }
            catch (Exception x)
            {
                Log.e("ffdb", "after cursor");
            }

            Log.e("ffdb", "after cursor: "+cursor);
            Log.e("ffdb", "move to first: "+cursor.moveToFirst());
            Log.e("ffdb", "cursor.getCount: " + cursor.getCount());

            if (!(cursor.moveToFirst()) || cursor.getCount() ==0) {

                Log.e("ffdb", "cursor null"  );

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageView placeholder = (ImageView) findViewById(R.id.placeholder);
                        Point p = new Point();
                        getWindowManager().getDefaultDisplay().getSize(p);
                        placeholder.getLayoutParams().width = (p.x);
                        placeholder.getLayoutParams().height = (p.y);
                        placeholder.requestLayout();
                        placeholder.setVisibility(View.VISIBLE);
                        placeholder.setImageResource(R.drawable.placeholder);
                        ((View) findViewById(R.id.recyclerView_Notices)).setVisibility(View.GONE);
                    }
                });
            } else {
                int ctr = 0;

                do  {
                    ctr++;
                    Log.e("ffdb", "fetching from db  "+ctr);
                    NoticeInfo noticeInfo=new NoticeInfo();

                    noticeInfo.n_id = cursor.getInt(0);
                    noticeInfo.title = cursor.getString(1);
                    noticeInfo.uploadedBy = cursor.getString(2);
                    noticeInfo.uploadDate = cursor.getString(3);
                    noticeInfo.exp = cursor.getString(4);
                    noticeInfo.noticeBoard = cursor.getString(5);
                    noticeInfo.link = cursor.getString(6);
                    noticeInfo.md5=cursor.getString(7);
                    noticeInfo.isFav=Integer.parseInt(cursor.getString(8));

                    Log.e("notices_db", cursor.getPosition() + "  " + noticeInfo.n_id + "  " + noticeInfo.noticeBoard + "  " + noticeInfo.uploadedBy + "  " + noticeInfo.title + "  " + noticeInfo.uploadDate + "  " + noticeInfo.exp + "  " + noticeInfo.link + "  ");
                    noticeList.add(noticeInfo);
                    //cursor.moveToNext();

                }while (cursor.moveToNext());

                Log.e("manish","no error11");

                Log.e("manish","no error");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("error_manish ", "" + e.toString());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    void refresh() {
        Intent intent;
        intent= new Intent(getBaseContext(),NoticesActivity.class);
        intent.putExtra("nb", nbClicked);
        intent.putExtra("title", title);
        startActivity(intent);
        finish();
    }

}
