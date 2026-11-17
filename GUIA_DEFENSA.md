# Guía del grupo – PPAI Entrega 2 (CU 36)

Grupo 1 · 3K4 · Bolsines · **CU 36 – Consultar seguimiento de bolsines**

Esta guía cubre qué hicimos, cómo levantar el sistema, cómo explicar el patrón y qué mostrar en la defensa oral.

---

## Índice
1. [Resumen en un minuto](#1-resumen-en-un-minuto)
2. [Qué hay en el repositorio](#2-qué-hay-en-el-repositorio)
3. [Cómo levantar todo](#3-cómo-levantar-todo)
4. [Guion de la demo](#4-guion-de-la-demo)
5. [El patrón Adapter, explicado](#5-el-patrón-adapter-explicado)
6. [Recorrido del código para la defensa](#6-recorrido-del-código-para-la-defensa)
7. [Persistencia (base de datos)](#7-persistencia-base-de-datos)
8. [Correcciones de la Entrega 1 que aplicamos](#8-correcciones-de-la-entrega-1-que-aplicamos)
9. [Preguntas probables y cómo responderlas](#9-preguntas-probables-y-cómo-responderlas)
10. [Checklist antes de entregar y de la defensa](#10-checklist-antes-de-entregar-y-de-la-defensa)
11. [Glosario rápido](#11-glosario-rápido)

---

## 1. Resumen en un minuto

**El caso de uso.** El Encargado de Bolsines (EB) de una Comisión Médica (CM) quiere ver en un mapa dónde están los bolsines que envió y que todavía están viajando (estado **Enviado**). Puede filtrarlos y elegir uno. Si quiere, el sistema le manda un mail al **Gerente de la CM destino** con la ubicación de ese bolsín.

**El problema de diseño.** Cada bolsín lleva un **GPS Tracker**, y hay **3 modelos distintos**. Cada uno se consulta diferente y responde en otro formato:

| Modelo | Qué pide | Qué devuelve |
|---|---|---|
| XTR-4500L | API Key + nº bolsín + código CM **origen** | Un **JSON** |
| NavTrack QX-7A | API Key + nº bolsín + código CM **destino** | Un **texto separado por comas** |
| GeoPulse MTR-900 | API Key + nº bolsín | Una **matriz** |

**La solución.** Aplicamos el patrón **Adapter**. Hay un "adaptador" por modelo que traduce la respuesta a un formato único (`UbicacionBolsin`). El resto del sistema no sabe ni le importa qué modelo de GPS tiene cada bolsín.

**Qué entregamos.**
- El documento de la Entrega 2 en **Word (.docx)**, editable: `docs/entrega2/PPAI2026_3K4_G1_E2_Diseño.docx`.
- El **sistema funcionando**: web, con base de datos, contra la API real de la cátedra. Andan el flujo básico y los **5 flujos alternativos**; la consigna pide al menos 2.

---

## 2. Qué hay en el repositorio

```
CU-36-G1/
├── GUIA_DEFENSA.md              ← esta guía
├── implementacion/              ← EL SISTEMA (código)
│   ├── README.md                ← resumen técnico de cómo correrlo
│   ├── api-gps-tracker/         ← API del GPS Tracker de la cátedra (con 5 bolsines agregados)
│   ├── backend/                 ← Java + Spring Boot (la lógica y la base de datos)
│   └── frontend/                ← React (las pantallas que ve el usuario)
├── docs/
│   ├── diagramas/               ← diagramas en PlantUML (.puml): clases, secuencias y máquinas de estado
│   └── entrega2/
│       ├── PPAI2026_3K4_G1_E2_Diseño.docx  ← EL DOCUMENTO DE LA ENTREGA (Word, editable)
│       ├── entrega 2_modelo_formato.docx   ← modelo de formato del grupo (referencia)
│       └── imagenes/                       ← diagramas (SVG) y capturas del sistema
└── PPAI/                        ← material de la cátedra
    ├── Enunciado/                    ← consignas, ERS y dominio
    ├── Entrega 1_G1/                 ← entrega 1 y su revisión
    ├── Recursos de Ayuda/            ← PDFs de ayuda de la cátedra
    └── diagramas_plantuUML/          ← diagramas exportados (PNG/SVG)
```

**Atención:** a la API de la cátedra le **agregamos 5 bolsines** (111 a 115), para que el mapa muestre varios. El día de la defensa hay que levantar **nuestra copia** (`implementacion/api-gps-tracker/`), no una bajada nueva del repo de la cátedra. El cambio está en `src/main/java/ar/edu/dsi/gpstracker/repository/InMemoryBolsinRepository.java`.

---

## 3. Cómo levantar todo

### Requisitos (una sola vez)
- **Java 17 o superior**. Para verificar: `java -version`.
- **Node.js 18 o superior**. Para verificar: `node -v`. Se baja de https://nodejs.org.
- **Internet**: lo usa el mapa (OpenStreetMap). La primera vez también descarga Maven y las librerías.
- No hace falta instalar Maven: el proyecto trae `mvnw`, que lo descarga solo.

### Arrancar (3 terminales, en este orden)

Copiar **solo las líneas de comandos** de cada bloque, sin agregarles nada. Todos los comandos se ejecutan desde la carpeta `CU-36-G1`.

**Terminal 1 – API del GPS Tracker (puerto 8080)**

Mac / Linux:
```bash
cd implementacion/api-gps-tracker
./mvnw spring-boot:run
```
Windows (PowerShell):
```powershell
cd implementacion\api-gps-tracker
.\mvnw.cmd spring-boot:run
```

**Terminal 2 – Backend (puerto 8081)**

Mac / Linux:
```bash
cd implementacion/backend
./mvnw spring-boot:run
```
Windows (PowerShell):
```powershell
cd implementacion\backend
.\mvnw.cmd spring-boot:run
```

**Terminal 3 – Frontend (puerto 5173)**

`npm install` va solo la primera vez. Es igual en Mac y en Windows; en Windows la carpeta se escribe `implementacion\frontend`.
```bash
cd implementacion/frontend
npm install
npm run dev
```

Cada servidor queda corriendo en su terminal: no hay que cerrarla. Para detenerlo, `Ctrl + C` en esa terminal.

Abrir en el navegador: **http://localhost:5173**

### Usuarios de prueba (contraseña `1234`)
En la pantalla de login hay botones que completan el usuario solos.

| Usuario | CM | Para qué sirve |
|---|---|---|
| `eb.cordoba` | Córdoba | Flujo básico y A2, A3, A4, A5 (tiene 6 bolsines en el mapa + 1 sin ubicación) |
| `eb.jujuy` | Jujuy | A1 (no tiene bolsines enviados) |
| `eb.rosario` | Rosario | Otra CM, con un solo bolsín |

### Problemas comunes
| Problema | Solución |
|---|---|
| "Port 8080/8081 already in use" | Ya hay algo corriendo en ese puerto. Cerrar la otra terminal o reiniciar la compu. En Mac: `lsof -ti tcp:8081 -sTCP:LISTEN \| xargs kill` (cambiar 8081 por el puerto) |
| Todos los bolsines aparecen "sin ubicación" | La API del GPS (terminal 1) no está corriendo |
| El mapa se ve gris | No hay internet |
| La pantalla dice "No se pudo conectar con el backend" | Falta levantar la terminal 2 |
| Quiero volver a los datos iniciales | Detener el backend y borrar la carpeta `implementacion/backend/data/`. Se recrea sola al iniciar |
| En Windows, `.\mvnw.cmd` no funciona | Abrir la terminal en esa carpeta. Si PowerShell bloquea scripts: `Set-ExecutionPolicy RemoteSigned` |

---

## 4. Guion de la demo

Duración aproximada: 5 minutos. Conviene que lo haga una persona y otra vaya explicando.

### Flujo básico
1. Entrar con **`eb.cordoba`**. Arriba a la derecha se ve el usuario, su rol y su CM.
2. Elegir **"Consultar seguimiento de bolsines"** (paso 1 del CU).
3. Mostrar la pantalla (pasos 2 a 6):
   - Arriba dice "Bolsines enviados desde Comisión Médica Córdoba": es **la CM del usuario logueado** (paso 2).
   - En el mapa aparecen los bolsines en estado **Enviado** de esa CM (pasos 3 a 6).
   - **Lo importante del patrón:** decir que los bolsines usan GPS de **modelos distintos** y que cada uno respondió en otro formato:
     - **101** y **113** → XTR-4500L (JSON)
     - **111** y **114** → NavTrack QX-7A (texto con comas)
     - **112** y **115** → GeoPulse MTR-900 (matriz)

     Cada tarjeta de la lista muestra el modelo.
4. Hacer clic en un bolsín del mapa (paso 7). Se ve el detalle: destino, precinto, GPS, coordenadas y última actualización.
5. Clic en **"Informar ubicación al Gerente"**. El sistema pregunta si enviar el mail (paso 8).
6. **"Enviar correo"** (paso 9). El sistema busca al Gerente de la CM destino (paso 10) y envía el mail con número de bolsín, fecha y hora y coordenadas (paso 11, CU incluido *Notificar ubicación de bolsín*).
   - El mail es **simulado**: se ve en pantalla y también en la **consola del backend** (terminal 2), por si lo piden.

### Flujos alternativos

| Flujo | Cómo mostrarlo |
|---|---|
| **A1** – No hay bolsines enviados | Salir, entrar con **`eb.jujuy`** y abrir la opción. Aparece "No hay bolsines en tránsito" |
| **A2** – No existe el precinto | En "Filtrar bolsines", escribir **99999** → Buscar. Aparece un aviso. (Con un precinto real, por ejemplo **50113**, filtra) |
| **A3** – El GPS no pudo informar la ubicación | Ya aparece solo en el aviso amarillo: el **bolsín 116** no existe en la API del GPS. Si apagan la API (terminal 1) y tocan **Actualizar**, todos caen en A3 |
| **A4** – El EB cancela | Botón **Cancelar** arriba a la derecha |
| **A5** – No quiere enviar el mail | En la pregunta del mail, elegir **"No enviar"** |

### Si piden ver la base de datos
Abrir **http://localhost:8081/h2-console** con estos datos:
- JDBC URL: `jdbc:h2:file:./data/bolsinesdb;AUTO_SERVER=TRUE`
- User: `sa`
- Password: vacía

Ahí se ven las tablas `BOLSIN`, `CAMBIO_ESTADO_BOLSIN`, `ESTADO`, `EMPLEADO`, etc. Por ejemplo: `SELECT * FROM BOLSIN`.

---

## 5. El patrón Adapter, explicado

### La idea con un ejemplo cotidiano
Tenés un cargador con enchufe de tres patas y el toma tiene dos agujeros: usás un **adaptador**. Ni el cargador ni el toma cambian; el adaptador hace que encajen.

En nuestro sistema pasa lo mismo:
- El **sistema** quiere siempre lo mismo: nº de bolsín, latitud, longitud y fecha/hora.
- Cada **GPS** "tiene otro enchufe": pide parámetros distintos y responde en otro formato.
- Cada **adaptador** traduce un GPS al formato que espera el sistema.

### Las piezas del patrón (nombres que hay que saber)

| Rol en el patrón | Nuestra clase | Qué hace |
|---|---|---|
| **Cliente** | `InterfazGPSTracker` | Pide la ubicación. Solo conoce la interfaz común; no sabe qué GPS hay del otro lado |
| **Target** (interfaz común) | `IAdaptadorGPSTracker` | Define **un solo método**: `obtenerUbicacion(...)`, que devuelve `UbicacionBolsin` |
| **Adapter** (adaptadores) | `AdaptadorGPSTrackerXTR4500L`, `AdaptadorGPSTrackerNavTrackQX7A`, `AdaptadorGPSTrackerGeoPulseMTR900` | Cada uno habla con **su** GPS y traduce la respuesta |
| **Adaptee** (lo que se adapta) | `ServicioGPSTrackerXTR4500L`, `ServicioGPSTrackerNavTrackQX7A`, `ServicioGPSTrackerGeoPulseMTR900` | Llaman a la API de cada GPS **tal como es**, con sus métodos originales (`getBolsinLocation`, `retrieveTrackingData`, `fetchCargoPositions`) |
| **Resultado común** | `UbicacionBolsin` | El formato único: número, latitud, longitud y fecha/hora |

### Paso a paso (lo que muestra el diagrama de secuencia)
1. El **Gestor** junta los bolsines en estado Enviado de la CM del usuario.
2. Para cada bolsín, le pide a `InterfazGPSTracker` su ubicación y le pasa también el **modelo de GPS** del bolsín.
3. `InterfazGPSTracker` crea el adaptador que corresponde al modelo (`crearAdaptador()`).
4. Llama a `adaptador.obtenerUbicacion(...)`, que es **el mismo método para los tres**.
5. El adaptador hace dos cosas:
   - `solicitarUbicacion()`: llama al servicio del GPS y recibe el JSON, el texto o la matriz.
   - `adaptarUbicacion()`: lo convierte en `UbicacionBolsin`.
6. El Gestor recibe siempre un `UbicacionBolsin` y arma el mapa.
7. Si el GPS falla, el adaptador avisa con un error (`GPSTrackerException`) y ese bolsín aparece como **"sin ubicación"**. Eso es el flujo **A3**.

### Por qué Adapter (para justificar)
- Sin el patrón, el Gestor tendría `if (modelo == ...)` y conocería los 3 formatos: queda acoplado a sistemas externos.
- Con el patrón, **agregar un GPS nuevo = agregar un adaptador nuevo**, sin tocar el Gestor ni la pantalla (principio abierto/cerrado).
- Cada adaptador tiene **una sola responsabilidad**: hablar con un modelo y traducirlo.
- Es el mismo problema que el ejemplo de la cátedra (monitores **AGCO, Case IH y Topcon**), que también se resolvió con Adapter.

### Dónde está en el código
`implementacion/backend/src/main/java/ar/edu/utn/dsi/bolsines/`

| Archivo | Rol |
|---|---|
| `boundary/interfaces/InterfazGPSTracker.java` | Cliente: `solicitarUbicacionBolsin()` y `crearAdaptador()` |
| `patrones/adapter/IAdaptadorGPSTracker.java` | Target |
| `patrones/adapter/AdaptadorGPSTracker*.java` | Los 3 adaptadores |
| `patrones/adapter/UbicacionBolsin.java` | Resultado común |
| `patrones/adapter/serviciosexternos/ServicioGPSTracker*.java` | Adaptees |

Todos los archivos del patrón tienen al principio un comentario `// PATRÓN ADAPTER - <rol>`. Para encontrarlos rápido en VS Code: `Ctrl/Cmd + Shift + F` → buscar `PATRÓN ADAPTER`.

---

## 6. Recorrido del código para la defensa

Orden recomendado para mostrar el código (sigue el diagrama de secuencia):

1. **`boundary/pantallas/PantallaConsultarSeguimientoBolsines.java`**: la «boundary». Cada método es una acción del EB, con el número de paso del CU en un comentario.
2. **`control/GestorConsultarSeguimientoBolsines.java`**: el «control». Está dividido por pasos del CU:
   - `buscarCM()`: Sesión → Usuario → Empleado → CM.
   - `buscarBolsinesEnviados()`: le pregunta **a cada Bolsín** si está Enviado (`esEnviado()`) y si es de mi CM (`esTuCMOrigen()`). Es el patrón **Experto**: el bolsín responde sobre sí mismo.
   - `obtenerUbicacionBolsines()`: **acá se usa el Adapter**, a través de `InterfazGPSTracker`.
   - `buscarMailDeGerenteCMDestino()`: loop sobre empleados con `sosGCMDeCM()`.
3. **`entidades/Bolsin.java`**: `esEnviado()` recorre los cambios de estado, busca el **actual** y le pregunta si es Enviado.
4. **`boundary/interfaces/InterfazGPSTracker.java`**: `crearAdaptador()` según el modelo.
5. **`patrones/adapter/AdaptadorGPSTrackerXTR4500L.java`**: `solicitarUbicacion()` y `adaptarUbicacion()`. Compararlo con el de NavTrack para mostrar que hacen lo mismo con formatos distintos.

### Cómo se corresponde el diagrama con el código

| En el diagrama | En el código |
|---|---|
| «boundary» Pantalla | `boundary/pantallas/` (endpoints) + `frontend/src/pantallas/` (lo que se ve) |
| «control» Gestor | `control/` |
| «entity» | `entidades/` |
| Boundary con el GPS | `boundary/interfaces/InterfazGPSTracker.java` |
| Patrón | `patrones/adapter/` |

**Diferencia con el diagrama de análisis (por si preguntan).** Como es una API REST, los mensajes del Gestor a la Pantalla (por ejemplo `mostrarMapaDeBolsines()`) se resuelven con lo que **devuelve** cada método. La pantalla nunca recibe entidades: recibe **DTO**, objetos solo con los datos a mostrar. La implementación de ejemplo de la cátedra lo hace igual.

---

## 7. Persistencia (base de datos)

- **Base de datos:** H2, guardada en archivo (`implementacion/backend/data/`). Los datos **persisten** entre reinicios.
- **Framework de persistencia:** JPA con Hibernate (Spring Data JPA).
  - Las clases de `entidades/` tienen `@Entity`: cada una es una tabla.
  - Las relaciones (`@ManyToOne`, `@OneToMany`) son las asociaciones del diagrama de clases.
  - `repositorios/` tiene una interfaz por entidad con `findAll()`, `save()`, etc. (no hay SQL escrito a mano).
- **Datos de prueba:** `configuracion/CargaDatosIniciales.java` los carga la primera vez: CMs, empleados, estados y bolsines.

---

## 8. Correcciones de la Entrega 1 que aplicamos

| Observación de la profe | Qué cambiamos |
|---|---|
| ¿Qué info muestra `mostrarComisionMedicaEmp()`? ¿De dónde la saca? | Se obtiene `Empleado.getCM()` y después `ComisionMedica.getNombre()` |
| "No necesita este puntero" (Gestor → Estado) | El Gestor ya no le habla a Estado: el Bolsín lo resuelve por su CambioEstado actual |
| "No utiliza CE actual" | `Bolsin.esEnviado()` busca el cambio de estado **actual** (`esActual()`) y le pregunta `esEnviado()` |
| `buscarBolsinEnEstadoEnviado()` "poco coherente" | Ahora es `Bolsin.esEnviado()` |
| "No busca CM destino ni nro de bolsín" | Se obtienen número, precinto, CM destino y modelo de GPS de cada bolsín |
| "?" antes de `getBolsinLocation()` y `getMapaVisual()` | Se agregaron `obtenerUbicacionBolsines()` y `armarMapaDePosicionDeBolsines()` |
| `sosGCM()`: "Lo puede resolver todo" / "Loop?" | Loop sobre empleados con `sosGCMDeCM(cm)`, que consulta a su Rol y a su CM |
| `seleccionado: Bolsin` "?" | Se eliminó esa línea de vida |
| CU incluido equivocado (decía Registrar Revisión) | Ahora es **Notificar ubicación de bolsín** |
| Asociación "estado" entre CambioEstadoBolsin y Empleado | Renombrada **responsable** |
| "Escalar las imágenes para mayor resolución" | Diagramas en SVG (vectorial) |
| Máquinas de estado: "Evento / método()" | Redibujadas (`docs/diagramas/04_...` y `05_...`, anexo del documento) con la notación *Evento / método()*. En Remito: guardas [toda / no toda la documentación es aceptada], sin la transición "no corresponde a CM destino" y con retorno a Creado por el CU 21 |

---

## 9. Preguntas probables y cómo responderlas

**¿Por qué Adapter y no Strategy?**
Strategy sirve para elegir entre **algoritmos propios** intercambiables. Acá el problema es que tenemos **servicios externos con interfaces incompatibles** que no podemos cambiar, y necesitamos que encajen en la interfaz que espera nuestro sistema. Eso es exactamente lo que resuelve Adapter.

**¿Qué pasa si aparece un cuarto modelo de GPS?**
Se crea `AdaptadorGPSTrackerNuevo` (y su servicio) implementando `IAdaptadorGPSTracker`, y se agrega una línea en `crearAdaptador()`. El Gestor y la pantalla no cambian.

**¿Cómo sabe el sistema qué modelo tiene cada bolsín?**
El `Bolsin` tiene el atributo `modeloGPSTracker`. `InterfazGPSTracker` lo usa para crear el adaptador correcto.

**¿Por qué los tres adaptadores reciben CM origen y CM destino si algunos no las usan?**
Porque la interfaz común tiene que servir para todos. Cada adaptador usa lo que su GPS necesita: XTR usa origen, NavTrack usa destino y GeoPulse ninguna.

**¿De dónde salen las API Keys?**
Están en `implementacion/backend/src/main/resources/application.properties`. La interfaz se las pasa a cada adaptador cuando lo crea, igual que en el ejemplo de la cátedra.

**¿Qué pasa si el GPS no responde?**
El adaptador lanza `GPSTrackerException`, el Gestor la atrapa y ese bolsín se muestra como "sin ubicación". Es el flujo **A3**, y se puede mostrar en vivo.

**¿Por qué no usaron Google Maps?**
Google Maps necesita una API key con facturación. Usamos **Leaflet + OpenStreetMap**, que cumple la misma función (mostrar coordenadas en un mapa) sin depender de una cuenta paga.

**¿El mail se envía de verdad?**
No: está **simulado** en `InterfazServidorCorreo`. Se registra en la consola del backend y se muestra en pantalla. La ERS pide Microsoft Graph, que excede el alcance del práctico.

**¿Dónde se aplica el patrón Experto (GRASP)?**
Cada objeto responde sobre sus propios datos: `Bolsin.esEnviado()`, `Bolsin.esTuCMOrigen()`, `Empleado.sosGCMDeCM()`, `ComisionMedica.esMiCM()`. El Gestor coordina, pero no decide por ellos.

**¿Por qué Java/Spring y React?**
Java es uno de los lenguajes que soporta la cátedra. Es el mismo stack del ejemplo de la cátedra y de la API del GPS. La ERS pide tecnología web.

---

## 10. Checklist antes de entregar y de la defensa

### Antes de entregar
- [x] Patrón **Adapter** confirmado con la profe (Ing. Judith Meles).
- [x] Sin apartado de Diseño de experiencia de usuario: según el aviso del equipo docente, no se pide en la Entrega 2.
- [ ] Revisar el documento en Word (carátula, fecha 20/11/2026, docentes e integrantes).
- [ ] Exportarlo a PDF desde Word: **Archivo → Guardar como → PDF**. La consigna dice que PDF es el único formato permitido.
- [ ] Subir **un solo integrante**, con el nombre `PPAI2026_3K4_G1_E2_Diseño.pdf`.

### Cómo cambiar algo del documento
- **Textos** (fecha, integrantes, explicaciones): editar directamente `docs/entrega2/PPAI2026_3K4_G1_E2_Diseño.docx` en Word.
- **Diagramas**: editar los `.puml` de `docs/diagramas/`, exportarlos desde https://www.plantuml.com/plantuml/uml/ (PNG o SVG) y reemplazar la imagen en Word (clic derecho → Cambiar imagen).

### El día de la defensa
- [ ] Levantar las 3 terminales **antes** de entrar y probar el flujo una vez.
- [ ] Tener internet (mapa).
- [ ] Tener abiertos en VS Code: `GestorConsultarSeguimientoBolsines.java`, `InterfazGPSTracker.java`, `IAdaptadorGPSTracker.java` y `AdaptadorGPSTrackerXTR4500L.java`.
- [ ] Tener a mano los diagramas (`docs/entrega2/imagenes/diagramas/`, en SVG).
- [ ] Repartir quién explica qué: demo, patrón y código.

---

## 11. Glosario rápido

| Término | Significado |
|---|---|
| **Backend** | La parte que corre en el servidor: lógica del CU y base de datos (Java) |
| **Frontend** | La parte visual que se ve en el navegador (React) |
| **API REST** | Forma en que el frontend le pide cosas al backend por HTTP (por ejemplo `POST /api/seguimiento-bolsines/iniciar`) |
| **Endpoint** | Una dirección concreta de la API. Cada método de la Pantalla es un endpoint |
| **DTO** | Objeto que solo lleva datos para mostrar, sin lógica. Se usa para no mandar entidades a la pantalla |
| **JPA / Hibernate** | Herramientas que guardan los objetos Java en la base de datos sin escribir SQL |
| **Spring Boot** | Framework de Java que arma el servidor web y conecta todo |
| **H2** | Base de datos liviana que se guarda en un archivo dentro del proyecto |
| **API Key** | Clave que identifica a quien llama a la API del GPS. Cada modelo tiene la suya |
| **Mock** | Versión simulada de un sistema real. La API del GPS de la cátedra es un mock |
