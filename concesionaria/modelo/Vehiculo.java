package concesionaria.modelo;

import java.io.Serializable;

/**
 * Clase abstracta que representa a cualquier vehiculo del stock de la
 * concesionaria. Es la ABSTRACCION del dominio: reune todo lo que tienen en
 * comun un automovil, una camioneta y una motocicleta.
 *
 * Es abstracta porque "un vehiculo" no existe en la realidad: en el playon de
 * la concesionaria siempre hay un auto, una camioneta o una moto concreta.
 * Por eso no se puede escribir new Vehiculo(...).
 *
 * Patron POJO / JavaBean:
 *   - todos los atributos son privados (encapsulamiento),
 *   - se accede a ellos solo por getters y setters publicos,
 *   - las subclases concretas tienen constructor sin argumentos.
 *
 * Nota de diseno sobre la visibilidad de los atributos:
 * Se dejan private y no protected. Las subclases igualmente los poseen (el
 * objeto hijo contiene esos atributos) y los cargan invocando super(...) en su
 * constructor o a traves de los setters publicos heredados. Declararlos
 * protected habilitaria que una subclase escribiera directamente
 * this.kilometraje = -500 saltandose las validaciones; manteniendolos privados
 * se conserva el encapsulamiento sin perder el patron JavaBean.
 *
 * @author Grupo TFI - Programacion II
 * @version 1.0
 */
public abstract class Vehiculo implements Serializable, Identificable, Depreciable {

    private static final long serialVersionUID = 1L;

    /** Anio mas antiguo que la concesionaria acepta recibir en stock. */
    public static final int ANIO_MINIMO = 1950;

    /** Anio en curso, calculado una sola vez al iniciar el programa. */
    public static final int ANIO_ACTUAL = java.time.Year.now().getValue();

    // ------------------------------------------------------------------
    // Atributos (privados: encapsulamiento)
    // Se usan WRAPPERS (Integer, Double) en lugar de tipos primitivos
    // (int, double) porque un wrapper puede valer null, y eso nos permite
    // detectar un dato que nunca fue cargado.
    // ------------------------------------------------------------------

    private String codigo;
    private String marca;
    private String modelo;
    private Integer anio;
    private String color;
    private EstadoVehiculo estado;
    private Integer kilometraje;
    private Double precioBase;

    /**
     * Constructor sin argumentos exigido por el patron JavaBean.
     * Es protected porque solo tiene sentido que lo invoquen las subclases
     * (una clase abstracta nunca se instancia directamente).
     */
    protected Vehiculo() {
    }

    /**
     * Constructor completo. Las subclases lo invocan con super(...) para
     * cargar la parte comun del vehiculo.
     *
     * @param codigo      identificador unico de stock
     * @param marca       marca del vehiculo
     * @param modelo      modelo del vehiculo
     * @param anio        anio de fabricacion
     * @param color       color de la unidad
     * @param estado      NUEVO (0 Km) o USADO
     * @param kilometraje kilometros recorridos (0 si es nuevo)
     * @param precioBase  precio de lista sin depreciar
     */
    protected Vehiculo(String codigo, String marca, String modelo, Integer anio,
                       String color, EstadoVehiculo estado, Integer kilometraje,
                       Double precioBase) {
        this.codigo = codigo;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.color = color;
        this.estado = estado;
        this.kilometraje = kilometraje;
        this.precioBase = precioBase;
    }

    // ------------------------------------------------------------------
    // Metodos abstractos: cada subclase esta OBLIGADA a implementarlos.
    // Aca nace el polimorfismo: el mismo llamado da resultados distintos
    // segun el objeto real que haya del otro lado.
    // ------------------------------------------------------------------

    /**
     * Devuelve el nombre del tipo de vehiculo. Lo define cada subclase.
     *
     * @return nombre del tipo de vehiculo ("Automovil", "Camioneta", "Motocicleta")
     */
    public abstract String getTipo();

    /**
     * Devuelve los datos que son exclusivos de cada tipo de vehiculo.
     *
     * @return datos propios del tipo (carroceria, estilo, cilindrada, etc.)
     */
    public abstract String getDetalleEspecifico();

    // ------------------------------------------------------------------
    // Comportamiento comun, implementado una sola vez para todos.
    // ------------------------------------------------------------------

