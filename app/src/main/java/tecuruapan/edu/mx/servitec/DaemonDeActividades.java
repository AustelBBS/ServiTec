package tecuruapan.edu.mx.servitec;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import lib.CentralDeConexiones;
import tecuruapan.edu.mx.servitec.ActividadesEscolares.InterfaceDeActualizacion;
import tecuruapan.edu.mx.servitec.ActividadesEscolares.NotificacionPush;

import static lib.CentralDeConexiones.miServicioSocial;

    public class
    DaemonDeActividades extends Service {
    public final static String TAG = "ServicioActividades";
    public static final long  INTERVALO = 1000 * 30; // 30 segundos
    private Handler handler = new Handler();
    private Timer timer = null;
    private NotificacionPush notificacion;

    public static ArrayList<InterfaceDeActualizacion> actividadesPorActualizar = new ArrayList<>();

    public DaemonDeActividades() {

    }
    public static void registrarInterfaz(InterfaceDeActualizacion inter) {
        actividadesPorActualizar.add(inter);
    }
    public static void removerInterfaz(InterfaceDeActualizacion inter) {
        actividadesPorActualizar.remove(inter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        notificacion = new NotificacionPush();
        super.onCreate();
        // cancelar si ya existe
        if(timer != null){
            timer.cancel();
        }else {// crear nuevo
            timer = new Timer();
        }
        //eshquedulear task
        timer.scheduleAtFixedRate(new Tarea(),0, INTERVALO);
    }

    private class Tarea extends TimerTask{
        @Override
        public void run() {
        handler.post(new Runnable() {
            @Override
            public void run() {
             new tareaNetwork().execute();
            }
        });

        }


    }


    class tareaNetwork extends AsyncTask<Void, Void, Void> {
        HashMap<String, String> actividadeNuevas;
        @Override
        protected Void doInBackground(Void... voids) {
            actividadeNuevas = miServicioSocial.recuperarActividades();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "Buscando cambios");
            SharedPreferences actividadesViejas = getSharedPreferences(CentralDeConexiones.ACTIVIDADES_GUARDADAS, 0);
            Set<String> llaves = actividadeNuevas.keySet();
            for(String llave: llaves){
                String valorViejo = actividadesViejas.getString(llave, "Error");
                String valorNuevo = actividadeNuevas.get(llave);

                if(!valorViejo.equals(valorNuevo)) {
                    guardar(llave, valorNuevo);
                    for(InterfaceDeActualizacion inter: actividadesPorActualizar) {
                        inter.actualizar();
                    }
                    notificacion.enviarNotificacion(DaemonDeActividades.this, llave + " ha pasado a: " + valorNuevo);
                    Log.d(TAG,"llave:'" + llave + "'");
                    Log.d(TAG, "valorNuevo en hashmap:'" + valorNuevo + "'");
                    Log.d(TAG, "valorViejo en preferences:'" + valorViejo + "'");
                }else {
//                    Log.d(TAG, "No se cambio nada");
                }

            }
        }

        void guardar(String actividad, String valor) {
            SharedPreferences.Editor editor = getSharedPreferences(CentralDeConexiones.ACTIVIDADES_GUARDADAS, 0).edit();
            editor.putString(actividad,valor);
            editor.commit();
        }
    }


}
