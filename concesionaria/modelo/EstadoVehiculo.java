package concesionaria.modelo;

/**
 * Tipo enumerativo que representa la condicion comercial de una unidad.
 *
 * Se usa un enum y no un String ni un numero porque el dominio solo admite
 * dos valores posibles: asi el compilador evita que se cargue "nuevvo" o un 3.
 *
 * @author Grupo TFI - Programacion II
 * @version 1.0
 */
public enum EstadoVehiculo {

    NUEVO("0 Km"),
    USADO("Usado");

    /** Texto que se le muestra al usuario por consola. */
    private final String descripcion;

    /**
     * Constructor del enum. Siempre es privado: las unicas instancias posibles
     * son las constantes declaradas arriba.
     *
     * @param descripcion texto legible para el usuario
     */
    private EstadoVehiculo(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return descripcion legible del estado
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Metodo static de ayuda: convierte un texto en una constante del enum
     * sin distinguir mayusculas ni espacios.
     *
     * @param texto texto a convertir (por ejemplo "nuevo")
     * @return la constante correspondiente, o null si el texto no coincide
     */
    public static EstadoVehiculo desdeTexto(String texto) {
        if (texto == null) {
            return null;
        }
        for (EstadoVehiculo estado : EstadoVehiculo.values()) {
            if (estado.name().equalsIgnoreCase(texto.trim())) {
                return estado;
            }
        }
        return null;
    }

    /**
     * Se sobreescribe toString() para que el menu muestre "0 Km" en lugar de
     * "NUEVO". Ojo: para guardar en el archivo se usa name(), no toString().
     *
     * @return descripcion legible
     */
    @Override
    public String toString() {
        return descripcion;
    }
}
