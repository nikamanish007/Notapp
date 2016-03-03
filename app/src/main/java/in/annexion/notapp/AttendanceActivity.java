package in.annexion.notapp;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;

import java.util.ArrayList;

public class AttendanceActivity extends AppCompatActivity implements CourseAdapter.ClickListener
{
    ArrayList<CourseInfo> courseList;
    ArcProgress arcProgress;
    TextView textView_CourseCodeShowing;
    CourseInfo courseInfo;
    RecyclerView recyclerView_Cources;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    int lastSelection=0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        arcProgress=(ArcProgress)findViewById(R.id.arc_progress);
        textView_CourseCodeShowing=(TextView)findViewById(R.id.textView_CourseCodeShowing);

        courseInfo=new CourseInfo();
        courseList=new ArrayList<CourseInfo>();
        for(int i=0;i<5;i++)
            courseList.add(courseInfo);

        recyclerView_Cources=(RecyclerView)findViewById(R.id.recyclerView_Courses);
        recyclerView_Cources.setHasFixedSize(true);

        layoutManager=new LinearLayoutManager(this);
        recyclerView_Cources.setLayoutManager(layoutManager);

        adapter= new CourseAdapter(courseList,getBaseContext(),this);
        recyclerView_Cources.setAdapter(adapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void itemClicked(View view, int position)
    {
        recyclerView_Cources.getChildAt(lastSelection).setBackgroundColor(getResources().getColor(R.color.white));
        view.setBackgroundColor(getResources().getColor(R.color.colorAccentLight));

        //((TextView)recyclerView_Cources.getChildAt(lastSelection).findViewById(R.id.textView_CourseCode)).setTextColor(getResources().getColor(R.color.black));
        //((TextView)view.findViewById(R.id.textView_CourseCode)).setTextColor(getResources().getColor(R.color.colorAccent));
        //((TextView)recyclerView_Cources.getChildAt(lastSelection).findViewById(R.id.textView_CourseTitle)).setTextColor(getResources().getColor(R.color.black));
        //((TextView)view.findViewById(R.id.textView_CourseTitle)).setTextColor(getResources().getColor(R.color.colorAccent));

        String courseCodeSelected=((TextView) view.findViewById(R.id.textView_CourseCode)).getText().toString();

        textView_CourseCodeShowing.setText(courseCodeSelected);

        int i=(int) (Math.random()*100);

        //arcProgress.setProgress(i);

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
}