    /**
     * Calcula el valor actual de la unidad.
     *
     * Las unidades 0 Km se venden al precio de lista. Las usadas pierden un
     * porcentaje por cada anio de antiguedad; ese porcentaje lo define cada
     * subclase en porcentajeDepreciacionAnual(), por eso este unico metodo
     * sirve para los tres tipos de vehiculo (polimorfismo).
     *
     * La depreciacion nunca supera el TOPE_DEPRECIACION definido en la
     * interfaz Depreciable.
     *
     * @return valor actual en pesos
     */
    @Override
    public double calcularValorActual() {
        if (precioBase == null) {
            return 0.0;
        }
        if (estado == EstadoVehiculo.NUEVO) {
            return precioBase.doubleValue();
        }
        int antiguedad = ANIO_ACTUAL - (anio == null ? ANIO_ACTUAL : anio.intValue());
        if (antiguedad < 0) {
            antiguedad = 0;
        }
        double depreciacion = antiguedad * porcentajeDepreciacionAnual();
        if (depreciacion > TOPE_DEPRECIACION) {
            depreciacion = TOPE_DEPRECIACION;
        }
        return precioBase.doubleValue() * (1 - depreciacion);
    }

    /**
     * Calcula los anios que tiene la unidad.
     *
     * @return antiguedad de la unidad en anios
     */
    public int getAntiguedad() {
        if (anio == null) {
            return 0;
        }
        int antiguedad = ANIO_ACTUAL - anio.intValue();
        return antiguedad < 0 ? 0 : antiguedad;
    }

    /**
     * Indica si la unidad es 0 Km.
     *
     * @return true si la unidad es 0 Km
     */
    public boolean esNuevo() {
        return estado == EstadoVehiculo.NUEVO;
    }

    // ------------------------------------------------------------------
    // Getters y setters (JavaBean)
    // ------------------------------------------------------------------

    /**
     * @return codigo unico de stock. Implementa la interfaz Identificable.
     */
    @Override
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public EstadoVehiculo getEstado() {
        return estado;
    }

    public void setEstado(EstadoVehiculo estado) {
        this.estado = estado;
    }

    public Integer getKilometraje() {
        return kilometraje;
    }

    public void setKilometraje(Integer kilometraje) {
        this.kilometraje = kilometraje;
    }

    public Double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(Double precioBase) {
        this.precioBase = precioBase;
    }

    // ------------------------------------------------------------------
    // Sobreescritura de los metodos esenciales heredados de Object
    // ------------------------------------------------------------------

    /**
     * Dos vehiculos son el mismo si comparten el codigo de stock, sin importar
     * de que tipo sean. Se compara por codigo y no por marca/modelo porque la
     * concesionaria puede tener dos Corolla blancos iguales, pero son dos
     * unidades distintas.
     *
     * @param obj objeto a comparar
     * @return true si representan la misma unidad
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Vehiculo)) {
            return false;
        }
        Vehiculo otro = (Vehiculo) obj;
        if (this.codigo == null || otro.codigo == null) {
            return false;
        }
        return this.codigo.equalsIgnoreCase(otro.codigo);
    }

    /**
     * Se sobreescribe junto con equals(): dos objetos iguales DEBEN devolver
     * el mismo hashCode, porque es lo que usan HashMap y HashSet para
     * ubicarlos. Si solo sobreescribieramos equals(), el Map fallaria.
     *
     * @return codigo hash basado en el codigo de stock
     */
    @Override
    public int hashCode() {
        return codigo == null ? 0 : codigo.toUpperCase().hashCode();
    }

    /**
     * Ficha resumida de la unidad. Usa getTipo() y getDetalleEspecifico(),
     * que son abstractos, por lo que una misma linea de codigo imprime los
     * datos correctos para un auto, una camioneta o una moto.
     *
     * @return representacion en texto del vehiculo
     */
    @Override
    public String toString() {
        return String.format("[%s] %-11s %-14s %-12s %4d  %-10s %-6s %9s Km  %s",
                codigo,
                getTipo(),
                marca,
                modelo,
                anio == null ? 0 : anio.intValue(),
                color,
                estado,
                String.format("%,d", kilometraje == null ? 0 : kilometraje.intValue()),
                getDetalleEspecifico());
    }
}
