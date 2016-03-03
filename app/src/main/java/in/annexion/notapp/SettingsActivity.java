package in.annexion.notapp;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import android.view.MenuItem;
import android.widget.DatePicker;
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
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setupActionBar();



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
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context)
    {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * {@inheritDoc}
     */
    /*@Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target)
    {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }*/


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

                if(preference.getKey().equals("class"))
                {
                    MainActivity.updateClass=true;
                }
                if(preference.getKey().equals("branch"))
                {
                    MainActivity.updateBranch=true;
                }

                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
            }
            else if (preference instanceof MultiSelectListPreference)
            {
                MainActivity.updateIcons=true;
                MainActivity.selections=new ArrayList<>(hashSet);

                MainActivity.updateDPrefs=true;
            }
            else if(preference instanceof EditTextPreference)
            {
                preference.setSummary(stringValue);

                if(preference.getKey().equals("fname")||preference.getKey().equals("lname")||preference.getKey().equals("email"))
                {
                    MainActivity.updateNav=true;
                }

                if (preference.getKey().equals("fname"))
                {
                    MainActivity.updateFName=true;
                }
                if (preference.getKey().equals("lname"))
                {
                    MainActivity.updateLName=true;
                }
                if (preference.getKey().equals("number"))
                {
                    MainActivity.updateNumber=true;
                }
                if (preference.getKey().equals("email"))
                {
                    MainActivity.updateEMail=true;
                }
                if (preference.getKey().equals("password"))
                {
                    MainActivity.updatePassword=true;
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
        if(preference.getKey().equals("pref_depts"))
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

            SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getActivity());

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("class"));
            bindPreferenceSummaryToValue(findPreference("branch"));
            bindPreferenceSummaryToValue(findPreference("pref_depts"));

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
    public static class EditProfilePreferenceFragment extends PreferenceFragment implements DatePickerDialog.OnDateSetListener
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_editprofile);
            setHasOptionsMenu(true);


            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("fname"));
            bindPreferenceSummaryToValue(findPreference("lname"));
            bindPreferenceSummaryToValue(findPreference("email"));
            bindPreferenceSummaryToValue(findPreference("number"));
            bindPreferenceSummaryToValue(findPreference("DOB"));

            Preference preference_DOB=(Preference)findPreference("DOB");
            preference_DOB.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new DatePickerDialog(getActivity(),EditProfilePreferenceFragment.this,1995,0,1).show();
                    return false;
                }
            });
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Preference preference=findPreference("DOB");
            preference.setSummary(""+dayOfMonth+"-"+""+(monthOfYear+1)+"-"+""+year);
            SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("DOB",""+dayOfMonth+"-"+""+(monthOfYear+1)+"-"+""+year);
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
    }

}
