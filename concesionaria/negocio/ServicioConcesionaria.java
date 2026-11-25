package concesionaria.negocio;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import concesionaria.datos.Repositorio;
import concesionaria.excepciones.DatoInvalidoException;
import concesionaria.excepciones.PersistenciaException;
import concesionaria.excepciones.VehiculoDuplicadoException;
import concesionaria.excepciones.VehiculoNoEncontradoException;
import concesionaria.modelo.EstadoVehiculo;
import concesionaria.modelo.Vehiculo;

/**
 * CAPA DE NEGOCIO.
 *
 * Aca viven las reglas de la concesionaria: que se puede cargar, que no, y
 * como se calculan los reportes. Esta clase no imprime nada por pantalla ni
 * lee nada del teclado (eso es tarea del menu), y tampoco sabe si los datos
 * terminan en un archivo o en una base (eso es tarea del repositorio).
 *
 * Esa division es el esquema "Diseno vs. Negocio" que pide el enunciado.
 *
 * @author Grupo TFI - Programacion II
 * @version 1.0
 */
public class ServicioConcesionaria {

    /** Precio minimo aceptado para una unidad. */
    public static final double PRECIO_MINIMO = 1.0;

    /** Kilometraje maximo razonable para una unidad usada. */
    public static final int KILOMETRAJE_MAXIMO = 1000000;

    /**
     * Se declara del tipo de la INTERFAZ, no de la clase concreta.
     * Asi el servicio funciona con cualquier repositorio que cumpla el
     * contrato (archivo, base de datos, memoria) sin cambiar una linea.
     */
    private final Repositorio<Vehiculo> repositorio;

    /**
     * @param repositorio repositorio donde se almacenan los vehiculos
     */
    public ServicioConcesionaria(Repositorio<Vehiculo> repositorio) {
        this.repositorio = repositorio;
    }

    // ------------------------------------------------------------------
    // Altas, bajas y modificaciones
    // ------------------------------------------------------------------

    /**
     * Registra una unidad nueva en el stock.
     *
     * @param vehiculo vehiculo a registrar
     * @throws DatoInvalidoException        si algun dato rompe las reglas del negocio
     * @throws VehiculoDuplicadoException   si el codigo ya esta en uso
     * @throws PersistenciaException        si no se pudo guardar el archivo
     */
    public void registrarVehiculo(Vehiculo vehiculo)
            throws DatoInvalidoException, VehiculoDuplicadoException, PersistenciaException {

        validar(vehiculo);

        if (!repositorio.agregar(vehiculo)) {
            throw new VehiculoDuplicadoException(
                    "Ya existe un vehiculo registrado con el codigo " + vehiculo.getCodigo());
        }
        // Se persiste enseguida para que el dato sobreviva aunque el programa
        // se cierre de manera inesperada.
        repositorio.guardar();
    }

    /**
     * Elimina una unidad del stock.
     *
     * @param codigo codigo de la unidad
     * @throws VehiculoNoEncontradoException si no existe esa unidad
     * @throws PersistenciaException         si no se pudo guardar el archivo
     */
    public void eliminarVehiculo(String codigo)
            throws VehiculoNoEncontradoException, PersistenciaException {

        if (!repositorio.eliminar(codigo)) {
            throw new VehiculoNoEncontradoException(
                    "No existe ningun vehiculo con el codigo " + codigo);
        }
        repositorio.guardar();
    }

    /**
     * Actualiza el precio de lista de una unidad.
     *
     * @param codigo      codigo de la unidad
     * @param nuevoPrecio nuevo precio de lista
     * @throws VehiculoNoEncontradoException si no existe esa unidad
     * @throws DatoInvalidoException         si el precio no es valido
     * @throws PersistenciaException         si no se pudo guardar el archivo
     */
    public void actualizarPrecio(String codigo, Double nuevoPrecio)
            throws VehiculoNoEncontradoException, DatoInvalidoException, PersistenciaException {

        Vehiculo vehiculo = buscarPorCodigo(codigo);
        if (nuevoPrecio == null || nuevoPrecio.doubleValue() < PRECIO_MINIMO) {
            throw new DatoInvalidoException("El precio debe ser mayor a cero.");
        }
        vehiculo.setPrecioBase(nuevoPrecio);
        repositorio.guardar();
    }

