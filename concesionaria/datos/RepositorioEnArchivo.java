package concesionaria.datos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import concesionaria.excepciones.DatoInvalidoException;
import concesionaria.excepciones.PersistenciaException;
import concesionaria.modelo.Identificable;

/**
 * Repositorio generico que mantiene los objetos en memoria dentro de una
 * COLECCION y los persiste en un archivo de texto plano.
 *
 * Es abstracta porque sabe COMO administrar la coleccion y COMO leer y
 * escribir el archivo, pero no sabe como se convierte un objeto concreto en
 * una linea de texto. Esa parte la define cada repositorio hijo.
 * (Es el patron "metodo plantilla": el esqueleto esta aca, el detalle abajo.)
 *
 * Coleccion elegida: LinkedHashMap, porque
 *   - Map permite buscar por codigo en forma directa, sin recorrer todo,
 *   - "Linked" conserva el orden en que se fueron cargando los vehiculos.
 *
 * @param <T> tipo de objeto administrado, debe ser Identificable
 * @author Grupo TFI - Programacion II
 * @version 1.0
 */
public abstract class RepositorioEnArchivo<T extends Identificable> implements Repositorio<T> {

    /** Caracter que separa los campos dentro del archivo. */
    public static final String SEPARADOR = ";";

    /** Marca de comentario: las lineas que empiezan asi se ignoran. */
    public static final String COMENTARIO = "#";

    /** Coleccion en memoria: clave = codigo en mayusculas, valor = objeto. */
    private final Map<String, T> elementos = new LinkedHashMap<String, T>();

    /** Nombre del archivo donde se persisten los datos. */
    private final String rutaArchivo;

    /** Cantidad de lineas que no se pudieron leer en la ultima carga. */
    private int lineasConError;

    /**
     * @param rutaArchivo nombre o ruta del archivo de persistencia
     */
    protected RepositorioEnArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    // ------------------------------------------------------------------
    // Metodos abstractos: los completa cada repositorio concreto
    // ------------------------------------------------------------------

    /**
     * Convierte un objeto en la linea de texto que se guardara en el archivo.
     *
     * @param elemento objeto a convertir
     * @return linea de texto con los campos separados por SEPARADOR
     */
    protected abstract String convertirALinea(T elemento);

    /**
     * Reconstruye un objeto a partir de una linea del archivo.
     *
     * @param linea linea leida del archivo
     * @return objeto reconstruido
     * @throws DatoInvalidoException si la linea esta mal formada
     */
    protected abstract T convertirDesdeLinea(String linea) throws DatoInvalidoException;

    /**
     * Encabezado que se escribe como comentario al inicio del archivo.
     *
     * @return texto del encabezado
     */
    protected abstract String encabezadoArchivo();

    // ------------------------------------------------------------------
    // Operaciones sobre la coleccion (iguales para cualquier tipo T)
    // ------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean agregar(T elemento) {
        if (elemento == null || elemento.getCodigo() == null) {
            return false;
        }
        String clave = normalizar(elemento.getCodigo());
        if (elementos.containsKey(clave)) {
            return false;
        }
        elementos.put(clave, elemento);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> listarTodos() {
        // Se devuelve una copia en forma de List para que nadie modifique el
        // Map interno desde afuera (encapsulamiento de la coleccion).
        return new ArrayList<T>(elementos.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T buscarPorCodigo(String codigo) {
        if (codigo == null) {
            return null;
        }
        return elementos.get(normalizar(codigo));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean eliminar(String codigo) {
        if (codigo == null) {
            return false;
        }
        return elementos.remove(normalizar(codigo)) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int cantidad() {
        return elementos.size();
    }

    // ------------------------------------------------------------------
    // Persistencia en archivo
    // ------------------------------------------------------------------

    /**
     * Lee el archivo completo y reconstruye la coleccion en memoria.
     *
     * Si una linea esta corrupta no se cae el programa: se descarta esa linea,
     * se cuenta el error y se sigue leyendo el resto.
     *
     * @throws PersistenciaException si el archivo existe pero no se puede leer
     */
    @Override
    public void cargar() throws PersistenciaException {
        elementos.clear();
        lineasConError = 0;

        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            // Primera ejecucion: todavia no hay archivo. No es un error.
            return;
        }

        BufferedReader lector = null;
        try {
            lector = new BufferedReader(new FileReader(archivo));
            String linea = lector.readLine();
            while (linea != null) {
                String limpia = linea.trim();
                if (limpia.length() > 0 && !limpia.startsWith(COMENTARIO)) {
                    try {
                        T elemento = convertirDesdeLinea(limpia);
                        agregar(elemento);
                    } catch (DatoInvalidoException e) {
                        lineasConError++;
                    }
                }
                linea = lector.readLine();
            }
        } catch (IOException e) {
            // Se envuelve la excepcion de Java en una excepcion propia.
            throw new PersistenciaException(
                    "No se pudo leer el archivo " + rutaArchivo, e);
        } finally {
            // El finally se ejecuta siempre, haya o no error: aca se cierra
            // el archivo para no dejar recursos abiertos.
            cerrar(lector);
        }
    }

    @Override
    public void guardar() throws PersistenciaException {
        PrintWriter escritor = null;
        try {
            escritor = new PrintWriter(rutaArchivo);
            escritor.println(COMENTARIO + " " + encabezadoArchivo());
            for (T elemento : elementos.values()) {
                escritor.println(convertirALinea(elemento));
            }
        } catch (IOException e) {
            throw new PersistenciaException(
                    "No se pudo escribir el archivo " + rutaArchivo, e);
        } finally {
            if (escritor != null) {
                escritor.close();
            }
        }
    }

    /**
     * @return cantidad de lineas descartadas en la ultima carga
     */
    public int getLineasConError() {
        return lineasConError;
    }

    /**
     * @return ruta del archivo de persistencia
     */
    public String getRutaArchivo() {
        return rutaArchivo;
    }

    /**
     * Normaliza un codigo para usarlo como clave del Map.
     *
     * @param codigo codigo original
     * @return codigo sin espacios y en mayusculas
     */
    private String normalizar(String codigo) {
        return codigo.trim().toUpperCase();
    }

    /**
     * Cierra el lector controlando la excepcion.
     *
     * @param lector lector a cerrar (puede ser null)
     */
    private void cerrar(BufferedReader lector) {
        if (lector != null) {
            try {
                lector.close();
            } catch (IOException e) {
                // No hay nada util que hacer si falla el cierre.
            }
        }
    }
}
