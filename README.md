# API REST — Torneo Primera Málaga

Servicios web (API REST) del sistema de gestión del **Torneo Primera Málaga**
(Málaga, Santander). Proyecto formativo SENA — Tecnología en Análisis y
Desarrollo de Software (ADSO). Módulo: **diseño y codificación de servicios API**.

Esta API se construye sobre el mismo modelo de datos ya diseñado y
documentado en los módulos anteriores del proyecto (base de datos MySQL) y
sobre la lógica ya validada en el módulo de codificación (capa JDBC). Aquí
esa lógica se expone como servicios REST consumibles por cualquier cliente
(frontend web, app móvil, Postman, otro sistema).

## 1. Arquitectura

Arquitectura en capas, igual al estándar ya usado en el proyecto:

```
Controller (REST, HTTP)  →  Service (reglas de negocio)  →  Repository (JPA)  →  MySQL
```

```
src/main/java/com/torneomalaga/api/
├── TorneoApiApplication.java   # Punto de entrada
├── config/                     # Configuración de Swagger/OpenAPI
├── controller/                 # 8 controladores REST (uno por servicio)
├── service/                    # Reglas de negocio y validaciones
├── repository/                 # Spring Data JPA (acceso a datos)
├── model/                      # Entidades JPA (mapeo a las tablas)
└── exception/                  # Manejo centralizado de errores
```

## 2. Tecnologías

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3.3 (Spring Web, Spring Data JPA, Validation) |
| Base de datos | MySQL (mismo esquema del módulo de BD) |
| Documentación de servicios | springdoc-openapi (Swagger UI) |
| Gestor de dependencias | Maven |
| Control de versiones | Git / GitHub |

## 3. Servicios expuestos

Todas las rutas parten de `http://localhost:8080`. Todas devuelven y reciben JSON.
Corresponden a las **8 tablas reales** del proyecto (verificadas contra los archivos
`.ibd` de la base de datos): `campeonato`, `equipo`, `jugador`, `arbitro`, `partido`,
`goleador`, `tabla_posiciones` y `sugerencia`.

| Servicio | Recurso | Métodos disponibles |
|---|---|---|
| Campeonatos | `/api/campeonatos` | GET, GET /{id}, POST, PUT /{id}, DELETE /{id} |
| Equipos | `/api/equipos` | GET, GET /{id}, POST, PUT /{id}, DELETE /{id} |
| Jugadores | `/api/jugadores` | GET (?equipoId=), GET /{id}, POST, PUT /{id}, DELETE /{id} |
| Árbitros | `/api/arbitros` | GET, GET /{id}, POST, PUT /{id}, DELETE /{id} |
| Partidos | `/api/partidos` | GET (?equipoId=), GET /{id}, POST, PUT /{id}, DELETE /{id} |
| Goleadores | `/api/goleadores` | GET (?campeonatoId=), GET /{id}, POST, PUT /{id}, DELETE /{id} |
| Tabla de posiciones | `/api/tabla-posiciones` | GET (?campeonatoId=), GET /{id}, POST, PUT /{id}, DELETE /{id}, POST /recalcular/{campeonatoId} |
| Sugerencias | `/api/sugerencias` | GET, GET /{id}, POST, DELETE /{id} |

### ⚠️ Nota sobre las columnas

No tuve acceso al `CREATE TABLE` original de tu base de datos (solo a los archivos
binarios `.ibd`), así que reconstruí las columnas en dos niveles de certeza:

- **Confirmadas** con datos reales extraídos de tus `.ibd` (nombres de equipos, colores,
  estadios, nombres y roles de árbitros, textos de sugerencias, nombre del campeonato).
- **Supuestas** de forma razonable donde no había texto legible que las confirmara
  (por ejemplo, las columnas exactas de `jugador`, `goleador` y `tabla_posiciones`).

Cada campo "supuesto" está marcado con un comentario `[supuesto]` en `schema_referencia.sql`
y en el Javadoc de la clase correspondiente en `model/`. Antes de conectar la API a tu base
de datos real, compara esas columnas contra un `SHOW CREATE TABLE` de cada tabla y ajusta
los nombres en las entidades si difieren.

