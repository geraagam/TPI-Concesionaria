# Documentación técnica — Sistema de gestión de stock

**Trabajo Final Integrador — Programación II — UTN FRRe — 2026**

| | |
|---|---|
| **Escenario** | Concesionaria Multimarcas de vehículos |
| **Lenguaje** | Java (compatible con JDK 8 o superior) |
| **Tipo de aplicación** | Consola interactiva |
| **Persistencia** | Archivo de texto plano delimitado (`stock.csv`) |
| **Dependencias externas** | Ninguna (solo la biblioteca estándar de Java) |

### Integrantes del grupo

| Apellido y nombre | Legajo | Responsabilidad principal |
|---|---|---|
| Gamarra, Gerardo Andrés | 31300 | *(completar)* |
| *(completar)* | | |
| Aquino, Leandro Miguel| | |

---

## 1. Objetivo y alcance

El sistema administra el stock de una concesionaria que comercializa automóviles,
camionetas y motocicletas, distinguiendo unidades nuevas (0 Km) de usadas
(con kilometraje recorrido).

Funcionalidades implementadas:

- Alta, baja y modificación de unidades, con validación de todos los datos.
- Consultas por código, por marca, por tipo de vehículo y por estado.
- Reporte de valorización del stock, con depreciación diferenciada por tipo.
- Persistencia automática: el stock se conserva entre ejecuciones.

Queda fuera del alcance la gestión de clientes, ventas y facturación: el enunciado
se centra en el registro y la valorización del stock.

---

## 2. Dominio del problema y abstracción

### 2.1 Entidades identificadas

Del enunciado surgen los atributos obligatorios: marca, modelo, año, color y
carrocería (o estilo de motocicleta). A estos se agregaron tres atributos
necesarios para que el sistema sea operable:

| Atributo agregado | Justificación |
|---|---|
| **Código de stock** | Dos unidades pueden compartir marca, modelo, año y color (dos Corolla blancos 2022) y ser unidades distintas. Sin un identificador único no es posible seleccionar, modificar ni dar de baja una unidad en particular. |
| **Estado y kilometraje** | El enunciado exige distinguir unidades 0 Km de usadas; el kilometraje es el dato que materializa esa distinción. |
| **Precio base** | El enunciado menciona "valores comerciales". Es además la base del reporte de valorización. |

### 2.2 Jerarquía de clases

```
                    Vehiculo  «abstract»
                        │
        ┌───────────────┼───────────────┐
    Automovil       Camioneta       Motocicleta
```

`Vehiculo` concentra los ocho atributos comunes y el comportamiento compartido.
Cada subclase aporta únicamente lo que la distingue:

| Clase | Atributos propios | Depreciación anual |
|---|---|---|
| `Automovil` | carrocería, cantidad de puertas | 10 % |
| `Camioneta` | carrocería, capacidad de carga, tracción integral | 8 % |
| `Motocicleta` | estilo, cilindrada | 12 % |

`Vehiculo` se declaró **abstracta** porque no existe en el dominio una unidad que
sea "un vehículo" sin ser alguno de los tres tipos concretos, y porque dos de sus
métodos (`getTipo()` y `getDetalleEspecifico()`) no admiten implementación a ese
nivel de abstracción.

### 2.3 Tipos de datos

Los atributos numéricos se modelaron con **clases wrapper** (`Integer`, `Double`,
`Boolean`) en lugar de tipos primitivos. El motivo es que un wrapper admite `null`,
lo que permite distinguir *"el precio es cero"* de *"el precio no fue cargado"*.
Con un `double` primitivo ambos casos llegarían como `0.0`.

Los valores de dominio acotado se modelaron con **tipos enumerativos**
(`EstadoVehiculo`, `TipoCarroceria`, `EstiloMoto`) en lugar de cadenas o
constantes numéricas, de modo que el compilador impida valores inválidos.

---

## 3. Arquitectura: esquema "Diseño vs. Negocio"

El código se organizó en capas, materializadas como paquetes Java:

```
concesionaria.presentacion   →   interacción con el usuario
         ↓
concesionaria.negocio        →   reglas del negocio y cálculos
         ↓
concesionaria.datos          →   colección en memoria y persistencia
         ↓
                                 stock.csv
```

Cada capa depende únicamente de la inmediata inferior. Reglas respetadas:

| Capa | Paquete | Qué hace | Qué **no** hace |
|---|---|---|---|
| Presentación | `presentacion` | Menús, lectura de teclado, formato de salida | No decide reglas ni accede al archivo |
| Negocio | `negocio` | Valida, calcula, coordina | No imprime ni conoce `java.io` |
| Datos | `datos` | Administra la colección, lee y escribe el archivo | No valida reglas de negocio |
| Dominio | `modelo` | Representa las entidades | No conoce a ninguna de las capas |

`App` es la única clase que conoce las tres capas simultáneamente: construye el
repositorio, se lo inyecta al servicio y este al menú.

