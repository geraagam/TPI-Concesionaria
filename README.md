# Concesionaria Multimarcas — Trabajo Final Integrador

**Cátedra:** Programación II — UTN FRRe — Tecnicatura Universitaria en Programación — Año 2026

Sistema de consola en Java para la gestión del stock de una concesionaria multimarcas
que comercializa automóviles, camionetas y motocicletas, nuevas (0 Km) y usadas.

> 📄 La justificación de cada decisión de diseño está en **[DOCUMENTACION-TECNICA.md](DOCUMENTACION-TECNICA.md)**.
> 🤖 El informe de uso de IAG exigido por la cátedra está en **[INFORME-USO-IAG.md](INFORME-USO-IAG.md)**.
> 🔧 Cómo trabajar en equipo sobre este repositorio: **[GUIA-GIT-EQUIPO.md](GUIA-GIT-EQUIPO.md)**.
> 📐 El diagrama de clases está en **[uml/diagrama-clases.png](uml/diagrama-clases.png)**.
> 📚 La documentación de clases y métodos (JavaDoc) se abre en **`doc/index.html`**.

## Integrantes

| Apellido y nombre | Legajo | Usuario de GitHub |
|---|---|---|
| Gamarra, Gerardo Andrés | 31300 | *geraagam* |
| *(completar)* | | |
| *(completar)* | | |

---

## 1. Cómo ejecutarlo

### Requisitos

JDK 8 o superior. No requiere ninguna biblioteca externa.

### En BlueJ

1. Abrir BlueJ.
2. Menú **Project → Open Non BlueJ...** (en algunas versiones, *"Abrir proyecto que no es de BlueJ"*).
3. Seleccionar la carpeta **`TPI-Concesionaria`** completa, no un archivo suelto.
   BlueJ la convierte en proyecto y muestra el paquete `concesionaria`.
4. Doble clic en el paquete `concesionaria` para entrar a los subpaquetes.
5. Botón **Compile**. Ninguna caja debe quedar con rayas diagonales.
6. Clic derecho sobre la clase **`App`** → **`void main(String[] args)`** → **Ok**.

> El archivo `stock.csv` debe permanecer en la raíz de `TPI-Concesionaria`, porque
> es el directorio de trabajo que usa BlueJ durante la ejecución.

### En Visual Studio Code

1. **Archivo → Abrir carpeta** → `TPI-Concesionaria`.
2. Esperar a que el *Extension Pack for Java* termine de analizar el proyecto.
3. Abrir `concesionaria/App.java` y hacer clic en **Run** (o `F5`).

### Por línea de comandos

```bash
javac -encoding UTF-8 -d build concesionaria/App.java
java -cp build concesionaria.App
```

> Alcanza con nombrar `App.java`: el compilador sigue las referencias y compila por su
> cuenta las veinte clases del proyecto. Se evita así el uso de comodines (`*.java`),
> que la consola de Windows no expande igual que la de Linux.

---

## 2. Estructura del proyecto

```
TPI-Concesionaria/
├── concesionaria/
│   ├── App.java                      ← arranque: construye las tres capas
│   ├── presentacion/                 ← CAPA DE DISEÑO (interacción con el usuario)
│   │   ├── MenuConsola.java
│   │   └── LectorConsola.java
│   ├── negocio/                      ← CAPA DE NEGOCIO (reglas y cálculos)
│   │   └── ServicioConcesionaria.java
│   ├── datos/                        ← CAPA DE DATOS (colección + persistencia)
│   │   ├── Repositorio.java              (interfaz genérica)
│   │   ├── RepositorioEnArchivo.java     (clase abstracta genérica)
│   │   └── RepositorioVehiculo.java
│   ├── modelo/                       ← DOMINIO DEL PROBLEMA
│   │   ├── Vehiculo.java                 (clase abstracta)
│   │   ├── Automovil.java
│   │   ├── Camioneta.java
│   │   ├── Motocicleta.java
│   │   ├── Identificable.java            (interfaz)
│   │   ├── Depreciable.java              (interfaz)
│   │   ├── EstadoVehiculo.java           (enum)
│   │   ├── TipoCarroceria.java           (enum)
│   │   └── EstiloMoto.java               (enum)
│   └── excepciones/                  ← EXCEPCIONES PROPIAS
│       ├── DatoInvalidoException.java
│       ├── VehiculoDuplicadoException.java
│       ├── VehiculoNoEncontradoException.java
│       └── PersistenciaException.java
├── consigna/                         ← enunciado original de la cátedra
├── doc/                              ← JavaDoc generado (abrir doc/index.html)
├── uml/                              ← diagrama de clases (.dot, .png, .svg)
├── stock.csv                         ← persistencia (se crea y actualiza solo)
├── DOCUMENTACION-TECNICA.md          ← decisiones de diseño justificadas
├── INFORME-USO-IAG.md                ← informe de uso de IA (guía de cátedra)
├── GUIA-GIT-EQUIPO.md                ← cómo trabajar en equipo con Git
└── README.md
```

