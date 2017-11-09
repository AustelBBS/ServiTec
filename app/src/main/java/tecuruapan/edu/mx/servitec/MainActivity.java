package tecuruapan.edu.mx.servitec;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button actividades, resumen, perfil, cerrar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actividades = (Button) findViewById(R.id.actividad);
        resumen = (Button) findViewById(R.id.resumen);
        perfil = (Button) findViewById(R.id.perfil);
        cerrar = (Button) findViewById(R.id.cerrar);

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
                cerrarCesion();
            }
        });
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

    private void cerrarCesion() {
        finish();
    }
}
