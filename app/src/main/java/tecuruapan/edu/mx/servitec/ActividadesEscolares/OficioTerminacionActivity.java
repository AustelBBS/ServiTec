package tecuruapan.edu.mx.servitec.ActividadesEscolares;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import lib.CentralDeConexiones;
import lib.ServicioSocial;
import tecuruapan.edu.mx.servitec.ActividadesActivity;
import tecuruapan.edu.mx.servitec.DaemonDeActividades;
import tecuruapan.edu.mx.servitec.R;

public class OficioTerminacionActivity extends AppCompatActivity implements InterfaceDeActualizacion{

    TextView estadoTextView;
    ImageView estadoImageView;

    private long downloadQueueId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oficio_terminacion);
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
        String estado = preferences.getString(ServicioSocial.OFICIO_T, "error");
        estadoTextView.setText(estado);
        estadoImageView.setImageResource(ActividadesActivity.imagenId(estado));
    }


    public void descargarFormato(View sender) {
        downloadQueueId = CentralDeConexiones.descargar(this, ServicioSocial.LINK_DESCARGA_CARTA_TERMINACION,
                "Carta_terminacion.pdf",
                "ServiTec",
                "Carta_terminacion.pdf"
                );
    }

    public void subirDocumento(View sender) {
        // seleccionar archivo
        CentralDeConexiones.SubirArchivoAsync.seleccionarArchivo(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CentralDeConexiones.SubirArchivoAsync.REQUEST_CODE && resultCode == Activity.RESULT_OK){
            new CentralDeConexiones.SubirArchivoAsync(this, "Subiendo Oficio de Terminación", data.getData(), ServicioSocial.LINK_SUBIR_OFICIO_T,
                    this)
                    .execute();
        }
    }

    @Override
    public void actualizar() {
        actualizarEstado();
    }
}
