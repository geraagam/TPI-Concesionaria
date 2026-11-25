package concesionaria.modelo;

/**
 * Contrato que deben cumplir todos los objetos que puedan ser guardados en un
 * repositorio: tienen que poder identificarse a si mismos con un codigo unico.
 *
 * Esta interfaz es la que permite que el repositorio generico funcione: el
 * repositorio no sabe que guarda (puede ser un Vehiculo, un Cliente, etc.),
 * pero sabe que cualquier cosa que guarde sabe decir cual es su codigo.
 *
 * @author Grupo TFI - Programacion II
 * @version 1.0
 */
public interface Identificable {

    /**
     * Devuelve el identificador unico del objeto dentro del sistema.
     *
     * @return codigo unico (por ejemplo "V001")
     */
    String getCodigo();
}
