# Diagramas de diseño – CU 36

| Carpeta | Archivo | Diagrama | En el documento |
|---|---|---|---|
| `1_clases/` | `Figura_1_clases_diseno_adapter` | Clases de diseño con el patrón Adapter | 2. Vista estática |
| `2_secuencia/` | `Figura_2_secuencia_patron_adapter` | Secuencia de la parte rediseñada por el patrón (19 mensajes) | 3. Vista dinámica |
| `2_secuencia/` | `Figura_3_secuencia_flujo_basico` | Secuencia del flujo básico completo (46 mensajes) | 3. Vista dinámica |
| `3_maquinas_de_estado/` | `Figura_4_maquina_estados_bolsin` | Máquina de estados del Bolsín (corregida) | 6. Anexo |
| `3_maquinas_de_estado/` | `Figura_5_maquina_estados_remito` | Máquina de estados del Remito (corregida) | 6. Anexo |

**Diagramas de secuencia y código:** cada mensaje de las Figuras 2 y 3 tiene un número, y en el código hay un comentario con ese número en la línea que lo envía o lo recibe. Por ejemplo, `// [Fig. 3 · 12]` es el mensaje 12 de `Figura_3_secuencia_flujo_basico`. El código sigue el mismo orden que los diagramas.

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
Si se cambia un diagrama de secuencia, hay que revisar que los números de los comentarios `[Fig. N · M]` del código sigan coincidiendo.
