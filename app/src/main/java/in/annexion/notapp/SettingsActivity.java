package in.annexion.notapp;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity
{
    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;
    static int cunt;
    static boolean isEditProf;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setupActionBar();

        cunt=0;
        isEditProf=false;

        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editor=sharedPreferences.edit();

        if(((getIntent()).getStringExtra("optionSelected")).equals("settings"))
            getFragmentManager().beginTransaction().replace(android.R.id.content, new AppPreferenceFragment()).commit();
        else
        {
            getFragmentManager().beginTransaction().replace(android.R.id.content, new EditProfilePreferenceFragment()).commit();
            if(getSupportActionBar()!=null)
                getSupportActionBar().setTitle("Edit Profile");
        }

    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar()
    {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        else
        {
            Toast.makeText(getBaseContext(), "ACTION BAR is null", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
       return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent intent;
        if(isEditProf) {
            if (sharedPreferences.getBoolean("isFirstTime", false)) {
                intent = new Intent(this, SettingsActivity.class);
                intent.putExtra("optionSelected", "settings");
                startActivity(intent);
                finish();
            } else {
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
        else {
            if (sharedPreferences.getBoolean("isFirstTime", false)) {
                intent=new Intent(this, HelpActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                intent=new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value)
        {
            String stringValue=null;
            HashSet hashSet=null;

            SharedPreferences.Editor editor=sharedPreferences.edit();

            if(preference instanceof MultiSelectListPreference) {
                hashSet=(HashSet)value;
            }
            else {
                stringValue = value.toString();
            }

            if (preference instanceof ListPreference)
            {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                if(preference.getKey().equals("c_name"))
                {
                    Log.e("onPreference:",""+preference.getKey());
                    editor.putBoolean("updateClass", true);
                    editor.commit();
                }
                if(preference.getKey().equals("d_name"))
                {
                    Log.e("onPreference:",""+preference.getKey());
                    editor.putBoolean("updateBranch", true);
                    editor.commit();
                }

                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
            }
            else if (preference instanceof MultiSelectListPreference)
            {
                MainActivity.selections=new ArrayList<>(hashSet);
                Log.e("onPreference:",""+preference.getKey());
                editor.putBoolean("updateDprefs", true);
                editor.commit();
            }
            else if(preference instanceof EditTextPreference)
            {
                preference.setSummary(stringValue);

                if(preference.getKey().equals("f_name")||preference.getKey().equals("l_name")||preference.getKey().equals("email"))
                {
                    MainActivity.updateNav=true;
                }

                if (preference.getKey().equals("f_name"))
                {
                    Log.e("onPreference:",""+preference.getKey());
                    editor.putBoolean("updateFname", true);
                    editor.commit();
                }
                else if (preference.getKey().equals("l_name"))
                {
                    Log.e("onPreference:",""+preference.getKey());
                    editor.putBoolean("updateLname", true);
                    editor.commit();
                }
                else if (preference.getKey().equals("phone"))
                {
                    Log.e("onPreference:",""+preference.getKey());
                    editor.putBoolean("updateNumber", true);
                    editor.commit();
                }
                else if (preference.getKey().equals("email"))
                {
                    Log.e("onPreference:",""+preference.getKey());
                    editor.putBoolean("updateEmail", true);
                    editor.commit();
                }
                else if (preference.getKey().equals("pword"))
                {
                    Log.e("onPreference:",""+preference.getKey());
                    editor.putBoolean("updatePassword", true);
                    editor.commit();
                }
                else if(preference.getKey().equals("dob"))
                {
                    Log.e("onPreference:",""+preference.getKey());
                    editor.putBoolean("updateDOB",true);
                    editor.commit();
                }
            }

            if(isEditProf) {
                if (cunt < 4) {
                    editor.putBoolean("updateFname", false);
                    editor.putBoolean("updateLname", false);
                    editor.putBoolean("updateEmail", false);
                    editor.putBoolean("updateNumber", false);
                    editor.commit();
                    cunt++;
                    Log.e("Settings Activity", "We're Done!");
                }
            }
            else {
                if (cunt < 2) {
                    editor.putBoolean("updateClass",false);
                    editor.putBoolean("updateBranch", false);
                    editor.commit();
                    cunt++;
                    Log.e("Settings Activity", "We're Done!");
                }
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference)
    {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        if(preference.getKey().equals("prefs"))
          sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
             PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getStringSet(preference.getKey(), new HashSet<String>(Arrays.asList(new String[]{}))));
        else
          sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
             PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName)
    {
        return     PreferenceFragment.class.getName().equals(fragmentName)
                || AppPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AppPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_app);
            setHasOptionsMenu(true);

            SharedPreferences.Editor editor=sharedPreferences.edit();

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("c_name"));
            bindPreferenceSummaryToValue(findPreference("d_name"));
            bindPreferenceSummaryToValue(findPreference("prefs"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item)
        {
            int id = item.getItemId();
            if (id == android.R.id.home)
            {
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class EditProfilePreferenceFragment extends PreferenceFragment implements DatePickerDialog.OnDateSetListener , Preference.OnPreferenceClickListener ,  View.OnClickListener
    {
        EditText editText_CurrentPassword,editText_NewPassword,editText_ConfirmNewPassword;
        AppCompatButton button_Done;
        Dialog dialog;
        Preference pword;
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_editprofile);
            setHasOptionsMenu(true);

            isEditProf=true;
            pword=(Preference)findPreference("pword");
            pword.setOnPreferenceClickListener(this);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("f_name"));
            bindPreferenceSummaryToValue(findPreference("l_name"));
            bindPreferenceSummaryToValue(findPreference("email"));
            bindPreferenceSummaryToValue(findPreference("phone"));
            bindPreferenceSummaryToValue(findPreference("dob"));

            Preference preference_DOB=findPreference("dob");
            preference_DOB.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new DatePickerDialog(getActivity(), EditProfilePreferenceFragment.this, 1995, 0, 1).show();
                    return false;
                }
            });
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Preference preference=findPreference("dob");
            preference.setSummary("" + year + "-" + "" + (monthOfYear + 1) + "-" + "" + dayOfMonth);
            SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("dob",""+year+"-"+""+(monthOfYear+1)+"-"+""+dayOfMonth);
            editor.commit();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item)
        {
            int id = item.getItemId();
            if (id == android.R.id.home)
            {
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            Log.e("SettingsActivity","Inside onPreferenceClicked");
            if(preference.getKey().equals("pword"))
            {
                Log.e("SettingsActivity","Inside onPreferenceClicked");
                dialog=new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_change_password);
                dialog.setTitle("Edit Password");
                dialog.show();
                button_Done=(AppCompatButton)dialog.findViewById(R.id.button_Done);
                editText_CurrentPassword=(EditText)dialog.findViewById(R.id.editText_CurrentPassword);
                editText_NewPassword=(EditText)dialog.findViewById(R.id.editText_NewPassword);
                editText_ConfirmNewPassword=(EditText)dialog.findViewById(R.id.editText_ConfirmNewPassword);
                button_Done.setOnClickListener(this);
            }
            return false;
        }
        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.button_Done:
                    onClickDone();
                    break;
            }
        }

        private void onClickDone() {
            String currentPassword,newPassword,confirmNewPassword;
            currentPassword=editText_CurrentPassword.getText().toString();
            newPassword=editText_NewPassword.getText().toString();
            confirmNewPassword=editText_ConfirmNewPassword.getText().toString();

            if(!currentPassword.equals(sharedPreferences.getString("pword","")))
                editText_CurrentPassword.setError("EnTer Valid Password");
            else if(!newPassword.equals(confirmNewPassword)){
                editText_ConfirmNewPassword.setText("");
                editText_ConfirmNewPassword.setError("Passowod Doest Not Matches");
            }
            else {
                editor.putBoolean("updatePassword", true);
                editor.putString("pword", editText_NewPassword.getText().toString());
                editor.commit();
                dialog.dismiss();
            }
        }
    }
}
