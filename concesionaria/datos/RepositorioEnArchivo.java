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

public abstract class RepositorioEnArchivo<T extends Identificable> implements Repositorio<T> {

    public static final String SEPARADOR = ";";

    public static final String COMENTARIO = "#";

    private final Map<String, T> elementos = new LinkedHashMap<String, T>();

    private final String rutaArchivo;

    private int lineasConError;

    protected RepositorioEnArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    protected abstract String convertirALinea(T elemento);

    protected abstract T convertirDesdeLinea(String linea) throws DatoInvalidoException;

    protected abstract String encabezadoArchivo();

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

    @Override
    public List<T> listarTodos() {
        return new ArrayList<T>(elementos.values());
    }

    @Override
    public T buscarPorCodigo(String codigo) {
        if (codigo == null) {
            return null;
        }
        return elementos.get(normalizar(codigo));
    }

    @Override
    public boolean eliminar(String codigo) {
        if (codigo == null) {
            return false;
        }
        return elementos.remove(normalizar(codigo)) != null;
    }

    @Override
    public int cantidad() {
        return elementos.size();
    }

    @Override
    public void cargar() throws PersistenciaException {
        elementos.clear();
        lineasConError = 0;

        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
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
            throw new PersistenciaException(
                    "No se pudo leer el archivo " + rutaArchivo, e);
        } finally {
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

    public int getLineasConError() {
        return lineasConError;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    private String normalizar(String codigo) {
        return codigo.trim().toUpperCase();
    }

    private void cerrar(BufferedReader lector) {
        if (lector != null) {
            try {
                lector.close();
            } catch (IOException e) {
            }
        }
    }
}
