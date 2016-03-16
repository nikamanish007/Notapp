package in.annexion.notapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import me.relex.circleindicator.CircleIndicator;

public class RegisterActivity extends AppCompatActivity
{


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private MyViewPager viewPager;
    AlertDialogManager alert=new AlertDialogManager();
    ConnectionDetector cd;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        cd=new ConnectionDetector(getApplicationContext());

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        viewPager = (MyViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(mSectionsPagerAdapter);

        CircleIndicator circleIndicator=(CircleIndicator)findViewById(R.id.circular_indicator);
        circleIndicator.setViewPager(viewPager);

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Registration")
                .setMessage("Are you sure you want to cancel Registration? \n(All your progessed will be rolled back)")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(RegisterActivity.this, "LoggedOut", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getBaseContext(),LoginActivity.class));
                        finish();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        public SectionsPagerAdapter(FragmentManager fm) {super(fm);}
        @Override
        public Fragment getItem(int position)
        {
            Fragment fragment=null;
            switch (position)
            {
                case 0:
                    fragment= FirstFragment.newInstance(position + 1);
                    break;
                case 1:
                    fragment= SecondFragment.newInstance(position + 1);
                    break;
                case 2:
                    fragment= ThirdFragment.newInstance(position + 1);
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount()
        {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
    public static class FirstFragment extends Fragment
    {
        String PRN="", Password="", ConfirmPassword="";
        AppCompatButton button_Register;
        private static final String ARG_SECTION_NUMBER = "section_number";

        public static FirstFragment newInstance(int sectionNumber)
        {
            FirstFragment fragment = new FirstFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public FirstFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_register_1, container, false);
            final EditText editText_PRN , editText_Password , editText_ConfirmPassword;

            editText_PRN=(EditText)rootView.findViewById(R.id.editText_PRN);
            editText_Password=(EditText)rootView.findViewById(R.id.editText_Password); editText_Password.setEnabled(false);
            editText_ConfirmPassword=(EditText)rootView.findViewById(R.id.editText_ConfirmPassword); editText_ConfirmPassword.setEnabled(false);
            button_Register=(AppCompatButton)rootView.findViewById(R.id.button_register);

            button_Register.setEnabled(false);

            editText_PRN.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        PRN = editText_PRN.getText().toString();
                        if (isInvalid(PRN)) {
                            editText_PRN.setError("Enter valid PRN..!");
                            editText_PRN.requestFocus();
                        } else
                            editText_Password.setEnabled(true);
                    }
                    return false;
                }

                private boolean isInvalid(String PRN) {
                    if (PRN.length() != 10 || PRN.charAt(0) != '2' || PRN.charAt(1) != '0' || !Character.isDigit(PRN.charAt(2)) || !Character.isDigit(PRN.charAt(3)) || !(PRN.charAt(4) == 'B' || PRN.charAt(4) == 'M') || !Character.isLetter(PRN.charAt(5)) || !Character.isLetter(PRN.charAt(6)) || !Character.isDigit(PRN.charAt(7)) || !Character.isDigit(PRN.charAt(8)) || !Character.isDigit(PRN.charAt(9)))
                        return true;
                    else
                        return false;
                }
            });
            editText_Password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        Password = editText_Password.getText().toString();
                        if (PRN.length() == 0)
                            editText_Password.setError("Can't be blank..!");
                        else {
                            editText_ConfirmPassword.setEnabled(true);
                            button_Register.setEnabled(true);
                            button_Register.setTextColor(getResources().getColor(R.color.colorAccent));
                        }
                    }
                    return false;
                }
            });

            editText_ConfirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        Password = editText_Password.getText().toString();
                        ConfirmPassword = editText_ConfirmPassword.getText().toString();
                        if (!Password.equals(ConfirmPassword)) {
                            editText_ConfirmPassword.setError("Password does not match.");
                            editText_ConfirmPassword.requestFocus();
                        } else if (PRN.length() != 10) {
                            editText_PRN.setError("Please Enter Valid PRN");
                            editText_PRN.requestFocus();
                        }
                    }
                    return false;
                }
            });


            button_Register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Authenticate().execute(PRN, Password);
                }
            });

            return rootView;
        }

        public class Authenticate extends AsyncTask<String,Void,Boolean>
        {
            ProgressDialog progressDialog;
            InputStream is = null;
            JSONObject jObj = null;
            String json = "";
            int consent;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog=new ProgressDialog(getActivity());
                progressDialog.setTitle("Registering");
                progressDialog.setMessage("Please wait...");
                progressDialog.show();

            }

            @Override
            protected Boolean doInBackground(String[] params) {
                boolean result=false;
                String PRN= "";
                String Password= "";
                try {
                    PRN = URLEncoder.encode(params[0], "utf-8");
                    Password=URLEncoder.encode(params[1],"utf-8");
                } catch (UnsupportedEncodingException e) {
                    Log.e("Encoding gandesh",":"+e);
                    e.printStackTrace();
                }
                try {
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    String url="http://notapp.in/json/register.php?PRN="+PRN+"&"+"password="+Password+"";
                    HttpGet httpGet = new HttpGet(url);
                    Log.e("url register: ",url);
                    HttpResponse httpResponse = null;
                    httpResponse = httpClient.execute(httpGet);

                    HttpEntity httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();
                }catch (Exception e) {
                    Log.e("Authenticate Error: ",""+e);
                }

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    is.close();
                    json = sb.toString();
                } catch (Exception e)
                {
                    Log.e("Authenticate", "Error converting result " + e.toString());
                }

                publishProgress();
                // try parse the string to a JSON object
                try {
                    jObj = new JSONObject(json);
                    JSONArray jsonArray= jObj.getJSONArray("result");
                    JSONObject j = jsonArray.getJSONObject(0);
                    consent=j.getInt("consent");
                } catch (JSONException e) {
                    Log.e("Authenticate", "Error parsing data " + e.toString());
                }

                Log.e("Consent : ",""+consent);
                if(consent==1)
                    result=true;
                return result;
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                progressDialog.dismiss();
                boolean isConnected=new ConnectionDetector(getActivity().getBaseContext()).isConnectingToInternet();
                if(!isConnected) {
                    AlertDialogManager alertDialog=new AlertDialogManager();
                    alertDialog.showAlertDialog(getActivity(), "No Internet Connection", "Please make sure you are connected.", isConnected);
                }
                else {
                    if (result) {
                        Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout_parentView), "Registered Succesfully..!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        ViewPager vp = (ViewPager) getActivity().findViewById(R.id.viewPager);
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("PRN", ((EditText) getActivity().findViewById(R.id.editText_PRN)).getText().toString());
                        editor.putString("pword", ((EditText) getActivity().findViewById(R.id.editText_Password)).getText().toString());
                        editor.commit();
                        vp.setCurrentItem(1);
                    } else {
                        Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout_parentView), "Invalid PRN..!!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        ((EditText) getActivity().findViewById(R.id.editText_PRN)).setText("");
                        ((EditText) getActivity().findViewById(R.id.editText_PRN)).requestFocus();
                    }
                }
            }
        }
    }
    public static class SecondFragment extends Fragment implements View.OnTouchListener
    {
        private static final String ARG_SECTION_NUMBER = "section_number";
        AppCompatButton button_Update;
        String[] firstName = new String[1];
        String[] lastName = new String[1];
        String[] email = new String[1];
        String[] phone = new String[1];
        String[] dob=new String[1];
        CircleImageView avatar;


        public static SecondFragment newInstance(int sectionNumber)
        {
            SecondFragment fragment = new SecondFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public SecondFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            final View rootView = inflater.inflate(R.layout.fragment_register_2, container, false);
            final EditText [] editText_FirstName={(EditText)rootView.findViewById(R.id.editText_FirstName)};
            final EditText [] editText_LastName={(EditText)rootView.findViewById(R.id.editText_LastName)};
            final EditText [] editText_EMail={(EditText)rootView.findViewById(R.id.editText_Email)};
            final EditText [] editText_Phone={(EditText)rootView.findViewById(R.id.editText_Phone)};
            final AppCompatButton [] button_DOB={(AppCompatButton)rootView.findViewById(R.id.button_DOB)};

            avatar=(CircleImageView)rootView.findViewById(R.id.avatar);
            button_Update=(AppCompatButton)rootView.findViewById(R.id.button_Update);

            editText_FirstName[0].setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        firstName[0] = editText_FirstName[0].getText().toString();
                        if (firstName[0].length() != 0) {
                            editText_LastName[0].setEnabled(true);
                        } else {
                            editText_FirstName[0].setError("Cannot be blank.");
                            editText_FirstName[0].requestFocus();
                        }
                    }
                    return false;
                }
            });
            editText_LastName[0].setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        lastName[0] = editText_LastName[0].getText().toString();
                        if (lastName[0].length() != 0) {
                            editText_EMail[0].setEnabled(true);
                        } else {
                            editText_LastName[0].setError("Cannot be blank.");
                            editText_LastName[0].requestFocus();
                        }
                    }
                    return false;
                }
            });
            editText_EMail[0].setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        email[0] = editText_EMail[0].getText().toString();
                        if (email[0].length() != 0) {
                            editText_Phone[0].setEnabled(true);
                        } else {
                            editText_EMail[0].setError("Cannot be blank.");
                            editText_EMail[0].requestFocus();
                        }
                    }
                    return false;
                }
            });

            editText_Phone[0].setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        phone[0] = editText_Phone[0].getText().toString();
                        if (phone[0].length() != 0) {
                            Log.e("phone[0]", ":" + phone[0]);
                        } else {
                            editText_Phone[0].setError("Cannot be blank.");
                            editText_Phone[0].requestFocus();
                        }
                    }
                    return false;
                }
            });
            button_DOB[0].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialogFragment picker = new DatePickerDialogFragment(rootView);
                    picker.show(getFragmentManager(), "datePicker");
                }
            });


            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 1);
                }
            });

            button_Update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyViewPager viewPager=(MyViewPager)getActivity().findViewById(R.id.viewPager);
                    viewPager.setCurrentItem(2);
                }
            });

            return rootView;
        }

        @Override
        public void onActivityResult(int requestCode,int resultCode, Intent imageReturnedIntent) {
            super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
            SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getActivity());
            switch(requestCode) {
                case 1:
                    if(resultCode == RESULT_OK){
                        try {
                            final Uri imageUri = imageReturnedIntent.getData();
                            String path = imageUri.getPath();
                            final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                            avatar.setImageBitmap(selectedImage);

                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("avatarPath", path);
                            editor.commit();

                            AppCompatButton button_DOB=(AppCompatButton)getActivity().findViewById(R.id.button_DOB);
                            button_DOB.setText(sharedPreferences.getString("dob",""));

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
            }
        }

        @Override
        public void onDestroy() {

            super.onDestroy();

            SharedPreferences  sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getActivity());
            final SharedPreferences.Editor editor=sharedPreferences.edit();
            dob[0]=sharedPreferences.getString("dob","DOBgandla");

            new SendProfile().execute(sharedPreferences.getString("PRN", ""), firstName[0], lastName[0], email[0], phone[0], dob[0]);
            editor.putString("fname", firstName[0]);
            editor.putString("lname", lastName[0]);
            editor.putString("email", email[0]);
            editor.putString("phone", phone[0]);
            editor.commit();
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if(v.getId()==R.id.avatar)
            {
                if (event.getAction()==MotionEvent.ACTION_DOWN) {
                    avatar.setBorderWidth(5);
                }
                else {
                    avatar.setBorderWidth(1);
                }
            }
            else if(v.getId()==R.id.button_DOB)
            {
                AppCompatButton button_DOB=(AppCompatButton)getActivity().findViewById(R.id.button_DOB);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    button_DOB.setBackgroundColor(getResources().getColor(R.color.grayDark));
                }
                else {
                    button_DOB.setBackgroundColor(getResources().getColor(R.color.transperent));
                }
            }

            return false;
        }

        private class SendProfile extends AsyncTask <String, Void ,Void>{
            @Override
            protected Void doInBackground(String... params) {

                try {
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    params[0]= URLEncoder.encode(params[0],"utf-8");
                    params[1]= URLEncoder.encode(params[1],"utf-8");
                    params[2]= URLEncoder.encode(params[2],"utf-8");
                    params[3]= URLEncoder.encode(params[3],"utf-8");
                    params[4]= URLEncoder.encode(params[4],"utf-8");
                    params[5]= URLEncoder.encode(params[5],"utf-8");

                    String url="http://notapp.in/json/upProf.php?PRN="+params[0]+"&fname="+params[1]+"&lname="+params[2]+"&email="+params[3]+"&phone="+params[4]+"&dob="+params[5];
                    HttpGet httpGet = new HttpGet(url);

                    Log.e("url sendProf",url);

                    HttpResponse httpResponse = null;
                    httpResponse = httpClient.execute(httpGet);

                }catch(Exception e){
                    Log.e("SendProfError: ",""+e);
                }
                return null;
            }
        }
    }
    public static class ThirdFragment extends Fragment
    {
        private static final String ARG_SECTION_NUMBER = "section_number";
        Set<String> dept_prefs;
        String _class="", _branch="",_dept_prefs="";
        AppCompatButton button_Done;

        public static ThirdFragment newInstance(int sectionNumber)
        {
            ThirdFragment fragment = new ThirdFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public ThirdFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            final View rootView = inflater.inflate(R.layout.fragment_register_3, container, false);

            button_Done=(AppCompatButton)rootView.findViewById(R.id.button_Done);
            button_Done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity().getBaseContext(), MainActivity.class));
                    getActivity().finish();

                    SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor=sharedPreferences.edit();

                    editor.putBoolean("isLoggedIn", true);
                    editor.commit();
                }
            });

            return rootView;
        }


        @Override
        public void onDestroy() {
            super.onDestroy();
            SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor=sharedPreferences.edit();

            _class=sharedPreferences.getString("c_name","b1");
            _branch=sharedPreferences.getString("b_name", "cse");
            dept_prefs=sharedPreferences.getStringSet("prefs", new HashSet<String>(Arrays.asList(new String[]{})));
            Iterator iterator=dept_prefs.iterator();
            for (int i=0;i<dept_prefs.size();i++) {
                _dept_prefs+=""+iterator.next();
                _dept_prefs+=",";
            }

            new SendCLass().execute(sharedPreferences.getString("PRN",""),_class,_branch,_dept_prefs);

            editor.putBoolean("isLoggedIn",true);
            editor.commit();

        }

        private class SendCLass extends AsyncTask <String, Void ,Void>{
            @Override
            protected Void doInBackground(String... params) {

                try {

                    params[0]= URLEncoder.encode(params[0],"utf-8");
                    params[1]= URLEncoder.encode(params[1],"utf-8");
                    params[2]= URLEncoder.encode(params[2],"utf-8");
                    params[3]= URLEncoder.encode(params[3],"utf-8");

                    DefaultHttpClient httpClient = new DefaultHttpClient();

                    String url="http://notapp.in/json/getPrefs.php?PRN="+params[0]+"&class="+params[1]+"&branch="+params[2]+"&dprefs="+params[3];
                    HttpGet httpGet = new HttpGet(url);
                    Log.e("SendClass:",url);

                    HttpResponse httpResponse = null;
                    httpResponse = httpClient.execute(httpGet);

                }catch(Exception e){
                    Log.e("SendProfError: ",""+e);
                }
                return null;
            }
        }
    }
}
