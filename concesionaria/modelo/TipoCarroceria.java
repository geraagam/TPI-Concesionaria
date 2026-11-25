package concesionaria.modelo;

/**
 * Tipo enumerativo con las carrocerias admitidas para automoviles y camionetas.
 *
 * @author Grupo TFI - Programacion II
 * @version 1.0
 */
public enum TipoCarroceria {

    SEDAN("Sedan"),
    HATCHBACK("Hatchback"),
    SUV("SUV"),
    COUPE("Coupe"),
    FAMILIAR("Familiar"),
    PICKUP("Pick-up"),
    FURGON("Furgon");

    private final String descripcion;

    private TipoCarroceria(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return descripcion legible de la carroceria
     */
    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
