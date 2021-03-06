package in.co.rubberduck.notapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import in.co.rubberduck.notapp.R;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener , View.OnTouchListener , View.OnFocusChangeListener
{
    AppCompatButton button_register;
    TextView textView_Login,textView_NotAMember;
    EditText editText_PRN,editText_Password,editText_PasswordConfirm;
    TextInputLayout textInput_PRN,textInput_Password,textInput_PasswordConfirm;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    AlertDialogManager alert;
    LinearLayout linearLayout_login_parentView;
    boolean done;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context=getBaseContext();

        alert=new AlertDialogManager();

        linearLayout_login_parentView=(LinearLayout)findViewById(R.id.login_parentView);
        textView_Login=(TextView)findViewById(R.id.textView_Login);
        textView_NotAMember=(TextView)findViewById(R.id.textView_NotAMember);
        editText_PRN=(EditText)findViewById(R.id.editText_PRN);
        editText_Password=(EditText)findViewById(R.id.editText_Password);
        editText_PasswordConfirm=(EditText)findViewById(R.id.editText_PasswordConfirm);
        textInput_PRN=(TextInputLayout)findViewById(R.id.textInput_PRN);
        textInput_Password=(TextInputLayout)findViewById(R.id.textInput_Password);
        textInput_PasswordConfirm=(TextInputLayout)findViewById(R.id.textInput_PasswordConfirm);

        textView_Login.setOnClickListener(this);
        editText_PRN.setOnFocusChangeListener(this);
        editText_Password.setOnFocusChangeListener(this);
        editText_PasswordConfirm.setOnFocusChangeListener(this);

        button_register=(AppCompatButton)findViewById(R.id.button_Register);
        button_register.setOnClickListener(this);
        button_register.setOnTouchListener(this);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        switch (v.getId())
        {
            case R.id.editText_PRN:
                onFocusPRN(hasFocus);
                break;
            case R.id.editText_Password:
                onFocusPassword(hasFocus);
                break;
            case R.id.editText_PasswordConfirm:
                onFocusConfirmPassword(hasFocus);
                break;
        }
    }

    private void onFocusPRN(boolean hasFocus)
    {
        String PRN = editText_PRN.getText().toString();
        if(!hasFocus)
        {
            if(isInvalid(PRN))
                editText_PRN.setError("Enter Valid PRN !");
        }
    }

    private void onFocusPassword(boolean hasFocus)
    {
        String PRN = editText_PRN.getText().toString();
        String Password=editText_Password.getText().toString();
        if(!hasFocus)
        {
            if(Password.equals("")&&!PRN.equals(""))
                editText_Password.setError("Cannot be blank!");
        }
    }
    private void onFocusConfirmPassword(boolean hasFocus)
    {
        String PRN = editText_PRN.getText().toString();
        String Password=editText_Password.getText().toString();
        String PasswordConfirm=editText_PasswordConfirm.getText().toString();
        if(!hasFocus)
        {
            if(PasswordConfirm.equals("")&&!PRN.equals("")&&!Password.equals(""))
                editText_PasswordConfirm.setError("Cannot be blank!!");
        }
    }

    private boolean isInvalid(String PRN) {
        if(PRN.length() != 10||PRN.charAt(0)!='2'||PRN.charAt(1)!='0'||!Character.isDigit(PRN.charAt(2))||!Character.isDigit(PRN.charAt(3))||!(PRN.charAt(4)=='B'||PRN.charAt(4)=='M')||!Character.isLetter(PRN.charAt(5))||!Character.isLetter(PRN.charAt(6))||!Character.isDigit(PRN.charAt(7))||!Character.isDigit(PRN.charAt(8))||!Character.isDigit(PRN.charAt(9)))
            return  true;
        else
            return false;
    }

    public class Authenticate extends AsyncTask<String,Void,Boolean> {
        ProgressDialog progressDialog;
        InputStream is = null;
        JSONObject jObj = null;
        String json = "";
        int consent;
        boolean result,isConnectingToInternet,hasActiveConnection;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setTitle("Registering");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

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
        protected Boolean doInBackground(String[] params) {
            String PRN = "";
            String Password = "";

            isConnectingToInternet=isConnectingToInternet();

            Log.e("ConnectionD", " is connecting"+isConnectingToInternet);

            hasActiveConnection=false;

            if(isConnectingToInternet)
            {
                hasActiveConnection = hasActiveConnection();
            }

            if(isConnectingToInternet)
            {
                hasActiveConnection = hasActiveConnection();
            }

            Log.e("ConnectionD", "has active " + hasActiveConnection);

            if(!hasActiveConnection) {
                return  result;
            }
            else {

                try {
                    PRN = URLEncoder.encode(params[0], "utf-8");
                    Password = URLEncoder.encode(params[1], "utf-8");
                } catch (UnsupportedEncodingException e) {
                    Log.e("Encoding gandesh", ":" + e);
                    e.printStackTrace();
                }
                try {
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    String url = "http://notapp.wce.ac.in/json/register.php?PRN=" + PRN + "&" + "password=" + Password + "";
                    HttpGet httpGet = new HttpGet(url);
                    Log.e("url register: ", url);
                    HttpResponse httpResponse = null;
                    httpResponse = httpClient.execute(httpGet);

                    HttpEntity httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();
                } catch (Exception e) {
                    Log.e("Authenticate Error: ", "" + e);
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
                } catch (JSONException e) {
                    Log.e("Authenticate", "Error parsing data " + e.toString());
                }

                Log.e("Consent : ", "" + consent);
            }
            return consent == 1;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();
            done=result;
            if (!result) {
                if(hasActiveConnection) {
                    Snackbar.make(findViewById(R.id.login_parentView), "Registration Failed! Please enter valid credentials.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    editText_PRN.setText("");
                    editText_PRN.requestFocus();
                    editText_Password.setText("");
                    editText_PasswordConfirm.setText("");
                }
                else {
                    alert.showAlertDialog(RegisterActivity.this, "Offline or Weak Connection!", "Connect to Internet and try again.", false);
                }
            }
            else {
                conclude();
            }
        }
    }

    void conclude() {
        Toast.makeText(getBaseContext(), "Registered Successfully!!", Toast.LENGTH_LONG).show();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("PRN", editText_PRN.getText().toString());
        editor.putString("pword", editText_Password.getText().toString());
        editor.putBoolean("isLoggedIn", true);
        editor.putBoolean("isFirstTime", true);
        editor.commit();

        startActivity(new Intent(getBaseContext(),MainActivity.class));
        finish();
        Log.e("SyncNotices", "done=" + done);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_Register:
                String PRN = editText_PRN.getText().toString();
                String Password = editText_Password.getText().toString();
                String PasswordConfirm = editText_PasswordConfirm.getText().toString();

                if (isInvalid(PRN))
                    editText_PRN.setError("Enter PRN!!");
                else if (Password.equals(""))
                    editText_Password.setError("Cannot be blank!!");
                else if (PasswordConfirm.equals(""))
                    editText_PasswordConfirm.setError("Cannot be blank!!");
                else {
                    if (!Password.equals(PasswordConfirm)) {
                        editText_PasswordConfirm.setText("");
                        editText_PasswordConfirm.setError("Password doesn't match.!!");
                    } else
                        new Authenticate().execute(PRN, Password);
                }
                break;
            case R.id.textView_Login:
                Log.e("Register", "Login Clicked");
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
                finish();
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            v.setBackgroundColor(getResources().getColor(R.color.grayDark));
        else
            v.setBackgroundColor(getResources().getColor(R.color.white));
        return false;
    }
}