    /**
     * Actualiza el kilometraje de una unidad usada.
     *
     * @param codigo          codigo de la unidad
     * @param nuevoKilometraje nuevo kilometraje
     * @throws VehiculoNoEncontradoException si no existe esa unidad
     * @throws DatoInvalidoException         si el kilometraje no es valido o la unidad es 0 Km
     * @throws PersistenciaException         si no se pudo guardar el archivo
     */
    public void actualizarKilometraje(String codigo, Integer nuevoKilometraje)
            throws VehiculoNoEncontradoException, DatoInvalidoException, PersistenciaException {

        Vehiculo vehiculo = buscarPorCodigo(codigo);
        if (vehiculo.esNuevo()) {
            throw new DatoInvalidoException(
                    "La unidad " + codigo + " es 0 Km: no corresponde cargarle kilometraje.");
        }
        if (nuevoKilometraje == null || nuevoKilometraje.intValue() < 0
                || nuevoKilometraje.intValue() > KILOMETRAJE_MAXIMO) {
            throw new DatoInvalidoException(
                    "El kilometraje debe estar entre 0 y " + KILOMETRAJE_MAXIMO + ".");
        }
        vehiculo.setKilometraje(nuevoKilometraje);
        repositorio.guardar();
    }

    // ------------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------------

    /**
     * @return todas las unidades del stock
     */
    public List<Vehiculo> listarTodos() {
        return repositorio.listarTodos();
    }

    /**
     * @param codigo codigo buscado
     * @return la unidad encontrada
     * @throws VehiculoNoEncontradoException si no existe esa unidad
     */
    public Vehiculo buscarPorCodigo(String codigo) throws VehiculoNoEncontradoException {
        Vehiculo vehiculo = repositorio.buscarPorCodigo(codigo);
        if (vehiculo == null) {
            throw new VehiculoNoEncontradoException(
                    "No existe ningun vehiculo con el codigo " + codigo);
        }
        return vehiculo;
    }

    /**
     * Busca por marca aproximada, sin distinguir mayusculas.
     *
     * @param marca texto a buscar dentro de la marca
     * @return lista de unidades que coinciden
     */
    public List<Vehiculo> buscarPorMarca(String marca) {
        List<Vehiculo> resultado = new ArrayList<Vehiculo>();
        if (marca == null) {
            return resultado;
        }
        String buscado = marca.trim().toLowerCase();
        for (Vehiculo vehiculo : repositorio.listarTodos()) {
            if (vehiculo.getMarca() != null
                    && vehiculo.getMarca().toLowerCase().contains(buscado)) {
                resultado.add(vehiculo);
            }
        }
        return resultado;
    }

    /**
     * @param estado estado buscado (NUEVO o USADO)
     * @return unidades en ese estado
     */
    public List<Vehiculo> filtrarPorEstado(EstadoVehiculo estado) {
        List<Vehiculo> resultado = new ArrayList<Vehiculo>();
        for (Vehiculo vehiculo : repositorio.listarTodos()) {
            if (vehiculo.getEstado() == estado) {
                resultado.add(vehiculo);
            }
        }
        return resultado;
    }

    /**
     * Filtra por tipo de vehiculo aprovechando el polimorfismo: se le pregunta
     * a cada objeto que tipo es, sin preguntar por su clase.
     *
     * @param tipo "Automovil", "Camioneta" o "Motocicleta"
     * @return unidades de ese tipo
     */
    public List<Vehiculo> filtrarPorTipo(String tipo) {
        List<Vehiculo> resultado = new ArrayList<Vehiculo>();
        if (tipo == null) {
            return resultado;
        }
        for (Vehiculo vehiculo : repositorio.listarTodos()) {
            if (vehiculo.getTipo().equalsIgnoreCase(tipo.trim())) {
                resultado.add(vehiculo);
            }
        }
        return resultado;
    }

    // ------------------------------------------------------------------
    // Reportes
    // ------------------------------------------------------------------

    /**
     * Suma el valor actual de todo el stock.
     *
     * Es el ejemplo mas claro de polimorfismo del sistema: el metodo recorre
     * una lista de Vehiculo y llama siempre a calcularValorActual(), pero cada
     * objeto aplica su propio porcentaje de depreciacion.
     *
     * @return valor total del stock en pesos
     */
    public double calcularValorTotalStock() {
        double total = 0.0;
        for (Vehiculo vehiculo : repositorio.listarTodos()) {
            total += vehiculo.calcularValorActual();
        }
        return total;
    }

