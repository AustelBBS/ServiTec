package lib;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import tecuruapan.edu.mx.servitec.ActividadesEscolares.TercerInformeActivity;

/**
 * Created by mar on 16/11/17.
 */

public class CentralDeConexiones {
    public static final String ACTIVIDADES_GUARDADAS = "estado_actividades";
    public static ServicioSocial miServicioSocial;


    private static final CentralDeConexiones ourInstance = new CentralDeConexiones();

    public static CentralDeConexiones getInstance() {
        return ourInstance;
    }

    private CentralDeConexiones() {
    }

    public static long descargar(Context context, String url, String titulo, String descripcion, final String nombre) {
        Uri formatoUri = Uri.parse(url);
        long descargaId;
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(formatoUri);

        request.setTitle(titulo);
        request.setDescription(descripcion);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, nombre);

        // intent receiver para que sque uatostada cuando termine  la descarga
        IntentFilter filter =  new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context, nombre + " descargado", Toast.LENGTH_SHORT).show();
            }
        };
        context.registerReceiver(downloadReceiver, filter);
        return  downloadManager.enqueue(request);
    }
}
