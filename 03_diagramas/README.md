# Diagramas de diseño – CU 36

| N° | Diagrama | Dónde va en el documento |
|---|---|---|
| 01 | Clases de diseño con el patrón Adapter | 2. Vista estática (Figura 1) |
| 02 | Secuencia de la parte rediseñada por el patrón (modelo XTR-4500L) | 3. Vista dinámica (Figura 2) |
| 03 | Secuencia del flujo básico completo, con correcciones de la E1 | 3. Vista dinámica (Figura 3) |
| 04 | Máquina de estados del Bolsín (corregida) | 6. Anexo (Figura 4) |
| 05 | Máquina de estados del Remito (corregida) | 6. Anexo (Figura 5) |

## Carpetas
- `fuentes_plantuml/`: archivos `.puml`. **Si hay que cambiar un diagrama, se edita acá.**
- `png/`: imágenes que van en el documento Word.
- `svg/`: las mismas imágenes en formato vectorial, para hacer zoom sin que se pixelen.

## Cómo modificar un diagrama
1. Editar el `.puml` en `fuentes_plantuml/`.
2. Pegar su contenido en https://www.plantuml.com/plantuml/uml/ y apretar **Submit**.
3. Descargar con los links **PNG** y **SVG** de abajo del diagrama y reemplazar los archivos en `png/` y `svg/`.
4. En el documento Word: clic derecho sobre la imagen → **Cambiar imagen** → elegir el PNG nuevo.

No subir el `skinparam dpi` por encima de 120: el servidor de PlantUML corta las imágenes a 4096 px de ancho.