    /**
     * Cuenta cuantas unidades hay de cada tipo.
     *
     * @return mapa con el tipo como clave y la cantidad como valor
     */
    public Map<String, Integer> contarPorTipo() {
        Map<String, Integer> conteo = new LinkedHashMap<String, Integer>();
        for (Vehiculo vehiculo : repositorio.listarTodos()) {
            String tipo = vehiculo.getTipo();
            Integer actual = conteo.get(tipo);
            if (actual == null) {
                conteo.put(tipo, Integer.valueOf(1));
            } else {
                conteo.put(tipo, Integer.valueOf(actual.intValue() + 1));
            }
        }
        return conteo;
    }

    /**
     * @return cantidad total de unidades en stock
     */
    public int cantidadEnStock() {
        return repositorio.cantidad();
    }

    /**
     * Propone el proximo codigo libre con el formato V001, V002, etc.
     *
     * @return codigo sugerido
     */
    public String generarCodigoSugerido() {
        int maximo = 0;
        for (Vehiculo vehiculo : repositorio.listarTodos()) {
            String codigo = vehiculo.getCodigo();
            if (codigo != null && codigo.length() > 1
                    && codigo.toUpperCase().charAt(0) == 'V') {
                try {
                    int numero = Integer.parseInt(codigo.substring(1));
                    if (numero > maximo) {
                        maximo = numero;
                    }
                } catch (NumberFormatException e) {
                    // El codigo no sigue el formato Vnnn: se ignora.
                }
            }
        }
        return String.format("V%03d", Integer.valueOf(maximo + 1));
    }

    // ------------------------------------------------------------------
    // Reglas de validacion del negocio
    // ------------------------------------------------------------------

    /**
     * Controla que la unidad cumpla todas las reglas antes de registrarla.
     *
     * Las validaciones viven aca y no en los setters para que el modelo quede
     * como un JavaBean puro y para que las reglas esten todas juntas en un
     * solo lugar.
     *
     * @param vehiculo vehiculo a validar
     * @throws DatoInvalidoException si alguna regla no se cumple
     */
    private void validar(Vehiculo vehiculo) throws DatoInvalidoException {
        if (vehiculo == null) {
            throw new DatoInvalidoException("No se recibio ningun vehiculo.");
        }
        exigirTexto(vehiculo.getCodigo(), "codigo");
        exigirTexto(vehiculo.getMarca(), "marca");
        exigirTexto(vehiculo.getModelo(), "modelo");
        exigirTexto(vehiculo.getColor(), "color");

        if (vehiculo.getEstado() == null) {
            throw new DatoInvalidoException("Hay que indicar si la unidad es 0 Km o usada.");
        }
        if (vehiculo.getAnio() == null
                || vehiculo.getAnio().intValue() < Vehiculo.ANIO_MINIMO
                || vehiculo.getAnio().intValue() > Vehiculo.ANIO_ACTUAL + 1) {
            throw new DatoInvalidoException("El anio debe estar entre "
                    + Vehiculo.ANIO_MINIMO + " y " + (Vehiculo.ANIO_ACTUAL + 1) + ".");
        }
        if (vehiculo.getPrecioBase() == null
                || vehiculo.getPrecioBase().doubleValue() < PRECIO_MINIMO) {
            throw new DatoInvalidoException("El precio debe ser mayor a cero.");
        }
        if (vehiculo.getKilometraje() == null
                || vehiculo.getKilometraje().intValue() < 0
                || vehiculo.getKilometraje().intValue() > KILOMETRAJE_MAXIMO) {
            throw new DatoInvalidoException(
                    "El kilometraje debe estar entre 0 y " + KILOMETRAJE_MAXIMO + ".");
        }
        // Regla propia del negocio: una unidad 0 Km no puede tener kilometros.
        if (vehiculo.esNuevo() && vehiculo.getKilometraje().intValue() != 0) {
            throw new DatoInvalidoException(
                    "Una unidad 0 Km no puede tener kilometraje recorrido.");
        }
    }

    /**
     * Controla que un texto obligatorio no venga vacio ni con el separador
     * del archivo adentro (romperia la persistencia).
     *
     * @param valor texto a controlar
     * @param campo nombre del campo, para el mensaje
     * @throws DatoInvalidoException si el texto no sirve
     */
    private void exigirTexto(String valor, String campo) throws DatoInvalidoException {
        if (valor == null || valor.trim().length() == 0) {
            throw new DatoInvalidoException("El campo " + campo + " no puede quedar vacio.");
        }
        if (valor.contains(";")) {
            throw new DatoInvalidoException(
                    "El campo " + campo + " no puede contener el caracter ';'.");
        }
    }
}
