package tecuruapan.edu.mx.servitec.ActividadesEscolares;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import lib.CentralDeConexiones;
import lib.ServicioSocial;
import tecuruapan.edu.mx.servitec.ActividadesActivity;
import tecuruapan.edu.mx.servitec.DaemonDeActividades;
import tecuruapan.edu.mx.servitec.R;
public class InformeGlobalActivity extends AppCompatActivity implements InterfaceDeActualizacion {

    TextView estadoTextView;
    ImageView estadoImageView;

    private long downloadQueueId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informe_global);
        estadoImageView = (ImageView) findViewById(R.id.imagen_estado_sr);
        estadoTextView = (TextView) findViewById(R.id.textView_estado_ot);
        actualizarEstado();
        DaemonDeActividades.registrarInterfaz(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DaemonDeActividades.removerInterfaz(this);
    }

    public void actualizarEstado() {
        SharedPreferences preferences = getSharedPreferences(CentralDeConexiones.ACTIVIDADES_GUARDADAS, 0);
        String estado = preferences.getString(ServicioSocial.INFORME_G, "error");
        estadoTextView.setText(estado);
        estadoImageView.setImageResource(ActividadesActivity.imagenId(estado));
    }

    public void descargarFormato(View sender) {
        downloadQueueId = CentralDeConexiones.descargar(this, CentralDeConexiones.miServicioSocial.linkFormatoInformeGlobal()
                , "Formato de Informe Global",
                "Descargando el formato de Informe Global.", ServicioSocial.ARCHIVO_INFORME_G);
    }

    public void subirDocumento(View sender) {
        // seleccionar documento
        CentralDeConexiones.SubirArchivoAsync.seleccionarArchivo(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == CentralDeConexiones.SubirArchivoAsync.REQUEST_CODE){
            new CentralDeConexiones.SubirArchivoAsync(this, "Subiendo Informe Global", data.getData(), ServicioSocial.LINK_SUBIR_INFORME_G, this)
                    .execute();
        }
    }

    @Override
    public void actualizar() {
        actualizarEstado();
    }
}
