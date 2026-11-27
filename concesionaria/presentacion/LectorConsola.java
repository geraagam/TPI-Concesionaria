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
    private final Scanner scanner;
    public LectorConsola() {
        this.scanner = new Scanner(System.in);
    }
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
    public <E extends Enum<E>> E leerOpcionEnum(String titulo, E[] opciones) {
        System.out.println(titulo);
        for (int i = 0; i < opciones.length; i++) {
            System.out.println("    " + (i + 1) + ") " + opciones[i]);
        }
        Integer elegida = leerEntero("  Opcion (1-" + opciones.length + "): ", 1, opciones.length);
        return opciones[elegida.intValue() - 1];
    }
    public void esperarEnter() {
        System.out.print("\n  Presione ENTER para volver al menu...");
        scanner.nextLine();
    }
    public void cerrar() {
        scanner.close();
    }
}
