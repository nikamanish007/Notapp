package in.annexion.notapp;

import android.animation.ObjectAnimator;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AttendanceActivity extends AppCompatActivity implements CourseAdapter.ClickListener
{
    ArrayList<CourseInfo> courseList;
    ArcProgress arcProgress;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView textView_CourseTitleShowing;
    RecyclerView recyclerView_Courses;
    RecyclerView.Adapter adapter;
    ImageView imageView_placeholder2;
    RecyclerView.LayoutManager layoutManager;
    SQLiteDatabase db;
    SharedPreferences sharedPreferences;
    int lastSelection=0;
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
    String PRN;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        File file = new File(Environment.getExternalStorageDirectory().toString()+ "/Notapp/DB");
        if (!file.exists()) {
            file.mkdirs();
        }

        arcProgress=(ArcProgress)findViewById(R.id.arc_progress);
        arcProgress.setProgress(75);
        arcProgress.setFinishedStrokeColor(Color.GRAY);
        textView_CourseTitleShowing=(TextView)findViewById(R.id.textView_CourseTitleShowing);
        textView_CourseTitleShowing.setText("Choose a Course");

        courseList=new ArrayList<CourseInfo>();

        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        recyclerView_Courses=(RecyclerView)findViewById(R.id.recyclerView_Courses);
        recyclerView_Courses.setHasFixedSize(true);
        imageView_placeholder2=(ImageView)findViewById(R.id.imageView_placeholder);

        layoutManager=new LinearLayoutManager(this);
        recyclerView_Courses.setLayoutManager(layoutManager);

        adapter= new CourseAdapter(courseList,getBaseContext(),this);
        recyclerView_Courses.setAdapter(adapter);

        fetchFromDB();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                PRN=sharedPreferences.getString("PRN","");
                new SyncAttendance().execute(PRN);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void fetchFromDB()
    {
        db=SQLiteDatabase.openOrCreateDatabase("" + Environment.getExternalStorageDirectory() + "/Notapp/DB/attendance.db", null, null);
        try {
            cursor = db.rawQuery("select * from attendance", null);
        }
        catch (Exception x)
        {
            Log.e("ffdb", "after cursor");
        }

        int ctr=0;

        if (!(cursor.moveToFirst()) || cursor.getCount() ==0) {
            Toast.makeText(getBaseContext(),"No Data!",Toast.LENGTH_LONG).show();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageView_placeholder2.setImageResource(R.drawable.placeholder2);
                    adapter.notifyDataSetChanged();
                }
            });
        }
        else {


            do {
                ctr++;
                Log.e("ffdb", "fetching from db  " + ctr);
                CourseInfo courseInfo = new CourseInfo();

                courseInfo.courseCode = cursor.getString(0);
                courseInfo.courseTitle = cursor.getString(1);
                courseInfo.percentage = Float.parseFloat(cursor.getString(2));

                courseList.add(courseInfo);
            } while (cursor.moveToNext());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageView_placeholder2.setVisibility(View.INVISIBLE);
                    adapter.notifyDataSetChanged();
                }
            });
        }

        cursor.close();
        db.close();

    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void itemClicked(View view, int position)
    {
        arcProgress.setFinishedStrokeColor(Color.argb(1, 20, 20, 20));
        String courseCodeSelected=((TextView) view.findViewById(R.id.textView_CourseTitle)).getText().toString();

        textView_CourseTitleShowing.setText(courseCodeSelected);

        int i=(int)(Float.parseFloat(((TextView) view.findViewById(R.id.textView_percentage)).getText().toString()));

        if (i < 75)
            arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.red));
        else
            arcProgress.setFinishedStrokeColor(getResources().getColor(R.color.colorAccent));

        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(arcProgress, "progress", 0, i);
        progressAnimator.setDuration(i*10);
        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.start();

        lastSelection=position;

        arcProgress.clearAnimation();

    }

    @Override
    public void itemTouched(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            view.setBackgroundColor(getResources().getColor(R.color.grayForeground));
        else
            view.setBackgroundColor(getResources().getColor(R.color.white));
    }

    private class SyncAttendance extends AsyncTask<String,Void,Void> {
        private JSONArray dataJsonArr;
        JSONObject jsonObject;
        boolean hasActiveConnection=false;

        public boolean isConnectingToInternet(){
            boolean res=false;
            ConnectivityManager cm = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                res=true;
            }
            return res;
        }

        public  boolean hasActiveConnection() {
            Boolean toPostExecute=false;
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://10.10.13.213").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(5000);
                urlc.connect();
                toPostExecute = (urlc.getResponseCode() == 204 || urlc.getResponseCode()==200);
                Log.e("CD", "ResponseCode: " + urlc.getResponseCode() + "  " + toPostExecute);
            } catch (IOException e) {
                Log.e("ConnectionD", "Error checking internet connection", e);
            }
            return toPostExecute;
        }

        @Override
        protected Void doInBackground(String... params)
        {
            String courseCode="2CS246", courseTitle = null, percentage = null;

            if(isConnectingToInternet())
            {
                hasActiveConnection = hasActiveConnection();
            }
            if(!hasActiveConnection) {
                Snackbar.make(findViewById(R.id.linearLayout_parentView), "No Internet Connection!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
            else {
                try {
                    db = SQLiteDatabase.openOrCreateDatabase("" + Environment.getExternalStorageDirectory() + "/Notapp/DB/attendance.db", null, null);
                    JsonParser jParser = new JsonParser();
                    JSONObject json = jParser.getJSONFromUrl("http://10.10.5.140/n/moodle/attendance.php?prn=" + params[0]);
                    Log.e("AttendanceActivity", "after jParser.getJSONFromUrl");
                    dataJsonArr = json.getJSONArray("result");
                    Log.e("AttendanceActivity", "after jParser.getJSONArray");
                    int length = dataJsonArr.length();

                    Log.e("AttendanceActivity", "Length json: " + length + " " + dataJsonArr.length());

                    db.execSQL("drop table if exists attendance");
                    db.execSQL("create table attendance(courseCode varchar(10) , courseTitle varchar(50) , percentage varchar(10))");

                    Log.e("AttendanceActivity", "Database ");

                    for (int i = 0; i < length; i++) {
                        jsonObject = dataJsonArr.getJSONObject(i);
                        courseCode = jsonObject.getString("coursecode");
                        courseTitle = jsonObject.getString("coursetitle");
                        percentage = jsonObject.getString("percentage");

                        db.execSQL("insert into attendance values('" + courseCode + "','" + courseTitle + "','" + percentage + "')");
                    }
                } catch (Exception e) {
                    Log.e("AttendanceActivity", "JSON: " + e);
                    e.printStackTrace();
                }
                db.close();
            }
            Log.e("AttendanceActivity","hasActiveConn:"+hasActiveConnection);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            if(hasActiveConnection)
            {
                startActivity(new Intent(getBaseContext(), AttendanceActivity.class));
                finish();
            }
            swipeRefreshLayout.setRefreshing(false);
        }
    }


}
