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

public class NotificacionPush {

    public void enviarNotificacion(Context context, String mensaje) {
        //TODO: usar un icono custom
        NotificationCompat.Builder mConstructor = new NotificationCompat.Builder(context);
        Intent intent = new Intent(context, ActividadesActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(context, (int) (Math.random() * 100), intent, 0);
        mConstructor.setSmallIcon(R.drawable.ic_notificacion);
        mConstructor.setContentTitle("Cambio en documentos");
        mConstructor.setContentText(mensaje);
        mConstructor.setContentIntent(contentIntent);
        mConstructor.setAutoCancel(true);
        //Instancia de servicio de NotficationManager
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        mNotificationManager.notify(001, mConstructor.build());
    }

}


