package tecuruapan.edu.mx.servitec;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import lib.CentralDeConexiones;
import tecuruapan.edu.mx.servitec.ActividadesEscolares.InterfaceDeActualizacion;

public class MainActivity extends AppCompatActivity implements InterfaceDeActualizacion {
    static Intent intentService = null;
    ProgressBar barra;
    TextView etiquetaP, fecha;
    Button actividades, resumen, perfil, cerrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actividades = (Button) findViewById(R.id.actividad);
        resumen = (Button) findViewById(R.id.resumen);
        perfil = (Button) findViewById(R.id.perfil);
        cerrar = (Button) findViewById(R.id.cerrar);
        barra = (ProgressBar) findViewById(R.id.progress);
        etiquetaP = (TextView) findViewById(R.id.etiqueta_progreso);
        fecha = (TextView) findViewById(R.id.tvFecha);
        actividades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiar("actividad");
            }
        });
        resumen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiar("resumen");
            }
        });

        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiar("perfil");
            }
        });

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CerrarAsync().execute();
            }
        });
        new ProgresoAsync ().execute();
        intentService = new Intent(this, DaemonDeActividades.class);
        startService(intentService);
        DaemonDeActividades.registrarInterfaz(this);
        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("MMM MM dd, yyyy h:mm a");
        String fechaCadena = sdf.format(date);
        fecha.setText(fechaCadena);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DaemonDeActividades.removerInterfaz(this);
    }

    private void cambiar(String activity) {
        Intent intent;
        switch (activity) {
            case "actividad":
                intent = new Intent(this, ActividadesActivity.class);
                startActivity(intent);
                break;
            case "resumen":
                intent = new Intent(this, ActividadesActivity.class);
                startActivity(intent);
                break;
            case "perfil":
                intent = new Intent(this, PerfilActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(getApplicationContext(), "Esto no deberia de pasar", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void actualizar() {
        new ProgresoAsync ().execute();

    }

    class ProgresoAsync extends  AsyncTask<Void, Void, Void>{

            int progreso;
            @Override
            protected Void doInBackground(Void... voids) {
                progreso = CentralDeConexiones.miServicioSocial.progresoDeLiberacion();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                barra.setProgress(progreso);
                etiquetaP.setText("Progreso General: " + progreso + "%");
                super.onPostExecute(aVoid);
            }


    }
    class CerrarAsync extends AsyncTask<Void, Void, Void>{
        String error = "";
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                CentralDeConexiones.miServicioSocial.cerrarSesion();

            } catch (Exception e) {
                e.printStackTrace();
                error = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void Void) {
            if(error.isEmpty()){
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);
                Toast.makeText(MainActivity.this, "Se ha cerrado tu sesión", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(MainActivity.this, "Wow hubo un problema al cerrar tu sesión", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
