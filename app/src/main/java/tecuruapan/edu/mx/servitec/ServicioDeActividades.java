package tecuruapan.edu.mx.servitec;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BiConsumer;

import lib.CentralDeConexiones;

import static android.content.ContentValues.TAG;
import static lib.CentralDeConexiones.miServicioSocial;

public class ServicioDeActividades extends Service {
    public final static String TAG = "ServicioActividades";
    public static final long  INTERVALO = 1000 * 30; // 60 segundos
    private Handler handler = new Handler();
    private Timer timer = null;
    public ServicioDeActividades() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
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
            actividadeNuevas = CentralDeConexiones.miServicioSocial.recuperarActividades();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            SharedPreferences actividadesViejas = getSharedPreferences(CentralDeConexiones.ACTIVIDADES, 0);
            Set<String> llaves = actividadeNuevas.keySet();
            for(String llave: llaves){
                String valorViejo = actividadesViejas.getString(llave, "Error");
                String valorNuevo = actividadeNuevas.get(llave);

                if(!(valorViejo == valorNuevo)) {
                    Toast.makeText(ServicioDeActividades.this, "Hubo un cambio en tus documentos" , Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"llave:'" + llave + "'");
                    Log.d(TAG, "valor en hashmap:'" + valorNuevo + "'");
                    Log.d(TAG, "valor en preferences:'" + valorViejo + "'");
                    break;

                }else {
                    Log.d(TAG, "No se cambio nada");
                }
            }
        }
    }


}
