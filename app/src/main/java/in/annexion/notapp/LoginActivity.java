package in.annexion.notapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    AppCompatButton button_login;
    TextView textView_Register;
    EditText editText_PRN,editText_Password;
    SharedPreferences sharedPreferences;
    ConnectionDetector cd;
    AlertDialogManager alert;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cd=new ConnectionDetector(getBaseContext());

        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        alert=new AlertDialogManager();

        button_login=(AppCompatButton)findViewById(R.id.button_Login);
        button_login.setOnClickListener(this);
        button_login.setOnTouchListener(this);

        textView_Register=(TextView)findViewById(R.id.textView_Register);
        textView_Register.setOnClickListener(this);
        textView_Register.setOnTouchListener(this);

        editText_PRN=(EditText)findViewById(R.id.editText_PRN);
        editText_Password=(EditText)findViewById(R.id.editText_Password);

        editText_PRN.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String PRN;
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
                if(PRN.length() != 10||PRN.charAt(0)!='2'||PRN.charAt(1)!='0'||!Character.isDigit(PRN.charAt(2))||!Character.isDigit(PRN.charAt(3))||!(PRN.charAt(4)=='B'||PRN.charAt(4)=='M')||!Character.isLetter(PRN.charAt(5))||!Character.isLetter(PRN.charAt(6))||!Character.isDigit(PRN.charAt(7))||!Character.isDigit(PRN.charAt(8))||!Character.isDigit(PRN.charAt(9)))
                    return  true;
                else
                    return false;
            }
        });
        editText_Password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String password;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    password = editText_PRN.getText().toString();
                    if (password.equals("")) {
                        editText_PRN.setError("Cannot be blank..!");
                        editText_PRN.requestFocus();
                    } else
                        editText_Password.setEnabled(true);
                }
                return false;
            }
        });

    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.button_Login:
                new AuthenticateLogin(LoginActivity.this).execute(editText_PRN.getText().toString(), editText_Password.getText().toString());
                break;

            case R.id.textView_Register:
                startActivity(new Intent(getBaseContext(), RegisterActivity.class));
                finish();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        switch (v.getId())
        {
            case R.id.button_Login:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    button_login.setBackgroundColor(getResources().getColor(R.color.grayDark));
                }
                else {
                    button_login.setBackgroundColor(getResources().getColor(R.color.white));
                }
                break;
            case R.id.textView_Register:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    textView_Register.setTextColor(getResources().getColor(R.color.grayDark));
                }
                else{
                    textView_Register.setTextColor(getResources().getColor(R.color.white));
                }
        }
        return false;
    }

    public class AuthenticateLogin extends AsyncTask<String,Void,Boolean>
    {
        ProgressDialog progressDialog;
        private InputStream is;
        private String json;
        private JSONObject jObj;
        private int consent;
        boolean result,isConnectingToInternet,hasActiveConnection;
        String fname, lname , email , phone , dob ,dprefs , c_name , d_name;
        Context context;
        SharedPreferences sharedPreferences;

        AuthenticateLogin(Context context) {
            this.context=context;
            result=false;
            isConnectingToInternet=false;
            hasActiveConnection=false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(context);
            progressDialog.setTitle("Logging In");
            progressDialog.setMessage("Please Wait");
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        public boolean isConnectingToInternet(){
            boolean res=false;
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                res=true;
            }
            return res;
        }

        public  boolean hasActiveConnection() {
            Boolean toPostExecute=false;
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://notapp.wce.ac.in").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(5000);
                urlc.connect();
                toPostExecute = (urlc.getResponseCode() == 204 || urlc.getResponseCode()==200);
                Log.e("CD", "ResponseCode: " + urlc.getResponseCode() + "  " + toPostExecute);
            } catch (IOException e) {
                Log.e("ConnectionD", "Error checking internet connection", e);
            }
            return toPostExecute;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            consent=0;

            isConnectingToInternet=isConnectingToInternet();

            Log.e("ConnectionD", " is connecting"+isConnectingToInternet);

            hasActiveConnection=false;

            if(isConnectingToInternet)
            {
                hasActiveConnection = hasActiveConnection();
            }

            Log.e("ConnectionD", "has active "+hasActiveConnection);

            if(!hasActiveConnection) {
                return  result;
            }
            else
            {
                try {
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet("http://notapp.wce.ac.in/json/login.php?PRN=" + params[0] + "&password=" + params[1] + "");
                    HttpResponse httpResponse = null;
                    httpResponse = httpClient.execute(httpGet);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();
                } catch (IOException ioe) {
                    Log.e("Login IO:", " ");
                }

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    json = sb.toString();
                } catch (Exception e) {
                    Log.e("Authenticate", "Error converting result " + e.toString());
                }

                publishProgress();
                // try parse the string to a JSON object
                try {
                    jObj = new JSONObject(json);
                    JSONArray jsonArray = jObj.getJSONArray("result");
                    JSONObject j = jsonArray.getJSONObject(0);
                    consent = j.getInt("consent");

                    if (consent == 1) {
                        fname = j.getString("f_name");
                        lname = j.getString("l_name");
                        email = j.getString("email");
                        phone = j.getString("phone");
                        dob = j.getString("dob");
                        dprefs=j.getString("dprefs");
                        c_name=j.getString("c_name");
                        d_name=j.getString("d_name");
                        /*if(dprefs.length()!=0)
                            dprefs=dprefs.substring(0,dprefs.length()-1);
                        else
                            dprefs="";*/
                        Log.e("Authenticate", "Dprefs:"+" "+dprefs );

                        Set<String> set=new HashSet<String>();
                        StringTokenizer stringTokenizer=new StringTokenizer(dprefs,",");
                        while (stringTokenizer.hasMoreTokens())
                        {
                            String s = stringTokenizer.nextToken();
                            Log.e("Authenticate", ""+s );
                            set.add(s);
                        }

                        String year, month, date;
                        year = dob.substring(0, 3);
                        month = dob.substring(5, 6);
                        date = dob.substring(8, 9);

                        dob = "" + Integer.parseInt(date) + "-" + "" + Integer.parseInt(month) + "-" + year;

                        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("f_name", fname);
                        editor.putString("l_name", lname);
                        editor.putString("email", email);
                        editor.putString("phone", phone);
                        editor.putString("dob", dob);
                        editor.putString("c_name", c_name);
                        editor.putString("d_name", d_name);
                        editor.putStringSet("prefs",set);
                        editor.commit();
                        result = true;
                    }

                } catch (JSONException e) {
                    Log.e("Authenticate", "Error parsing data " + e.toString());
                    hasActiveConnection=false;
                }
                return result;
            }
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if(aVoid) {
                EditText editText_PRN=(EditText)findViewById(R.id.editText_PRN);
                SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("PRN",editText_PRN.getText().toString());
                editor.putBoolean("isLoggedIn", true);
                editor.commit();
                Intent intent=new Intent(getBaseContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                finish();
            }
            else {
                if(hasActiveConnection) {
                    Snackbar.make(findViewById(R.id.login_parentView), "Login Failed! Please enter valid credentials.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                else {
                    alert.showAlertDialog(context, "Offline or Weak Connection!", "Connect to Internet and try again.", false);
                }
            }
        }
    }
}
