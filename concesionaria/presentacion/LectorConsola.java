package concesionaria.presentacion;

import java.util.Scanner;

/**
 * Herramienta de entrada de datos por consola (control de "data-entry").
 *
 * Su unica responsabilidad es conseguir un dato valido del usuario: si la
 * persona escribe cualquier cosa, vuelve a preguntar hasta que el dato sirva.
 * Gracias a esta clase, el menu nunca recibe basura y el programa no se corta
 * nunca por un error de tipeo.
 *
 * @author Grupo TFI - Programacion II
 * @version 1.0
 */
public class LectorConsola {

    /** Scanner unico para toda la aplicacion. */
    private final Scanner scanner;

    /**
     * Crea el lector sobre la entrada estandar (el teclado).
     */
    public LectorConsola() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Pide un texto obligatorio.
     *
     * @param mensaje texto que se le muestra al usuario
     * @return texto ingresado, sin espacios al principio ni al final
     */
    public String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            if (entrada.length() > 0) {
                return entrada;
            }
            System.out.println("  [!] El dato no puede quedar vacio. Intente de nuevo.");
        }
    }

    /**
     * Pide un numero entero dentro de un rango.
     *
     * Se usa el wrapper Integer y su metodo parseInt para convertir el texto.
     * Si el usuario escribe "hola", parseInt lanza NumberFormatException: la
     * atrapamos, avisamos y volvemos a preguntar.
     *
     * @param mensaje texto que se le muestra al usuario
     * @param minimo  valor minimo aceptado
     * @param maximo  valor maximo aceptado
     * @return numero valido
     */
    public Integer leerEntero(String mensaje, int minimo, int maximo) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            try {
                Integer valor = Integer.valueOf(Integer.parseInt(entrada));
                if (valor.intValue() < minimo || valor.intValue() > maximo) {
                    System.out.println("  [!] El valor debe estar entre " + minimo
                            + " y " + maximo + ".");
                } else {
                    return valor;
                }
            } catch (NumberFormatException e) {
                System.out.println("  [!] '" + entrada + "' no es un numero entero. Intente de nuevo.");
            }
        }
    }

    /**
     * Pide un numero con decimales mayor o igual a un minimo.
     *
     * Acepta tanto la coma como el punto decimal, porque en Argentina se
     * escribe 1500,50 y Java espera 1500.50.
     *
     * @param mensaje texto que se le muestra al usuario
     * @param minimo  valor minimo aceptado
     * @return numero valido
     */
    public Double leerDecimal(String mensaje, double minimo) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim().replace(",", ".");
            try {
                Double valor = Double.valueOf(entrada);
                if (valor.doubleValue() < minimo) {
                    System.out.println("  [!] El valor no puede ser menor a " + minimo + ".");
                } else {
                    return valor;
                }
            } catch (NumberFormatException e) {
                System.out.println("  [!] '" + entrada + "' no es un numero valido. Intente de nuevo.");
            }
        }
    }

    /**
     * Pide una respuesta de si o no.
     *
     * @param mensaje texto que se le muestra al usuario
     * @return true si contesto que si
     */
    public boolean leerSiNo(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (S/N): ");
            String entrada = scanner.nextLine().trim().toUpperCase();
            if (entrada.equals("S") || entrada.equals("SI")) {
                return true;
            }
            if (entrada.equals("N") || entrada.equals("NO")) {
                return false;
            }
            System.out.println("  [!] Responda S o N.");
        }
    }

    /**
     * METODO GENERICO: muestra las constantes de cualquier enum numeradas y
     * devuelve la que el usuario elija.
     *
     * &lt;E extends Enum&lt;E&gt;&gt; significa "sirve para cualquier tipo E,
     * siempre que E sea un enum". Con este unico metodo se eligen estados,
     * carrocerias y estilos de moto: no hace falta escribir uno para cada uno.
     *
     * @param <E>      tipo de enum
     * @param titulo   titulo del listado
     * @param opciones constantes disponibles (se obtienen con values())
     * @return constante elegida por el usuario
     */
    public <E extends Enum<E>> E leerOpcionEnum(String titulo, E[] opciones) {
        System.out.println(titulo);
        for (int i = 0; i < opciones.length; i++) {
            System.out.println("    " + (i + 1) + ") " + opciones[i]);
        }
        Integer elegida = leerEntero("  Opcion (1-" + opciones.length + "): ", 1, opciones.length);
        return opciones[elegida.intValue() - 1];
    }

    /**
     * Espera a que el usuario presione Enter para continuar.
     */
    public void esperarEnter() {
        System.out.print("\n  Presione ENTER para volver al menu...");
        scanner.nextLine();
    }

    /**
     * Cierra el Scanner. Se llama una sola vez, al salir del programa.
     */
    public void cerrar() {
        scanner.close();
    }
}
