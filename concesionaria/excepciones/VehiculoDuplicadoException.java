package concesionaria.excepciones;

public class VehiculoDuplicadoException extends Exception {

    private static final long serialVersionUID = 1L;

    public VehiculoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