**Consecuencia práctica:** reemplazar la consola por una interfaz gráfica implica
modificar solo el paquete `presentacion`; reemplazar el archivo por una base de
datos SQL implica crear una nueva clase que implemente la interfaz `Repositorio`
sin tocar el negocio.

---

## 4. Decisiones de diseño

### 4.1 Atributos privados en la clase abstracta

Los atributos de `Vehiculo` se declararon `private` y no `protected`.

Las subclases no acceden a ellos directamente, sino a través del constructor
`super(...)` o de los setters públicos heredados. La alternativa (`protected`)
habría permitido que una subclase escribiera `this.kilometraje = -500`
salteándose toda validación. Manteniéndolos privados se conserva el
encapsulamiento sin perder el patrón JavaBean.

### 4.2 Validación en la capa de negocio, no en los setters

Los setters del modelo son setters JavaBean puros, sin lógica. Todas las reglas
están centralizadas en `ServicioConcesionaria.validar()`.

El criterio fue mantener las reglas en un único punto del sistema: modificar una
regla implica editar un solo método, en lugar de buscarla entre los setters de
cuatro clases.

Reglas implementadas: campos obligatorios no vacíos; año entre 1950 y el año
siguiente al actual; precio mayor a cero; kilometraje entre 0 y 1.000.000;
coherencia entre estado y kilometraje (una unidad 0 Km no puede registrar
kilómetros); código de stock único.

### 4.3 Dependencia hacia la interfaz, no hacia la implementación

`ServicioConcesionaria` declara su dependencia como `Repositorio<Vehiculo>` y no
como `RepositorioVehiculo`. El servicio desconoce si los datos terminan en un
archivo, en memoria o en una base de datos.

### 4.4 Colección elegida

El repositorio almacena las unidades en un **`LinkedHashMap<String, Vehiculo>`**:

- `Map` porque la búsqueda por código es la operación más frecuente y en un mapa
  es de acceso directo, mientras que en una lista exigiría recorrerla completa.
- `Linked` porque conserva el orden de incorporación de las unidades.

Hacia el exterior, `listarTodos()` devuelve una `List` nueva, de modo que ninguna
otra clase pueda modificar la colección interna.

### 4.5 Sobreescritura de `equals()`, `hashCode()` y `toString()`

`equals()` y `hashCode()` se sobreescriben en `Vehiculo` y operan sobre el código
de stock. Ambos se redefinen en conjunto porque las colecciones basadas en hash
localizan primero el elemento por su `hashCode()` y solo luego comparan con
`equals()`: redefinir uno solo rompería el funcionamiento del `Map`.

`toString()` se implementa una única vez en `Vehiculo` y se apoya en los métodos
abstractos, por lo que una sola implementación produce la ficha correcta para los
tres tipos de unidad.

### 4.6 Programación genérica

Se aplicó en dos niveles:

1. **Repositorio genérico.** `Repositorio<T extends Identificable>` y
   `RepositorioEnArchivo<T extends Identificable>` definen el comportamiento de
   cualquier repositorio. `RepositorioVehiculo extends RepositorioEnArchivo<Vehiculo>`
   solo aporta la conversión entre objeto y línea de texto.

   La cota `T extends Identificable` garantiza que todo elemento almacenado pueda
   informar su código. Si el sistema creciera a clientes o ventas, no haría falta
   reescribir la lógica de almacenamiento.

2. **Método genérico.** `LectorConsola.leerOpcionEnum()` está declarado como
   `<E extends Enum<E>> E leerOpcionEnum(String, E[])` y resuelve la selección por
   pantalla de cualquier enumeración del sistema, evitando tres métodos casi
   idénticos.

`RepositorioEnArchivo` aplica además el patrón **método plantilla**: define el
algoritmo de carga y guardado, y delega en métodos abstractos
(`convertirALinea()`, `convertirDesdeLinea()`) la parte que depende del tipo
concreto.

### 4.7 Codificación del código fuente

El código fuente se escribió íntegramente en ASCII (sin tildes ni eñes, también en
los comentarios). La razón es que la codificación de los archivos `.java` (UTF-8)
y la de la consola de Windows (CP1252) no coinciden, y los textos con acentos se
muestran alterados. Esta decisión garantiza una salida idéntica en BlueJ, en VS
Code y en consola. La documentación en Markdown sí utiliza acentuación normal.

---

## 5. Persistencia

### 5.1 Formato

Archivo de texto plano, un registro por línea, campos separados por `;`.
Las líneas que comienzan con `#` son comentarios y se ignoran.

```
AUTOMOVIL  ;codigo;marca;modelo;anio;color;estado;km;precio;carroceria;puertas
CAMIONETA  ;codigo;marca;modelo;anio;color;estado;km;precio;carroceria;cargaKg;4x4
MOTOCICLETA;codigo;marca;modelo;anio;color;estado;km;precio;estilo;cilindrada
```

El primer campo actúa como discriminador de tipo y permite reconstruir la
subclase correcta durante la lectura.

### 5.2 Momento de escritura

