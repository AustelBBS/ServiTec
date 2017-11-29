package tecuruapan.edu.mx.servitec.ActividadesEscolares;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import lib.CentralDeConexiones;
import lib.ServicioSocial;
import tecuruapan.edu.mx.servitec.ActividadesActivity;
import tecuruapan.edu.mx.servitec.R;


public class CartaPresentacionActivity extends AppCompatActivity {
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
    tipoOrgSpinner;

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
        tipoOrgSpinner = (Spinner) findViewById(R.id.spinner_organismo);

        ArrayList<String> ambitos = new ArrayList<String>();
        ambitos.add("Federal");
        ambitos.add("Municipal");
        ambitos.add("Estatal");
        ambitos.add("Privado");
        ambitoSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ambitos));

        ArrayList<String> tipos = new ArrayList<String>();
        tipos.add("Público");
        tipos.add("Privado");
        tipos.add("Otro");
        tipoOrgSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tipos));

        activarEntradas(false);
        ponerDatos();
        actualizarEstado();
    }

    private void ponerDatos() {
        // poner datos en las entradas;
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
                "Recuerda que sólo es un ejemplo.");
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
        tipoOrgSpinner.setEnabled(activados);

    }
}
