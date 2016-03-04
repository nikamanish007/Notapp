package in.annexion.notapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class FavouritesActivity extends AppCompatActivity implements FavoritesAdapter.ClickListener
{

    RecyclerView recyclerView_Favorites;
    RecyclerView.LayoutManager layoutManager;
    FavoritesAdapter adapter;
    ArrayList<NoticeInfo> favoriteList;
    SQLiteDatabase db;
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


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        File file = new File(Environment.getExternalStorageDirectory().toString()+ "/Notapp/DB");
        if (!file.exists()) {
            file.mkdirs();
        }

        db= SQLiteDatabase.openDatabase("" + Environment.getExternalStorageDirectory() + "/Notapp/DB/notapp.db", null, SQLiteDatabase.CREATE_IF_NECESSARY | SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING);

        favoriteList=new ArrayList<NoticeInfo>();

        adapter=new FavoritesAdapter(favoriteList,getBaseContext(),this);

        recyclerView_Favorites=(RecyclerView)findViewById(R.id.recyclerView_Favorites);
        registerForContextMenu(recyclerView_Favorites);

        layoutManager=new LinearLayoutManager(this);
        recyclerView_Favorites.setLayoutManager(layoutManager);

        recyclerView_Favorites.setAdapter(adapter);
        fetchFromDB();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private void fetchFromDB()
    {
        try {

            try {
                cursor = db.rawQuery("select * from notices where isFav=1 order by n_id desc", null);
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
                //Log.e("ffdb", "fetching from db");
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

                    Log.e("notices_db", cursor.getPosition() + "  " + noticeInfo.n_id + "  " + noticeInfo.noticeBoard + "  " + noticeInfo.uploadedBy + "  " + noticeInfo.title + "  " + noticeInfo.uploadDate + "  " + noticeInfo.exp + "  " + noticeInfo.link + "  ");

                    favoriteList.add(noticeInfo);
                    //cursor.moveToNext();

                }while (cursor.moveToNext());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("error_manish ", "" + e.toString());
        }
    }

    @Override
    public void itemClicked(View view, int position)
    {
        NoticeInfo item=favoriteList.get(position);
        String title,link;
        title=item.title;
        link=item.link;

        Log.e("intent na", title + "  " + link);

        Intent intent=new Intent(getBaseContext(), MainNoticeActivity.class);
        intent.putExtra("title",title);
        intent.putExtra("nb","");
        intent.putExtra("link", link);
        startActivity(intent);
        finish();
    }

    @Override
    public void itemTouched(View view, MotionEvent motionEvent)
    {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
            view.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));
        else
            view.setBackgroundColor(getResources().getColor(R.color.white));

    }

    @Override
    public void removeFromFav(int pos)
    {
        View viewToBeRemoved=recyclerView_Favorites.getChildAt(pos);
        int nID=Integer.parseInt(((TextView) viewToBeRemoved.findViewById(R.id.textView_nID)).getText().toString());
        db.execSQL("update notices set isFav=0 where n_id="+nID);

        recyclerView_Favorites.removeViewAt(pos);
        adapter.notifyDataSetChanged();

        startActivity(new Intent(getBaseContext(),FavouritesActivity.class));
        finish();

    }
}
