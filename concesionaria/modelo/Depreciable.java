package concesionaria.modelo;

/**
 * Comportamiento que se le "transmite" a todo bien que pierde valor con el
 * paso del tiempo.
 *
 * Es el ejemplo de interfaz como transmision de comportamiento: la interfaz
 * define QUE se puede hacer (calcular el valor actual) y cada clase decide
 * COMO lo hace (cada tipo de vehiculo se deprecia distinto).
 *
 * @author Grupo TFI - Programacion II
 * @version 1.0
 */
public interface Depreciable {

    /**
     * Tope maximo de depreciacion acumulada (70%).
     * En una interfaz, todo atributo es implicitamente public static final,
     * es decir, una constante.
     */
    double TOPE_DEPRECIACION = 0.70;

    /**
     * Porcentaje de valor que el bien pierde por cada anio de antiguedad.
     *
     * @return porcentaje expresado en tanto por uno (0.10 = 10% anual)
     */
    double porcentajeDepreciacionAnual();

    /**
     * Calcula cuanto vale hoy el bien, aplicando la depreciacion correspondiente.
     *
     * @return valor actual en pesos
     */
    double calcularValorActual();
}
