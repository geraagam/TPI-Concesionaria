# Informe de uso de Inteligencia Artificial Generativa (IAG)

**Trabajo Final Integrador — Programación II — UTN FRRe — Comisiones 2 y 3 — Año 2026**

Este informe responde a los puntos **b**, **d** y **e** de la *Guía obligatoria: Uso de
Inteligencia Artificial Generativa (IAG) y Defensa Individual en Coloquio*.

| | |
|---|---|
| **Alumno** | Gamarra, Gerardo Andrés |
| **Legajo** | 31300 |
| **Comisión** | Comision 1.3 |
| **IAG seleccionada** | Anthropic Claude |

---

## 1. IAG seleccionada

La herramienta incorporada al desarrollo del Trabajo Final Integrador fue
**Anthropic Claude**, plataforma de acceso gratuito basada en un Gran Modelo de
Lenguaje (LLM), conforme al punto 2.a de la guía de cátedra.

---

## 2. Justificación técnica de la elección

La elección se fundamentó en cuatro criterios técnicos, evaluados frente a las
alternativas gratuitas disponibles (ChatGPT en su versión gratuita, Google Gemini,
DeepSeek y GitHub Copilot):

**a) Coherencia arquitectónica en proyectos multiclase.**
El TFI no es un ejercicio aislado sino un sistema de veinte clases distribuidas en
cuatro paquetes, donde una decisión tomada en el modelo repercute en la capa de datos
y en la de presentación. La capacidad de mantener coherente la totalidad del proyecto
dentro de una misma conversación —sin perder de vista las clases escritas al principio
al generar las del final— fue el criterio de mayor peso. Herramientas orientadas al
autocompletado dentro del editor, como GitHub Copilot, resultan eficaces para completar
líneas o métodos aislados, pero no para sostener una arquitectura en capas completa.

**b) Verificación efectiva del código producido.**
Durante el desarrollo, el código no se incorporó al proyecto por simple copiado: fue
compilado y ejecutado para verificar su funcionamiento antes de aceptarlo. Se verificó
la compilación contra las versiones 8, 11, 17 y 21 del JDK —relevante porque la cátedra
exige que el proyecto compile en BlueJ, cuya versión de JDK puede diferir de la del
entorno de desarrollo— y se ejecutaron recorridos completos del menú, incluyendo casos
de error. Esta posibilidad de validar empíricamente la salida, en lugar de confiar en
la sugerencia textual, fue determinante: varios de los fallos documentados en el
apartado 4 se detectaron precisamente por esta vía.

**c) Explicación del razonamiento de diseño, no solo del resultado.**
Dado que la guía de cátedra condiciona la aprobación del coloquio a la capacidad de
justificar las decisiones de diseño, se priorizó una herramienta que explicitara el
*por qué* de cada elección (por ejemplo, por qué mantener los atributos `private` en
lugar de `protected`, o por qué validar en la capa de negocio y no en los setters) y no
únicamente el código resultante. Esa fundamentación es la que se trasladó a los
comentarios internos y al documento de decisiones de diseño.

**d) Calidad de la interacción en español técnico.**
La totalidad del proyecto, su documentación y su defensa se desarrollan en español. Se
valoró la precisión terminológica en castellano para conceptos de POO (sobreescritura,
encapsulamiento, polimorfismo, realización de interfaces), evitando calcos del inglés
que dificultarían la exposición oral.

**Limitación asumida.** La principal desventaja frente a GitHub Copilot es que Claude no
se integra de forma nativa al editor, por lo que el flujo de trabajo requiere alternar
entre la conversación y el entorno de desarrollo. Se asumió ese costo a cambio de los
cuatro criterios anteriores.

---

## 3. Ámbito de aplicación dentro del proyecto

*(Punto 2.c de la guía: la cátedra otorga libertad total sobre dónde emplear la IAG.
Describí acá, con precisión, en qué partes la usaste. Una descripción inexacta se cae
sola en el coloquio.)*

La IAG se empleó en las siguientes áreas:

- **Diseño del esquema de clases y jerarquías POO:** definición de la clase abstracta
  `Vehiculo` y de las subclases `Automovil`, `Camioneta` y `Motocicleta`, junto con la
  distribución de atributos comunes y específicos.
- **Implementación de la persistencia:** lectura y escritura del archivo `stock.csv`
  mediante `BufferedReader` y `PrintWriter`, incluido el formato de serialización y el
  tratamiento de líneas corruptas.
- **Colecciones y programación genérica:** diseño de la interfaz
  `Repositorio<T extends Identificable>`, de la clase abstracta genérica
  `RepositorioEnArchivo<T>` y del método genérico `leerOpcionEnum()`.
- **Manejo de excepciones:** definición de las cuatro excepciones propias del dominio y
  del encadenamiento de excepciones técnicas (`IOException`, `NumberFormatException`)
  hacia excepciones de negocio.
- **Documentación interna:** redacción de comentarios JavaDoc y remarks explicativos.
- **Diagrama de clases UML:** generación del diagrama a partir de la implementación.

En todos los casos el código fue revisado, compilado y ejecutado antes de incorporarse
al proyecto, y se introdujeron correcciones sobre las propuestas iniciales según se
detalla en el apartado siguiente.

