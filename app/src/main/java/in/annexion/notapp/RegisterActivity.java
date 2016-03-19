package in.annexion.notapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener , View.OnTouchListener , View.OnFocusChangeListener
{
    AppCompatButton button_register;
    EditText editText_PRN,editText_Password,editText_PasswordConfirm;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editText_PRN=(EditText)findViewById(R.id.editText_PRN);
        editText_Password=(EditText)findViewById(R.id.editText_Password);
        editText_PasswordConfirm=(EditText)findViewById(R.id.editText_PasswordConfirm);

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
            case R.id.editText_ConfirmPassword:
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setTitle("Registering");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

        }

        @Override
        protected Boolean doInBackground(String[] params) {
            String PRN = "";
            String Password = "";
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
            return consent == 1;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();

            if (result) {
                conclude();
            } else {
                Snackbar.make(findViewById(R.id.login_parentView), "Invalid PRN..!!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                editText_PRN.setText("");
                editText_PRN.requestFocus();
                editText_Password.setText("");
                editText_PasswordConfirm.setText("");
            }
        }
    }

    private void conclude()
    {
        Toast.makeText(getBaseContext(),"Registered Successfully!!",Toast.LENGTH_LONG).show();
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("PRN", editText_PRN.getText().toString());
        editor.putString("pword", editText_Password.getText().toString());
        editor.commit();

        Intent intent=new Intent(getBaseContext(),SettingsActivity.class);
        intent.putExtra("optionSelected", "editProfile");
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v)
    {
        String PRN = editText_PRN.getText().toString();
        String Password=editText_Password.getText().toString();
        String PasswordConfirm=editText_PasswordConfirm.getText().toString();
        if(v.getId()==R.id.button_Register)
        {
            if(isInvalid(PRN))
                editText_PRN.setError("Enter PRN!!");
            else if(Password.equals(""))
                editText_Password.setError("Cannot be blank!!");
            else if(PasswordConfirm.equals(""))
                editText_PasswordConfirm.setError("Cannot be blank!!");
            else
            {
                if(!Password.equals(PasswordConfirm))
                {
                    editText_PasswordConfirm.setText("");
                    editText_PasswordConfirm.setError("Password doesn't match.!!");
                }
                else
                    new Authenticate().execute(PRN,Password);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        switch (v.getId())
        {
            case R.id.button_Login:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    button_register.setBackgroundColor(getResources().getColor(R.color.grayDark));
                }
                else {
                    button_register.setBackgroundColor(getResources().getColor(R.color.white));
                }
                break;
        }
        return false;
    }
}
