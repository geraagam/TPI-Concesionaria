package concesionaria.modelo;

/**
 * Tipo enumerativo con los estilos de motocicleta que comercializa la
 * concesionaria. Es el equivalente a la carroceria, pero para motos.
 *
 * @author Grupo TFI - Programacion II
 * @version 1.0
 */
public enum EstiloMoto {

    DEPORTIVA("Deportiva"),
    NAKED("Naked"),
    TOURING("Touring"),
    ENDURO("Enduro"),
    SCOOTER("Scooter"),
    CHOPPER("Chopper");

    private final String descripcion;

    private EstiloMoto(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return descripcion legible del estilo
     */
    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
