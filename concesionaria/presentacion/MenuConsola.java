package concesionaria.presentacion;

import java.util.List;
import java.util.Map;

import concesionaria.excepciones.DatoInvalidoException;
import concesionaria.excepciones.PersistenciaException;
import concesionaria.excepciones.VehiculoDuplicadoException;
import concesionaria.excepciones.VehiculoNoEncontradoException;
import concesionaria.modelo.Automovil;
import concesionaria.modelo.Camioneta;
import concesionaria.modelo.EstadoVehiculo;
import concesionaria.modelo.EstiloMoto;
import concesionaria.modelo.Motocicleta;
import concesionaria.modelo.TipoCarroceria;
import concesionaria.modelo.Vehiculo;
import concesionaria.negocio.ServicioConcesionaria;

/**
 * CAPA DE PRESENTACION (diseno).
 *
 * Es la unica clase que habla con el usuario: muestra menus, pide datos y
 * muestra resultados. No decide nada del negocio: para cada operacion le pide
 * el trabajo al ServicioConcesionaria y se limita a mostrar lo que pasa,
 * incluidos los mensajes de las excepciones.
 *
 * @author Grupo TFI - Programacion II
 * @version 1.0
 */
public class MenuConsola {

    private static final String LINEA =
            "------------------------------------------------------------------------------------------------";

    private final ServicioConcesionaria servicio;
    private final LectorConsola lector;

    /**
     * @param servicio capa de negocio que resuelve las operaciones
     */
    public MenuConsola(ServicioConcesionaria servicio) {
        this.servicio = servicio;
        this.lector = new LectorConsola();
    }

    /**
     * Arranca el ciclo principal del programa: muestra el menu una y otra vez
     * hasta que el usuario elige salir.
     */
    public void iniciar() {
        mostrarBienvenida();
        int opcion;
        do {
            mostrarMenu();
            opcion = lector.leerEntero("  Elija una opcion: ", 0, 10).intValue();
            System.out.println();
            ejecutarOpcion(opcion);
        } while (opcion != 0);

        lector.cerrar();
    }

