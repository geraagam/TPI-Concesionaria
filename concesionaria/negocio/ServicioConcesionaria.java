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
 * @author Grupo TFI - Programacion II
 * @version 1.0
 */
public class ServicioConcesionaria {

    public static final double PRECIO_MINIMO = 1.0;

    /** Kilometraje maximo razonable para una unidad usada. */
    public static final int KILOMETRAJE_MAXIMO = 1000000;

    private final Repositorio<Vehiculo> repositorio;

    public ServicioConcesionaria(Repositorio<Vehiculo> repositorio) {
        this.repositorio = repositorio;
    }

    // ------------------------------------------------------------------
    // Altas, bajas y modificaciones
    // ------------------------------------------------------------------
    public void registrarVehiculo(Vehiculo vehiculo)
            throws DatoInvalidoException, VehiculoDuplicadoException, PersistenciaException {

        validar(vehiculo);

        if (!repositorio.agregar(vehiculo)) {
            throw new VehiculoDuplicadoException(
                    "Ya existe un vehiculo registrado con el codigo " + vehiculo.getCodigo());
        }
        repositorio.guardar();
    }
    public void eliminarVehiculo(String codigo)
            throws VehiculoNoEncontradoException, PersistenciaException {

        if (!repositorio.eliminar(codigo)) {
            throw new VehiculoNoEncontradoException(
                    "No existe ningun vehiculo con el codigo " + codigo);
        }
        repositorio.guardar();
    }
    public void actualizarPrecio(String codigo, Double nuevoPrecio)
            throws VehiculoNoEncontradoException, DatoInvalidoException, PersistenciaException {

        Vehiculo vehiculo = buscarPorCodigo(codigo);
        if (nuevoPrecio == null || nuevoPrecio.doubleValue() < PRECIO_MINIMO) {
            throw new DatoInvalidoException("El precio debe ser mayor a cero.");
        }
        vehiculo.setPrecioBase(nuevoPrecio);
        repositorio.guardar();
    }
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
    public List<Vehiculo> listarTodos() {
        return repositorio.listarTodos();
    }
    public Vehiculo buscarPorCodigo(String codigo) throws VehiculoNoEncontradoException {
        Vehiculo vehiculo = repositorio.buscarPorCodigo(codigo);
        if (vehiculo == null) {
            throw new VehiculoNoEncontradoException(
                    "No existe ningun vehiculo con el codigo " + codigo);
        }
        return vehiculo;
    }
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
    public List<Vehiculo> filtrarPorEstado(EstadoVehiculo estado) {
        List<Vehiculo> resultado = new ArrayList<Vehiculo>();
        for (Vehiculo vehiculo : repositorio.listarTodos()) {
            if (vehiculo.getEstado() == estado) {
                resultado.add(vehiculo);
            }
        }
        return resultado;
    }
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
    public double calcularValorTotalStock() {
        double total = 0.0;
        for (Vehiculo vehiculo : repositorio.listarTodos()) {
            total += vehiculo.calcularValorActual();
        }
        return total;
    }
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
    public int cantidadEnStock() {
        return repositorio.cantidad();
    }
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

        if (vehiculo.esNuevo() && vehiculo.getKilometraje().intValue() != 0) {
            throw new DatoInvalidoException(
                    "Una unidad 0 Km no puede tener kilometraje recorrido.");
        }
    }
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
