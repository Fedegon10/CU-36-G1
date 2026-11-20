# Diagramas de diseño – CU 36

| Carpeta | Archivo | Diagrama | En el documento |
|---|---|---|---|
| `1_clases/` | `Figura_1_clases_diseno_adapter` | Clases de diseño con el patrón Adapter | 2. Vista estática |
| `1_clases/` | `Comparacion_antes_sin_patron` | Estructura sin el patrón (InterfazGPSTracker con los tres servicios) | 1.4. Antes y después |
| `1_clases/` | `Comparacion_despues_con_adapter` | Estructura con el patrón Adapter (versión resumida de la Figura 1) | 1.4. Antes y después |
| `2_secuencia/` | `Figura_2_secuencia_patron_adapter` | Secuencia de la parte rediseñada por el patrón (19 mensajes) | 3. Vista dinámica |
| `2_secuencia/` | `Figura_3_secuencia_flujo_basico` | Secuencia del flujo básico completo (46 mensajes) | 3. Vista dinámica |
| `2_secuencia/` | `Comparacion_secuencia_antes_sin_patron` | Obtención de la ubicación sin el patrón (alt por modelo) | 3.4. Antes y después |
| `2_secuencia/` | `Comparacion_secuencia_despues_con_adapter` | Obtención de la ubicación con el patrón Adapter (resumen de la Figura 2) | 3.4. Antes y después |
| `3_maquinas_de_estado/` | `Figura_4_maquina_estados_bolsin` | Máquina de estados del Bolsín (corregida) | 5. Anexo |
| `3_maquinas_de_estado/` | `Figura_5_maquina_estados_remito` | Máquina de estados del Remito (corregida) | 5. Anexo |

**Diagramas de secuencia y código:** el código sigue el mismo orden que los diagramas, con los mismos nombres de métodos y los mismos objetos que reciben cada mensaje. En la Figura 3, los submensajes (7.1, 17.1, 19.1, 22.1 y 37.1) son las delegaciones internas de cada entidad, por ejemplo `Bolsin.getNombreCMDestino()` que le pide `getNombre()` a su CM destino. Los comentarios del código indican el paso del CU.

## Formatos
Cada diagrama está en tres formatos, con el mismo nombre:
- `.png`: imagen que va en el documento Word.
- `.svg`: imagen vectorial, para hacer zoom sin que se pixele.
- `.puml`: fuente PlantUML. **Si hay que cambiar un diagrama, se edita este archivo.**

## Cómo modificar un diagrama
1. Editar el `.puml`.
2. Pegar su contenido en https://www.plantuml.com/plantuml/uml/ y apretar **Submit**.
3. Descargar con los links **PNG** y **SVG** de abajo del diagrama y reemplazar los archivos con el mismo nombre.
4. En el documento Word: clic derecho sobre la imagen → **Cambiar imagen** → elegir el PNG nuevo.

No subir el `skinparam dpi` por encima de 120: el servidor de PlantUML corta las imágenes a 4096 px de ancho.
Si se cambia un diagrama de secuencia, hay que revisar que el código siga el mismo orden y los mismos nombres de métodos.
