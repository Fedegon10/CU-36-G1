# PPAI 2026 · Bolsines · CU 36 Consultar seguimiento de bolsines

**Diseño de Sistemas de Información** · UTN FRC · Curso **3K4** · **Grupo 1**
Docente tutora: Ing. Judith Meles · Patrón aplicado: **Adapter**

---

## 📌 Entrega 2: todo está en [`1_ENTREGA_2/`](1_ENTREGA_2/)

| Orden para mostrar | Carpeta | Qué hay |
|---|---|---|
| **1. Documento** | [`1_ENTREGA_2/1_documento_UV/`](1_ENTREGA_2/1_documento_UV/) | `PPAI2026_3K4_G1_E2_Diseño.docx`: el documento completo. Para el aula virtual se exporta a PDF desde Word (Archivo → Guardar como → PDF) y lo sube **un solo integrante** |
| **2. Diagramas** | [`1_ENTREGA_2/2_diagramas/`](1_ENTREGA_2/2_diagramas/) | `1_clases/` (Figura 1) · **`2_secuencia/`** (Figuras 2 y 3: las que se comparan con el código) · `3_maquinas_de_estado/` (Figuras 4 y 5) |
| **3. Implementación** | [`1_ENTREGA_2/3_implementacion/`](1_ENTREGA_2/3_implementacion/) | `api-gps-tracker/` (API de la cátedra) · `backend/` (Java: lógica, patrón y base de datos) · `frontend/` (pantallas) |

> En el código, cada mensaje de los diagramas de secuencia tiene un comentario con su número. Por ejemplo, `// [Fig. 3 · 12]` es el mensaje 12 de `Figura_3_secuencia_flujo_basico.png`.
> Por aviso del equipo docente, en la Entrega 2 **no** se pide el apartado de Diseño de experiencia de usuario.

---

## 🗂️ Estructura del repositorio

```
CU-36-G1/
├── README.md                              ← este archivo (empezar acá)
│
├── 1_ENTREGA_2/                           ← TODO LO DE LA ENTREGA 2
│   ├── 1_documento_UV/
│   │   ├── PPAI2026_3K4_G1_E2_Diseño.docx   documento de la entrega (se edita acá)
│   │   └── recursos_del_documento/          capturas usadas en el documento y modelo de formato
│   ├── 2_diagramas/
│   │   ├── 1_clases/                        Figura 1: clases de diseño (patrón Adapter)
│   │   ├── 2_secuencia/                     Figura 2: secuencia del patrón Adapter
│   │   │                                    Figura 3: secuencia del flujo básico completo
│   │   └── 3_maquinas_de_estado/            Figuras 4 y 5: Bolsín y Remito
│   └── 3_implementacion/
│       ├── README.md                        cómo levantarlo y dónde está cada clase
│       ├── api-gps-tracker/                 API del GPS Tracker de la cátedra (con bolsines agregados)
│       ├── backend/                         Java + Spring Boot + JPA/H2
│       └── frontend/                        React
│
├── 2_GUIAS_DEL_GRUPO/                     ← material interno del grupo (no se entrega)
│   ├── GUIA_DEFENSA.md                      cómo levantar, guion de la demo, patrón, mensaje por mensaje y preguntas
│   └── GUIA_PERSONAL_CU36.pdf               CU de la cátedra + diagramas + cada paso con su captura y su código
│
├── 3_ENTREGA_1/                           ← entrega anterior y su revisión con correcciones
└── 4_MATERIAL_CATEDRA/                    ← enunciado, ejemplo resuelto de la cátedra y enlaces
```

Cada diagrama está en tres formatos: `.png` (va en el documento), `.svg` (vectorial, para hacer zoom) y `.puml` (fuente editable).

---

## 🚀 Levantar el sistema (resumen)

Requisitos: Java 17 o superior y Node.js 18 o superior. Desde la carpeta `CU-36-G1`, una terminal por cada parte:

```bash
cd 1_ENTREGA_2/3_implementacion/api-gps-tracker
./mvnw spring-boot:run
```
```bash
cd 1_ENTREGA_2/3_implementacion/backend
./mvnw spring-boot:run
```
```bash
cd 1_ENTREGA_2/3_implementacion/frontend
npm install
npm run dev
```

Abrir **http://localhost:5173** y entrar con el usuario `eb.cordoba` (contraseña `1234`).
Los comandos para Windows y el guion de la demo están en [`2_GUIAS_DEL_GRUPO/GUIA_DEFENSA.md`](2_GUIAS_DEL_GRUPO/GUIA_DEFENSA.md).

---

## 👥 Integrantes

| Apellido y nombre | Legajo |
|---|---|
| Baudo, Ramiro | 78737 |
| Esquivel, Ignacio | 408359 |
| Frances, David | 80370 |
| Gon, Federico | 94470 |
| Vasquez, Facundo Santiago | 403818 |
