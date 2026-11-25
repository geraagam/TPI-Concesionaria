package concesionaria.datos;

import java.util.List;

import concesionaria.excepciones.PersistenciaException;
import concesionaria.modelo.Identificable;

/**
 * Interfaz GENERICA que define que operaciones ofrece cualquier repositorio
 * de datos del sistema, sin importar que tipo de objeto guarde.
 *
 * La letra T es un parametro de tipo: se reemplaza por el tipo real recien
 * cuando alguien escribe Repositorio&lt;Vehiculo&gt;. La condicion
 * "T extends Identificable" obliga a que lo que se guarde sepa dar su codigo.
 *
 * Gracias a esta interfaz, la capa de negocio no depende de SI los datos se
 * guardan en un archivo, en una base de datos o en memoria: solo depende del
 * contrato. Si manana se cambia a SQL, el negocio no se toca.
 *
 * @param <T> tipo de objeto que administra el repositorio
 * @author Grupo TFI - Programacion II
 * @version 1.0
 */
public interface Repositorio<T extends Identificable> {

    /**
     * Agrega un elemento si su codigo no esta en uso.
     *
     * @param elemento elemento a agregar
     * @return true si se agrego, false si el codigo ya existia
     */
    boolean agregar(T elemento);

    /**
     * @return lista con todos los elementos almacenados
     */
    List<T> listarTodos();

    /**
     * @param codigo codigo a buscar (no distingue mayusculas)
     * @return el elemento encontrado, o null si no existe
     */
    T buscarPorCodigo(String codigo);

    /**
     * @param codigo codigo del elemento a eliminar
     * @return true si se elimino, false si no existia
     */
    boolean eliminar(String codigo);

    /**
     * @return cantidad de elementos almacenados
     */
    int cantidad();

    /**
     * Carga los datos desde el medio de almacenamiento.
     *
     * @throws PersistenciaException si falla la lectura
     */
    void cargar() throws PersistenciaException;

    /**
     * Guarda los datos en el medio de almacenamiento.
     *
     * @throws PersistenciaException si falla la escritura
     */
    void guardar() throws PersistenciaException;
}
