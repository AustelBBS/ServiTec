package tecuruapan.edu.mx.servitec;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;

import lib.CentralDeConexiones;
import lib.ServicioSocial;

public class ActividadesActivity extends AppCompatActivity {
    ImageView imagenCurso, imagenPresentacion, imagenRegistro, imagenPrimer,
            imagenSegundo, imagenTercer, imagenGlobal, imagenTerminacion, imagenEvaluacion;
    HashMap<String, String> actividades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividades);

        imagenCurso = (ImageView) findViewById(R.id.imagen_curso);
        imagenPresentacion = (ImageView) findViewById(R.id.imagen_presentacion);
        imagenRegistro = (ImageView) findViewById(R.id.imagen_registro);
        imagenPrimer = (ImageView) findViewById(R.id.imagen_primero);
        imagenSegundo= (ImageView) findViewById(R.id.imagen_segundo);
        imagenTercer = (ImageView) findViewById(R.id.imagen_tercer);
        imagenTerminacion = (ImageView) findViewById(R.id.imagen_terminacion);
        imagenGlobal = (ImageView) findViewById(R.id.imagen_global);
        imagenEvaluacion = (ImageView) findViewById(R.id.imagen_evaluacion);
        actualizarEstados();
        startService(new Intent(this, ServicioDeActividades.class));

    }

    // compara los estados de las actividades guardadas en persistentement
    // en contra de el objeto actividades
    @TargetApi(Build.VERSION_CODES.N)
    private HashMap<String, String> buscarCambios() {
        final HashMap<String, String> actividadesActualizadas = new HashMap<>();
        final SharedPreferences estadosGuardados = getSharedPreferences(CentralDeConexiones.ACTIVIDADES, 0);
        actividades.forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String actividadActual , String estadoActual) {
                if(!estadoActual.equals(estadosGuardados.getString(actividadActual, null))){
                    actividadesActualizadas.put(actividadActual, estadoActual);
                }
            }
        });
        return actividadesActualizadas;
    }

    private void actualizarEstados() {
        NetworkTask bajarEstados = new NetworkTask();
        bajarEstados.execute();
    }



    private int imagenId (String tipo) {
        switch (tipo) {
            case ServicioSocial.APROBADO:
                return R.drawable.ic_aceptado;
            case ServicioSocial.ERROR:
                return R.drawable.ic_rechazado;
            case ServicioSocial.VACIO:
                return R.drawable.ic_defecto;
            default:
                Log.e("ActividadesActivity", "Error, caso no esperado al obtener tipo de imagen.");
                return R.drawable.ic_revisado;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void guardarEstados() {
        final SharedPreferences.Editor estadosEditor = getSharedPreferences(CentralDeConexiones.ACTIVIDADES, 0).edit();
        actividades.forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String actividad, String estado) {
                estadosEditor.putString(actividad, estado);
            }
        });


        estadosEditor.commit();
    }

    private void ponerImagenes(final HashMap<String, String> actividades) {
        this.actividades  = actividades;
        runOnUiThread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void run() {
                imagenTercer.setImageResource(imagenId(actividades.get("Tercer Avance")));
                imagenCurso.setImageResource(imagenId(actividades.get("Asistencia en curso de Inducción")));
                imagenRegistro.setImageResource(imagenId(actividades.get("Solicitud de Registro Servicio Social")));
                imagenGlobal.setImageResource(imagenId(actividades.get("Informe Global")));
                imagenEvaluacion.setImageResource(imagenId(actividades.get("Carta de evaluación receptora")));
                imagenPresentacion.setImageResource(imagenId(actividades.get("Solicitar carta de presentación")));
                imagenSegundo.setImageResource(imagenId(actividades.get("Segundo Avance")));
                imagenTerminacion.setImageResource(imagenId(actividades.getOrDefault("Oficio de Terminación", "error")));
                imagenPrimer.setImageResource(imagenId(actividades.get("Primer avance")));


            }
        });
    }

    class NetworkTask extends AsyncTask<Void, Void, HashMap<String, String>>{

        @Override
        protected HashMap<String, String> doInBackground(Void... voids) {
            return CentralDeConexiones.miServicioSocial.recuperarActividades();
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(HashMap<String, String> resultados) {
            super.onPostExecute(resultados);
            ponerImagenes(resultados);
        }
    }


}
