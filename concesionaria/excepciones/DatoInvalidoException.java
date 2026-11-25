package concesionaria.excepciones;

/**
 * Excepcion propia que se lanza cuando un dato no cumple las reglas del
 * negocio: un anio imposible, un precio negativo, un campo vacio, etc.
 *
 * Extiende de Exception (y no de RuntimeException) para que sea una excepcion
 * CONTROLADA: el compilador obliga a atraparla o declararla, y asi nos
 * aseguramos de que nadie la ignore por error.
 *
 * @author Grupo TFI - Programacion II
 * @version 1.0
 */
public class DatoInvalidoException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * @param mensaje explicacion del problema para mostrarle al usuario
     */
    public DatoInvalidoException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con causa: permite envolver una excepcion de Java (por
     * ejemplo NumberFormatException) dentro de una excepcion del dominio.
     *
     * @param mensaje explicacion del problema
     * @param causa   excepcion original que provoco el error
     */
    public DatoInvalidoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
