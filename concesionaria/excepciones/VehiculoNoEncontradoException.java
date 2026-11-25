package concesionaria.excepciones;

/**
 * Excepcion propia que se lanza cuando se busca, modifica o elimina un
 * vehiculo que no existe en el stock.
 *
 * @author Grupo TFI - Programacion II
 * @version 1.0
 */
public class VehiculoNoEncontradoException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * @param mensaje explicacion del problema
     */
    public VehiculoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
