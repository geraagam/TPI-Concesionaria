package concesionaria.modelo;

/**
 * Camioneta o utilitario. Agrega a los datos comunes la capacidad de carga y
 * si tiene traccion integral (4x4).
 *
 * @author Grupo TFI - Programacion II
 * @version 1.0
 */
public class Camioneta extends Vehiculo {

    /** Una camioneta se deprecia mas lento que un auto. */
    private static final double DEPRECIACION_ANUAL = 0.08;

    private TipoCarroceria carroceria;
    private Double capacidadCargaKg;
    private Boolean traccionIntegral;

    /**
     * Constructor sin argumentos (JavaBean).
     */
    public Camioneta() {
        super();
    }

    /**
     * Constructor completo.
     *
     * @param codigo           identificador unico de stock
     * @param marca            marca de la camioneta
     * @param modelo           modelo de la camioneta
     * @param anio             anio de fabricacion
     * @param color            color de la unidad
     * @param estado           NUEVO (0 Km) o USADO
     * @param kilometraje      kilometros recorridos
     * @param precioBase       precio de lista
     * @param carroceria       tipo de carroceria
     * @param capacidadCargaKg capacidad de carga en kilogramos
     * @param traccionIntegral true si es 4x4
     */
    public Camioneta(String codigo, String marca, String modelo, Integer anio,
                     String color, EstadoVehiculo estado, Integer kilometraje,
                     Double precioBase, TipoCarroceria carroceria,
                     Double capacidadCargaKg, Boolean traccionIntegral) {
        super(codigo, marca, modelo, anio, color, estado, kilometraje, precioBase);
        this.carroceria = carroceria;
        this.capacidadCargaKg = capacidadCargaKg;
        this.traccionIntegral = traccionIntegral;
    }

    public TipoCarroceria getCarroceria() {
        return carroceria;
    }

    public void setCarroceria(TipoCarroceria carroceria) {
        this.carroceria = carroceria;
    }

    public Double getCapacidadCargaKg() {
        return capacidadCargaKg;
    }

    public void setCapacidadCargaKg(Double capacidadCargaKg) {
        this.capacidadCargaKg = capacidadCargaKg;
    }

    public Boolean getTraccionIntegral() {
        return traccionIntegral;
    }

    public void setTraccionIntegral(Boolean traccionIntegral) {
        this.traccionIntegral = traccionIntegral;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTipo() {
        return "Camioneta";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDetalleEspecifico() {
        String traccion = (traccionIntegral != null && traccionIntegral.booleanValue()) ? "4x4" : "4x2";
        return carroceria + " / " + (capacidadCargaKg == null ? 0.0 : capacidadCargaKg) + " Kg / " + traccion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double porcentajeDepreciacionAnual() {
        return DEPRECIACION_ANUAL;
    }
}
