# TVMaze API Middleware

API REST en Java que funciona como middleware de [TVMaze](https://www.tvmaze.com/api).
Permite buscar shows, consultar el detalle de un show con caché en MongoDB y registrar
comentarios con calificación.

## Tecnologías

- Java 17
- Spring Boot 4.1.1 (Web MVC, Validation, RestClient, Data MongoDB)
- MongoDB Atlas
- Maven
- JUnit 5, Mockito, MockMvc, MockRestServiceServer
- GitHub Actions (CI)

## Requisitos

- JDK 17 o superior
- Maven 3.9 o superior
- Una instancia de MongoDB (Atlas o local)

## Configuración

La cadena de conexión **no está en el código**. Se lee de la variable `MONGODB_URI`,
que puede definirse de dos formas:

**Opción 1: archivo `.env`** (ignorado por Git)

```bash
cp .env.example .env
```

Edita `.env` con tu cadena de conexión:

```
MONGODB_URI=mongodb+srv://<usuario>:<password>@<cluster>.mongodb.net/tvmaze?retryWrites=true&w=majority
```

**Opción 2: variable de entorno**

```bash
export MONGODB_URI="mongodb+srv://..."
```

Si no se define, se usa `mongodb://localhost:27017/tvmaze`.

## Ejecución

```bash
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

## Pruebas

```bash
mvn verify
```

Las pruebas no requieren conexión a MongoDB ni a TVMaze: usan mocks.

## Endpoints

### A. Buscar shows

```
GET /api/shows/search?q={criterio}
```

Consulta TVMaze y agrega los comentarios guardados de cada show.

```json
[
  {
    "id": 1,
    "name": "Under the Dome",
    "channel": "CBS",
    "summary": "<p>...</p>",
    "genres": ["Drama", "Science-Fiction", "Thriller"],
    "comments": [
      { "comment": "Muy buena serie", "rating": 5 }
    ]
  }
]
```

`channel` toma el nombre de `network` y, si no existe, el de `webChannel`.

### B. Obtener un show

```
GET /api/shows/{showId}
```

Regresa el objeto show completo de TVMaze más el arreglo `comments`.
Primero busca en la caché de MongoDB; si no existe, consulta TVMaze y lo guarda.

### C. Registrar un comentario

```
POST /api/shows/{showId}/comments
Content-Type: application/json

{ "comment": "Muy buena serie", "rating": 5 }
```

Respuesta `201 Created`:

```json
{ "status": "success", "message": "Comentario registrado correctamente" }
```

Validaciones: `comment` obligatorio (máx. 500 caracteres) y `rating` obligatorio entre 0 y 5.
Si el show no existe en TVMaze, responde `404`.

## Manejo de errores

Todos los errores regresan el mismo formato y nunca exponen el stack trace:

```json
{
  "timestamp": "2026-09-26T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "La peticion contiene datos invalidos",
  "path": "/api/shows/1/comments",
  "details": ["rating: La calificacion maxima es 5"]
}
```

| Código | Caso |
|---|---|
| 400 | Parámetros o cuerpo inválidos |
| 404 | Show inexistente o ruta no encontrada |
| 502 | TVMaze no disponible o respondió con error |
| 503 | MongoDB no disponible |
| 500 | Error no controlado (mensaje genérico) |

## Arquitectura

```
src/main/java/com/examen/tvmaze
├── controller/   Endpoints REST
├── service/      Lógica de negocio (caché, comentarios)
├── repository/   Acceso a MongoDB (Spring Data)
├── client/       Consumo de la API de TVMaze
├── model/        Documentos de MongoDB
├── dto/          Objetos de entrada y salida de la API
└── exception/    Excepciones y manejador global (@RestControllerAdvice)
```

## Decisiones de diseño

- **Caché con TTL:** la colección `shows_cache` tiene un índice TTL de 24 horas sobre
  `cachedAt`. MongoDB elimina los documentos vencidos y el siguiente request vuelve a
  consultar TVMaze, así la información no queda desactualizada.
- **Comentarios fuera de la caché:** se agregan al momento de responder, por lo que un
  comentario nuevo se ve de inmediato aunque el show venga de la caché. No tienen TTL.
- **Sin consultas N+1:** en la búsqueda, los comentarios de todos los shows se obtienen
  con una sola consulta (`showId in [...]`) y se agrupan en memoria.
- **Índice en `comments.showId`:** todas las consultas de comentarios filtran por show.
- **Errores de TVMaze controlados:** el cliente traduce los errores HTTP y de red a
  excepciones propias, que el manejador global convierte en respuestas estandarizadas.
- **Credenciales fuera del repositorio:** la URI de MongoDB se lee de un `.env` local o de
  una variable de entorno.

## Colección de Postman

En `postman/TVMaze-API.postman_collection.json` están todas las peticiones, incluidos los
casos de error. Se importa en Postman con **Import** y usa la variable `baseUrl`
(`http://localhost:8080`).