---

## 4. Documentación de errores e inexactitudes detectadas

*(Punto 3.d de la guía. Los cuatro casos siguientes son reales y verificables en el
código: podés mostrar el antes y el después durante el coloquio.)*

### Caso 1 — Conversión incorrecta de datos: notación científica en la persistencia

**Categoría:** error lógico / conversión incorrecta de datos.

La implementación inicial del método `convertirALinea()` concatenaba directamente el
wrapper `Double` a la línea de texto:

```java
linea.append(SEPARADOR).append(vehiculo.getPrecioBase());
```

Al ejecutar el programa y abrir el archivo generado, los precios se habían almacenado
en **notación científica**:

```
AUTOMOVIL;V001;Toyota;Corolla;2022;Blanco;USADO;45000;2.85E7;SEDAN;4
```

**Causa:** la concatenación invoca implícitamente `Double.toString()`, que para valores
grandes devuelve notación exponencial. El archivo seguía siendo legible por el programa
—`Double.valueOf("2.85E7")` funciona— pero resultaba incomprensible para una persona y
hacía inviable la edición o inspección manual del archivo de stock.

**Corrección aplicada:** se incorporó un método de formateo explícito:

```java
private String formatearNumero(Double valor) {
    if (valor == null) {
        return "0.00";
    }
    return String.format(Locale.US, "%.2f", valor);
}
```

**Cómo se detectó:** no por inspección del código, sino por **ejecutar el programa y
abrir el archivo resultante**. El código compilaba y funcionaba; el defecto solo era
visible en la salida.

---

### Caso 2 — Sesgo de configuración regional: dependencia del *locale* del sistema

**Categoría:** sesgo conceptual / error latente de portabilidad.

Al corregir el Caso 1, la solución inmediata habría sido `String.format("%.2f", valor)`,
sin especificar el `Locale`. Esa versión **funciona correctamente en un equipo
configurado en inglés y falla en uno configurado en español**: `String.format()` utiliza
la configuración regional del sistema, de modo que en una máquina en español el precio
se escribiría como `28500000,00` con coma decimal. Al releer el archivo,
`Double.valueOf("28500000,00")` lanzaría una `NumberFormatException` y el registro se
descartaría.

**Relevancia práctica:** el proyecto se desarrolla en equipos configurados en español y
debe ejecutarse en los equipos de la cátedra. El archivo generado en una máquina podría
no ser legible en otra.

**Corrección aplicada:** fijar explícitamente `Locale.US` en el formateo, de modo que el
separador decimal sea siempre el punto, con independencia de la configuración del equipo.

Este caso ilustra un sesgo frecuente del código generado automáticamente: asume un
entorno de ejecución homogéneo y omite consideraciones de internacionalización que solo
se manifiestan al cambiar de máquina.

---

### Caso 3 — Error sintáctico: sentencia `import` duplicada

**Categoría:** error sintáctico.

El archivo `RepositorioVehiculo.java` fue generado con la misma importación repetida:

```java
import concesionaria.modelo.TipoCarroceria;
import concesionaria.modelo.TipoCarroceria;
```

Java tolera la duplicación —no es un error de compilación— pero constituye código
defectuoso que cualquier revisión de calidad señalaría. Se eliminó la línea redundante.

---

### Caso 4 — Código muerto: sobreescritura redundante de `toString()`

**Categoría:** sesgo conceptual / código innecesario.

Las tres subclases concretas se generaron inicialmente con una sobreescritura de
`toString()` que únicamente delegaba en la superclase:

```java
@Override
public String toString() {
    return super.toString();
}
```

**Problema:** esa redefinición no agrega comportamiento alguno. El método heredado de
`Vehiculo` ya produce la salida correcta para cada subclase, porque se apoya en los
métodos abstractos `getTipo()` y `getDetalleEspecifico()`. Mantenerla habría sido
contradictorio con el principio que el propio diseño busca demostrar: que el
polimorfismo evita repetir código en las subclases.

**Corrección aplicada:** se eliminaron las tres sobreescrituras. `toString()` queda
implementado una única vez, en la clase abstracta.

---

### Conclusión del apartado

Los cuatro casos comparten un rasgo: **el código compilaba y se ejecutaba en todos
ellos**. Ninguno habría sido detectado por el compilador. Los Casos 1 y 2 se
identificaron ejecutando el programa e inspeccionando la salida real; los Casos 3 y 4,
mediante revisión manual del código. Esto confirma el criterio de la cátedra: la
verificación y auditoría del código generado es una competencia independiente de la
capacidad de generarlo.

---

## 5. Valoración crítica personal de la experiencia

*(Punto 3.e de la guía.)*



---

## Referencias del proyecto

- Decisiones de diseño justificadas: [`DOCUMENTACION-TECNICA.md`](DOCUMENTACION-TECNICA.md)
- Diagrama de clases: [`uml/diagrama-clases.png`](uml/diagrama-clases.png)
- Documentación de clases y métodos: `doc/index.html`
- Enunciado y guías de la cátedra: carpeta [`consigna/`](consigna/)
