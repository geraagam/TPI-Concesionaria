package concesionaria.datos;

import java.util.Locale;

import concesionaria.excepciones.DatoInvalidoException;
import concesionaria.modelo.Automovil;
import concesionaria.modelo.Camioneta;
import concesionaria.modelo.EstadoVehiculo;
import concesionaria.modelo.EstiloMoto;
import concesionaria.modelo.Motocicleta;
import concesionaria.modelo.TipoCarroceria;
import concesionaria.modelo.Vehiculo;

/**
 * Repositorio concreto de vehiculos.
 *
 * Hereda de RepositorioEnArchivo toda la administracion de la coleccion y del
 * archivo, y solo agrega lo que es propio del dominio: como se escribe un
 * vehiculo en una linea de texto y como se lo vuelve a armar al leerlo.
 *
 * Formato de cada linea (campos separados por punto y coma):
 *   AUTOMOVIL  ;codigo;marca;modelo;anio;color;estado;km;precio;carroceria;puertas
 *   CAMIONETA  ;codigo;marca;modelo;anio;color;estado;km;precio;carroceria;cargaKg;4x4
 *   MOTOCICLETA;codigo;marca;modelo;anio;color;estado;km;precio;estilo;cilindrada
 *
 * @author Grupo TFI - Programacion II
 * @version 1.0
 */
public class RepositorioVehiculo extends RepositorioEnArchivo<Vehiculo> {

    private static final String TIPO_AUTOMOVIL = "AUTOMOVIL";
    private static final String TIPO_CAMIONETA = "CAMIONETA";
    private static final String TIPO_MOTOCICLETA = "MOTOCICLETA";

