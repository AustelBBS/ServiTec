package lib;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import tecuruapan.edu.mx.servitec.ActividadesEscolares.InterfaceDeActualizacion;
import tecuruapan.edu.mx.servitec.ActividadesEscolares.TercerInformeActivity;
import tecuruapan.edu.mx.servitec.DaemonDeActividades;
import tecuruapan.edu.mx.servitec.LoginActivity;

/**
 * Created by mar on 16/11/17.
 */

public class CentralDeConexiones {
    public static final String ACTIVIDADES_GUARDADAS = "estado_actividades";
    public static ServicioSocial miServicioSocial;


    private static final CentralDeConexiones ourInstance = new CentralDeConexiones();

    public static CentralDeConexiones getInstance() {
        return ourInstance;
    }

    private CentralDeConexiones() {
    }

    public static long descargar(Context context, String url, String titulo, String descripcion, final String nombre) {
        Uri formatoUri = Uri.parse(url);
        long descargaId;
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(formatoUri);

        request.setTitle(titulo);
        request.setDescription(descripcion);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, nombre);

        // intent receiver para que sque uatostada cuando termine  la descarga
        IntentFilter filter =  new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context, nombre + " descargado", Toast.LENGTH_SHORT).show();
            }
        };
        context.registerReceiver(downloadReceiver, filter);
        return  downloadManager.enqueue(request);
    }

    public static class SubirArchivoAsync extends AsyncTask<Void, Void, Void> {

        public final static  int REQUEST_CODE = 666;
        int serverResponseCode;
        Context context;
        ProgressDialog dialog;
        String titulo;
        Uri uri;
        String direccion;
        String paginaRespuesta;
        String fileName;

        public SubirArchivoAsync(Context c, String titulo, Uri uri, String direccion) {
            this.context = c;
            this.titulo = titulo;
            this.uri = uri;
            this.direccion = direccion;
        }

        public static void seleccionarArchivo(Activity activity) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/pdf");
            activity.startActivityForResult(intent, REQUEST_CODE);
        }

        public static String buscarNombre (Uri uri, Context context) {
            Cursor cursor = context.getContentResolver().query(uri,  null, null, null, null);
            try{
                assert cursor != null;
                cursor.moveToFirst();
                String nombre = cursor.getString(cursor.getColumnIndex("_display_name"));
                Log.d("CentralDeConexiones", "display name el archivo por subir: " + nombre);
                return nombre;
            }catch (Exception e){

            } finally {
                cursor.close();
            }
            return "";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fileName = buscarNombre(uri, context);
            dialog = ProgressDialog.show(context, titulo, fileName, true);
            dialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... params) {

            HttpURLConnection conn;
            DataOutputStream dos;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1024 * 1024;

            try {

                // open a URL connection to the Servlet
                InputStream fileInputStream = context.getContentResolver().openInputStream(uri);
                URL url = new URL(direccion);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setInstanceFollowRedirects(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8\n");
                conn.setRequestProperty("Accept-Encoding" ,"gzip, deflate, br");
                conn.setRequestProperty("Accept-Language", "en-US,en;q=0.9,es;q=0.8");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Cache-Control","max-age=0");
                conn.setRequestProperty("Connection", "Keep-Alive");
                // content lenght? meh
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("Cookie", miServicioSocial.COOKIE_PHP + "=" + miServicioSocial.getCookie());

                conn.setRequestProperty("DNT","1");
                conn.setRequestProperty("User-Agent","Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.75 Safari/537.36");

                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"archivo\";filename=\"" + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                StringBuilder respuesta = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                for (String line; (line = reader.readLine()) != null;) {
//                        Log.d("MAR",line);
                    respuesta.append(line);
                }
                paginaRespuesta = respuesta.toString();

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("tag", "Exception : " + e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            dialog.dismiss();
            if(serverResponseCode == 200 && paginaRespuesta.contains("Copiado correctamente")){
                Toast.makeText(context, "Copiado correctamente", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context, "Error, no se pudo subir el archivo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class SubirFotoAsync extends AsyncTask<Void, Void, Void>{

        public final static  int REQUEST_CODE = 999;
        int serverResponseCode;
        Context context;
        ProgressDialog dialog;
        String titulo;
        Uri uri;
        String direccion;
        String paginaRespuesta;
        String fileName;
        InterfaceDeActualizacion alTerminar;

        public SubirFotoAsync(Context c, String titulo, Uri uri, String direccion, InterfaceDeActualizacion alTerminar) {
            this.context = c;
            this.titulo = titulo;
            this.uri = uri;
            this.direccion = direccion;
            this.alTerminar = alTerminar;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fileName = SubirArchivoAsync.buscarNombre(uri, context);
            dialog = ProgressDialog.show(context, titulo, fileName, true);
            dialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... params) {

            HttpURLConnection conn;
            DataOutputStream dos;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1024 * 1024;

            try {

                // open a URL connection to the Servlet
                InputStream fileInputStream = context.getContentResolver().openInputStream(uri);
                URL url = new URL(direccion);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setInstanceFollowRedirects(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8\n");
                conn.setRequestProperty("Accept-Encoding" ,"gzip, deflate, br");
                conn.setRequestProperty("Accept-Language", "en-US,en;q=0.9,es;q=0.8");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Cache-Control","max-age=0");
                conn.setRequestProperty("Connection", "Keep-Alive");
                // content lenght? meh
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("Cookie", miServicioSocial.COOKIE_PHP + "=" + miServicioSocial.getCookie());

                conn.setRequestProperty("DNT","1");
                conn.setRequestProperty("User-Agent","Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.75 Safari/537.36");

                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"archivo\";filename=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes("Content-Type: image/jpeg" + lineEnd);
                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                StringBuilder respuesta = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                for (String line; (line = reader.readLine()) != null;) {
//                        Log.d("MAR",line);
                    respuesta.append(line);
                }
                paginaRespuesta = respuesta.toString();

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("tag", "Exception : " + e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            dialog.dismiss();
            if(serverResponseCode == 200 && paginaRespuesta.contains("REMPLAZADO CORRECTAMENTE")){
                Toast.makeText(context, "Reemplazado correctamente", Toast.LENGTH_SHORT).show();
                alTerminar.actualizar();
            }else{
                Toast.makeText(context, "Error, no se pudo subir el archivo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class tareaNetwork extends AsyncTask<Void, Void, Void> {
        HashMap<String, String> actividadeNuevas;
        Context context;

        public tareaNetwork (Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            actividadeNuevas = miServicioSocial.recuperarActividades();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("CentralDeConexiones", "Buscando cambios");
            SharedPreferences actividadesViejas = context.getSharedPreferences(CentralDeConexiones.ACTIVIDADES_GUARDADAS, 0);
            Set<String> llaves = actividadeNuevas.keySet();
            for(String llave: llaves){
                String valorViejo = actividadesViejas.getString(llave, "Error");
                String valorNuevo = actividadeNuevas.get(llave);

                if(!valorViejo.equals(valorNuevo)) {
                    guardar(llave, valorNuevo);
//                    for(InterfaceDeActualizacion inter: actividadesPorActualizar) {
//                        inter.actualizar();
//                    }

                }else {
//                    Log.d(TAG, "No se cambio nada");
                }

            }
        }

        void guardar(String actividad, String valor) {
            SharedPreferences.Editor editor = context.getSharedPreferences(CentralDeConexiones.ACTIVIDADES_GUARDADAS, 0).edit();
            editor.putString(actividad,valor);
            editor.commit();
        }
    }
}
