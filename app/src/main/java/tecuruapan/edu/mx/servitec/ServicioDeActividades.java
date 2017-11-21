package tecuruapan.edu.mx.servitec;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BiConsumer;

import lib.CentralDeConexiones;

import static lib.CentralDeConexiones.miServicioSocial;

public class ServicioDeActividades extends Service {
    public static final long  INTERVALO = 1000 * 30; // diez segundos
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
                @TargetApi(Build.VERSION_CODES.N)
                @Override
                public void run() {
                    boolean huboCambio = false;
                    // conectarme a internet de manera asincrona


                    final HashMap<String, String> actividadesNuevas = CentralDeConexiones.miServicioSocial.recuperarActividades();
                    final SharedPreferences actividadesViejas = getSharedPreferences(CentralDeConexiones.ACTIVIDADES,0);
                    String [] llaves = (String []) actividadesNuevas.keySet().toArray();
                    for(String llave : llaves){
                        if(actividadesNuevas.get(llave).equals(actividadesViejas.getString(llave, null))){
                            huboCambio = true;
                              break;
                        }
                    }
                    if(huboCambio)
                        Toast.makeText(ServicioDeActividades.this, "Hubo un cambio en las actividades", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    class NetworkTask extends AsyncTask<Void, Void, Void> {
        public HashMap<String, String> resultado;
        @Override
        protected Void doInBackground(Void... voids) {
            resultado = CentralDeConexiones.miServicioSocial.recuperarActividades();
            return null;
        }
    }

}