    /**
     * @param rutaArchivo nombre del archivo de stock (por ejemplo "stock.csv")
     */
    public RepositorioVehiculo(String rutaArchivo) {
        super(rutaArchivo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String encabezadoArchivo() {
        return "Stock de la Concesionaria Multimarcas. No editar a mano.";
    }

    /**
     * Convierte un vehiculo en una linea de texto.
     *
     * Se usa instanceof para saber de que subclase se trata y poder guardar
     * tambien sus atributos propios.
     *
     * @param vehiculo vehiculo a convertir
     * @return linea de texto lista para escribir en el archivo
     */
    @Override
    protected String convertirALinea(Vehiculo vehiculo) {
        StringBuilder linea = new StringBuilder();

        if (vehiculo instanceof Automovil) {
            linea.append(TIPO_AUTOMOVIL);
        } else if (vehiculo instanceof Camioneta) {
            linea.append(TIPO_CAMIONETA);
        } else {
            linea.append(TIPO_MOTOCICLETA);
        }

        // Parte comun a todos los vehiculos.
        linea.append(SEPARADOR).append(vehiculo.getCodigo());
        linea.append(SEPARADOR).append(vehiculo.getMarca());
        linea.append(SEPARADOR).append(vehiculo.getModelo());
        linea.append(SEPARADOR).append(vehiculo.getAnio());
        linea.append(SEPARADOR).append(vehiculo.getColor());
        // Se guarda name() y no toString(): el archivo necesita el nombre
        // exacto de la constante (USADO), no el texto bonito ("Usado").
        linea.append(SEPARADOR).append(vehiculo.getEstado().name());
        linea.append(SEPARADOR).append(vehiculo.getKilometraje());
        linea.append(SEPARADOR).append(formatearNumero(vehiculo.getPrecioBase()));

        // Parte propia de cada subclase.
        if (vehiculo instanceof Automovil) {
            Automovil auto = (Automovil) vehiculo;
            linea.append(SEPARADOR).append(auto.getCarroceria().name());
            linea.append(SEPARADOR).append(auto.getCantidadPuertas());
        } else if (vehiculo instanceof Camioneta) {
            Camioneta camioneta = (Camioneta) vehiculo;
            linea.append(SEPARADOR).append(camioneta.getCarroceria().name());
            linea.append(SEPARADOR).append(formatearNumero(camioneta.getCapacidadCargaKg()));
            linea.append(SEPARADOR).append(camioneta.getTraccionIntegral());
        } else {
            Motocicleta moto = (Motocicleta) vehiculo;
            linea.append(SEPARADOR).append(moto.getEstilo().name());
            linea.append(SEPARADOR).append(moto.getCilindrada());
        }

        return linea.toString();
    }

    /**
     * Reconstruye el vehiculo que representa una linea del archivo.
     *
     * @param linea linea leida
     * @return vehiculo armado (Automovil, Camioneta o Motocicleta)
     * @throws DatoInvalidoException si la linea esta incompleta o mal formada
     */
    @Override
    protected Vehiculo convertirDesdeLinea(String linea) throws DatoInvalidoException {
        // El -1 hace que split no descarte los campos vacios del final.
        String[] campos = linea.split(SEPARADOR, -1);

        if (campos.length < 11) {
            throw new DatoInvalidoException("La linea no tiene todos los campos: " + linea);
        }

        String tipo = campos[0].trim().toUpperCase();
        String codigo = campos[1].trim();
        String marca = campos[2].trim();
        String modelo = campos[3].trim();
        Integer anio = aEntero(campos[4], "anio");
        String color = campos[5].trim();
        EstadoVehiculo estado = aEstado(campos[6]);
        Integer kilometraje = aEntero(campos[7], "kilometraje");
        Double precio = aDecimal(campos[8], "precio");

        if (TIPO_AUTOMOVIL.equals(tipo)) {
            TipoCarroceria carroceria = aCarroceria(campos[9]);
            Integer puertas = aEntero(campos[10], "cantidad de puertas");
            return new Automovil(codigo, marca, modelo, anio, color, estado,
                    kilometraje, precio, carroceria, puertas);

        } else if (TIPO_CAMIONETA.equals(tipo)) {
            if (campos.length < 12) {
                throw new DatoInvalidoException("Faltan campos de camioneta: " + linea);
            }
            TipoCarroceria carroceria = aCarroceria(campos[9]);
            Double carga = aDecimal(campos[10], "capacidad de carga");
            // Boolean.parseBoolean nunca falla: cualquier texto distinto de
            // "true" se interpreta como false.
            Boolean traccion = Boolean.valueOf(Boolean.parseBoolean(campos[11].trim()));
            return new Camioneta(codigo, marca, modelo, anio, color, estado,
                    kilometraje, precio, carroceria, carga, traccion);

        } else if (TIPO_MOTOCICLETA.equals(tipo)) {
            EstiloMoto estilo = aEstilo(campos[9]);
            Integer cilindrada = aEntero(campos[10], "cilindrada");
            return new Motocicleta(codigo, marca, modelo, anio, color, estado,
                    kilometraje, precio, estilo, cilindrada);
        }

        throw new DatoInvalidoException("Tipo de vehiculo desconocido: " + tipo);
    }

    /**
     * Convierte un Double en texto con dos decimales y punto decimal.
     *
     * Se usa Locale.US a proposito: si se dejara el idioma del sistema, en una
     * PC configurada en espanol el numero saldria con coma (28500000,00) y al
     * volver a leer el archivo Double.valueOf fallaria. El archivo siempre se
     * escribe igual, no importa en que computadora corra el programa.
     *
     * @param valor numero a convertir
     * @return texto del numero con punto decimal
     */
    private String formatearNumero(Double valor) {
        if (valor == null) {
            return "0.00";
        }
        return String.format(Locale.US, "%.2f", valor);
    }

    // ------------------------------------------------------------------
    // Conversiones de texto a wrappers y enums.
    // Cada una traduce un error tecnico de Java en una excepcion del dominio.
    // ------------------------------------------------------------------

    /**
     * Convierte texto a Integer usando el wrapper Integer.
     *
     * @param texto  texto a convertir
     * @param campo  nombre del campo, para el mensaje de error
     * @return valor convertido
     * @throws DatoInvalidoException si el texto no es un numero entero
     */
    private Integer aEntero(String texto, String campo) throws DatoInvalidoException {
        try {
            return Integer.valueOf(texto.trim());
        } catch (NumberFormatException e) {
            throw new DatoInvalidoException(
                    "El campo " + campo + " no es un numero entero valido: " + texto, e);
        }
    }

    /**
     * Convierte texto a Double usando el wrapper Double.
     *
     * @param texto texto a convertir
     * @param campo nombre del campo, para el mensaje de error
     * @return valor convertido
     * @throws DatoInvalidoException si el texto no es un numero
     */
    private Double aDecimal(String texto, String campo) throws DatoInvalidoException {
        try {
            return Double.valueOf(texto.trim());
        } catch (NumberFormatException e) {
            throw new DatoInvalidoException(
                    "El campo " + campo + " no es un numero valido: " + texto, e);
        }
    }

    /**
     * @param texto nombre de la constante
     * @return estado correspondiente
     * @throws DatoInvalidoException si el texto no corresponde a ningun estado
     */
    private EstadoVehiculo aEstado(String texto) throws DatoInvalidoException {
        EstadoVehiculo estado = EstadoVehiculo.desdeTexto(texto);
        if (estado == null) {
            throw new DatoInvalidoException("Estado desconocido: " + texto);
        }
        return estado;
    }

    /**
     * @param texto nombre de la constante
     * @return carroceria correspondiente
     * @throws DatoInvalidoException si el texto no corresponde a ninguna carroceria
     */
    private TipoCarroceria aCarroceria(String texto) throws DatoInvalidoException {
        try {
            return TipoCarroceria.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DatoInvalidoException("Carroceria desconocida: " + texto, e);
        }
    }

    /**
     * @param texto nombre de la constante
     * @return estilo de moto correspondiente
     * @throws DatoInvalidoException si el texto no corresponde a ningun estilo
     */
    private EstiloMoto aEstilo(String texto) throws DatoInvalidoException {
        try {
            return EstiloMoto.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DatoInvalidoException("Estilo de moto desconocido: " + texto, e);
        }
    }
}
