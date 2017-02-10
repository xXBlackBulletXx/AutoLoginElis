package antoniorusso.botloginelis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;


/**
 * Created by Antonio Russo on 09/02/2017.
 */

public class WifiReceiver extends BroadcastReceiver {

    static final int READ_BLOCK_SIZE = 100;
    static public String Username;
    static public String Password;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI){
            if (netInfo.getExtraInfo().contains("ELIS.org Roma")) {
                new MyAsyncTask().execute("Miao");
                try {
                    FileInputStream fis = context.openFileInput("values.txt");
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader bufferedReader = new BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }
                    String[] values = sb.toString().split(";");
                    Username = values[0];
                    Password = values[1];
                    Log.i("STRINGA FILE", values[0]);
                }catch (Exception e){
                    Log.i("EXCEPTION READ FILE", e.getMessage());
                }

            }
        }
        else {
            Log.d("WifiReceiver", "Don't have Wifi Connection");
        }
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Double> {

        @Override
        protected Double doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0]);
            return null;
        }

        public void postData(String valueIWantToSend) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://fw.elis.org:4100/wgcgi.cgi");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                //List Parameters POST REQUEST
                nameValuePairs.add(new BasicNameValuePair("fw_username", Username));
                nameValuePairs.add(new BasicNameValuePair("fw_password", Password));
                nameValuePairs.add(new BasicNameValuePair("fw_domain", "ELIS.ORG"));
                nameValuePairs.add(new BasicNameValuePair("submit", "Login"));
                nameValuePairs.add(new BasicNameValuePair("action", "fw_logon"));
                nameValuePairs.add(new BasicNameValuePair("fw_logon_type", "logon"));
                nameValuePairs.add(new BasicNameValuePair("redirect", ""));
                nameValuePairs.add(new BasicNameValuePair("lang", "en-US"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                Log.i("SITE", "CONNECTED");

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
        }
    }
}