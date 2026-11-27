package concesionaria.datos;

import java.util.List;

import concesionaria.excepciones.PersistenciaException;
import concesionaria.modelo.Identificable;

public interface Repositorio<T extends Identificable> {

    boolean agregar(T elemento);

    List<T> listarTodos();

    T buscarPorCodigo(String codigo);

    boolean eliminar(String codigo);

    int cantidad();

    void cargar() throws PersistenciaException;

    void guardar() throws PersistenciaException;
}
