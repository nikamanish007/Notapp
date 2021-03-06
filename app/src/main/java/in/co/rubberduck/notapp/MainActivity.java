package in.co.rubberduck.notapp;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , View.OnClickListener, View.OnTouchListener{
    View navHeader;
    TextView textView;
    ImageButton [] imageButtons;
    CircleImageView avatar;
    NavigationView navigationView;
    DrawerLayout drawer;
    SharedPreferences sharedPreferences;
    Context context;
    SharedPreferences.Editor editor;
    static SwipeRefreshLayout swipeRefreshLayout_main;


    int [] ids= new int[30];
    int [] unreads=new int[30];
    int [] reads=new int[30];

    String [] sids,intentArray,titleArray,drawableReadArray,drawableUnreadArray;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public static String name;

    static List<String> selections;
    static boolean synced=false;

    static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(!sharedPreferences.getBoolean("isLoggedIn",false)){
            Log.e("MainActivity","isLoggedIn: "+sharedPreferences.getBoolean("isLoggedIn", false));
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }
        else {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            swipeRefreshLayout_main=(SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout_main);

            editor = sharedPreferences.edit();
            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    // checking for type intent filter
                    if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                        Log.e("MainActivity","Refreshed on Broadcast.");
                        refresh();
                    }
                }
            };

            context = getBaseContext();
            activity=MainActivity.this;
            if(sharedPreferences.getBoolean("isFirstTime",false))
                InitialBusiness.doBusiness(activity);

            if(!synced)
                new SyncPrefs().execute(context);

            swipeRefreshLayout_main.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    new SyncNotices(context).execute();
                }
            });

            drawer = (DrawerLayout) findViewById(R.id.drawer_layout_parentView);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

            drawer.setDrawerListener(toggle);
            toggle.syncState();

            navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            navHeader = navigationView.getHeaderView(0);

            avatar = (CircleImageView) navHeader.findViewById(R.id.avatar);
            avatar.setOnClickListener(this);
            avatar.setOnTouchListener(this);

            setListeners();
            iconsUpdate();
            navUpdate();

            if (checkPlayServices()) {
                registerGCM();
            }
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void refresh() {
        startActivity(new Intent(context, MainActivity.class));
        finish();
    }

    private void registerGCM() {
        Intent intent = new Intent(this, GcmIntentService.class);
        intent.putExtra("key", "register");
        startService(intent);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000)
                        .show();
            } else {
                Log.e("GCM", "This device is not supported. Google Play Services not installed!");
                Toast.makeText(getApplicationContext(), "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        iconsUpdate();


        if(sharedPreferences.getBoolean("isFirstTime",false))
            InitialBusiness.doBusiness(activity);

        if(!synced)
            new SyncPrefs().execute(context);
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationManager notificationManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void setListeners()
    {
        sids=getResources().getStringArray(R.array.sids);
        drawableReadArray=getResources().getStringArray(R.array.drawablesRead);
        drawableUnreadArray=getResources().getStringArray(R.array.drawablesUnread);
        imageButtons= new ImageButton[30];
        for(int i=0;i<30;i++)
        {
            ids[i] = getResources().getIdentifier(sids[i], "id", getApplicationContext().getPackageName());
            reads[i] = getResources().getIdentifier(drawableReadArray[i],"drawable",getApplicationContext().getPackageName());
            unreads[i]= getResources().getIdentifier(drawableUnreadArray[i],"drawable",getApplicationContext().getPackageName());

            imageButtons[i]=(ImageButton) findViewById(ids[i]);
            imageButtons[i].setOnClickListener(this);
            imageButtons[i].setOnTouchListener(this);
        }
        intentArray=getResources().getStringArray(R.array.intent);
        titleArray=getResources().getStringArray(R.array.title);
    }

    public void navUpdate()
    {
        String s="";

        Typeface roboto_light = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoCondensed-Light.ttf");

        textView=(TextView)navHeader.findViewById(R.id.textView_Name);
        s+=sharedPreferences.getString("f_name", "Your");
        s+=" ";
        s+=sharedPreferences.getString("l_name", "Name");
        textView.setText(s);
        textView.setTypeface(roboto_light);

        textView=(TextView)navHeader.findViewById(R.id.textView_email);
        s=sharedPreferences.getString("PRN", "20__B__0__");
        textView.setText(s);

        File imgFile=new File(sharedPreferences.getString("avatarPath",""));
        Log.e("MainActivity"," imgFile "+imgFile);

        if(imgFile.exists()){
             try {
                 Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                 avatar.setImageBitmap(myBitmap);
                 Log.e("MainActivity"," avatar Image set.");
             }
             catch (OutOfMemoryError e) {
                 Toast.makeText(context,"Avatar Image too large.",Toast.LENGTH_SHORT).show();
                 Log.e("MainActivity"," myBitmap null");
             }
            Log.e("MainActivity"," avatar Image set.");
        }
    }

    private void iconsUpdate()
    {
        int setCount=0;
        LinearLayout linearLayout;
        boolean read;
        Set<String> set = sharedPreferences.getStringSet("prefs", new Set<String>() {
            @Override
            public boolean add(String object) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends String> collection) {
                return false;
            }

            @Override
            public void clear() {}

            @Override
            public boolean contains(Object object) {
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> collection) {
                return false;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @NonNull
            @Override
            public Iterator<String> iterator() {
                return null;
            }

            @Override
            public boolean remove(Object object) {
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> collection) {
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> collection) {
                return false;
            }

            @Override
            public int size() {
                return 0;
            }

            @NonNull
            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @NonNull
            @Override
            public <T> T[] toArray(T[] array) {
                return null;
            }
        });
        selections=new ArrayList<>(set);
        CardView cardView_basicSciences=(CardView)findViewById(R.id.cardView_basicSciences);
        CardView cardView_misc=(CardView)findViewById(R.id.cardView_misc);
        CardView cardView_dclubs=(CardView)findViewById(R.id.cardView_dclubs);
        CardView cardView_ndclubs=(CardView)findViewById(R.id.cardView_ndclubs);

        for(int i=1;i<=30;i++)
        {
            read=sharedPreferences.getBoolean(""+(i-1),false);
            if (!read)
                imageButtons[i-1].setBackgroundResource(reads[i-1]);
            else
                imageButtons[i-1].setBackgroundResource(unreads[i-1]);

            if(!selections.contains(""+i))
            {
                linearLayout=(LinearLayout)imageButtons[i-1].getParent();
                ((ViewGroup)imageButtons[i-1].getParent()).removeView(imageButtons[i - 1]);
                linearLayout.addView(imageButtons[i - 1], linearLayout.getChildCount());

                imageButtons[i-1].setImageResource(R.color.foreground);
                imageButtons[i-1].setEnabled(false);
            }
            else
            {
                setCount++;
                imageButtons[i-1].setImageResource(R.color.transperent);
                imageButtons[i-1].setEnabled(true);
            }
        }

        //basicSciences
        if (!selections.contains("7")&&!selections.contains("8")&&!selections.contains("9")&&!selections.contains("10")) {
            cardView_basicSciences.setVisibility(View.GONE);
            Log.e("MainActivity","gone basicSciences");
        }
        else {
            cardView_basicSciences.setVisibility(View.VISIBLE);
            Log.e("MainActivity","visible basicSciences");
        }
        //dclubs
        if (!selections.contains("7")&&!selections.contains("8")&&!selections.contains("9")&&!selections.contains("10")&&!selections.contains("11")&&!selections.contains("12")) {
            cardView_dclubs.setVisibility(View.GONE);
            Log.e("MainActivity","gone dclubs");
        }
        else {
            cardView_dclubs.setVisibility(View.VISIBLE);
            Log.e("MainActivity","visible dclubs");
        }
        //ndclubs
        if (!selections.contains("13")&&!selections.contains("14")&&!selections.contains("15")&&!selections.contains("16")&&!selections.contains("17")) {
            cardView_ndclubs.setVisibility(View.GONE);
            Log.e("MainActivity","gone ndclubs");
        }
        else {
            cardView_ndclubs.setVisibility(View.VISIBLE);
            Log.e("MainActivity","visible ndclubs");
        }
        //misc
        if (!selections.contains("25")&&!selections.contains("26")&&!selections.contains("27")&&!selections.contains("28")&&!selections.contains("29")&&!selections.contains("30")) {
            cardView_misc.setVisibility(View.GONE);
            Log.e("MainActivity","gone misc");
        }
        else {
            cardView_misc.setVisibility(View.VISIBLE);
            Log.e("MainActivity","visible misc");
        }

        if(setCount==0)
            Snackbar.make(this.findViewById(R.id.coordinatorLayout_parentViewMA), "You have no Notice boards selected!\nGo to Settings to change your preferences." , Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    @Override
    public void onBackPressed()
    {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout_parentView);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorites) {
            startActivity(new Intent(this,FavouritesActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        Intent intent;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.edit_profile)
        {
            intent=new Intent(MainActivity.this,SettingsActivity.class);
            intent.putExtra("optionSelected","editProfile");
            startActivity(intent);
        }
        else if (id == R.id.attendance)
        {
            intent=new Intent(MainActivity.this,AttendanceActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.settings)
        {
            intent=new Intent(MainActivity.this,SettingsActivity.class);
            intent.putExtra("optionSelected","settings");
            startActivity(intent);
        }
        else if (id == R.id.help)
        {
            intent=new Intent(MainActivity.this,HelpActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.about)
        {
            intent=new Intent(MainActivity.this,AboutUsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.logout)
        {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to log out?\nAll your data would be deleted.")
                    .setIcon(R.drawable.ic_logout)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            new Logout().execute(MainActivity.this,sharedPreferences.getString("PRN",""));
                            Log.e("MainActivity","Logout Called");
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v)
    {
        Intent intent;
        int i=v.getId();
        switch (i)
        {
            case R.id.avatar:
                intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
                break;

            case R.id.imageButton_it:
                editor.putBoolean(""+0,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb", intentArray[0]);
                intent.putExtra("title", titleArray[0]);
                startActivity(intent);
                break;
            case R.id.imageButton_cse:
                editor.putBoolean(""+1,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[1]);
                intent.putExtra("title",titleArray[1]);
                startActivity(intent);
                break;
            case R.id.imageButton_eln:
                editor.putBoolean(""+2,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[2]);
                intent.putExtra("title",titleArray[2]);
                startActivity(intent);
                break;
            case R.id.imageButton_ele:
                editor.putBoolean(""+3,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[3]);
                intent.putExtra("title",titleArray[3]);
                startActivity(intent);
                break;
            case R.id.imageButton_civ:
                editor.putBoolean(""+4,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[4]);
                intent.putExtra("title",titleArray[4]);
                startActivity(intent);
                break;
            case R.id.imageButton_mech:
                editor.putBoolean(""+5,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[5]);
                intent.putExtra("title",titleArray[5]);
                startActivity(intent);
                break;
            case R.id.imageButton_phy:
                editor.putBoolean(""+6,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[6]);
                intent.putExtra("title",titleArray[6]);
                startActivity(intent);
                break;
            case R.id.imageButton_chem:
                editor.putBoolean(""+7,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[7]);
                intent.putExtra("title",titleArray[7]);
                startActivity(intent);
                break;
            case R.id.imageButton_math:
                editor.putBoolean(""+8,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[8]);
                intent.putExtra("title",titleArray[8]);
                startActivity(intent);
                break;
            case R.id.imageButton_bio:
                editor.putBoolean(""+9,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[9]);
                intent.putExtra("title",titleArray[9]);
                startActivity(intent);
                break;
            case R.id.imageButton_sait:
                editor.putBoolean(""+10,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[10]);
                intent.putExtra("title",titleArray[10]);
                startActivity(intent);
                break;
            case R.id.imageButton_acses:
                editor.putBoolean(""+11,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[11]);
                intent.putExtra("title",titleArray[11]);
                startActivity(intent);
                break;
            case R.id.imageButton_elesa:
                editor.putBoolean(""+12,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[12]);
                intent.putExtra("title",titleArray[12]);
                startActivity(intent);
                break;
            case R.id.imageButton_eesa:
                editor.putBoolean(""+13,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[13]);
                intent.putExtra("title",titleArray[13]);
                startActivity(intent);
                break;
            case R.id.imageButton_cesa:
                editor.putBoolean(""+14,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[14]);
                intent.putExtra("title",titleArray[14]);
                startActivity(intent);
                break;
            case R.id.imageButton_mesa:
                editor.putBoolean(""+15,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[15]);
                intent.putExtra("title",titleArray[15]);
                startActivity(intent);
                break;
            case R.id.imageButton_wlug:
                editor.putBoolean(""+16,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[16]);
                intent.putExtra("title",titleArray[16]);
                startActivity(intent);
                break;
            case R.id.imageButton_pace:
                editor.putBoolean(""+17,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[17]);
                intent.putExtra("title",titleArray[17]);
                startActivity(intent);
                break;
            case R.id.imageButton_rotr:
                editor.putBoolean(""+18,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[18]);
                intent.putExtra("title",titleArray[18]);
                startActivity(intent);
                break;
            case R.id.imageButton_softa:
                editor.putBoolean(""+19,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[19]);
                intent.putExtra("title",titleArray[19]);
                startActivity(intent);
                break;
            case R.id.imageButton_artC:
                editor.putBoolean(""+20,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[20]);
                intent.putExtra("title",titleArray[20]);
                startActivity(intent);
                break;
            case R.id.imageButton_admin:
                editor.putBoolean(""+21,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[21]);
                intent.putExtra("title",titleArray[21]);
                startActivity(intent);
                break;
            case R.id.imageButton_exam:
                editor.putBoolean(""+22,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[22]);
                intent.putExtra("title",titleArray[22]);
                startActivity(intent);
                break;
            case R.id.imageButton_tpo:
                editor.putBoolean(""+23,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[23]);
                intent.putExtra("title",titleArray[23]);
                startActivity(intent);
                break;
            case R.id.imageButton_sports:
                editor.putBoolean(""+24,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[24]);
                intent.putExtra("title",titleArray[24]);
                startActivity(intent);
                break;
            case R.id.imageButton_schol:
                editor.putBoolean(""+25,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[25]);
                intent.putExtra("title",titleArray[25]);
                startActivity(intent);
                break;
            case R.id.imageButton_rector:
                editor.putBoolean(""+26,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[26]);
                intent.putExtra("title",titleArray[26]);
                startActivity(intent);
                break;
            case R.id.imageButton_lost_found:
                editor.putBoolean(""+27,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[27]);
                intent.putExtra("title",titleArray[27]);
                startActivity(intent);
                break;
            case R.id.imageButton_vision:
                editor.putBoolean(""+28,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[28]);
                intent.putExtra("title",titleArray[28]);
                startActivity(intent);
                break;
            case R.id.imageButton_library:
                editor.putBoolean(""+29,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[29]);
                intent.putExtra("title",titleArray[29]);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent imageReturnedIntent)
    {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    try {
                        final Uri imageUri = imageReturnedIntent.getData();
                        String path = getPath(imageUri);
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        avatar.setImageBitmap(selectedImage);
                        Log.e("MainActivity","path fetched"+path);
                        editor.putString("avatarPath",path);
                        editor.commit();
                    } catch (FileNotFoundException e) {
                        Toast.makeText(context,"File Not Found",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (OutOfMemoryError e) {
                        Toast.makeText(context,"File Too Large.",Toast.LENGTH_SHORT).show();
                    }
                }
        }

    }

    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor =getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null)
            return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        switch (v.getId())
        {
            case R.id.avatar:
                if (event.getAction()==MotionEvent.ACTION_DOWN)
                {
                    avatar.setBorderWidth(5);
                }
                else
                {
                    avatar.setBorderWidth(1);
                }
                break;

            default:
                ImageButton imageButton=(ImageButton)v;

                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    imageButton.setImageResource(R.color.foreground);
                }
                else
                {
                    imageButton.setImageResource(R.color.transperent);
                }
        }
        return false;
     }
}