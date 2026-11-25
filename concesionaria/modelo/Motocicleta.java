package concesionaria.modelo;

/**
 * Motocicleta. En lugar de carroceria tiene estilo, y agrega la cilindrada.
 *
 * @author Grupo TFI - Programacion II
 * @version 1.0
 */
public class Motocicleta extends Vehiculo {

    /** Una moto pierde valor mas rapido que un auto. */
    private static final double DEPRECIACION_ANUAL = 0.12;

    private EstiloMoto estilo;
    private Integer cilindrada;

    /**
     * Constructor sin argumentos (JavaBean).
     */
    public Motocicleta() {
        super();
    }

    /**
     * Constructor completo.
     *
     * @param codigo      identificador unico de stock
     * @param marca       marca de la moto
     * @param modelo      modelo de la moto
     * @param anio        anio de fabricacion
     * @param color       color de la unidad
     * @param estado      NUEVO (0 Km) o USADO
     * @param kilometraje kilometros recorridos
     * @param precioBase  precio de lista
     * @param estilo      estilo de motocicleta
     * @param cilindrada  cilindrada en centimetros cubicos
     */
    public Motocicleta(String codigo, String marca, String modelo, Integer anio,
                       String color, EstadoVehiculo estado, Integer kilometraje,
                       Double precioBase, EstiloMoto estilo, Integer cilindrada) {
        super(codigo, marca, modelo, anio, color, estado, kilometraje, precioBase);
        this.estilo = estilo;
        this.cilindrada = cilindrada;
    }

    public EstiloMoto getEstilo() {
        return estilo;
    }

    public void setEstilo(EstiloMoto estilo) {
        this.estilo = estilo;
    }

    public Integer getCilindrada() {
        return cilindrada;
    }

    public void setCilindrada(Integer cilindrada) {
        this.cilindrada = cilindrada;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTipo() {
        return "Motocicleta";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDetalleEspecifico() {
        return estilo + " / " + (cilindrada == null ? 0 : cilindrada) + " cc";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double porcentajeDepreciacionAnual() {
        return DEPRECIACION_ANUAL;
    }
}
