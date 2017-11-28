package tecuruapan.edu.mx.servitec.ActividadesEscolares;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import lib.CentralDeConexiones;
import lib.ServicioSocial;
import tecuruapan.edu.mx.servitec.ActividadesActivity;
import tecuruapan.edu.mx.servitec.R;
public class CursoInduccionActivity extends AppCompatActivity {
    TextView estadoTextView;
    ImageView estadoImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curso_induccion);
        estadoImageView = (ImageView) findViewById(R.id.imagen_estado_ta);
        estadoTextView = (TextView) findViewById(R.id.textview_curso_estado);
        actualizarDatos();
    }

    private void actualizarDatos() {
        SharedPreferences preferences = getSharedPreferences(CentralDeConexiones.ACTIVIDADES_GUARDADAS, 0);
        String estado = preferences.getString(ServicioSocial.CURSO, "error");
        estadoImageView.setImageResource(ActividadesActivity.imagenId(estado));
    }


}
