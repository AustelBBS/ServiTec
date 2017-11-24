package tecuruapan.edu.mx.servitec;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import lib.CentralDeConexiones;

public class PerfilActivity extends AppCompatActivity {
    TextView textViewMatricula, textViewSemestre, textViewCarrera,textViewId, textViewNombre, textViewDireccion, textViewTelefono, textViewCelular,textViewCorreo,
    textViewPeriodo;
    Button botonCambiarImagen, botonCambiarContrasenia, botonEditar;
    ImageView imageViewPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        textViewMatricula = (TextView) findViewById(R.id.matricula);
        textViewSemestre= (TextView) findViewById(R.id.semestre);
        textViewCarrera = (TextView) findViewById(R.id.carrera);
        textViewId= (TextView) findViewById(R.id.id_unico);
        textViewNombre = (TextView) findViewById(R.id.tvNombre);
        textViewDireccion = (TextView) findViewById(R.id.tvDireccion);
        textViewTelefono = (TextView) findViewById(R.id.tvTelefono);
        textViewCelular = (TextView) findViewById(R.id.tvCelular);
        textViewCorreo = (TextView) findViewById(R.id.tvCorreo);
        textViewPeriodo= (TextView) findViewById(R.id.tvPeriodo);
        imageViewPerfil = (ImageView) findViewById(R.id.imageView_perfil);
        botonCambiarContrasenia.setVisibility(View.INVISIBLE);
        botonEditar.setVisibility(View.INVISIBLE);
        botonCambiarImagen.setVisibility(View.INVISIBLE);

        new bajarDatos().execute();

    }


         class bajarDatos extends AsyncTask<Void, Void, Void>{
                HashMap<String, String> datos;
                Bitmap imagen;
             @Override
             protected Void doInBackground(Void... voids) {
                 datos = CentralDeConexiones.miServicioSocial.recuperarMisDatos();
                 imagen = CentralDeConexiones.miServicioSocial.descargarImagen();
                 return null;
             }

             @Override
             protected void onPostExecute(Void aVoid) {
                 imageViewPerfil.setImageBitmap(imagen);
                 textViewMatricula.setText(datos.get("matricula"));
                 textViewSemestre.setText(datos.get("semestre"));
                 textViewCarrera.setText(datos.get("carrera"));
                 textViewId.setText(datos.get("idUnico"));
                 textViewNombre.setText("Nombre: " + datos.get("nombre"));
                 textViewDireccion.setText("Dirección: " + datos.get("direccion"));
                 textViewCelular.setText("Celular: " + datos.get("celular"));
                 textViewTelefono.setText("Teléfono: " + datos.get("telefono"));
                 textViewCorreo.setText("Corrreo: " + datos.get("correo"));
                 textViewPeriodo.setText("Periodo: " + datos.get("periodo"));
             }
         }


}
