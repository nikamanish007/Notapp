package in.annexion.notapp;

/**
 * Created by fanatic on 5/2/16.
 */
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.DatePicker;

public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    View rootView;
    public DatePickerDialogFragment(View rootView) {
        this.rootView=rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), this, 1985,0, 1);
    }

    public void onDateSet(DatePicker view, int yy, int mm, int dd) {

        populateSetDate(""+dd+"-"+(mm+1)+"-"+yy);
    }
    public void populateSetDate(String date) {
        AppCompatButton button_DOB=(AppCompatButton)rootView.findViewById(R.id.button_DOB);
        button_DOB.setText(date);

        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("DOB",date);
        editor.commit();

    }
}


