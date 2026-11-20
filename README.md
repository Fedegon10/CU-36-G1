# PPAI 2026 · Bolsines · CU 36 Consultar seguimiento de bolsines

**Diseño de Sistemas de Información** · UTN FRC · Curso **3K4** · **Grupo 1**
Docente tutora: Ing. Judith Meles · Patrón aplicado: **Adapter**

---

## 📌 Entrega 2

| Orden para mostrar | Archivo o carpeta | Qué hay |
|---|---|---|
| **1. Documento** | [PPAI2026_3K4_G1_E2_Diseño.pdf](PPAI2026_3K4_G1_E2_Dise%C3%B1o.pdf) | Documento completo de diseño en PDF |
| **2. Diagramas** | [`diagramas/`](diagramas/) | `1_clases/` (Figura 1) · **`2_secuencia/`** (Figuras 2 y 3: las que se comparan con el código) · `3_maquinas_de_estado/` (Figuras 4 y 5) |
| **3. Implementación** | [`implementacion/`](implementacion/) | `api-gps-tracker/` (API de la cátedra) · `backend/` (Java: lógica, patrón y base de datos) · `frontend/` (pantallas) |

> El código sigue el mismo orden y usa los mismos nombres de métodos que los diagramas de secuencia. Los comentarios indican el paso del CU, por ejemplo `// Paso 2: busca la CM del usuario logueado`.
> Por aviso del equipo docente, en la Entrega 2 **no** se pide el apartado de Diseño de experiencia de usuario.

---

## 🗂️ Estructura del repositorio

```text
CU-36-G1/
├── README.md
├── PPAI2026_3K4_G1_E2_Diseño.pdf
├── diagramas/
│   ├── README.md
│   ├── 1_clases/                  Figura 1: clases de diseño (patrón Adapter)
│   ├── 2_secuencia/               Figuras 2 y 3: patrón Adapter y flujo básico
│   └── 3_maquinas_de_estado/      Figuras 4 y 5: Bolsín y Remito
└── implementacion/
    ├── README.md                  Cómo levantarlo y dónde está cada clase
    ├── api-gps-tracker/            API del GPS Tracker de la cátedra
    ├── backend/                    Java + Spring Boot + JPA/H2
    └── frontend/                   React
```

Cada diagrama está en tres formatos: `.png` (va en el documento), `.svg` (vectorial, para hacer zoom) y `.puml` (fuente editable).

---

## 🚀 Levantar el sistema (resumen)

Requisitos: Java 17 o superior y Node.js 18 o superior.

Abrir directamente la carpeta `implementacion` en el editor. Abrir **tres terminales**, cada una ubicada inicialmente en esa carpeta, y ejecutar los siguientes bloques **en este orden**. Dejar cada proceso ejecutándose en su terminal.

**1) Terminal 1: API GPS Tracker (puerto 8080)**

```bash
cd api-gps-tracker
./mvnw spring-boot:run
```

**2) Terminal 2: backend (puerto 8081)**

```bash
cd backend
./mvnw spring-boot:run
```

**3) Terminal 3: frontend (puerto 5173)**

```bash
cd frontend
npm install
npm run dev
```

`npm install` es necesario la primera vez o cuando cambian las dependencias. En los siguientes inicios basta con `cd frontend` y `npm run dev` desde una terminal nueva ubicada en `implementacion`.

En Windows (PowerShell), reemplazar `./mvnw spring-boot:run` por `.\mvnw.cmd spring-boot:run` en las terminales 1 y 2. Los comandos del frontend son los mismos.

Abrir **http://localhost:5173** y entrar con el usuario `eb.cordoba` (contraseña `1234`).
El guion de la demo y el detalle de cada parte están en [`implementacion/README.md`](implementacion/README.md).

---

## 👥 Integrantes

| Apellido y nombre | Legajo |
|---|---|
| Baudo, Ramiro | 78737 |
| Esquivel, Ignacio | 408359 |
| Frances, David | 80370 |
| Gon, Federico | 94470 |
| Vasquez, Facundo Santiago | 403818 |
