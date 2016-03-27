package in.annexion.notapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class HelpActivity extends AppCompatActivity {

    ViewPager viewPager_Help;
    FragmentPagerAdapter pagerAdapter;
    Bundle args;
    static int[] imageIDs=new int[5];
    static ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageIDs[0]=R.drawable.it;
        imageIDs[1]=R.drawable.cse;
        imageIDs[2]=R.drawable.eln;
        imageIDs[3]=R.drawable.ele;
        imageIDs[4]=R.drawable.civ;

        args=new Bundle();
        viewPager_Help=(ViewPager)findViewById(R.id.viewPager_Help);
        pagerAdapter= new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager_Help.setAdapter(pagerAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment spf=null;
            switch (position)
            {
                case 0:
                    spf=new FirstSlidePageFragment();
                    break;
                case 1:
                    spf=new SecondSlidePageFragment();
                    break;
                case 2:
                    spf=new ThirdSlidePageFragment();
                    break;
                case 3:
                    spf=new ForthSlidePageFragment();
                    break;
                case 4:
                    spf=new FifthSlidePageFragment();
                    break;
            }
            return spf;
        }

        @Override
        public int getCount() {
            return 5;
        }
    }

    public static class FirstSlidePageFragment extends Fragment
    {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.layout_view_pager, container, false);
            Log.e("HelpActivity","First");
            imageView=(ImageView)rootView.findViewById(R.id.imageView_Help);
            imageView.setImageDrawable(getResources().getDrawable(imageIDs[0]));
            return rootView;
        }
    }
    public static class SecondSlidePageFragment extends Fragment
    {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.layout_view_pager, container, false);
            Log.e("HelpActivity","Second");
            imageView=(ImageView)rootView.findViewById(R.id.imageView_Help);
            imageView.setImageDrawable(getResources().getDrawable(imageIDs[1]));
            return rootView;
        }
    }
    public static class ThirdSlidePageFragment extends Fragment
    {
        Bundle args;
        ImageView imageView;
        int ID;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.layout_view_pager, container, false);
            Log.e("HelpActivity","Third");
            imageView=(ImageView)rootView.findViewById(R.id.imageView_Help);
            imageView.setImageDrawable(getResources().getDrawable(imageIDs[2]));
            return rootView;
        }
    }
    public static class ForthSlidePageFragment extends Fragment
    {
        Bundle args;
        ImageView imageView;
        int ID;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.layout_view_pager, container, false);
            Log.e("HelpActivity","Forth");
            imageView=(ImageView)rootView.findViewById(R.id.imageView_Help);
            imageView.setImageDrawable(getResources().getDrawable(imageIDs[3]));
            return rootView;
        }
    }
    public static class FifthSlidePageFragment extends Fragment
    {
        Bundle args;
        ImageView imageView;
        int ID;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.layout_view_pager, container, false);
            Log.e("HelpActivity","Fifth");
            imageView=(ImageView)rootView.findViewById(R.id.imageView_Help);
            imageView.setImageDrawable(getResources().getDrawable(imageIDs[4]));
            return rootView;
        }
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }
}