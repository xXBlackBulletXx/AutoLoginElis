package antoniorusso.botloginelis;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.R.attr.data;
import static android.R.attr.path;

/**
 * Created by Antonio Russo on 09/02/2017.
 */

public class MainActivity extends AppCompatActivity {
    static public String Username;
    static public String Password;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isStoragePermissionGranted();

        final EditText UsernameTxt = (EditText) findViewById(R.id.txtUsername);
        final EditText PasswordTxt = (EditText) findViewById(R.id.txtPassword);
        Button LoginBtn = (Button) findViewById(R.id.LoginBtn);
        Button LogoutBtn = (Button) findViewById(R.id.LogoutBtn);

        Username = UsernameTxt.getText().toString();
        Password = PasswordTxt.getText().toString();

        LogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyAsyncTaskLogout().execute("Miao");
            }
        });

        LoginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    String FILENAME = "values.txt";
                    String string = UsernameTxt.getText().toString()+";"+PasswordTxt.getText().toString()+";";

                    FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                    fos.write(string.getBytes());
                    fos.close();

                    //display file saved message
                    Toast.makeText(getBaseContext(), "File saved successfully!",
                            Toast.LENGTH_SHORT).show();

                    new MyAsyncTaskLogin().execute("Miao");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
                Log.i("PERMISSION", "GRANTED");
            } else {
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                Log.i("PERMISSION", "NOT GRANTED");
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.i("PERMISSION", "AUTOMATIC GRANTED");
        }
    }

    private class MyAsyncTaskLogout extends AsyncTask<String, Integer, Double> {
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
                nameValuePairs.add(new BasicNameValuePair("Logout", "Logout"));
                nameValuePairs.add(new BasicNameValuePair("action", "fw_logon"));
                nameValuePairs.add(new BasicNameValuePair("fw_logon_type", "logout"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                Log.i("SITE", "LOGOUT SUCCESS");

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
        }
    }

    private class MyAsyncTaskLogin extends AsyncTask<String, Integer, Double> {

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

                Log.i("SITE", response.getParams().toString());
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
        }
    }
}
