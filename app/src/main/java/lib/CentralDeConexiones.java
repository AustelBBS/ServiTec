package lib;

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
}
