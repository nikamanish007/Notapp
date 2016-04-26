package in.co.rubberduck.notapp;

/**
 * Created by fanatic on 16/2/16.
 */

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import in.co.rubberduck.notapp.R;

public class GcmIntentService extends IntentService {

    private static final String TAG = GcmIntentService.class.getSimpleName();

    SharedPreferences sharedPreferences;

    public GcmIntentService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getBoolean("isLoggedIn",false))
            registerGCM();

        Log.e("gcmintent","onHandelIntent");
    }

    /**
     * Registering with GCM and obtaining the gcm registration id
     */
    private void registerGCM() {
        String token = null;

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.e(TAG, "GCM Registration Token: " + token);

            // sending the registration id to our server
            sendRegistrationToServer(token);

            sharedPreferences.edit().putBoolean(Config.SENT_TOKEN_TO_SERVER, true).apply();

        } catch (Exception e) {
            Log.e(TAG, "Failed to complete token refresh", e);

            sharedPreferences.edit().putBoolean(Config.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        //Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
       // registrationComplete.putExtra("token", token);
       // LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {

        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
        String PRN=sharedPreferences.getString("PRN","");

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            String url="http://notapp.wce.ac.in/json/gcmRegister.php"+"?PRN="+PRN+"&regId="+token;
            HttpGet httpGet = new HttpGet(url);
            Log.e("url register: ",url);
            HttpResponse httpResponse = null;
            httpResponse = httpClient.execute(httpGet);
        }
        catch (Exception e){
            Log.e("GCMIntentService->","HttpGet: "+e);
        }

    }

}
