package tecuruapan.edu.mx.servitec;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.function.BiConsumer;

import lib.CentralDeConexiones;
import lib.ServicioSocial;
import tecuruapan.edu.mx.servitec.ActividadesEscolares.CartaEvaluacionActivity;
import tecuruapan.edu.mx.servitec.ActividadesEscolares.CartaPresentacionActivity;
import tecuruapan.edu.mx.servitec.ActividadesEscolares.CursoInduccionActivity;
import tecuruapan.edu.mx.servitec.ActividadesEscolares.InformeGlobalActivity;
import tecuruapan.edu.mx.servitec.ActividadesEscolares.InterfaceDeActualizacion;
import tecuruapan.edu.mx.servitec.ActividadesEscolares.OficioTerminacionActivity;
import tecuruapan.edu.mx.servitec.ActividadesEscolares.PrimerInformeActivity;
import tecuruapan.edu.mx.servitec.ActividadesEscolares.SegundoInformeActivity;
import tecuruapan.edu.mx.servitec.ActividadesEscolares.SolicitudRegistroActivity;
import tecuruapan.edu.mx.servitec.ActividadesEscolares.TercerInformeActivity;

public class ActividadesActivity extends AppCompatActivity implements  View.OnClickListener, InterfaceDeActualizacion{
    ImageView imagenCurso,
            imagenPresentacion,
            imagenRegistro,
            imagenPrimer,
            imagenSegundo,
            imagenTercer,
            imagenGlobal,
            imagenTerminacion,
            imagenEvaluacion;
    HashMap<String, String> actividades;
    TextView curso,
            cartaPre,
            solicitudRe,
            primerA,
            segundoA,
            tercerA,
            oficioT,
            informeG,
            cartaEva,
            fecha;

    public static int imagenId(String tipo) {
        switch (tipo) {
            case ServicioSocial.APROBADO:
                return R.drawable.ic_aceptado;
            case ServicioSocial.ERROR:
                return R.drawable.ic_rechazado;
            case ServicioSocial.VACIO:
                return R.drawable.ic_defecto;
            default:
                Log.e("ActividadesActivity", "Error, caso no esperado al obtener tipo de imagen.");
                Log.e("ActividadesActivity", tipo);
                return R.drawable.ic_revisado;
        }
    }

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



        curso = (TextView) findViewById(R.id.curso);
        cartaPre = (TextView) findViewById(R.id.carta_Pre);
        solicitudRe = (TextView) findViewById(R.id.solicitud_R);
        primerA = (TextView) findViewById(R.id.primer_A);
        segundoA = (TextView) findViewById(R.id.segundo_A);
        tercerA = (TextView) findViewById(R.id.tercer_A);
        oficioT = (TextView) findViewById(R.id.oficio_T);
        informeG = (TextView) findViewById(R.id.informe_G);
        cartaEva = (TextView) findViewById(R.id.carta_eva);
        fecha = (TextView) findViewById(R.id.tvFecha);

        curso.setTag("curso");
        cartaPre.setTag("cartaP");
        solicitudRe.setTag("solicitudR");
        primerA.setTag("primerA");
        segundoA.setTag("segundoA");
        tercerA.setTag("tercerA");
        oficioT.setTag("oficioT");
        informeG.setTag("informeG");
        cartaEva.setTag("cartaE");

        curso.setOnClickListener(this);
        cartaPre.setOnClickListener(this);
        solicitudRe.setOnClickListener(this);
        primerA.setOnClickListener(this);
        segundoA.setOnClickListener(this);
        tercerA.setOnClickListener(this);
        oficioT.setOnClickListener(this);
        informeG.setOnClickListener(this);
        cartaEva.setOnClickListener(this);

        actualizarEstados();
        DaemonDeActividades.registrarInterfaz(this);

        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("MMM MM dd, yyyy h:mm a");
        String fechaCadena = sdf.format(date);
        fecha.setText(fechaCadena);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DaemonDeActividades.registrarInterfaz(this);
    }

    // compara los estados de las actividades guardadas en persistentement
    // en contra de el objeto actividades
    @TargetApi(Build.VERSION_CODES.N)
    private HashMap<String, String> buscarCambios() {
        final HashMap<String, String> actividadesActualizadas = new HashMap<>();
        final SharedPreferences estadosGuardados = getSharedPreferences(CentralDeConexiones.ACTIVIDADES_GUARDADAS, 0);
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




    @TargetApi(Build.VERSION_CODES.N)
    private void guardarEstados() {
        final SharedPreferences.Editor estadosEditor = getSharedPreferences(CentralDeConexiones.ACTIVIDADES_GUARDADAS, 0).edit();
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
                imagenTercer.setImageResource(imagenId(actividades.get(ServicioSocial.TERCER_A)));
                imagenCurso.setImageResource(imagenId(actividades.get(ServicioSocial.CURSO)));
                imagenRegistro.setImageResource(imagenId(actividades.get(ServicioSocial.SOLICITUD_RE)));
                imagenGlobal.setImageResource(imagenId(actividades.get(ServicioSocial.INFORME_G)));
                imagenEvaluacion.setImageResource(imagenId(actividades.get(ServicioSocial.CARTA_EV)));
                imagenPresentacion.setImageResource(imagenId(actividades.get(ServicioSocial.CARTA_PRE)));
                imagenSegundo.setImageResource(imagenId(actividades.get(ServicioSocial.SEGUNDO_A)));
                imagenTerminacion.setImageResource(imagenId(actividades.get(ServicioSocial.OFICIO_T)));
                imagenPrimer.setImageResource(imagenId(actividades.get(ServicioSocial.PRIMER_A)));


            }
        });
    }

    @Override
    public void onClick(View view) {
        String tag = view.getTag().toString();
        switch (tag){
            case "curso":
                startActivity(new Intent(ActividadesActivity.this, CursoInduccionActivity.class));
                break;
            case "cartaP":
                startActivity(new Intent(ActividadesActivity.this, CartaPresentacionActivity.class));
                break;
            case "solicitudR":
                startActivity(new Intent(ActividadesActivity.this, SolicitudRegistroActivity.class));
                break;
            case "primerA":
                startActivity(new Intent(ActividadesActivity.this, PrimerInformeActivity.class));
                break;
            case "segundoA":
                startActivity(new Intent(ActividadesActivity.this, SegundoInformeActivity.class));
                break;
            case "tercerA":
                startActivity(new Intent(ActividadesActivity.this, TercerInformeActivity.class));
                break;
            case "oficioT":
                startActivity(new Intent(ActividadesActivity.this, OficioTerminacionActivity.class));
                break;
            case "informeG":
                startActivity(new Intent(ActividadesActivity.this, InformeGlobalActivity.class));
                break;
            case "cartaE":
                startActivity(new Intent(ActividadesActivity.this, CartaEvaluacionActivity.class));
                break;
        }
    }

    @Override
    public void actualizar() {
        actualizarEstados();
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
            guardarEstados();
        }
    }


}
