package in.annexion.notapp;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , View.OnClickListener, View.OnTouchListener {
    View navHeader;
    TextView textView;
    ImageButton [] imageButtons;
    CircleImageView avatar;
    NavigationView navigationView;
    DrawerLayout drawer;
    SharedPreferences sharedPreferences;
    Context context;
    int [] ids= new int[30];
    String [] sids;
    String [] intentArray,titleArray,drawableReadArray,drawableUnreadArray;
    int [] unreads=new int[30];
    int [] reads=new int[30];

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public static String name;

    static List<String> selections;

    static boolean updateNav=true, updateIcons=true;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(!sharedPreferences.getBoolean("isLoggedIn",false)){
            Log.e("MainActivity","isLoggedIn: "+sharedPreferences.getBoolean("isLoggedIn", false));
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }
        else {

            if((new ConnectionDetector(getBaseContext())).isConnectingToInternet())
               sync();

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

            context = getBaseContext();

            setListeners();

            if (updateIcons)
                iconsUpdate();

            if (updateNav)
                navUpdate();


            if (checkPlayServices()) {
                registerGCM();
            }
        }
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

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void sync() {

        boolean updateClass,updateBranch,updateDPrefs,updateFName,updateLName,updateEMail,updateNumber,updateDOB,updatePassword;

        SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(""+ Environment.getExternalStorageDirectory()+"/Notapp/DB/notapp.db",null,null);
        db.enableWriteAheadLogging();

        Cursor cursor=new Cursor() {
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
        cursor=db.rawQuery("select * from syncStatus",null);

        if((cursor.moveToFirst()) ||cursor.getCount()>0)
        {
            updateFName = cursor.getInt(0) == 1;
            updateLName = cursor.getInt(1) == 1;
            updateEMail = cursor.getInt(2) == 1;
            updatePassword = cursor.getInt(3) == 1;
            updateNumber = cursor.getInt(4) == 1;
            updateDOB = cursor.getInt(5) == 1;
            updateClass = cursor.getInt(6) == 1;
            updateBranch = cursor.getInt(7) == 1;
            updateDPrefs = cursor.getInt(8) == 1;

            if (updateClass) {
                db.execSQL("update syncStatus set class=0");
                updateClass();
            }
            if (updateBranch) {
                db.execSQL("update syncStatus set branch=0");
                updateBranch();
            }
            if (updateDPrefs) {
                db.execSQL("update syncStatus set dprefs=0");
                updateDPrefs();
            }
            if (updateFName) {
                db.execSQL("update syncStatus set fname=0");
                updateFName();
            }
            if (updateLName) {
                db.execSQL("update syncStatus set lname=0");
                updateLName();
            }
            if (updateEMail) {
                db.execSQL("update syncStatus set email=0");
                updateEMail();
            }
            if (updateNumber) {
                db.execSQL("update syncStatus set number=0");
                updateNumber();
            }
            if (updateDOB) {
                db.execSQL("update syncStatus set dob=0");
                updateDOB();
            }
            if (updatePassword) {
                db.execSQL("update syncStatus set password=0");
                updatePassword();
            }
        }

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
        s+=sharedPreferences.getString("fname", "Your");
        s+=" ";
        s+=sharedPreferences.getString("lname", "Name");
        textView.setText(s);
        textView.setTypeface(roboto_light);


        textView=(TextView)navHeader.findViewById(R.id.textView_email);
        s=sharedPreferences.getString("PRN", "20__B__0__");
        textView.setText(s);



        File imgFile=new File(sharedPreferences.getString("avatarPath",""));

        if(imgFile.exists()){
            // Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            //avatar.setImageBitmap(myBitmap);
        }
    }

    private void iconsUpdate()
    {
        int setCount=0;
        LinearLayout linearLayout;
        Set<String> set = sharedPreferences.getStringSet("pref_depts", new Set<String>() {
            @Override
            public boolean add(String object) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends String> collection) {
                return false;
            }

            @Override
            public void clear() {

            }

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
        if(set!=null)
            selections=new ArrayList<>(set);
        for(int i=1;i<=30;i++)
        {
            boolean read=sharedPreferences.getBoolean(""+(i-1),false);
            Log.e("MainActivity","badge_"+read);
            if(!read)
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
        if(setCount==0)
            Snackbar.make(this.findViewById(R.id.coordinatorLayout_parentViewMA), "You have no Notice boards selected!\nGo to Settings to change your preferences." , Snackbar.LENGTH_LONG).setAction("Action", null).show();
        updateIcons=true;
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_parentView);
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
                    .setMessage("Are you sure you want to log out?")
                    .setIcon(R.drawable.ic_logout)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            Toast.makeText(MainActivity.this, "LoggedOut", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this,LoginActivity.class));
                            finish();
                            SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn",false);
                            editor.commit();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_parentView);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v)
    {
        Intent intent;
        SharedPreferences.Editor editor=sharedPreferences.edit();
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
            case R.id.imageButton_acses:
                editor.putBoolean(""+10,false);
                editor.commit();
                intent= new Intent(context,NoticesActivity.class);
                intent.putExtra("nb",intentArray[10]);
                intent.putExtra("title",titleArray[10]);
                startActivity(intent);
                break;
            case R.id.imageButton_sait:
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
                        String path = imageUri.getPath();
                        Toast.makeText(context,""+path,Toast.LENGTH_LONG).show();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        avatar.setImageBitmap(selectedImage);

                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("avatarPath",path);
                        editor.commit();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
        }

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


    public void updateClass()
    {
        String _class= sharedPreferences.getString("class", "b1");
        upload("class",_class);
    }
    public void updateBranch()
    {
        String _branch= sharedPreferences.getString("branch", "cse");
        upload("branch",_branch);
    }
    public void updateDPrefs()
    {
        Set<String> dprefs= sharedPreferences.getStringSet("dprefs", new HashSet<String>(Arrays.asList(new String[]{})));
        String _dprefs="";
        Iterator iterator=dprefs.iterator();
        for (int i=0;i<dprefs.size();i++) {
            _dprefs+=""+iterator.next();
            _dprefs+=",";
        }
        upload("dprefs",_dprefs);
    }
    public void updateFName()
    {
        String _fname= sharedPreferences.getString("fname", "");
        try {
            _fname= URLEncoder.encode(_fname,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        upload("fname",_fname);


    }
    public void updateLName()
    {
        String _lname= sharedPreferences.getString("lname", "");
        try {
            _lname= URLEncoder.encode(_lname,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        upload("lname",_lname);


    }
    public void updateEMail()
    {
        String _email= sharedPreferences.getString("email", "b1");
        try {
            _email= URLEncoder.encode(_email,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        upload("email",_email);
    }
    public void updateNumber()
    {
        String _number= sharedPreferences.getString("number", "b1");
        upload("phone",_number);

    }
    public void updatePassword()
    {
        String _password= sharedPreferences.getString("password", "");
        upload("password",_password);
    }
    public void updateDOB()
    {
        String _dob= sharedPreferences.getString("DOB", "");
        upload("DOB",_dob);

    }

    private void upload(String key,String value) {
        String URL="http://notapp.in/sync.php?PRN="+sharedPreferences.getString("PRN","")+"&"+key+"="+value;
        new Sync().execute(URL);
    }

    class Sync extends AsyncTask<String,Void,Void>
    {
        @Override
        protected Void doInBackground(String... params) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(params[0]);
            HttpResponse httpResponse = null;
            try {
                httpResponse = httpClient.execute(httpGet);
            } catch (IOException e) {
                Log.e("Sync:",""+e);
                e.printStackTrace();
            }
            return null;
        }
    }
}