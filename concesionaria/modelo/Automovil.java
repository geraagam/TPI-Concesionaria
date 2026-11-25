package concesionaria.modelo;

/**
 * Automovil de pasajeros. Hereda de Vehiculo todos los datos comunes y agrega
 * los suyos: la carroceria y la cantidad de puertas.
 *
 * @author Grupo TFI - Programacion II
 * @version 1.0
 */
public class Automovil extends Vehiculo {

    /** Porcentaje de valor que pierde un automovil por cada anio. */
    private static final double DEPRECIACION_ANUAL = 0.10;

    private TipoCarroceria carroceria;
    private Integer cantidadPuertas;

    /**
     * Constructor sin argumentos (JavaBean). Permite crear el objeto vacio y
     * cargarlo despues con los setters.
     */
    public Automovil() {
        super();
    }

    /**
     * Constructor completo.
     *
     * @param codigo          identificador unico de stock
     * @param marca           marca del automovil
     * @param modelo          modelo del automovil
     * @param anio            anio de fabricacion
     * @param color           color de la unidad
     * @param estado          NUEVO (0 Km) o USADO
     * @param kilometraje     kilometros recorridos
     * @param precioBase      precio de lista
     * @param carroceria      tipo de carroceria
     * @param cantidadPuertas cantidad de puertas
     */
    public Automovil(String codigo, String marca, String modelo, Integer anio,
                     String color, EstadoVehiculo estado, Integer kilometraje,
                     Double precioBase, TipoCarroceria carroceria,
                     Integer cantidadPuertas) {
        // super(...) carga los atributos privados que viven en Vehiculo.
        super(codigo, marca, modelo, anio, color, estado, kilometraje, precioBase);
        this.carroceria = carroceria;
        this.cantidadPuertas = cantidadPuertas;
    }

    public TipoCarroceria getCarroceria() {
        return carroceria;
    }

    public void setCarroceria(TipoCarroceria carroceria) {
        this.carroceria = carroceria;
    }

    public Integer getCantidadPuertas() {
        return cantidadPuertas;
    }

    public void setCantidadPuertas(Integer cantidadPuertas) {
        this.cantidadPuertas = cantidadPuertas;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTipo() {
        return "Automovil";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDetalleEspecifico() {
        return carroceria + " / " + (cantidadPuertas == null ? 0 : cantidadPuertas) + " puertas";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double porcentajeDepreciacionAnual() {
        return DEPRECIACION_ANUAL;
    }
}
