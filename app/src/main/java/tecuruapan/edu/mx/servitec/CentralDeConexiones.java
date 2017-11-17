package tecuruapan.edu.mx.servitec;

import lib.ServicioSocial;

/**
 * Created by mar on 16/11/17.
 */

public class CentralDeConexiones {
    static ServicioSocial miServicioSocial;


    private static final CentralDeConexiones ourInstance = new CentralDeConexiones();

    public static CentralDeConexiones getInstance() {
        return ourInstance;
    }

    private CentralDeConexiones() {
    }
}
