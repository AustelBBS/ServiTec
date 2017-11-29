package tecuruapan.edu.mx.servitec.ActividadesEscolares;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import lib.CentralDeConexiones;
import lib.ServicioSocial;
import tecuruapan.edu.mx.servitec.ActividadesActivity;
import tecuruapan.edu.mx.servitec.R;
public class SolicitudRegistroActivity extends AppCompatActivity {
    TextView estadoTextView;
    ImageView estadoImageView;
    private BroadcastReceiver downloadReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud_registro);

        estadoTextView = (TextView) findViewById(R.id.estado_tx_sol_re);
        estadoImageView = (ImageView) findViewById(R.id.imagen_estado_sr);
        actualizarEstado();
    }

    public void actualizarEstado() {
        SharedPreferences preferences = getSharedPreferences(CentralDeConexiones.ACTIVIDADES_GUARDADAS, 0);
        String estado = preferences.getString(ServicioSocial.SOLICITUD_RE, "error");
        estadoTextView.setText(estado);
        estadoImageView.setImageResource(ActividadesActivity.imagenId(estado));
    }

    public void bajarFormato(View sender){
        Uri formatoUri = Uri.parse(CentralDeConexiones.miServicioSocial.linkFormatoSolicitudRe());
        long descargaId;
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(formatoUri);

        request.setTitle("Formato de Solicitud de Registro");
        request.setDescription("Descargando el formato para la solicitud de registro.");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Solicitud_De_Registro.docx");

        // intent receiver para que sque uatostada cuando termine  la descarga
        IntentFilter filter =  new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        downloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(SolicitudRegistroActivity.this, "Descarga terminada.", Toast.LENGTH_SHORT).show();
            }
        };
        registerReceiver(downloadReceiver, filter);


        descargaId = downloadManager.enqueue(request);
    }

    public void subirSolicitud(View sender) {

    }
}
