# Tareas — proxy_backend

Cada bloque se trabaja en su rama y se integra a `main` con `git merge --no-ff`. Un commit convencional por tarea.

| # | Rama | Tareas | Criterio de aceptación |
|---|---|---|---|
| 1 | `chore/scaffold` | Estructura `src/`, `test/`, `.gitignore`, `README` base, `task.md` | El repo compila vacío y la estructura queda documentada |
| 2 | `feat/domain-proxies` | `Role`, `Classification`, `User`, `Document`, `DocumentSummary`, `DocumentContent`, `SeedData`, `AccessPolicy`, `RequestContext`, `DocumentService`, `RemoteDocumentService`, `DocumentProxy`, `AccessControlProxy`, `CacheProxy`, `LazyLoadProxy`, `LoggingProxy`, `AuditLog`, `AuditEntry`, `AppState` | `java -cp out com.bovedadocs.ProxySmokeTest` pasa todas las verificaciones |
| 3 | `feat/api-tests` | `ApiServer`, `Json`, `Main`, CORS, `layers`, errores 400/403/404, `ProxySmokeTest` | `GET /api/health` responde 200; Laura recibe 403 en `d2`; segunda lectura es cache hit |
| 4 | `build/docker` | `Dockerfile` multi-stage, `.dockerignore`, `docker-compose.yml`, non-root, healthcheck | `docker compose up --build -d` deja `/api/health` en verde |
| 5 | `docs/final` | README completo, UML Mermaid, API, demo, comparación con Decorator | Un lector puede levantar y demostrar el taller sin ayuda |

## Checklist

- [x] Dominio y datos semilla (4 usuarios, 8 documentos)
- [x] `AccessControlProxy` con rol, área y clasificación
- [x] `CacheProxy` con invalidación en escritura/borrado
- [x] `LazyLoadProxy` con reutilización de contenido pesado
- [x] `LoggingProxy` con auditoría de denegaciones
- [x] API REST con `layers` conmutables y traza por request
- [x] Pruebas de humo sin dependencias externas
- [x] Docker multi-stage con healthcheck
- [x] README y guion de demo