Detalle completo de cada servicio (parámetros, cuerpo de la petición,
respuestas y códigos HTTP) en el documento
`Documentacion_Servicios_API_TorneoMalaga.docx` incluido en este entregable,
y de forma interactiva en Swagger UI una vez la API está corriendo.

### Ejemplo de petición y respuesta — crear un equipo

```
POST /api/equipos
Content-Type: application/json

{
  "nombre": "Calidosos",
  "municipio": "Málaga",
  "colores": "Rojo y Blanco",
  "estadio": "Estadio Municipal de Málaga"
}
```

Respuesta `201 Created`:

```json
{
  "id": 1,
  "nombre": "Calidosos",
  "municipio": "Málaga",
  "colores": "Rojo y Blanco",
  "estadio": "Estadio Municipal de Málaga"
}
```

### Ejemplo de error estandarizado

```
GET /api/equipos/999
```

Respuesta `404 Not Found`:

```json
{
  "fecha": "2026-08-28T10:00:00",
  "codigo": 404,
  "mensaje": "Equipo con id 999 no fue encontrado(a)",
  "detalles": []
}
```

## 4. Documentación interactiva (Swagger)

Con la aplicación corriendo:

- Swagger UI: `http://localhost:8080/docs`
- Contrato OpenAPI (JSON): `http://localhost:8080/api-docs`

Cada endpoint queda documentado automáticamente a partir de las anotaciones
`@Tag` y `@Operation` puestas en cada controlador, y se puede probar
directamente desde el navegador.

## 5. Cómo ejecutar el proyecto

**Requisitos:** JDK 17, Maven 3.9+, MySQL 8 corriendo con el esquema del
proyecto (ver `schema_referencia.sql`, o el entregable oficial del módulo de BD).

```bash
# 1. Clonar el repositorio
git clone https://github.com/anderzongato-svg/torneo-api.git
cd torneo-api

# 2. Configurar credenciales locales (nunca se versionan)
cp src/main/resources/application-local.properties.example src/main/resources/application-local.properties
# editar ese archivo con el usuario/contraseña de tu MySQL local

# 3. Exportar variables de entorno (alternativa a application-local.properties)
export DB_URL=jdbc:mysql://localhost:3306/campeonato_futbol
export DB_USER=root
export DB_PASSWORD=tu_password

# 4. Ejecutar
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

## 6. Control de versiones (Git)

El proyecto se versiona con Git desde su creación, siguiendo el mismo
estándar de trazabilidad usado en el módulo de codificación (`torneo-amateur-jdbc`):

```bash
git init
git add .
git commit -m "feat: estructura inicial del proyecto Spring Boot"
git commit -m "feat: entidades JPA y configuracion de base de datos"
git commit -m "feat: repositorios Spring Data JPA"
git commit -m "feat: capa de servicios con reglas de negocio"
git commit -m "feat: controladores REST y documentacion Swagger"
git commit -m "feat: servicio de tabla de posiciones"
git commit -m "docs: README y documentacion tecnica de servicios"

git branch -M main
git remote add origin https://github.com/anderzongato-svg/torneo-api.git
git push -u origin main
```

**Convención de commits:** `feat:` (nueva funcionalidad), `fix:` (corrección),
`docs:` (documentación), `refactor:` (mejora interna sin cambiar comportamiento) —
la misma convención usada en el repositorio `torneo-amateur-jdbc`.

**Buenas prácticas aplicadas:**
- Credenciales de base de datos externalizadas y fuera del repositorio (`.gitignore`).
- Un commit por capa/funcionalidad, no un único commit gigante.
- `README.md` como punto de entrada para cualquiera que clone el repositorio.

## 7. Relación con los módulos anteriores del proyecto

| Módulo previo | Aporte a esta API |
|---|---|
| Diseño de base de datos (MySQL) | Modelo entidad-relación que aquí se mapea con JPA |
| Taller de SQL | Sentencias base para las operaciones que ahora expone cada servicio |
| Codificación (JDBC / `torneo-amateur-jdbc`) | Reglas de negocio y validaciones reutilizadas en la capa `service` |
| Front-end (HTML/CSS/JS) | Cliente consumidor natural de estos servicios en una siguiente iteración |
