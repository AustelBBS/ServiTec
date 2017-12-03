package tecuruapan.edu.mx.servitec.ActividadesEscolares;

import android.app.Activity;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import lib.CentralDeConexiones;
import lib.ServicioSocial;
import tecuruapan.edu.mx.servitec.ActividadesActivity;
import tecuruapan.edu.mx.servitec.DaemonDeActividades;
import tecuruapan.edu.mx.servitec.R;

public class CartaEvaluacionActivity extends AppCompatActivity implements InterfaceDeActualizacion{
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
        imageViewEstado = (ImageView) findViewById(R.id.imagen_estado_sr);
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

        DaemonDeActividades.registrarInterfaz(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DaemonDeActividades.removerInterfaz(this);
        Log.d(getClass().toString(), "On Destroy called" );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK && requestCode == CentralDeConexiones.SubirArchivoAsync.REQUEST_CODE){
            Uri uri = data.getData();
            new CentralDeConexiones.SubirArchivoAsync(this, "Subiendo Carta de evaluación receptora", uri,
                    ServicioSocial.LINK_SUBIR_CARTA_EVALUACION_RECEP, this)
                    .execute();
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
        CentralDeConexiones.SubirArchivoAsync.seleccionarArchivo(this);
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


    @Override
    public void actualizar() {
        ponerEstado();
    }



}
