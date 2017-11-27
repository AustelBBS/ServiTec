package tecuruapan.edu.mx.servitec.ActividadesEscolares;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import tecuruapan.edu.mx.servitec.R;

/**
 * Created by orveh on 11/27/2017.
 */

public class ServicioDeNotificaciones {

    public void enviarNotificacion(Context context) {
        //TODO: usar un icono custom
        NotificationCompat.Builder mConstructor = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_notificacion).setContentTitle("Cambio en documentos").setContentText("Hubo cambios");
        //Instancia de servicio de NotficationManager
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        mNotificationManager.notify(001, mConstructor.build());
    }

}


