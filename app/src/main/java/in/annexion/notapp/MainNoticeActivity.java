package in.annexion.notapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;

public class MainNoticeActivity extends AppCompatActivity
{
    PDFView pdfView_Notice;
    private OnLoadCompleteListener onLoadCompleteListener;
    private OnPageChangeListener onPageChangeListener;
    File file;
    String title, link, noticeBoard,md5;
    Intent intent;
    TextView textView_Message, textView_Title;
    Context context = this;
    Typeface roboto_Thin,roboto_CondensedLight;
    SQLiteDatabase db;
    int n_id;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_notice);

        textView_Message=(TextView)findViewById(R.id.textView_Message);
        textView_Title=(TextView)findViewById(R.id.textView_Title);
        roboto_Thin = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");
        roboto_CondensedLight = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoCondensed-Light.ttf");

        intent=getIntent();
        title=intent.getStringExtra("title");
        link=intent.getStringExtra("link");
        noticeBoard = intent.getStringExtra("nb");
        md5 = intent.getStringExtra("md5");
        n_id=intent.getIntExtra("n_id",0);

        textView_Title.setText(title);
        textView_Title.setTypeface(roboto_Thin);

        Log.e("intent", title+" "+link+" "+noticeBoard);

        file=new File(Environment.getExternalStorageDirectory(),"Notapp/"+link+".pdf");

        onLoadCompleteListener = new OnLoadCompleteListener() {
            @Override
            public void loadComplete(int nbPages)
            {
                //Snackbar.make(findViewById(R.id.frameLayout_parentView),title,Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        };
        onPageChangeListener=new OnPageChangeListener() {
            @Override
            public void onPageChanged(int page, int pageCount)
            {

            }
        };

        if(link.charAt(0)=='#')
        {

            ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
            stub.setLayoutResource(R.layout.content_main_notice);
            View inflated = stub.inflate();

            textView_Title=(TextView)inflated.findViewById(R.id.textView_Title);
            textView_Title.setText(title);
            textView_Title.setTypeface(roboto_CondensedLight);

            textView_Message=(TextView)inflated.findViewById(R.id.textView_Message);
            textView_Message.setText(link.substring(1,link.length()));
            //textView_Message.setTypeface(roboto_CondensedLight);

        }
        else if(file.exists()&&md5.equals(getMD5EncryptedString(file))||noticeBoard.equals(""))
        {
            try
            {
                pdfView_Notice = (PDFView) findViewById(R.id.pdfView_Notice);
                pdfView_Notice.fromFile(file).defaultPage(1).showMinimap(true).enableSwipe(true).onLoad(onLoadCompleteListener).onPageChange(onPageChangeListener).load();
            }
            catch (RuntimeException e)
            {
                Snackbar.make(findViewById(R.id.frameLayout_parentView),"File Corrupted!!" , Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
            catch (Exception e)
            {
                Log.e("file error","incomplete  "+e.toString());
                new DownloadFile().execute();
                Snackbar.make(findViewById(R.id.frameLayout_parentView),"File Corrupted!!" , Snackbar.LENGTH_LONG).setAction("Action", null).show();
                finish();
            }
        }
        else
        {
            db=SQLiteDatabase.openDatabase("" + Environment.getExternalStorageDirectory() + "/Notapp/DB/notapp.db", null, SQLiteDatabase.CREATE_IF_NECESSARY | SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING);
            cursor=new Cursor() {
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
            cursor=db.rawQuery("select isDone from notices where n_id=" + n_id, null);
            cursor.moveToFirst();

            Log.e("MainNoticeActivity", "Cursor: " + cursor);


            int isDone=cursor.getInt(0);

            Log.e("MainNoticeActivity", "isDone:"+isDone);


            if(isDone==0)
            {

                AlertDialogManager alert=new AlertDialogManager();
                alert.showAlertDialog(context, "Downloading Notice..", "Please Wait.", true);
                Log.e("MainNoticeActivity", "isDone=0 Still Downloading.");
            }
            else
            {
                db.execSQL("update notices set isDone=0 where n_id="+n_id);
                file.delete();
                new DownloadFile().execute();
                Log.e("MainNoticeActivity", "isDone=1 DownloadFile() called.");
            }
            db.close();
            cursor.close();

        }

    }

    public String getMD5EncryptedString(File file) {
        String result = "";
        char[] hexDigits = "0123456789abcdef".toCharArray();
        FileInputStream is= null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            Log.e("","FIle Not Found");
            e.printStackTrace();
        }

        try {
            byte[] bytes = new byte[4096];
            int read;
            MessageDigest digest = MessageDigest.getInstance("MD5");

            while ((read = is.read(bytes)) != -1) {
                digest.update(bytes, 0, read);
            }

            byte[] messageDigest = digest.digest();

            StringBuilder sb = new StringBuilder(32);

            for (byte b : messageDigest) {
                sb.append(hexDigits[(b >> 4) & 0x0f]);
                sb.append(hexDigits[b & 0x0f]);
            }

            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("MainNoticeActivity","md5: "+result+" "+this.md5);

        return result;
    }

    class DownloadFile extends AsyncTask<Void,Void,File>
    {
        long sum = 0;
        int file_size = 0;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            boolean isConnected=new ConnectionDetector(getBaseContext()).isConnectingToInternet();
            if(!isConnected) {
                Toast.makeText(context, "You are Offline !!", Toast.LENGTH_LONG).show();
                new android.support.v7.app.AlertDialog.Builder(context)
                        .setTitle("Notice File Corrupted")
                        .setMessage("Connect to donwload again.")
                        .setIcon(android.R.drawable.ic_popup_sync)
                        .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        }).show();

            }
            else {
                progressDialog = new ProgressDialog(context);
                progressDialog.setTitle("Please Wait");
                progressDialog.setMessage("Downloading ");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
            }
        }

        @Override
        protected File doInBackground(Void... params) {

            int count;

            byte [] data;
            File file = new File("");

            try {
                URL url = new URL("http://notapp.in/notices/" +noticeBoard+ "/" + link + ".pdf");

                Log.e("n_downloader url ", url.toString());
                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(2000);
                connection.connect();
                file_size = connection.getContentLength();

                // download the file
                file = new File(Environment.getExternalStorageDirectory().toString()+ "/Notapp");
                if (!file.exists()) {
                    file.mkdirs();
                }
                file = new File(Environment.getExternalStorageDirectory().toString() + "/Notapp/" + link + ".pdf");

                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                FileOutputStream fos = new FileOutputStream(file);

                data = new byte[file_size];

                publishProgress();


                while ((count = input.read(data)) != -1) {
                    sum += count;
                    //progressDialog.setMessage("Downloading " + (int)((sum*100)/file_size)+ "/100%");
                    publishProgress();
                    fos.write(data, 0, count);
                }
                fos.flush();
                input.close();
                fos.close();
            } catch (ConnectTimeoutException e ) {
                Log.e("n_downloader Error: ", e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {

            }
            return file;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage("Please Wait .. ");
            progressDialog.setTitle("Downloading : " + (int) ((sum * 100) / file_size+1)+"%");
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            try {
                progressDialog.dismiss();
            }
            catch (Exception e){}

            db=SQLiteDatabase.openDatabase(""+Environment.getExternalStorageDirectory()+"/Notapp/DB/notapp.db",null,SQLiteDatabase.CREATE_IF_NECESSARY | SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING);
            db.execSQL("update notices set isDone=1 where n_id="+n_id);
            db.close();

            Log.e("MainNoticeActivity", "isDone updated");

            try {
                pdfView_Notice = (PDFView) findViewById(R.id.pdfView_Notice);
                pdfView_Notice.fromFile(file).defaultPage(1).showMinimap(true).enableSwipe(true).onLoad(onLoadCompleteListener).onPageChange(onPageChangeListener).load();
            }catch (RuntimeException e) {
                Snackbar.make(findViewById(R.id.frameLayout_parentView),"File Corrupted!!" , Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
            catch (Exception e)
            {
                Log.e("file error",e.toString());
            }
        }
    }
}
