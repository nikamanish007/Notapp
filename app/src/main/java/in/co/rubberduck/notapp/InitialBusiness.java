package in.co.rubberduck.notapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.TextView;

/**
 * Created by fanatic on 28/4/16.
 */
public class InitialBusiness
{
    static boolean editProfileCalled , settingsCalled, done;
    static Dialog dialog;
    static TextView textView_InitialPrompt;
    static FloatingActionButton fab;
    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;
    static Context context;
    public static void doBusiness(final Activity activity)
    {

        if(dialog!=null)
            dialog.dismiss();
        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_initial);
        dialog.setCancelable(false);
        dialog.setTitle("Welcome");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        textView_InitialPrompt=(TextView)dialog.findViewById(R.id.textView_InitialPrompt);
        fab=(FloatingActionButton) dialog.findViewById(R.id.fab);
        context=activity.getApplicationContext();

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        editor=sharedPreferences.edit();

        editProfileCalled=sharedPreferences.getBoolean("editProfileCalled",false);
        settingsCalled=sharedPreferences.getBoolean("settingsCalled",false);
        done=sharedPreferences.getBoolean("done",false);


        if(!editProfileCalled)
        {
            textView_InitialPrompt.setText("Add your profile details");
            fab.setImageResource(R.drawable.ic_person);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent=new Intent(context,SettingsActivity.class);
                    intent.putExtra("optionSelected","editProfile");
                    activity.startActivity(intent);
                    editor.putBoolean("editProfileCalled",true);
                    editor.commit();
                }
            });
        }
        else if(!settingsCalled)
        {
            textView_InitialPrompt.setText("Set your class, branch & subscriptions");
            fab.setImageResource(R.drawable.ic_setting_dark);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent=new Intent(context,SettingsActivity.class);
                    intent.putExtra("optionSelected","settings");
                    activity.startActivity(intent);
                    editor.putBoolean("settingsCalled",true);
                    editor.commit();
                }
            });
        }
        else if(!done) {
            textView_InitialPrompt.setText("Now just swipe down and\n get your notice boards up to date.");
            fab.setImageResource(R.drawable.ic_done_all);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    editor.putBoolean("done",true);
                    editor.putBoolean("isFirstTime",false);
                    editor.commit();
                }
            });
        }
    }
}
