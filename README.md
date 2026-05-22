# User Service

Servicio de usuarios para ecosistemas de microservicios, desarrollado en Java 17 con Spring Boot 3. Gestiona el ciclo de vida de usuarios, autenticación basada en JWT y roles, con integración a bases de datos y preparado para interacción con otros servicios mediante prácticas de arquitectura limpia.

---

## Tabla de Contenidos

- [Descripción](#descripción)
- [Tecnologías principales](#tecnologías-principales)
- [Arquitectura y estructura](#arquitectura-y-estructura)
- [Configuración inicial y ejecución](#configuración-inicial-y-ejecución)
- [Variables de entorno](#variables-de-entorno)
- [Testing](#testing)
- [Mejoras sugeridas](#mejoras-sugeridas)
- [Contribuciones](#contribuciones)

---

## Descripción

User Service expone una API RESTful para el registro, gestión y autenticación de usuarios. Pensado como microservicio fundamental dentro de la plataforma de manejo de plazoletas, implementa control de acceso robusto y puede servir como backend para sistemas de autenticación y autorización en cualquier arquitectura distribuida.

---

## Tecnologías principales

- **Java 17**
- **Spring Boot 3** (Web, Security, Data JPA, Validation)
- **JWT (Json Web Tokens)**
- **MySQL** con JPA/Hibernate
- **Gradle 9.4.1** (wrapper incluido)
- **Lombok** y **MapStruct**
- Pruebas con **JUnit** y **Spring Test**

---

## Arquitectura y estructura

El proyecto sigue principios de Clean Architecture / DDD para desacoplar lógica de negocio:

```
src/
 ├─ main/
 │   ├─ java/com/pragma/plazoleta/
 │   │   ├─ application/    # Casos de uso, DTOs, handlers, mappers
 │   │   ├─ domain/         # Modelos, puertos/repositories (api, spi), excepciones
 │   │   └─ infrastructure/ # Controllers REST, persistencia, config de seguridad
 │   └─ resources/
 │       └─ application.yml # Configuración de entorno y datasource
 └─ test/
     └─ java/com/pragma/plazoleta/
```

- **application/**: orquesta casos de uso y coordinación entre capas (ej: registro, login).
- **domain/**: modelos, interfaces de persistencia/autorización y reglas del negocio.
- **infrastructure/**: endpoints REST, persistencia con JPA a MySQL, configuración JWT y seguridad.

---

## Configuración inicial y ejecución

### Pre-requisitos

- JDK 17+
- MySQL (corriendo y accesible en `localhost:3306`)
- Variables de entorno configuradas (ver abajo)
- Gradle (se recomienda usar el wrapper incluido)

### Instalación y ejecución

```sh
# Clona el repositorio
git clone https://github.com/Jhonmario8/service-user.git
cd service-user

# Crea la base de datos MySQL
CREATE DATABASE IF NOT EXISTS plazoleta;

# Compila y ejecuta la aplicación
./gradlew bootRun
```

La API estará disponible por defecto en `http://localhost:8080`.

---

## Variables de entorno

Ajusta (o exporta en tu sistema) las siguientes para producción/loca:

- `MYSQL_USER` — usuario MySQL.
- `MYSQL_PASSWORD` — password MySQL.
- `PRAGMA_JWT_KEY` — clave secreta JWT para firmar y validar tokens.

Estas variables se usan en `src/main/resources/application.yml` para la conexión y seguridad.

---

## Testing

Para lanzar las pruebas:

```sh
./gradlew test
```

Utiliza JUnit y Spring Security Test para cobertura de lógica de negocio y endpoints principales.

---

## Mejoras sugeridas

- Implementación de OAuth2/social login.
- Limitar intentos de login e incluir recaptcha.
- Documentación detallada de endpoints (Swagger/OpenAPI).
- DevOps: Dockerización y pipelines CI/CD.
- Soporte multi-idioma y auditoría de usuarios.

---

## Contribuciones

¡Las contribuciones son bienvenidas! Abre un issue o pull request siguiendo la convención de comunidad. Usa ramas descriptivas y documentación clara para código y cambios.

---

> Proyecto desarrollado por [Jhonmario8](https://github.com/Jhonmario8), orientado a microservicios seguros y escalables.