---

## 3. Funcionalidades

| Opción | Función |
|---|---|
| 1 | Alta de una unidad (auto, camioneta o moto) con validación completa |
| 2 | Listado del stock completo |
| 3 | Búsqueda por código, con precio de lista y valor actual depreciado |
| 4 | Búsqueda por marca (coincidencia parcial) |
| 5 | Listado por tipo de vehículo |
| 6 | Listado por estado (0 Km / usados) |
| 7 | Actualización del precio de lista |
| 8 | Actualización del kilometraje |
| 9 | Baja de una unidad, con confirmación |
| 10 | Reporte de valorización: composición del stock y valor total depreciado |
| 0 | Salir |

Cada alta, baja o modificación se guarda en el acto en `stock.csv`, de modo que los
datos sobreviven entre ejecuciones.

---

## 4. Requerimientos técnicos del enunciado

| # | Requerimiento | Dónde está implementado |
|---|---|---|
| 1 | Dominio y abstracción | `modelo/` — jerarquía `Vehiculo` |
| 2 | Arquitectura en capas | Paquetes `presentacion`, `negocio`, `datos` |
| 3 | Modelado POJO / JavaBean | `Vehiculo` y subclases: atributos privados + getters/setters |
| 4 | Herencia y polimorfismo | `Vehiculo` abstracta; `calcularValorActual()` |
| 5 | Interfaces de comportamiento | `Identificable`, `Depreciable`, `Repositorio<T>` |
| 6 | Manejo de excepciones | Paquete `excepciones` (4 excepciones propias) |
| 7 | Tipos enumerativos | `EstadoVehiculo`, `TipoCarroceria`, `EstiloMoto` |
| 8 | Interfaz de consola | `MenuConsola`, `LectorConsola` |
| 9 | Wrappers y conversiones | `Integer`, `Double`, `Boolean` en todo el modelo |
| 10 | Colecciones y sobreescritura | `Map` y `List`; `equals()`, `hashCode()`, `toString()` |
| 11 | Persistencia de datos | `RepositorioEnArchivo.cargar()` / `.guardar()` |
| 12 | Genéricos | `Repositorio<T extends Identificable>`; `leerOpcionEnum()` |
| 13 | Diagrama UML | `uml/diagrama-clases.png` |
| 14 | Documentación | JavaDoc en `doc/` + `DOCUMENTACION-TECNICA.md` |

---

## 5. Formato del archivo `stock.csv`

Un vehículo por línea, campos separados por `;`. Las líneas que comienzan con `#`
son comentarios.

```
AUTOMOVIL  ;codigo;marca;modelo;anio;color;estado;km;precio;carroceria;puertas
CAMIONETA  ;codigo;marca;modelo;anio;color;estado;km;precio;carroceria;cargaKg;4x4
MOTOCICLETA;codigo;marca;modelo;anio;color;estado;km;precio;estilo;cilindrada
```

Si una línea está corrupta, el programa la descarta, informa cuántas descartó y
continúa. Si el archivo no existe, el sistema inicia vacío y lo crea al guardar.

---

## 6. Nota sobre la codificación

El código fuente está escrito sin tildes ni eñes, de forma deliberada. La
codificación de los archivos `.java` (UTF-8) y la de la consola de Windows
(CP1252) no coinciden, y los textos acentuados se muestran alterados. Escribiendo
el código en ASCII la salida se ve idéntica en BlueJ, en VS Code y en consola.
La documentación en Markdown sí utiliza acentuación normal.

---

## 7. Regenerar la documentación

```bash
# JavaDoc
javadoc -encoding UTF-8 -charset UTF-8 -d doc -subpackages concesionaria

# Diagrama UML (requiere Graphviz)
dot -Tpng -Gdpi=110 uml/diagrama-clases.dot -o uml/diagrama-clases.png
```
