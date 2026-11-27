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

public class RepositorioVehiculo extends RepositorioEnArchivo<Vehiculo> {

    private static final String TIPO_AUTOMOVIL = "AUTOMOVIL";
    private static final String TIPO_CAMIONETA = "CAMIONETA";
    private static final String TIPO_MOTOCICLETA = "MOTOCICLETA";

    public RepositorioVehiculo(String rutaArchivo) {
        super(rutaArchivo);
    }

    @Override
    protected String encabezadoArchivo() {
        return "Stock de la Concesionaria Multimarcas. No editar a mano.";
    }

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

        linea.append(SEPARADOR).append(vehiculo.getCodigo());
        linea.append(SEPARADOR).append(vehiculo.getMarca());
        linea.append(SEPARADOR).append(vehiculo.getModelo());
        linea.append(SEPARADOR).append(vehiculo.getAnio());
        linea.append(SEPARADOR).append(vehiculo.getColor());
        linea.append(SEPARADOR).append(vehiculo.getEstado().name());
        linea.append(SEPARADOR).append(vehiculo.getKilometraje());
        linea.append(SEPARADOR).append(formatearNumero(vehiculo.getPrecioBase()));

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

    @Override
    protected Vehiculo convertirDesdeLinea(String linea) throws DatoInvalidoException {
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

    private String formatearNumero(Double valor) {
        if (valor == null) {
            return "0.00";
        }
        return String.format(Locale.US, "%.2f", valor);
    }

    private Integer aEntero(String texto, String campo) throws DatoInvalidoException {
        try {
            return Integer.valueOf(texto.trim());
        } catch (NumberFormatException e) {
            throw new DatoInvalidoException(
                    "El campo " + campo + " no es un numero entero valido: " + texto, e);
        }
    }

    private Double aDecimal(String texto, String campo) throws DatoInvalidoException {
        try {
            return Double.valueOf(texto.trim());
        } catch (NumberFormatException e) {
            throw new DatoInvalidoException(
                    "El campo " + campo + " no es un numero valido: " + texto, e);
        }
    }

    private EstadoVehiculo aEstado(String texto) throws DatoInvalidoException {
        EstadoVehiculo estado = EstadoVehiculo.desdeTexto(texto);
        if (estado == null) {
            throw new DatoInvalidoException("Estado desconocido: " + texto);
        }
        return estado;
    }

    private TipoCarroceria aCarroceria(String texto) throws DatoInvalidoException {
        try {
            return TipoCarroceria.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DatoInvalidoException("Carroceria desconocida: " + texto, e);
        }
    }

    private EstiloMoto aEstilo(String texto) throws DatoInvalidoException {
        try {
            return EstiloMoto.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DatoInvalidoException("Estilo de moto desconocido: " + texto, e);
        }
    }
}
