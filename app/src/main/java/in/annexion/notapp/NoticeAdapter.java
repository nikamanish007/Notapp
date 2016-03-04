package in.annexion.notapp;

import android.content.ContentResolver;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by sarang on 1/1/16.
 */
public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>
{

    private android.database.sqlite.SQLiteDatabase db;
    private ArrayList<NoticeInfo> noticeList;
    private Context context;
    public ClickListener clickListener;
    private int position;
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


    public NoticeAdapter(ArrayList<NoticeInfo> noticeList, Context context , ClickListener clickListener)
    {
        this.noticeList = noticeList;
        this.context=context;
        this.clickListener=clickListener;
    }

    @Override
    public int getItemCount()
    {
        return noticeList.size();
    }

    @Override
    public void onBindViewHolder(NoticeViewHolder noticeViewHolder, int i)
    {
        NoticeInfo noticeInfo = noticeList.get(i);
        noticeViewHolder.textView_NoticeTitle.setText(noticeInfo.title);
        noticeViewHolder.textView_UploadedBy.setText(noticeInfo.uploadedBy);
        noticeViewHolder.textView_Date.setText(noticeInfo.uploadDate);
        noticeViewHolder.textView_nID.setText(""+noticeInfo.n_id);

        db= SQLiteDatabase.openDatabase("" + Environment.getExternalStorageDirectory() + "/Notapp/DB/notapp.db", null, SQLiteDatabase.CREATE_IF_NECESSARY | SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING);

        cursor=db.rawQuery("select isRead from notices where n_id="+noticeInfo.n_id,null);
        cursor.moveToFirst();

        int isRead=cursor.getInt(0);
        if(isRead==0)   
            noticeViewHolder.textView_NoticeTitle.setTextColor(context.getResources().getColor(R.color.colorAccent));
    }

    @Override
    public NoticeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notice_item, viewGroup, false);
        return new NoticeViewHolder(itemView);
    }

    public class NoticeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener, View.OnLongClickListener, View.OnTouchListener {
        TextView textView_NoticeTitle;
        TextView textView_UploadedBy;
        TextView textView_Date;
        TextView textView_nID;

        public NoticeViewHolder(View itemView)
        {
            super(itemView);

            textView_NoticeTitle = (TextView) itemView.findViewById(R.id.textView_NoticeTitle);
            textView_UploadedBy = (TextView) itemView.findViewById(R.id.textView_UploadedBy);
            textView_Date = (TextView) itemView.findViewById(R.id.textView_Date);
            textView_nID=(TextView)itemView.findViewById(R.id.textView_nID);

            Typeface roboto_light = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoCondensed-Light.ttf");

            textView_NoticeTitle.setTypeface(roboto_light);
            textView_UploadedBy.setTypeface(roboto_light);
            textView_Date.setTypeface(roboto_light);
            textView_nID.setTypeface(roboto_light);

            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnClickListener(this);
            itemView.setOnTouchListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            clickListener.itemClicked(v, getAdapterPosition());
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
        {
            menu.setHeaderTitle(R.string.blank);

            MenuItem menuItemDelete=menu.add(0,0,0,"Delete");
            MenuItem menuItemFavorite=menu.add(0,1,0,"Add to Favorites");

            menuItemDelete.setOnMenuItemClickListener(this);
            menuItemFavorite.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            if (item.getItemId()==0)
            {
                Toast.makeText(context,""+getPos()+ "Deleted!!",Toast.LENGTH_LONG).show();
                clickListener.delete(getPos());
            }
            else
            {
                Toast.makeText(context,""+getPos()+ "Added to Favorites",Toast.LENGTH_LONG).show();
                clickListener.addToFav(getPos());
            }

            return false;
        }

        @Override
        public boolean onLongClick(View v) {
            setPos(getAdapterPosition());
            return false;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            clickListener.itemTouched(v,event);
            return false;
        }
    }

    public int getPos() {
        return position;
    }

    public void setPos(int position) {
        this.position = position;
    }

    public interface ClickListener
    {
        void itemClicked(View view,int position);
        void itemTouched(View view,MotionEvent motionEvent);
        void delete(int pos);
        void addToFav(int pos);
    }
}
