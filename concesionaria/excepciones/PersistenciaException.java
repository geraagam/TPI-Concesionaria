package concesionaria.excepciones;

/**
 * Excepcion propia de la capa de datos. Envuelve cualquier problema de
 * lectura o escritura del archivo de stock (IOException, archivo bloqueado,
 * permisos, etc.).
 *
 * De esta forma la capa de negocio y el menu no necesitan conocer IOException
 * ni nada de java.io: solo saben que "fallo la persistencia". Eso es
 * desacoplar las capas
 *
 * @author Grupo TFI - Programacion II
 * @version 1.0
 */
public class PersistenciaException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * @param mensaje explicacion del problema
     */
    public PersistenciaException(String mensaje) {
        super(mensaje);
    }

    /**
     * @param mensaje explicacion del problema
     * @param causa   excepcion original (normalmente una IOException)
     */
    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
