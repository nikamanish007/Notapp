package in.annexion.notapp;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sarang on 1/1/16.
 */
public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder>
{
    private ArrayList<CourseInfo> courseList;
    private ClickListener clickListener;
    private Context context;

    public CourseAdapter(ArrayList courseList, Context context , CourseAdapter.ClickListener clickListener)
    {
        this.courseList=courseList;
        this.context=context;
        this.clickListener=clickListener;
    }

    @Override
    public int getItemCount()
    {
       return courseList.size();
    }

    @Override
    public CourseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.course_item, viewGroup, false);
        return new CourseAdapter.CourseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CourseViewHolder courseViewHolder, int i)
    {
        CourseInfo courseInfo = courseList.get(i);
        courseViewHolder.textView_CourseCode.setText(courseInfo.courseCode);
        courseViewHolder.textView_CourseTitle.setText(courseInfo.courseTitle);
        courseViewHolder.textView_percentage.setText(""+courseInfo.percentage);
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener
    {
        TextView textView_CourseCode;
        TextView textView_CourseTitle;
        TextView textView_percentage;
        public CourseViewHolder(View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);

            Typeface robotoCondensed_light = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoCondensed-Light.ttf");


            textView_CourseCode=(TextView)itemView.findViewById(R.id.textView_CourseCode);
            textView_CourseTitle=(TextView)itemView.findViewById(R.id.textView_CourseTitle);
            textView_percentage=(TextView)itemView.findViewById(R.id.textView_percentage);

            textView_CourseTitle.setTypeface(robotoCondensed_light);
        }

        @Override
        public void onClick(View v)
        {
            clickListener.itemClicked(v,getAdapterPosition());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }
    }

    public interface ClickListener
    {
        void itemClicked(View view,int position);
        void itemTouched(View view,MotionEvent event);
    }
}
