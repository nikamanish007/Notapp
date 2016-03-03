package in.annexion.notapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    String title, link, noticeBoard,md5;
    Intent intent;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_notice);

        intent=getIntent();
        title=intent.getStringExtra("title");
        link=intent.getStringExtra("link");
        noticeBoard = intent.getStringExtra("nb");
        md5 = intent.getStringExtra("md5");

        Log.e("intent", title+" "+link+" "+noticeBoard);

        //NoticeDownloader noticeDownloader=new NoticeDownloader();
        //noticeDownloader.downloadFile(link);

        file=new File(Environment.getExternalStorageDirectory(),"Notapp/"+link+".pdf");


        onLoadCompleteListener = new OnLoadCompleteListener() {
            @Override
            public void loadComplete(int nbPages)
            {
                Snackbar.make(findViewById(R.id.frameLayout_parentView),title,Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        };
        onPageChangeListener=new OnPageChangeListener() {
            @Override
            public void onPageChanged(int page, int pageCount)
            {

            }
        };


        if(file.exists()&&md5.equals(getMD5EncryptedString(file))||noticeBoard.equals(""))
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
            new DownloadFile().execute();
        }

    }

    public String getMD5EncryptedString(File file)
    {
        String encTarget= file.toString();
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception while encrypting to md5");
            e.printStackTrace();
        } // Encryption algorithm
        mdEnc.update(encTarget.getBytes(), 0, encTarget.length());
        String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
        while ( md5.length() < 32 ) {
            md5 = "0"+md5;
        }
        return md5;
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
            progressDialog.setTitle("Downloading : " + (int) ((sum * 100) / file_size));
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
