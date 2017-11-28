package tecuruapan.edu.mx.servitec.ActividadesEscolares;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import lib.CentralDeConexiones;
import tecuruapan.edu.mx.servitec.ActividadesActivity;
import tecuruapan.edu.mx.servitec.R;

public class CartaEvaluacionActivity extends AppCompatActivity {
    final static int codigo = 42;
    Button botonDescargarFormato,
            botonSubirArchivo;
    TextView textViewEstado;
    ImageView imageViewEstado;
    private BroadcastReceiver downloadReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carta_evaluacion);
        botonDescargarFormato = (Button) findViewById(R.id.ev_bajar_formato);
        botonSubirArchivo = (Button) findViewById(R.id.ev_subir);
        textViewEstado = (TextView) findViewById(R.id.textview_ev_estado);
        imageViewEstado = (ImageView) findViewById(R.id.imagen_estado_ta);
        botonDescargarFormato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                descargarFormato();
            }
        });
        botonSubirArchivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarArchivo();
            }
        });
        ponerEstado();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK && requestCode == codigo){
            Uri uri = data.getData();
            subirDocumento(uri);
        }
    }

    private void ponerEstado() {
        SharedPreferences preferences = getSharedPreferences(CentralDeConexiones.ACTIVIDADES_GUARDADAS,0);
        String estado = preferences.getString("Carta de evaluación receptora","error");
        imageViewEstado.setImageResource(ActividadesActivity.imagenId(estado));
//        imageViewEstado.setImageResource(R.drawable.ic_revisado);
        textViewEstado.setText(estado);
    }



    private void seleccionarArchivo() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(intent, codigo);
    }

    private void descargarFormato() {
        Uri formatoUri = Uri.parse(CentralDeConexiones.miServicioSocial.linkFormatoEvaluacionR());
        long descargaId;
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(formatoUri);

        request.setTitle("Formato de Evaluación Receptora");
        request.setDescription("Descargando el formato para la carta de Evaluación Receptora.");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Evaluacion_Receptora.docx");

        // intent receiver para que sque uatostada cuando termine  la descarga
        IntentFilter filter =  new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        downloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(CartaEvaluacionActivity.this, "Descarga terminada.", Toast.LENGTH_SHORT).show();
            }
        };
        registerReceiver(downloadReceiver, filter);


        descargaId = downloadManager.enqueue(request);

    }

    private void subirDocumento(Uri uri) {
        new UploadFileAsync().execute(uri.toString());
    }


    private class UploadFileAsync extends AsyncTask<String, Void, String> {
        String serverResponseCode;

        @Override
        protected String doInBackground(String... params) {
            try {
//                String sourceFileUri = "/mnt/sdcard/abc.png";
                String sourceFileUri = "/storage/self/primary/Download/crack.pdf";//= params[0];
                Log.d("Carta", "source file uri" + sourceFileUri);
                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "------";
                String boundary = "WebKitFormBoundarykXA0q8IB263am5bi";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1 * 1024 * 1024;
                File sourceFile = new File(sourceFileUri);

                if (sourceFile.isFile()) {
                    try {
                        String upLoadServerUri = CentralDeConexiones.miServicioSocial.linkSubirEvaluacion();

                        // open a URL connection to the Servlet
                        FileInputStream fileInputStream = new FileInputStream(
                                sourceFile);
                        URL url = new URL(upLoadServerUri);

                        // Open a HTTP connection to the URL
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true); // Allow Inputs
                        conn.setDoOutput(true); // Allow Outputs
                        conn.setUseCaches(false); // Don't use a Cached Copy
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("subir", "Subir Archivo");
                        conn.setRequestProperty(CentralDeConexiones.miServicioSocial.COOKIE_PHP, CentralDeConexiones.miServicioSocial.getCookie());
                        conn.setRequestProperty("ENCTYPE","multipart/form-data");
                        conn.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);
                        conn.setRequestProperty("archivo", sourceFileUri);
                        conn.connect();
                        dos = new DataOutputStream(conn.getOutputStream());

                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"archivo\";filename=\"" + sourceFileUri + "\"" + lineEnd);

                        dos.writeBytes(lineEnd);

                        // create a buffer of maximum size
                        bytesAvailable = fileInputStream.available();

                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];

                        // read file and write it into form...
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        while (bytesRead > 0) {

                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0,bufferSize);

                        }

                        // send multipart form data necesssary after file
                        // data...
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                        // Responses from the server (code and message)
                        int serverResponseCode = conn.getResponseCode();
                        String serverResponseMessage = conn.getResponseMessage();
                        Log.d("Carta", conn.getContent().toString());

                        Log.d("Carta", serverResponseMessage);
                        if (serverResponseCode == 200) {
                            // messageText.setText(msg);
                            Log.d("Carta", "Codigo 200 luego de enviar");
                            // recursiveDelete(mDirectory1);

                        }

                        // close the streams //
                        fileInputStream.close();
                        dos.flush();
                        dos.close();

                    } catch (Exception e) {

                        // dialog.dismiss();
                        e.printStackTrace();

                    }
                    // dialog.dismiss();

                } // End else block


            } catch (Exception ex) {
                // dialog.dismiss();

                ex.printStackTrace();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(CartaEvaluacionActivity.this, "Terminado!", Toast.LENGTH_SHORT).show();
        }
    }

}
