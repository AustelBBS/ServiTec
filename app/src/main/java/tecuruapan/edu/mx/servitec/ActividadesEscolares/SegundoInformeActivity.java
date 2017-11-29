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

public class SegundoInformeActivity extends AppCompatActivity {
    final static int codigo = 44;

    TextView estadoTextView;
    ImageView estadoImageView;

    private BroadcastReceiver downloadReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segundo_informe);
        estadoImageView = (ImageView) findViewById(R.id.imagen_estado_sr);
        estadoTextView = (TextView) findViewById(R.id.textView_estado_sa);
        actualizarEstado();

    }
    public void actualizarEstado() {
        SharedPreferences preferences = getSharedPreferences(CentralDeConexiones.ACTIVIDADES_GUARDADAS, 0);
        String estado = preferences.getString(ServicioSocial.PRIMER_A, "error");
        estadoTextView.setText(estado);
        estadoImageView.setImageResource(ActividadesActivity.imagenId(estado));
    }

    public void descargarFormato(View sender) {
        Uri formatoUri = Uri.parse(CentralDeConexiones.miServicioSocial.linkFormatoInformeBimestral());
        long descargaId;
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(formatoUri);

        request.setTitle("Formato de Informe bimestral");
        request.setDescription("Descargando el formato para los informes bimestrales.");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Informe_Bimestral.docx");

        // intent receiver para que sque uatostada cuando termine  la descarga
        IntentFilter filter =  new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        downloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(SegundoInformeActivity.this, "Descarga terminada.", Toast.LENGTH_SHORT).show();
            }
        };
        registerReceiver(downloadReceiver, filter);


        descargaId = downloadManager.enqueue(request);

    }

    public void subirDocumento(View sender) {

    }

}
