package tecuruapan.edu.mx.servitec.ActividadesEscolares;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lib.CentralDeConexiones;
import lib.ServicioSocial;
import tecuruapan.edu.mx.servitec.ActividadesActivity;
import tecuruapan.edu.mx.servitec.DaemonDeActividades;
import tecuruapan.edu.mx.servitec.R;
public class TercerInformeActivity extends AppCompatActivity implements InterfaceDeActualizacion{


    TextView estadoTextView;
    ImageView estadoImageView;

    private long downloadQueueId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tercer_informe);
        estadoImageView = (ImageView) findViewById(R.id.imagen_estado_sr);
        estadoTextView = (TextView) findViewById(R.id.textView_estado_ta);
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
        String estado = preferences.getString(ServicioSocial.TERCER_A, "error");
        estadoTextView.setText(estado);
        estadoImageView.setImageResource(ActividadesActivity.imagenId(estado));
    }

    public void descargarFormato(View sender) {
        downloadQueueId = CentralDeConexiones.descargar(this, CentralDeConexiones.miServicioSocial.linkFormatoInformeBimestral()
                , ServicioSocial.ARCHIVO_INFORME_BI,
                "ServiTec", ServicioSocial.ARCHIVO_INFORME_BI);
    }

    public void subirDocumento(View sender) {
        // seleccionar archivo
        CentralDeConexiones.SubirArchivoAsync.seleccionarArchivo(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CentralDeConexiones.SubirArchivoAsync.REQUEST_CODE && resultCode == Activity.RESULT_OK){
            new CentralDeConexiones.SubirArchivoAsync(this, "Subiendo Tercer Informe",
                    data.getData(), ServicioSocial.LINK_SUBIR_AVANCE_3, this)
                    .execute();
        }
    }

    @Override
    public void actualizar() {
        actualizarEstado();
    }
}
