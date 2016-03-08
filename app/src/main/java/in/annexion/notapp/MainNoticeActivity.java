package in.annexion.notapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import org.apache.http.conn.ConnectTimeoutException;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainNoticeActivity extends AppCompatActivity
{
    PDFView pdfView_Notice;
    private OnLoadCompleteListener onLoadCompleteListener;
    private OnPageChangeListener onPageChangeListener;
    File file;
    String title, link, noticeBoard,md5,message;
    Intent intent;
    TextView textView_Message, textView_Title;
    Context context = this;
    Typeface roboto_Thin,roboto_CondensedLight;

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
        message=intent.getStringExtra("message");
        md5 = intent.getStringExtra("md5");

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

        if(!message.equals("")&&link.equals(""))
        {
            textView_Message.setText(message);
            textView_Message.setTypeface(roboto_CondensedLight);
        }
        else if(file.exists()&&md5==getMD5EncryptedString(file.toString())||noticeBoard.equals(""))
        {
            try {
                pdfView_Notice = (PDFView) findViewById(R.id.pdfView_Notice);
                pdfView_Notice.fromFile(file).defaultPage(1).showMinimap(true).enableSwipe(true).onLoad(onLoadCompleteListener).onPageChange(onPageChangeListener).load();
            }catch (RuntimeException e) {
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
            file.delete();
            new DownloadFile().execute();
            Log.e("MainNoticeActivity","DownloadFile() called.");
        }

    }

    public String getMD5EncryptedString(String input) {
        byte[] source;
        try {
            //Get byte according by specified coding.
            source = input.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            source = input.getBytes();
        }
        String result = null;
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source);
            //The result should be one 128 integer
            byte temp[] = md.digest();
            char str[] = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = temp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            result = new String(str);
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
        AlertDialogManager alert;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            boolean isConnected=new ConnectionDetector(getBaseContext()).isConnectingToInternet();
            if(!isConnected) {
                alert.showAlertDialog(context, "Offline or Weak Connection!", "Connect to Internet and try again.", false);
                NavUtils.navigateUpFromSameTask(MainNoticeActivity.this);
            }
            progressDialog=new ProgressDialog(context);
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("Downloading ");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected File doInBackground(Void... params) {

            int count;

            byte [] data;
            File file = new File("");

            try {
                //link= URLEncoder.encode(link, "utf-8");

                //String nb = getResources().getStringArray(R.array.intent)[Integer.parseInt(noticeBoard)];

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
            progressDialog.setTitle("Downloading : " + (int) ((sum * 100) / file_size+1));
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            progressDialog.dismiss();

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