    /**
     * Deriva la opcion elegida al metodo que corresponde.
     *
     * @param opcion numero elegido en el menu
     */
    private void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1:
                registrarVehiculo();
                break;
            case 2:
                listarTodo();
                break;
            case 3:
                buscarPorCodigo();
                break;
            case 4:
                buscarPorMarca();
                break;
            case 5:
                filtrarPorTipo();
                break;
            case 6:
                filtrarPorEstado();
                break;
            case 7:
                actualizarPrecio();
                break;
            case 8:
                actualizarKilometraje();
                break;
            case 9:
                eliminarVehiculo();
                break;
            case 10:
                mostrarReporte();
                break;
            case 0:
                System.out.println("  Stock guardado. Hasta luego!");
                break;
            default:
                System.out.println("  Opcion inexistente.");
                break;
        }
    }

    // ------------------------------------------------------------------
    // Opciones del menu
    // ------------------------------------------------------------------

    /**
     * Alta de una unidad: pide el tipo, los datos comunes y los propios del
     * tipo elegido, arma el objeto y se lo entrega al negocio.
     */
    private void registrarVehiculo() {
        System.out.println("  === ALTA DE UNIDAD ===");
        System.out.println("  Tipo de vehiculo:");
        System.out.println("    1) Automovil");
        System.out.println("    2) Camioneta");
        System.out.println("    3) Motocicleta");
        int tipo = lector.leerEntero("  Opcion (1-3): ", 1, 3).intValue();

        String sugerido = servicio.generarCodigoSugerido();
        System.out.println("  Codigo sugerido: " + sugerido);
        String codigo = lector.leerTexto("  Codigo de stock: ");
        String marca = lector.leerTexto("  Marca: ");
        String modelo = lector.leerTexto("  Modelo: ");
        Integer anio = lector.leerEntero("  Anio: ", Vehiculo.ANIO_MINIMO, Vehiculo.ANIO_ACTUAL + 1);
        String color = lector.leerTexto("  Color: ");

        EstadoVehiculo estado = lector.leerOpcionEnum("  Estado de la unidad:",
                EstadoVehiculo.values());

        Integer kilometraje;
        if (estado == EstadoVehiculo.NUEVO) {
            kilometraje = Integer.valueOf(0);
            System.out.println("  Kilometraje: 0 (unidad 0 Km)");
        } else {
            kilometraje = lector.leerEntero("  Kilometraje: ", 0,
                    ServicioConcesionaria.KILOMETRAJE_MAXIMO);
        }

        Double precio = lector.leerDecimal("  Precio de lista: $ ",
                ServicioConcesionaria.PRECIO_MINIMO);

        Vehiculo vehiculo = null;
        if (tipo == 1) {
            TipoCarroceria carroceria = lector.leerOpcionEnum("  Carroceria:",
                    TipoCarroceria.values());
            Integer puertas = lector.leerEntero("  Cantidad de puertas: ", 2, 5);
            vehiculo = new Automovil(codigo, marca, modelo, anio, color, estado,
                    kilometraje, precio, carroceria, puertas);

        } else if (tipo == 2) {
            TipoCarroceria carroceria = lector.leerOpcionEnum("  Carroceria:",
                    TipoCarroceria.values());
            Double carga = lector.leerDecimal("  Capacidad de carga (Kg): ", 0);
            boolean traccion = lector.leerSiNo("  Tiene traccion integral 4x4?");
            vehiculo = new Camioneta(codigo, marca, modelo, anio, color, estado,
                    kilometraje, precio, carroceria, carga, Boolean.valueOf(traccion));

        } else {
            EstiloMoto estilo = lector.leerOpcionEnum("  Estilo de moto:",
                    EstiloMoto.values());
            Integer cilindrada = lector.leerEntero("  Cilindrada (cc): ", 50, 2500);
            vehiculo = new Motocicleta(codigo, marca, modelo, anio, color, estado,
                    kilometraje, precio, estilo, cilindrada);
        }

        // Aca se ve el tratamiento de las excepciones propias: el negocio
        // avisa que paso y la consola lo traduce en un mensaje para el usuario.
        try {
            servicio.registrarVehiculo(vehiculo);
            System.out.println("\n  [OK] Unidad registrada y guardada:");
            System.out.println("       " + vehiculo);
        } catch (DatoInvalidoException e) {
            System.out.println("\n  [ERROR DE DATOS] " + e.getMessage());
        } catch (VehiculoDuplicadoException e) {
            System.out.println("\n  [CODIGO REPETIDO] " + e.getMessage());
        } catch (PersistenciaException e) {
            System.out.println("\n  [ERROR AL GUARDAR] " + e.getMessage());
        }
        lector.esperarEnter();
    }

    /**
     * Muestra todas las unidades del stock.
     */
    private void listarTodo() {
        imprimirListado("STOCK COMPLETO", servicio.listarTodos());
        lector.esperarEnter();
    }

    /**
     * Busca una unidad por su codigo exacto.
     */
    private void buscarPorCodigo() {
        String codigo = lector.leerTexto("  Codigo a buscar: ");
        try {
            Vehiculo vehiculo = servicio.buscarPorCodigo(codigo);
            System.out.println("\n  Unidad encontrada:");
            System.out.println("  " + vehiculo);
            System.out.println("  Antiguedad      : " + vehiculo.getAntiguedad() + " anio(s)");
            System.out.println("  Precio de lista : " + pesos(vehiculo.getPrecioBase().doubleValue()));
            System.out.println("  Valor actual    : " + pesos(vehiculo.calcularValorActual())
                    + "   (deprecia " + (int) (vehiculo.porcentajeDepreciacionAnual() * 100)
                    + "% por anio)");
        } catch (VehiculoNoEncontradoException e) {
            System.out.println("\n  [NO ENCONTRADO] " + e.getMessage());
        }
        lector.esperarEnter();
    }

    /**
     * Busca unidades cuya marca contenga el texto ingresado.
     */
    private void buscarPorMarca() {
        String marca = lector.leerTexto("  Marca a buscar: ");
        imprimirListado("RESULTADOS PARA LA MARCA '" + marca.toUpperCase() + "'",
                servicio.buscarPorMarca(marca));
        lector.esperarEnter();
    }

    /**
     * Filtra el stock por tipo de vehiculo.
     */
    private void filtrarPorTipo() {
        System.out.println("  Tipo a listar:");
        System.out.println("    1) Automovil");
        System.out.println("    2) Camioneta");
        System.out.println("    3) Motocicleta");
        int opcion = lector.leerEntero("  Opcion (1-3): ", 1, 3).intValue();
        String tipo = "Automovil";
        if (opcion == 2) {
            tipo = "Camioneta";
        } else if (opcion == 3) {
            tipo = "Motocicleta";
        }
        imprimirListado("UNIDADES DEL TIPO " + tipo.toUpperCase(),
                servicio.filtrarPorTipo(tipo));
        lector.esperarEnter();
    }

    /**
     * Filtra el stock por estado (0 Km o usados).
     */
    private void filtrarPorEstado() {
        EstadoVehiculo estado = lector.leerOpcionEnum("  Estado a listar:",
                EstadoVehiculo.values());
        imprimirListado("UNIDADES EN ESTADO " + estado.name(),
                servicio.filtrarPorEstado(estado));
        lector.esperarEnter();
    }

    /**
     * Cambia el precio de lista de una unidad.
     */
    private void actualizarPrecio() {
        String codigo = lector.leerTexto("  Codigo de la unidad: ");
        Double precio = lector.leerDecimal("  Nuevo precio de lista: $ ",
                ServicioConcesionaria.PRECIO_MINIMO);
        try {
            servicio.actualizarPrecio(codigo, precio);
            System.out.println("\n  [OK] Precio actualizado y guardado.");
        } catch (VehiculoNoEncontradoException e) {
            System.out.println("\n  [NO ENCONTRADO] " + e.getMessage());
        } catch (DatoInvalidoException e) {
            System.out.println("\n  [ERROR DE DATOS] " + e.getMessage());
        } catch (PersistenciaException e) {
            System.out.println("\n  [ERROR AL GUARDAR] " + e.getMessage());
        }
        lector.esperarEnter();
    }

    /**
     * Cambia el kilometraje de una unidad usada.
     */
    private void actualizarKilometraje() {
        String codigo = lector.leerTexto("  Codigo de la unidad: ");
        Integer kilometraje = lector.leerEntero("  Nuevo kilometraje: ", 0,
                ServicioConcesionaria.KILOMETRAJE_MAXIMO);
        try {
            servicio.actualizarKilometraje(codigo, kilometraje);
            System.out.println("\n  [OK] Kilometraje actualizado y guardado.");
        } catch (VehiculoNoEncontradoException e) {
            System.out.println("\n  [NO ENCONTRADO] " + e.getMessage());
        } catch (DatoInvalidoException e) {
            System.out.println("\n  [ERROR DE DATOS] " + e.getMessage());
        } catch (PersistenciaException e) {
            System.out.println("\n  [ERROR AL GUARDAR] " + e.getMessage());
        }
        lector.esperarEnter();
    }

    /**
     * Da de baja una unidad del stock, pidiendo confirmacion.
     */
    private void eliminarVehiculo() {
        String codigo = lector.leerTexto("  Codigo de la unidad a eliminar: ");
        try {
            Vehiculo vehiculo = servicio.buscarPorCodigo(codigo);
            System.out.println("  " + vehiculo);
            if (lector.leerSiNo("  Confirma la baja de esta unidad?")) {
                servicio.eliminarVehiculo(codigo);
                System.out.println("\n  [OK] Unidad eliminada del stock.");
            } else {
                System.out.println("\n  Operacion cancelada.");
            }
        } catch (VehiculoNoEncontradoException e) {
            System.out.println("\n  [NO ENCONTRADO] " + e.getMessage());
        } catch (PersistenciaException e) {
            System.out.println("\n  [ERROR AL GUARDAR] " + e.getMessage());
        }
        lector.esperarEnter();
    }

    /**
     * Reporte de valorizacion: cantidad por tipo y valor total del stock.
     */
    private void mostrarReporte() {
        System.out.println("  === REPORTE DE VALORIZACION DEL STOCK ===\n");
        System.out.println("  Unidades en stock: " + servicio.cantidadEnStock());

        Map<String, Integer> conteo = servicio.contarPorTipo();
        if (conteo.isEmpty()) {
            System.out.println("  (sin unidades cargadas)");
        } else {
            System.out.println("\n  Composicion por tipo:");
            for (Map.Entry<String, Integer> entrada : conteo.entrySet()) {
                System.out.println(String.format("    %-14s %3d unidad(es)",
                        entrada.getKey(), entrada.getValue()));
            }
        }

        System.out.println("\n  Unidades 0 Km  : " + servicio.filtrarPorEstado(EstadoVehiculo.NUEVO).size());
        System.out.println("  Unidades usadas: " + servicio.filtrarPorEstado(EstadoVehiculo.USADO).size());
        System.out.println("\n  VALOR TOTAL DEL STOCK (ya depreciado): "
                + pesos(servicio.calcularValorTotalStock()));
        lector.esperarEnter();
    }

    // ------------------------------------------------------------------
    // Utilidades de presentacion
    // ------------------------------------------------------------------

    /**
     * Imprime una lista de unidades con formato de tabla.
     *
     * @param titulo titulo del listado
     * @param lista  unidades a mostrar
     */
    private void imprimirListado(String titulo, List<Vehiculo> lista) {
        System.out.println("  === " + titulo + " ===\n");
        if (lista.isEmpty()) {
            System.out.println("  No hay unidades que mostrar.");
            return;
        }
        System.out.println(LINEA);
        System.out.println(String.format("  %-6s %-11s %-14s %-12s %-5s %-10s %-6s %12s  %s",
                "COD", "TIPO", "MARCA", "MODELO", "ANIO", "COLOR", "ESTADO", "KILOMETROS", "DETALLE"));
        System.out.println(LINEA);
        double total = 0.0;
        for (Vehiculo vehiculo : lista) {
            // Una sola linea imprime los tres tipos de vehiculo: el toString()
            // que se ejecuta es el que corresponde al objeto real.
            System.out.println("  " + vehiculo);
            total += vehiculo.calcularValorActual();
        }
        System.out.println(LINEA);
        System.out.println("  " + lista.size() + " unidad(es) - valor actual del listado: " + pesos(total));
    }

    /**
     * Formatea un importe en pesos.
     *
     * @param importe valor a formatear
     * @return importe con separador de miles y dos decimales
     */
    private String pesos(double importe) {
        return String.format("$ %,.2f", Double.valueOf(importe));
    }

    /**
     * Cartel de bienvenida.
     */
    private void mostrarBienvenida() {
        System.out.println();
        System.out.println("  ############################################################");
        System.out.println("  #        CONCESIONARIA MULTIMARCAS - GESTION DE STOCK      #");
        System.out.println("  #        Trabajo Final Integrador - Programacion II        #");
        System.out.println("  ############################################################");
        System.out.println("  Unidades cargadas desde el archivo: " + servicio.cantidadEnStock());
    }

    /**
     * Menu principal.
     */
    private void mostrarMenu() {
        System.out.println();
        System.out.println("  ==================== MENU PRINCIPAL ====================");
        System.out.println("   1) Registrar una unidad");
        System.out.println("   2) Listar el stock completo");
        System.out.println("   3) Buscar una unidad por codigo");
        System.out.println("   4) Buscar unidades por marca");
        System.out.println("   5) Listar por tipo de vehiculo");
        System.out.println("   6) Listar por estado (0 Km / usados)");
        System.out.println("   7) Actualizar el precio de una unidad");
        System.out.println("   8) Actualizar el kilometraje de una unidad");
        System.out.println("   9) Eliminar una unidad");
        System.out.println("  10) Reporte de valorizacion del stock");
        System.out.println("   0) Salir");
        System.out.println("  ========================================================");
    }
}
