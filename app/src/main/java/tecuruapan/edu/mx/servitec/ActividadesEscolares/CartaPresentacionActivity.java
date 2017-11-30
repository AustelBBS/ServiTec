package tecuruapan.edu.mx.servitec.ActividadesEscolares;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import lib.CentralDeConexiones;
import lib.ServicioSocial;
import tecuruapan.edu.mx.servitec.ActividadesActivity;
import tecuruapan.edu.mx.servitec.DaemonDeActividades;
import tecuruapan.edu.mx.servitec.R;


public class CartaPresentacionActivity extends AppCompatActivity implements InterfaceDeActualizacion {
    private long downloadID;

    TextView estadoTextView;
    ImageView estadoImageView;
    EditText nombreDependenciaEditText,
            encargadoEditText,
    puestoEditText,
    direccionEditText,
    telefonoEditText,
    programaEditText,
    subprogramaEditText,
    fechaIniEditText,
    fechaFinEditText;

    Spinner ambitoSpinner,
    orgSpinner;
    ArrayList<String> ambitos = new ArrayList<String>();
    ArrayList<String> organismos = new ArrayList<String>();

    Button botonDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carta_presentacion);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        estadoTextView = (TextView) findViewById(R.id.textview_ev_estado);
        estadoImageView= (ImageView) findViewById(R.id.imagen_estado_sr);
        botonDatos =(Button) findViewById(R.id.button_datos);
        botonDatos.setTag("editar");

        nombreDependenciaEditText = (EditText) findViewById(R.id.editText_dependencia);
        encargadoEditText = (EditText) findViewById(R.id.editText_encargado2);
        puestoEditText = (EditText) findViewById(R.id.editText_puesto);
        direccionEditText = (EditText) findViewById(R.id.editText_direccion2);
        telefonoEditText = (EditText) findViewById(R.id.editText_telefono);
        programaEditText = (EditText) findViewById(R.id.editText_nombre_programa2);
        subprogramaEditText = (EditText) findViewById(R.id.editText_nombre_subprograma);
        fechaIniEditText= (EditText) findViewById(R.id.editText_fecha_inicio);
        fechaFinEditText = (EditText) findViewById(R.id.editText_fecha_fin);
        ambitoSpinner = (Spinner) findViewById(R.id.spinner_ambito);
        orgSpinner = (Spinner) findViewById(R.id.spinner_organismo);

        ambitos.add("federal");
        ambitos.add("municipal");
        ambitos.add("estatal");
        ambitos.add("privado");
        ambitoSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ambitos));


        organismos.add("publico");
        organismos.add("privado");
        organismos.add("otro");
        orgSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, organismos));

        activarEntradas(false);
        ponerDatos();
        actualizarEstado();
        DaemonDeActividades.registrarInterfaz(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DaemonDeActividades.removerInterfaz(this);
    }

    private void ponerDatos() {
        // poner datos en las entradas;
        new BajarDatos().execute();
    }

    public void actualizarEstado() {
        SharedPreferences preferences = getSharedPreferences(CentralDeConexiones.ACTIVIDADES_GUARDADAS, 0);
        String estado = preferences.getString(ServicioSocial.CARTA_PRE, "error");
        estadoTextView.setText(estado);
        estadoImageView.setImageResource(ActividadesActivity.imagenId(estado));
    }
    public void descargarCarta(View sender) {
        downloadID = CentralDeConexiones.descargar(this,
                CentralDeConexiones.miServicioSocial.linkEjemploCartaAceptacion(),
                "Ejemplo carta de aceptación",
                "Recuerda que sólo es un ejemplo.", ServicioSocial.ARCHIVO_CARTA_A);
    }

    public void botonDatos(View sender) {
        String tipo = sender.getTag().toString();
        switch (tipo){
            case "editar":
                botonDatos.setText("Guardar Datos");
                botonDatos.setTag("guardar");
                activarEntradas(true);
                break;
            case "guardar":
                botonDatos.setText("Editar Datos");
                botonDatos.setTag("editar");
                activarEntradas(false);
                if(validarEntradas())
                    new subirDatosAsyncTask().execute();
                break;
        }

    }


    private void activarEntradas(boolean activados) {
        nombreDependenciaEditText.setEnabled(activados);
        encargadoEditText.setEnabled(activados);
        puestoEditText.setEnabled(activados);
        direccionEditText.setEnabled(activados);
        telefonoEditText.setEnabled(activados);
        programaEditText.setEnabled(activados);
        subprogramaEditText.setEnabled(activados);
        fechaIniEditText.setEnabled(activados);
        fechaFinEditText.setEnabled(activados);
        ambitoSpinner.setEnabled(activados);
        orgSpinner.setEnabled(activados);

    }

    @Override
    public void actualizar() {
        actualizarEstado();
    }

    class BajarDatos extends AsyncTask<Void, Void, Void>{
        HashMap<String, String> datos;
        @Override
        protected Void doInBackground(Void... voids) {
            datos = CentralDeConexiones.miServicioSocial.recuperarDatosCartaPresentacion();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            nombreDependenciaEditText.setText(datos.get("dependencia"));
            String ambito = datos.get("ambito");
            String organismo = datos.get("organismo");
            ponerAmbito(ambito);
            ponerOrganismo(organismo);
            encargadoEditText.setText(datos.get("encargado"));
            puestoEditText.setText(datos.get("puesto"));
            direccionEditText.setText(datos.get("direccion"));
            telefonoEditText.setText(datos.get("telefono"));
            programaEditText.setText(datos.get("programa"));
            subprogramaEditText.setText(datos.get("subprograma"));
            fechaIniEditText.setText(datos.get("fechaInicio"));
            fechaFinEditText.setText(datos.get("fechaFinal"));
        }
    }

    private void ponerOrganismo(String organismo) {
        orgSpinner.setSelection(organismos.indexOf(organismo));
    }

    private void ponerAmbito(String ambito) {
        ambitoSpinner.setSelection(ambitos.indexOf(ambito));
    }

    private boolean validarEntradas() {
        boolean datosOk = true;
        if(nombreDependenciaEditText.getText().toString().isEmpty()){
            Toast.makeText(this, "Nombre de dependencia vacío.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(encargadoEditText.getText().toString().isEmpty()){
            Toast.makeText(this, "Encargado de dependencia vacío.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(puestoEditText.getText().toString().isEmpty()){
            Toast.makeText(this, "Puesto del encargado vacío.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(direccionEditText.getText().toString().isEmpty()){
            Toast.makeText(this, "Dirección vacía.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(telefonoEditText.getText().toString().isEmpty()){
            Toast.makeText(this, "Teléfono vacío.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(programaEditText.getText().toString().isEmpty()){
            Toast.makeText(this, "Nombre del programa vacío.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(subprogramaEditText.getText().toString().isEmpty()){
            Toast.makeText(this, "Subprograma vacío.", Toast.LENGTH_SHORT).show();
            return false;
        }if(fechaIniEditText.getText().toString().isEmpty()){
            Toast.makeText(this, "Fecha de inicio vacía.", Toast.LENGTH_SHORT).show();
            return false;
        }if(fechaFinEditText.getText().toString().isEmpty()){
            Toast.makeText(this, "Fecha de final vacía.", Toast.LENGTH_SHORT).show();
            return false;
        }


        return datosOk;

    }
    class subirDatosAsyncTask extends AsyncTask<Void, Void, Void> {
        boolean datosActualizados;
        @Override
        protected Void doInBackground(Void... voids) {
            datosActualizados = CentralDeConexiones.miServicioSocial.actualizarDatosDependencia(nombreDependenciaEditText.getText().toString(),
                    ambitoSpinner.getSelectedItem().toString(),
                    orgSpinner.getSelectedItem().toString(),
                    encargadoEditText.getText().toString(),
                    puestoEditText.getText().toString(),
                    direccionEditText.getText().toString(),
                    telefonoEditText.getText().toString(),
                    programaEditText.getText().toString(),
                    subprogramaEditText.getText().toString(),
                    fechaIniEditText.getText().toString(),
                    fechaFinEditText.getText().toString());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(CartaPresentacionActivity.this, CentralDeConexiones.miServicioSocial.ultimoMensaje(), Toast.LENGTH_LONG).show();
            if(!datosActualizados) new BajarDatos ().execute();
        }
    }
}
