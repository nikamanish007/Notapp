package in.annexion.notapp;

import android.content.Context;
import android.graphics.Typeface;
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
 * Created by fanatic on 27/2/16.
 */
public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.NoticeViewHolder>
{

    private ArrayList<NoticeInfo> favoriteList;
    private Context context;
    public ClickListener clickListener;
    private int position;

    public FavoritesAdapter(ArrayList<NoticeInfo> favoriteList, Context context , ClickListener clickListener)
    {
        this.favoriteList = favoriteList;
        this.context=context;
        this.clickListener=clickListener;
    }

    @Override
    public int getItemCount()
    {
        return favoriteList.size();
    }

    @Override
    public void onBindViewHolder(NoticeViewHolder noticeViewHolder, int i)
    {
        NoticeInfo noticeInfo = favoriteList.get(i);
        noticeViewHolder.textView_NoticeTitle.setText(noticeInfo.title);
        noticeViewHolder.textView_UploadedBy.setText(noticeInfo.uploadedBy);
        noticeViewHolder.textView_Date.setText(noticeInfo.uploadDate);
        noticeViewHolder.textView_nID.setText(""+noticeInfo.n_id);
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

            MenuItem menuItemDelete=menu.add(0,0,0,"Delete as Favorite");

            menuItemDelete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item)
        {
            Toast.makeText(context, "" + getPos() + "Deleted!!", Toast.LENGTH_LONG).show();
            clickListener.removeFromFav(getPos());
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
        void removeFromFav(int pos);
    }
}
