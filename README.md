# PPAI 2026 · Bolsines · CU 36 Consultar seguimiento de bolsines

**Diseño de Sistemas de Información** · UTN FRC · Curso **3K4** · **Grupo 1**
Docente tutora: Ing. Judith Meles · Patrón aplicado: **Adapter**

---

## 📌 Qué se entrega (Entrega 2)

| Qué | Dónde | Estado |
|---|---|---|
| **Documento de la Entrega 2** (Word, editable) | [`01_entrega_2/PPAI2026_3K4_G1_E2_Diseño.docx`](01_entrega_2/) | Listo para revisar |
| PDF para subir al aula virtual | Se exporta desde Word: **Archivo → Guardar como → PDF** con el nombre `PPAI2026_3K4_G1_E2_Diseño.pdf` | Pendiente (lo sube **un solo integrante**) |
| Sistema funcionando (se muestra en la defensa oral) | [`02_implementacion/`](02_implementacion/) | Listo |

> Por aviso del equipo docente, en la Entrega 2 **no** se pide el apartado de Diseño de experiencia de usuario.

---

## 🗂️ Qué hay en cada carpeta

```
CU-36-G1/
├── README.md                ← este archivo (empezar acá)
├── GUIA_DEFENSA.md          ← guía para la defensa oral: cómo levantar, demo, patrón y preguntas
├── GUIA_PERSONAL_CU36.pdf   ← CU de la cátedra + diagramas + cada paso con su captura y su código (líneas exactas)
│
├── 01_entrega_2/            ← LO QUE SE ENTREGA
│   ├── PPAI2026_3K4_G1_E2_Diseño.docx    documento de la entrega (se edita acá)
│   ├── capturas_del_sistema/             capturas usadas en el documento
│   └── modelo_de_formato/                modelo de formato que usó el grupo como referencia
│
├── 02_implementacion/       ← EL SISTEMA (código)
│   ├── README.md                         cómo levantarlo y dónde está cada clase
│   ├── api-gps-tracker/                  API del GPS Tracker de la cátedra (con bolsines agregados)
│   ├── backend/                          Java + Spring Boot + JPA/H2 (lógica, patrón y base de datos)
│   └── frontend/                         React (pantallas)
│
├── 03_diagramas/            ← DIAGRAMAS DE DISEÑO
│   ├── fuentes_plantuml/                 archivos .puml (se editan acá)
│   ├── png/                              imágenes para el documento
│   └── svg/                              imágenes vectoriales (no se pixelan)
│
├── 04_entrega_1/            ← ENTREGA ANTERIOR
│   ├── Entrega1_Analisis_entregada.pdf
│   └── Entrega1_Revision_con_correcciones.pdf    (correcciones aplicadas en la Entrega 2)
│
└── 05_material_catedra/     ← MATERIAL DE LA CÁTEDRA
    ├── enunciado/                        consignas, ERS y descripción del caso
    ├── ejemplo_administracion_de_campos/ ejemplo resuelto de la cátedra (también usa Adapter)
    └── enlaces.md                        repos y páginas de la cátedra
```

---

## 🚀 Levantar el sistema (resumen)

Requisitos: Java 17 o superior y Node.js 18 o superior. Desde la carpeta `CU-36-G1`, una terminal por cada parte:

```bash
cd 02_implementacion/api-gps-tracker
./mvnw spring-boot:run
```
```bash
cd 02_implementacion/backend
./mvnw spring-boot:run
```
```bash
cd 02_implementacion/frontend
npm install
npm run dev
```

Abrir **http://localhost:5173** y entrar con el usuario `eb.cordoba` (contraseña `1234`).
Los comandos para Windows y el guion de la demo están en [GUIA_DEFENSA.md](GUIA_DEFENSA.md).

---

## 👥 Integrantes

| Apellido y nombre | Legajo |
|---|---|
| Baudo, Ramiro | 78737 |
| Esquivel, Ignacio | 408359 |
| Frances, David | 80370 |
| Gon, Federico | 94470 |
| Vasquez, Facundo Santiago | 403818 |