El archivo se reescribe inmediatamente después de cada alta, baja o modificación,
y no únicamente al salir del programa, para que los datos persistan aun ante un
cierre inesperado.

### 5.3 Robustez

- Si el archivo no existe, el sistema inicia con el stock vacío y lo crea en el
  primer guardado.
- Si una línea está mal formada, se descarta, se contabiliza y la lectura
  continúa con el resto. El programa no se interrumpe.
- Los valores decimales se escriben con `String.format(Locale.US, "%.2f", valor)`.
  Sin fijar el `Locale`, en un equipo configurado en español el separador decimal
  sería una coma y la relectura con `Double.valueOf()` fallaría. El archivo
  resulta así independiente de la configuración regional del equipo.
- El cierre de los flujos de entrada y salida se realiza en bloques `finally`.

---

## 6. Tratamiento de excepciones

Se definieron cuatro excepciones propias, todas derivadas de `Exception` y por lo
tanto **controladas**: el compilador obliga a capturarlas o declararlas.

| Excepción | Se lanza cuando | Origen |
|---|---|---|
| `DatoInvalidoException` | Un dato incumple una regla del negocio o una conversión falla | `ServicioConcesionaria`, `RepositorioVehiculo` |
| `VehiculoDuplicadoException` | El código de stock ya está registrado | `ServicioConcesionaria` |
| `VehiculoNoEncontradoException` | Se opera sobre una unidad inexistente | `ServicioConcesionaria` |
| `PersistenciaException` | Falla la lectura o escritura del archivo | `RepositorioEnArchivo` |

`PersistenciaException` y `DatoInvalidoException` admiten **encadenamiento**:
envuelven la excepción original (`IOException`, `NumberFormatException`) y la
exponen como un error del dominio. De este modo las capas superiores no necesitan
conocer `java.io` ni las particularidades del parseo.

Las excepciones se lanzan en la capa de negocio y se capturan en la capa de
presentación, que las traduce en mensajes para el usuario. El programa no se
interrumpe ante ningún error previsto.

---

## 7. Interfaz de consola y control de ingreso de datos

La clase `LectorConsola` concentra la validación sintáctica del ingreso. Cada
método insiste hasta obtener un dato utilizable:

| Método | Control que aplica |
|---|---|
| `leerTexto()` | Rechaza cadenas vacías |
| `leerEntero(msg, min, max)` | Verifica que sea entero y que esté en rango |
| `leerDecimal(msg, min)` | Acepta coma o punto como separador decimal |
| `leerSiNo()` | Admite únicamente S o N |
| `leerOpcionEnum()` | Presenta las constantes numeradas; impide valores inexistentes |

El flujo del menú evita además ingresos innecesarios: si la unidad es 0 Km, el
sistema no solicita kilometraje. Las bajas requieren confirmación explícita.

---

## 8. Compilación y ejecución

### Requisitos

JDK 8 o superior. El proyecto fue verificado compilando contra las versiones
8, 11, 17 y 21, sin errores ni advertencias (`javac -Xlint:all`).

### Por línea de comandos

```bash
javac -encoding UTF-8 -d build concesionaria/App.java
java -cp build concesionaria.App
```

### En BlueJ

**Project → Open Non BlueJ...** → seleccionar la carpeta `TPI-Concesionaria` →
botón **Compile** → clic derecho sobre `App` → `void main(String[] args)`.

El archivo `stock.csv` debe permanecer en la raíz del proyecto, que es el
directorio de trabajo durante la ejecución.

### En Visual Studio Code

Abrir la carpeta del proyecto con el *Extension Pack for Java* instalado y
ejecutar `concesionaria/App.java`.

### Regenerar la documentación JavaDoc

```bash
javadoc -encoding UTF-8 -charset UTF-8 -d doc -subpackages concesionaria
```

### Regenerar el diagrama UML

```bash
dot -Tpng -Gdpi=110 uml/diagrama-clases.dot -o uml/diagrama-clases.png
```

---

## 9. Estructura del repositorio

```
TPI-Concesionaria/
├── concesionaria/            Código fuente (4 paquetes + clase de arranque)
├── consigna/                 Enunciado original de la cátedra
├── doc/                      JavaDoc generado (abrir doc/index.html)
├── uml/                      Diagrama de clases (fuente .dot + .png + .svg)
├── stock.csv                 Archivo de persistencia con datos de ejemplo
├── DOCUMENTACION-TECNICA.md  Este documento
└── README.md                 Instalación, ejecución y resumen del sistema
```

---

## 10. Limitaciones conocidas y posibles extensiones

- La persistencia en archivo de texto no admite acceso concurrente de varios
  usuarios. Una base de datos SQL resolvería ese escenario; la arquitectura ya
  está preparada, ya que solo requeriría una nueva implementación de
  `Repositorio<Vehiculo>`.
- El sistema no registra operaciones de venta ni histórico de precios.
- La búsqueda por marca es una coincidencia parcial sin normalización de acentos.
- La interfaz es de consola; el enunciado la solicita explícitamente así.
