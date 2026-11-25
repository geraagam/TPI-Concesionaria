package concesionaria.excepciones;

/**
 * Excepcion propia que se lanza al intentar dar de alta un vehiculo cuyo
 * codigo de stock ya esta registrado.
 *
 * @author Grupo TFI - Programacion II
 * @version 1.0
 */
public class VehiculoDuplicadoException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * @param mensaje explicacion del problema
     */
    public VehiculoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
