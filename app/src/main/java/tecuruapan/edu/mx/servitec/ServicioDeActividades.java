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
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BiConsumer;

import lib.CentralDeConexiones;

import static lib.CentralDeConexiones.miServicioSocial;

public class ServicioDeActividades extends Service {
    public static final long  INTERVALO = 1000 * 20; // 60 segundos
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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CentralDeConexiones.miServicioSocial.recuperarActividades();
                    }
                }).start();
                Toast.makeText(ServicioDeActividades.this, CentralDeConexiones.miServicioSocial.actividades.size(), Toast.LENGTH_SHORT).show();
            }
        });

        }


    }


    class tareaNetwork extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

        }
    }


}
