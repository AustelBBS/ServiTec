package tecuruapan.edu.mx.servitec;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    EditText numControl;
    EditText contrasena;
    Button connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        connect = (Button) findViewById(R.id.ConectarBtn);
        numControl = (EditText) findViewById(R.id.NumControl);
        contrasena = (EditText) findViewById(R.id.Contrasena);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noControl = numControl.getText().toString();
                String contra = contrasena.getText().toString();

                conectar(noControl, contra);

            }
        });
        setSupportActionBar(toolbar);
    }

    public void conectar(String user, String pass) {
        HTTPHandler httpHandler = new HTTPHandler(user, pass);
        httpHandler.execute("http://tecuruapan.edu.mx/ssocial/?modulo=logeo");
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public class HTTPHandler extends AsyncTask<String, Void, Void> {
        String user, pass;

        public HTTPHandler(String user, String pass) {
            this.user = user;
            this.pass = pass;
        }

        @Override
        protected Void doInBackground(String... params) {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            String url = params[0];
            HttpPost httpost = new HttpPost(url);

            try {
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                nvps.add(new BasicNameValuePair("noControl", user));
                nvps.add(new BasicNameValuePair("clave", pass));
                httpost.setEntity(new UrlEncodedFormEntity(nvps));

            } catch (UnsupportedEncodingException e) {
                // writing error to Log
                e.printStackTrace();
            }
            try {
                HttpResponse response = httpclient.execute(httpost);

                // writing response to log
                Log.d("Http Response:", response.getStatusLine().toString());

                llamar(response.toString());
            } catch (ClientProtocolException e) {
                // writing exception to log
                e.printStackTrace();

            } catch (IOException e) {
                // writing exception to log
                e.printStackTrace();
            }

            return null;
        }

        public void llamar(final String response) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                }
            });
        }

    }

}
