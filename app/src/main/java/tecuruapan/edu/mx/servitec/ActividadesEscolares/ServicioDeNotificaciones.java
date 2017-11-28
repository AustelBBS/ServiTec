package tecuruapan.edu.mx.servitec.ActividadesEscolares;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import tecuruapan.edu.mx.servitec.ActividadesActivity;
import tecuruapan.edu.mx.servitec.R;

/**
 * Created by orveh on 11/27/2017.
 */

public class ServicioDeNotificaciones {

    public void enviarNotificacion(Context context) {
        //TODO: usar un icono custom
        NotificationCompat.Builder mConstructor = new NotificationCompat.Builder(context);
        Intent intent = new Intent(context, ActividadesActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, (int) (Math.random() * 100), intent, 0);
        mConstructor.setSmallIcon(R.drawable.ic_notificacion);
        mConstructor.setContentTitle("Cambio en documentos");
        mConstructor.setContentText("Hubo cambios");
        mConstructor.setContentIntent(contentIntent);
        //Instancia de servicio de NotficationManager
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        mNotificationManager.notify(001, mConstructor.build());
    }

}


