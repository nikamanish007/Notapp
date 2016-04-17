package in.annexion.notapp;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NoticesActivity extends AppCompatActivity implements NoticeAdapter.ClickListener, android.support.v7.view.ActionMode.Callback {
    ArrayList<NoticeInfo> noticeList;
    Intent intent;
    RecyclerView recyclerView_Notices;
    android.support.v7.view.ActionMode actionMode;
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
    Toolbar toolbar;
    boolean includesFav,longPressed,firstFlag;
    static boolean cameFromBack;
    AppBarLayout appBarLayout;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    CollapsingToolbarLayout collapsingToolbarLayout;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices);

        intent = getIntent();
        nbClicked = intent.getStringExtra("nb");
        title = intent.getStringExtra("title");

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    Log.e("MainActivity", "Refreshed no Broadcast.");
                    refresh();
                }
            }
        };


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(title);

        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        recyclerView_Notices = (RecyclerView) findViewById(R.id.recyclerView_Notices);
        recyclerView_Notices.setHasFixedSize(true);
        context = getBaseContext();

        File file = new File(Environment.getExternalStorageDirectory().toString() + "/Notapp/DB");
        if (!file.exists()) {
            file.mkdirs();
        }

        db = SQLiteDatabase.openDatabase("" + Environment.getExternalStorageDirectory() + "/Notapp/DB/notapp.db", null, SQLiteDatabase.CREATE_IF_NECESSARY | SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING);

        noticeList = new ArrayList<>();

        fetchFromDB();

        layoutManager = new LinearLayoutManager(this);
        recyclerView_Notices.setLayoutManager(layoutManager);
        recyclerView_Notices.setHasFixedSize(true);

        recyclerView_Notices.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING)
                    appBarLayout.setExpanded(false, true);
            }
        });


        adapter = new NoticeAdapter(noticeList, getBaseContext(), this);
        recyclerView_Notices.setAdapter(adapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void itemClicked(View view, int position) {
        if (actionMode != null) {
            myToggleSelection(position);

            if(firstFlag) {
                view.setBackgroundColor(getResources().getColor(R.color.colorAccentTransperent));
                firstFlag = false;
            }


            Log.e("NoticesActivity","onClick"+position);
            if (adapter.isSeleted(position)) {
                view.setBackgroundColor(getResources().getColor(R.color.colorAccentTransperent));
                Log.e("NoticesActivity", "selected");
            }
            else {
                view.setBackgroundColor(getResources().getColor(R.color.white));
                Log.e("NoticesActivity", "deselected");
            }

            //first favorite so far
            if (Integer.parseInt(((TextView)view.findViewById(R.id.textView_isFav)).getText().toString())==0&&includesFav) {
                includesFav=false;
                actionMode = startSupportActionMode(this);
                Log.e("NoticesActivity", "actionMode reStarted");
            }
            return;
        }

        NoticeInfo item = noticeList.get(position);
        String title, link, message, md5;
        title = item.title;
        link = item.link;
        message = item.message;
        md5 = item.md5;
        int nID = item.n_id;

        Log.e("intent na", title + "  " + link);

        db.execSQL("update notices set isRead=1 where n_id=" + nID);

        Intent intent = new Intent(getBaseContext(), MainNoticeActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("nb", nbClicked);
        intent.putExtra("link", link);
        intent.putExtra("message", message);
        intent.putExtra("md5", md5);
        intent.putExtra("n_id", nID);
        startActivity(intent);

    }

    @Override
    public void itemLongPress(View view,int pos) {
        longPressed=true;
        firstFlag = true;
        if (actionMode != null) {
            Log.e("NoticesActivity", "actionMode not null returned");
            return;
        }
        if(Integer.parseInt(((TextView)view.findViewById(R.id.textView_isFav)).getText().toString())!=0)
            includesFav=true;
        Log.e("NoticesActivity", "actionMode created");
        actionMode = startSupportActionMode(this);

        Log.e("NoticesActivity", "actionMode = " + actionMode);
    }


    private void myToggleSelection(int idx) {
        adapter.toggleSelection(idx);
        String title = "" + adapter.getSelectedItemCount() + " Notices selected";
        actionMode.setTitle(title);
    }

    @Override
    public void itemTouched(View view, MotionEvent event) {

        if(!longPressed) {
            Log.e("NoticesActivity","itemTouched");
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.e("NoticesActivity", "Event=" + event.getAction());
                view.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));
            }
            else {
                Log.e("NoticesActivity","Event="+event.getAction());
                view.setBackgroundColor(getResources().getColor(R.color.white));
            }
        }
    }

    @Override
    public void delete(int pos) {
        NoticeInfo item = noticeList.get(pos);
        int nID = item.n_id;
        Cursor cursor2;

        noticeList.remove(pos);
        cursor2 = db.rawQuery("select * from notices where n_id=" + nID, null);
        Log.e("Delete", "select link from notices where nid=" + nID);
        cursor2.moveToFirst();
        if(cursor2.getCount()!=0) {
            String gibberish = cursor2.getString(6);
            Log.e("Delete", "fh " + nID);
            db.execSQL("delete from notices where n_id=" + nID);
            Log.e("Delete", "delete * from notices where n_id=" + nID);

            File file = new File(Environment.getExternalStorageDirectory().toString() + "/Notapp/" + gibberish + ".pdf");

            if (file.exists())
                file.delete();
        }
        noticeList.remove(pos);
        adapter.notifyItemRemoved(pos);
        adapter.notifyItemRangeChanged(pos, noticeList.size());
    }

    @Override
    public void addToFav(int pos) {
        NoticeInfo item = noticeList.get(pos);
        int nID = item.n_id;
        db.execSQL("update notices set isFav=1 where n_id=" + nID);
        Log.e("NoticesActivity",""+nID + "added to favorites");
    }

    @Override
    public void removeFromFav(int pos) {
        NoticeInfo item = noticeList.get(pos);
        int nID = item.n_id;
        db.execSQL("update notices set isFav=0 where n_id=" + nID);
        adapter.notifyItemChanged(pos);
        adapter.notifyDataSetChanged();
    }

    public void fetchFromDB() {
        Log.e("ffdb", "coming in ffdb");

        File file = new File(Environment.getExternalStorageDirectory().toString() + "/Notapp/DB");
        if (!file.exists()) {
            file.mkdirs();
        }

        Log.e("ffdb", "db path:" + Environment.getExternalStorageDirectory() + "/Notapp/DB/notapp.db");

        try {

            try {
                cursor = db.rawQuery("select * from notices where noticeBoard= '" + nbClicked + "' order by n_id desc", null);
            } catch (Exception x) {
                Log.e("ffdb", "after cursor");
            }

            Log.e("ffdb", "after cursor: " + cursor);
            Log.e("ffdb", "move to first: " + cursor.moveToFirst());
            Log.e("ffdb", "cursor.getCount: " + cursor.getCount());

            if (!(cursor.moveToFirst()) || cursor.getCount() == 0) {

                Log.e("ffdb", "cursor null");

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

                do {
                    ctr++;
                    Log.e("ffdb", "fetching from db  " + ctr);
                    NoticeInfo noticeInfo = new NoticeInfo();

                    noticeInfo.n_id = cursor.getInt(0);
                    noticeInfo.title = cursor.getString(1);
                    noticeInfo.uploadedBy = cursor.getString(2);
                    noticeInfo.uploadDate = cursor.getString(3);
                    noticeInfo.exp = cursor.getString(4);
                    noticeInfo.noticeBoard = cursor.getString(5);
                    noticeInfo.link = cursor.getString(6);
                    noticeInfo.md5 = cursor.getString(7);
                    noticeInfo.isFav = Integer.parseInt(cursor.getString(8));
                    noticeInfo.isRead= Integer.parseInt(cursor.getString(9));

                    Log.e("notices_db", cursor.getPosition() + "  " + noticeInfo.n_id + "  " + noticeInfo.noticeBoard + "  " + noticeInfo.uploadedBy + "  " + noticeInfo.title + "  " + noticeInfo.uploadDate + "  " + noticeInfo.exp + "  " + noticeInfo.link + "  ");
                    noticeList.add(noticeInfo);
                } while (cursor.moveToNext());
                Log.e("NoticesActivity", "fetchFromDB - no Error");
            }
        } catch (Exception e) {
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

        Log.e("NoticesActivity","cameFromBack- "+cameFromBack);
        if(cameFromBack) {
            cameFromBack=false;
            refresh();
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    void refresh() {
        Intent intent;
        intent = new Intent(getBaseContext(), NoticesActivity.class);
        intent.putExtra("nb", nbClicked);
        intent.putExtra("title", title);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
        longPressed=true;

        MenuInflater inflater = mode.getMenuInflater();
        if(includesFav)
            inflater.inflate(R.menu.menu_notices_2, menu);
        else
            inflater.inflate(R.menu.menu_notices_1,menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(android.support.v7.view.ActionMode mode, MenuItem item) {
        List<Integer> selectedItemPositions;
        switch (item.getItemId()) {
            case R.id.menu_delete:
                selectedItemPositions = adapter.getSelectedItems();
                for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                    delete(selectedItemPositions.get(i));
                }
                break;
            case R.id.menu_favorite:
                selectedItemPositions = adapter.getSelectedItems();
                for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                    addToFav(selectedItemPositions.get(i));
                }
                refresh();
                break;
            case R.id.menu_deFavorite:
                selectedItemPositions = adapter.getSelectedItems();
                for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                    removeFromFav(selectedItemPositions.get(i));
                }
                refresh();
                break;
        }
        adapter.clearSelections();
        includesFav=false;
        longPressed=false;
        actionMode.finish();
        return true;
    }



    @Override
    public void onDestroyActionMode(android.support.v7.view.ActionMode mode) {
        this.actionMode = null;
    }
}
