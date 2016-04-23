package in.annexion.notapp;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sarang on 1/1/16.
 */
public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>
{
    private ArrayList<NoticeInfo> noticeList;
    private Context context;
    public ClickListener clickListener;
    private SparseBooleanArray selectedItems;
    private int position;
    NoticeInfo noticeInfo;

    public NoticeAdapter(ArrayList<NoticeInfo> noticeList, Context context , ClickListener clickListener)
    {
        this.noticeList = noticeList;
        this.context=context;
        this.clickListener=clickListener;
        selectedItems=new SparseBooleanArray();
    }

    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            Log.e("NoticesAdapter", "pos="+pos+"get=true");
            selectedItems.delete(pos);
        }
        else {
            Log.e("NoticesAdapter", "pos="+pos+"get=false");
            selectedItems.put(pos, true);
        }
    }

    public boolean isSeleted(int position) {
        return selectedItems.get(position, false);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    @Override
    public int getItemCount()
    {
        return noticeList.size();
    }

    @Override
    public void onBindViewHolder(NoticeViewHolder noticeViewHolder, int i) {
        noticeInfo = noticeList.get(i);
        noticeViewHolder.textView_NoticeTitle.setText(noticeInfo.title);
        noticeViewHolder.textView_UploadedBy.setText(noticeInfo.uploadedBy);
        noticeViewHolder.textView_Date.setText(noticeInfo.uploadDate);
        noticeViewHolder.textView_nID.setText("" + noticeInfo.n_id);
        noticeViewHolder.textView_isFav.setText("" + noticeInfo.isFav);
        if (noticeInfo.isFav == 1) {
            noticeViewHolder.imageView_fav.setImageResource(R.drawable.ic_heart_pink);
            Log.e("NoticesAdapter", "heart set for "+noticeInfo.n_id);
        }
        else{
            noticeViewHolder.imageView_fav.setImageResource(R.color.transperent);
            Log.e("NoticesAdapter", "null set set for "+noticeInfo.n_id);
        }

        if(noticeInfo.isRead==0)
            noticeViewHolder.textView_NoticeTitle.setTextColor(context.getResources().getColor(R.color.colorAccent));
        else
            noticeViewHolder.textView_NoticeTitle.setTextColor(context.getResources().getColor(R.color.textPrimary));

        noticeViewHolder.setBackground(isSeleted(i));
    }

    @Override
    public NoticeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notice_item, viewGroup, false);
        return new NoticeViewHolder(itemView);
    }

    public class NoticeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, MenuItem.OnMenuItemClickListener, View.OnLongClickListener, View.OnTouchListener {
        TextView textView_NoticeTitle;
        TextView textView_UploadedBy;
        TextView textView_Date;
        TextView textView_nID;
        TextView textView_isFav;
        ImageView imageView_fav;

        public NoticeViewHolder(View itemView)
        {
            super(itemView);

            textView_NoticeTitle = (TextView) itemView.findViewById(R.id.textView_NoticeTitle);
            textView_UploadedBy = (TextView) itemView.findViewById(R.id.textView_UploadedBy);
            textView_Date = (TextView) itemView.findViewById(R.id.textView_Date);
            textView_nID=(TextView)itemView.findViewById(R.id.textView_nID);
            textView_isFav=(TextView)itemView.findViewById(R.id.textView_isFav);
            imageView_fav=(ImageView)itemView.findViewById(R.id.imageView_fav);

            Typeface roboto_light = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoCondensed-Light.ttf");
            Typeface roboto_bold = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");

            textView_NoticeTitle.setTypeface(roboto_bold);
            textView_UploadedBy.setTypeface(roboto_light);
            textView_Date.setTypeface(roboto_light);

            itemView.setOnClickListener(this);
            itemView.setOnTouchListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setBackground(boolean isSelected){
            if(isSelected)
                itemView.setBackgroundColor(context.getResources().getColor(R.color.colorAccentTransperent));
            else
                itemView.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        @Override
        public void onClick(View v)
        {
            clickListener.itemClicked(v, getAdapterPosition());
        }


        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId())
            {
                case 0:
                    Toast.makeText(context, "" + getPos() + " Deleted!!", Toast.LENGTH_LONG).show();
                    clickListener.delete(getPos());
                    break;
                case 1:
                    Toast.makeText(context, "" + getPos() + " Added to Favorites", Toast.LENGTH_LONG).show();
                    clickListener.addToFav(getPos());
                    break;
                case 2:
                    Toast.makeText(context, "" + getPos() + " Removed from Favorites", Toast.LENGTH_LONG).show();
                    clickListener.removeFromFav(getPos());
            }

            return false;
        }

        @Override
        public boolean onLongClick(View v) {
            int pos=getAdapterPosition();
            clickListener.itemLongPress(v,pos);
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

    public interface ClickListener
    {
        void itemClicked(View view,int position);
        void itemTouched(View view,MotionEvent motionEvent);
        void itemLongPress(View view,int pos);
        void delete(int pos);
        void addToFav(int pos);
        void removeFromFav(int pos);
    }
}
