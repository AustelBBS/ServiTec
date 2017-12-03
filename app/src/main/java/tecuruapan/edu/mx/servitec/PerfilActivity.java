package tecuruapan.edu.mx.servitec;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import lib.CentralDeConexiones;
import lib.ServicioSocial;

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
        // seleccionar foto
        Intent intent = new Intent();
        intent.setType("image/jpeg");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),999);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == 999) {
            new CentralDeConexiones.SubirArchivoAsync(this,
                    "Subiendo foto de perfil",
                    data.getData(),
                    ServicioSocial.LINK_SUBI_FOTO).execute();
        }
    }

    ProgressBar progressBar;
    EditText passActual, passNuevo, passConfirmacion;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void cambiarContrasenia(View sender) {
        // istas que forman el dialogo
        View vista = getLayoutInflater().inflate(R.layout.cambio_pass_dialog_layout, null);
        progressBar  = (ProgressBar) vista.findViewById(R.id.progressBarCambioPass);
        passActual = (EditText) vista.findViewById(R.id.passactualEditText);
        passNuevo = (EditText) vista.findViewById(R.id.passNuevoEditText);
        passConfirmacion = (EditText) vista.findViewById(R.id.passCofirmacionEditText);
        progressBar.setVisibility(View.INVISIBLE);

        // mostrando dialogo
        final AlertDialog dialogo = new AlertDialog.Builder(this)
                .setTitle("Cambio de contraseña")
                .setPositiveButton("Cambiar", null)
                .setNegativeButton("Cancelar", null)
                .setView(vista)
                .setCancelable(false)
                .create();
        dialogo.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                final Button botonOk = dialogo.getButton(AlertDialog.BUTTON_POSITIVE);
                final Button botonCancelar = dialogo.getButton(AlertDialog.BUTTON_NEGATIVE);
                botonOk.setOnClickListener(new View.OnClickListener() {
                    class CambiarPassAsyncTask extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            progressBar.setVisibility(View.VISIBLE);
                            botonOk.setEnabled(false);
                            botonCancelar.setEnabled(false);
                            passActual.setEnabled(false);
                            passNuevo.setEnabled(false);
                            passConfirmacion.setEnabled(false);
                        }

                        @Override
                        protected Void doInBackground(Void... voids) {
                            CentralDeConexiones.miServicioSocial.cambiarPass(passActual.getText().toString(), passNuevo.getText().toString(), passConfirmacion.getText().toString());
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            Toast.makeText(PerfilActivity.this, CentralDeConexiones.miServicioSocial.ultimoMensaje(), Toast.LENGTH_SHORT).show();
                            dialogo.dismiss();
                        }
                    }

                    @Override
                    public void onClick(View view) {
                        new CambiarPassAsyncTask().execute();
                    }
                });
            }
        });

        dialogo.show();
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
