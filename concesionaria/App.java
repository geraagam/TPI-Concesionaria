package concesionaria;

import concesionaria.datos.RepositorioVehiculo;
import concesionaria.excepciones.PersistenciaException;
import concesionaria.negocio.ServicioConcesionaria;
import concesionaria.presentacion.MenuConsola;

/**
 * Punto de entrada del sistema.
 *
 * Esta clase es la unica que conoce a las tres capas al mismo tiempo: crea el
 * repositorio (datos), se lo entrega al servicio (negocio) y se lo entrega al
 * menu (presentacion). De ahi en adelante cada capa solo habla con la de
 * abajo. Si manana se cambia el archivo por una base de datos, lo unico que
 * se toca es esta linea.
 *
 * @author Grupo TFI - Programacion II
 * @version 1.0
 */
public class App {
    public App() {
    }

    public static final String ARCHIVO_STOCK = "stock.csv";
    public static void main(String[] args) {

        // 1) Capa de datos.
        RepositorioVehiculo repositorio = new RepositorioVehiculo(ARCHIVO_STOCK);
        try {
            repositorio.cargar();
            if (repositorio.getLineasConError() > 0) {
                System.out.println("  [AVISO] Se descartaron "
                        + repositorio.getLineasConError()
                        + " linea(s) con datos invalidos del archivo de stock.");
            }
        } catch (PersistenciaException e) {
            System.out.println("  [AVISO] No se pudo leer el archivo de stock: " + e.getMessage());
            System.out.println("          El sistema arranca vacio y creara el archivo al guardar.");
        }

        // 2) Capa de negocio, que depende del repositorio.
        ServicioConcesionaria servicio = new ServicioConcesionaria(repositorio);

        // 3) Capa de presentacion, que depende del negocio.
        MenuConsola menu = new MenuConsola(servicio);
        menu.iniciar();
    }
}
