# Guías por módulo

## learning-api — circuito completo

Enviar texto a `POST /api/messages`, un objeto a `POST /api/users` y eventos correlacionados a `POST /api/events/{key}`. Revisar Kafka UI y los logs: el producer usa `KafkaTemplate`; los tres listeners muestran cómo un serializer debe coincidir con su deserializer.

Ejercicios: enviar tres veces `customer-42` y luego `customer-99`; comparar sus particiones en Kafka UI. Iniciar una segunda instancia con el mismo group id y observar los mensajes `REBALANCE`. Cambiar temporalmente `auto-offset-reset` de `earliest` a `latest` y explicar por qué un grupo nuevo deja de leer el histórico.

## wikimedia-producer — fuente externa y perfiles

`local` es el perfil por defecto y manda un JSON fijo, ideal para pruebas repetibles. `wikimedia` abre una conexión SSE a Wikimedia y publica cada cambio. El perfil mantiene la infraestructura igual pero reemplaza el adaptador de entrada; ese es el valor de desacoplar mediante Kafka.

## wikimedia-consumer — evento a una base

El listener recibe `ConsumerRecord` para poder loguear key, partición y offset. Guarda primero en JPA y recién después retorna; con la semántica habitual at-least-once, una caída puede provocar reproceso, por lo que una aplicación real debería usar una clave de idempotencia.

### Recorrido E2E: comprobar MySQL desde Swagger

Este recorrido aparece también como “Flujo E2E: de Kafka a MySQL” en el módulo 22 del front-end.

1. Ejecutar `docker compose up -d` desde la raíz.
2. En otra terminal, iniciar `wikimedia-consumer` con `./mvnw.cmd -pl wikimedia-consumer spring-boot:run`.
3. En una tercera terminal, iniciar `wikimedia-producer` con `./mvnw.cmd -pl wikimedia-producer spring-boot:run`. Su perfil `local` predeterminado publica un ejemplo.
4. Abrir `http://localhost:8081/swagger-ui.html` y ejecutar `GET /api/wikimedia-events`.

Los POST de `http://localhost:8080/swagger-ui.html` son ejercicios independientes del módulo `learning-api`: publican en `learning.*` y se observan en sus listeners/logs, pero no se guardan en MySQL.

## advanced-labs — propiedades que se pagan con complejidad

Iniciar `advanced-labs` y probar `POST /labs/idempotent/{key}`, `POST /labs/transaction/{id}` y `POST /labs/state/{key}`. El último endpoint acepta body ausente para enviar un tombstone; recordar que la compactación no borra inmediatamente. Para Streams, publicar manualmente en `labs.stream.input.v1` y observar `labs.stream.output.v1` con el valor en mayúsculas. Los labs muestran configuración y flujos, pero un broker no permite comprobar replicación, ISR ni failover.
