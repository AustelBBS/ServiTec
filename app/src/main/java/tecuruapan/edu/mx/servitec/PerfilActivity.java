package tecuruapan.edu.mx.servitec;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import lib.CentralDeConexiones;

public class PerfilActivity extends AppCompatActivity {
    TextView textViewMatricula,
            textViewSemestre,
            textViewCarrera,
            textViewId;
    EditText editTextNombre,
            editTextDireccion, 
            editTextTelefono, 
            editTextCelular,
            editTextCorreo,
            editTextPeriodo;
    Button botonCambiarImagen, 
            botonCambiarContrasenia, 
            botonEditar;
    ImageView imageViewPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        textViewMatricula = (TextView) findViewById(R.id.matricula);
        textViewSemestre = (TextView) findViewById(R.id.semestre);
        textViewCarrera = (TextView) findViewById(R.id.carrera);
        textViewId = (TextView) findViewById(R.id.id_unico);
        editTextNombre = (EditText) findViewById(R.id.tvNombre);
        editTextDireccion = (EditText) findViewById(R.id.tvDireccion);
        editTextTelefono = (EditText) findViewById(R.id.tvTelefono);
        editTextCelular = (EditText) findViewById(R.id.tvCelular);
        editTextCorreo = (EditText) findViewById(R.id.tvCorreo);
        editTextPeriodo= (EditText) findViewById(R.id.tvPeriodo);
        imageViewPerfil = (ImageView) findViewById(R.id.imageView_perfil);
        botonCambiarContrasenia = (Button) findViewById(R.id.button_contrasenia);
        botonEditar = (Button) findViewById(R.id.button_editar);
        botonCambiarImagen = (Button) findViewById(R.id.boton_perfil);


        new bajarDatos().execute();
        new bajarImagenAsyncTask().execute();

    }

    public void editarDatos(View sender){
        if(botonEditar.getText().equals("editar mi cuenta")){
            botonEditar.setText("guardar cambios");
            Log.d("ActiviadesActivity", botonEditar.getText().toString());
            editTextNombre.setEnabled(true);
            editTextDireccion.setEnabled(true);
            editTextTelefono.setEnabled(true);
            editTextCelular.setEnabled(true);
            editTextCorreo.setEnabled(true);
//            editTextPeriodo.setEnabled(true);


        }else if(botonEditar.getText().equals("guardar cambios")){
            botonEditar.setText("editar mi cuenta");

            editTextNombre.setEnabled(false);
            editTextDireccion.setEnabled(false);
            editTextTelefono.setEnabled(false);
            editTextCelular.setEnabled(false);
            editTextCorreo.setEnabled(false);
//            editTextPeriodo.setEnabled(true);
            if(validarDatos())
                new subirDatosAsyncTask().execute();
            new bajarDatos().execute();
        }

    }
    public void cambiarFoto(View sender) {
        Toast.makeText(this, "Ups! Esto aún no funciona.", Toast.LENGTH_SHORT).show();
    }

    public void cambiarContrasenia(View sender) {
        Toast.makeText(this, "Ups!  Esto aún no funciona.", Toast.LENGTH_SHORT).show();
    }

    private boolean validarDatos() {
        boolean datosOk = true;
        if(editTextNombre.getText().toString().isEmpty()){
            Toast.makeText(this, "El nombre no puede ir vacío", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(editTextCorreo.getText().toString().isEmpty()){
            Toast.makeText(this, "El correo no puede ir vacío", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(editTextCelular.getText().toString().isEmpty()){
            Toast.makeText(this, "El celular no puede ir vacío", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(editTextTelefono.getText().toString().isEmpty()){
            Toast.makeText(this, "El teléfono no puede ir vacío", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(editTextDireccion.getText().toString().isEmpty()){
            Toast.makeText(this, "La dirección no puede ir vacía", Toast.LENGTH_SHORT).show();
            return false;
        }
        return datosOk;
    }

    class bajarImagenAsyncTask extends  AsyncTask<Void, Void, Void> {
        Bitmap imagen;
        @Override
        protected Void doInBackground(Void... voids) {
            imagen = CentralDeConexiones.miServicioSocial.descargarImagen();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            imageViewPerfil.setImageBitmap(imagen);
        }
    }


     class bajarDatos extends AsyncTask<Void, Void, Void>{
            HashMap<String, String> datos;
         @Override
         protected Void doInBackground(Void... voids) {
             datos = CentralDeConexiones.miServicioSocial.recuperarMisDatos();
             return null;
         }

         @Override
         protected void onPostExecute(Void aVoid) {
             textViewMatricula.setText(datos.get("matricula"));
             textViewSemestre.setText(datos.get("semestre"));
             textViewCarrera.setText(datos.get("carrera"));
             textViewId.setText(datos.get("idUnico"));
             editTextNombre.setText(datos.get("nombre"));
             editTextDireccion.setText(datos.get("direccion"));
             editTextCelular.setText(datos.get("celular"));
             editTextTelefono.setText(datos.get("telefono"));
             editTextCorreo.setText(datos.get("correo"));
             editTextPeriodo.setText(datos.get("periodo"));
         }
     }
     class subirDatosAsyncTask extends AsyncTask<Void, Void, Void>{
        String resultado;
         @Override
         protected void onPreExecute() {
             super.onPreExecute();
         }

         @Override
         protected Void doInBackground(Void... voids) {
             String nombre = editTextNombre.getText().toString();
             String direccion = editTextDireccion.getText().toString();
             String telefono = editTextTelefono.getText().toString();
             String celular = editTextCelular.getText().toString();
             String correo = editTextCorreo.getText().toString();
             String periodo = editTextPeriodo.getText().toString();
             CentralDeConexiones.miServicioSocial.actualizarMisDatos(nombre, direccion, telefono, celular, correo, periodo);
             resultado = CentralDeConexiones.miServicioSocial.ultimoMensaje();
             return null;
         }

         @Override
         protected void onPostExecute(Void aVoid) {
             super.onPostExecute(aVoid);
             Toast.makeText(PerfilActivity.this, resultado, Toast.LENGTH_SHORT).show();
         }
     }

}
