# CU 36 – Consultar seguimiento de bolsines (implementación)

| Parte | Tecnología | Puerto |
|---|---|---|
| API GPS Tracker (de la cátedra, en `api-gps-tracker/`, con los bolsines 111 a 115 agregados) | Java 17 + Spring Boot (mock) | 8080 |
| Backend | Java 17 + Spring Boot 3 + Spring Data JPA (Hibernate) + H2 | 8081 |
| Frontend | React 18 + Vite + Material UI + Leaflet (OpenStreetMap) | 5173 |

## Cómo levantarlo (3 terminales, en este orden)

```bash
# 1) API mock del GPS Tracker
cd implementacion/api-gps-tracker
./mvnw spring-boot:run          # Windows: .\mvnw.cmd spring-boot:run

# 2) Backend
cd implementacion/backend
./mvnw spring-boot:run          # Windows: .\mvnw.cmd spring-boot:run

# 3) Frontend
cd implementacion/frontend
npm install                      # solo la primera vez
npm run dev
```

Abrir **http://localhost:5173**.

- La base de datos es H2 **en archivo** (`implementacion/backend/data/`). Los datos se cargan la primera vez y persisten entre reinicios.
- Para volver a los datos iniciales: detener el backend y borrar la carpeta `implementacion/backend/data/`.
- Consola de la BD: http://localhost:8081/h2-console (JDBC URL `jdbc:h2:file:./data/bolsinesdb;AUTO_SERVER=TRUE`, usuario `sa`, sin contraseña).
- El mapa usa OpenStreetMap: necesita internet.

## Usuarios de prueba (contraseña `1234`)

| Usuario | CM | Para mostrar |
|---|---|---|
| `eb.cordoba` | Comisión Médica Córdoba | Flujo básico, A2, A3, A4, A5 |
| `eb.jujuy` | Comisión Médica Jujuy | A1 (no hay bolsines enviados) |
| `eb.rosario` | Comisión Médica Rosario | Otra CM con un solo bolsín |

## Guion para la defensa

| Flujo | Cómo mostrarlo |
|---|---|
| **Básico** | `eb.cordoba` → *Consultar seguimiento de bolsines* → clic en un bolsín del mapa → *Informar ubicación al Gerente* → *Enviar correo*. El mail aparece en pantalla y en la consola del backend. |
| **A1** – No hay bolsines enviados | Ingresar con `eb.jujuy` y abrir la opción. |
| **A2** – No existe el precinto | Filtrar por un precinto inexistente (ej. `99999`). |
| **A3** – El GPS no informa ubicación | Aparece solo: el bolsín **116** no existe en la API mock y se muestra en el aviso amarillo. Si se apaga la API, todos los bolsines caen en A3. |
| **A4** – Cancelar el CU | Botón *Cancelar*. |
| **A5** – No envía el correo | En la pregunta, elegir *No enviar*. |
| **Adapter** | Bolsines 101 y 113 → XTR-4500L (JSON) · 111 y 114 → NavTrack QX-7A (texto con comas) · 112 y 115 → GeoPulse MTR-900 (matriz). |

## Dónde está cada cosa (backend)

`backend/src/main/java/ar/edu/utn/dsi/bolsines/`

| Carpeta | Contenido | Estereotipo |
|---|---|---|
| `boundary/pantallas/` | `PantallaConsultarSeguimientoBolsines`, `PantallaIniciarSesion` (endpoints REST) | «boundary» |
| `boundary/interfaces/` | `InterfazGPSTracker` (cliente del Adapter), `InterfazServidorCorreo` (mail simulado) | «boundary» |
| `control/` | `GestorConsultarSeguimientoBolsines`, `GestorNotificarUbicacionBolsin`, `GestorSesion` | «control» |
| `entidades/` | `Bolsin`, `CambioEstadoBolsin`, `Estado`, `ComisionMedica`, `Empleado`, `Rol`, `Usuario`, `Sesion` | «entity» |
| `patrones/adapter/` | `IAdaptadorGPSTracker` (target), los 3 `AdaptadorGPSTracker...`, `UbicacionBolsin` | **Patrón Adapter** |
| `patrones/adapter/serviciosexternos/` | `ServicioGPSTracker...` (llaman a la API de cada modelo) | Adaptee |
| `repositorios/` | Acceso a la base de datos (Spring Data JPA) | – |
| `dto/` | Datos que se envían a la pantalla | – |
| `excepciones/` | `FlujoAlternativoException`, `GPSTrackerException` | – |
| `configuracion/` | `CargaDatosIniciales` (datos de prueba), `ManejadorErrores` | – |

## Dónde está cada cosa (frontend)

`frontend/src/`

| Carpeta | Contenido |
|---|---|
| `pantallas/` | `PantallaIniciarSesion`, `PantallaMenuPrincipal`, `PantallaConsultarSeguimientoBolsines` |
| `componentes/` | `MapaBolsines`, `PanelFiltros`, `ListaBolsines`, `AvisoBolsinesSinUbicacion`, `DialogoConfirmarEnvioMail`, `DialogoMailEnviado`, `BarraSuperior` |
| `servicios/` | `apiBolsines.js`: todas las llamadas al backend |
| `utilidades/` | `formatos.js`: formato de fechas |
