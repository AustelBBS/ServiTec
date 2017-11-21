package tecuruapan.edu.mx.servitec;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import lib.CentralDeConexiones;
import lib.ServicioSocial;

public class LoginActivity extends AppCompatActivity {

    EditText numControl;
    EditText contrasena;
    Button connect;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        connect = (Button) findViewById(R.id.ConectarBtn);
        numControl = (EditText) findViewById(R.id.NumControl);
        contrasena = (EditText) findViewById(R.id.Contrasena);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);
        progressBar.setVisibility(View.INVISIBLE);
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
        progressBar.setVisibility(View.VISIBLE);
        CentralDeConexiones.miServicioSocial = new ServicioSocial(user, pass);

        AsyncTask<Void, Void, Void> networkTask = new AsyncTask<Void, Void, Void>() {
            String res;
            @Override
            protected Void doInBackground(Void... voids) {
                res = CentralDeConexiones.miServicioSocial.iniciarSesion();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this, res, Toast.LENGTH_SHORT).show();
                if(CentralDeConexiones.miServicioSocial.sesionIniciada) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };
        networkTask.execute();


    }

    /*
    Depecrated, have direct access to db
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

    }*/

}
