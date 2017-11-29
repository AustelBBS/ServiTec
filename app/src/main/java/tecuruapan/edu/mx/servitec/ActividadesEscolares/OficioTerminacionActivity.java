package tecuruapan.edu.mx.servitec.ActividadesEscolares;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lib.CentralDeConexiones;
import lib.ServicioSocial;
import tecuruapan.edu.mx.servitec.ActividadesActivity;
import tecuruapan.edu.mx.servitec.R;

public class OficioTerminacionActivity extends AppCompatActivity {

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
    }
    public void actualizarEstado() {
        SharedPreferences preferences = getSharedPreferences(CentralDeConexiones.ACTIVIDADES_GUARDADAS, 0);
        String estado = preferences.getString(ServicioSocial.OFICIO_T, "error");
        estadoTextView.setText(estado);
        estadoImageView.setImageResource(ActividadesActivity.imagenId(estado));
    }


    public void descargarFormato(View sender) {
        downloadQueueId = CentralDeConexiones.descargar(this, ""
                , "Ejemplo de Carta de Terminación",
                "Descargando el formato de la carta de terminación");
    }

    public void subirDocumento(View sender) {

    }
}
