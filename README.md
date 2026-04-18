# Plazoleta API

API REST desarrollada con **Spring Boot** para gestión de usuarios y autenticación con **JWT**, siguiendo una arquitectura por capas (application, domain, infrastructure).

## Tabla de contenido

- [Tecnologías](#tecnologías)
- [Arquitectura](#arquitectura)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Requisitos previos](#requisitos-previos)
- [Configuración](#configuración)
    - [Base de datos](#base-de-datos)
    - [JWT](#jwt)
- [Ejecución del proyecto](#ejecución-del-proyecto)
- [Endpoints principales](#endpoints-principales)
    - [Auth](#auth)
    - [Users](#users)
- [Ejemplos de payloads](#ejemplos-de-payloads)
- [Manejo de errores](#manejo-de-errores)
- [Troubleshooting](#troubleshooting)
- [Pruebas](#pruebas)
- [Mejoras sugeridas](#mejoras-sugeridas)

---

## Tecnologías

- Java 17+ (recomendado)
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- Hibernate
- MySQL
- Gradle
- JWT (JJWT)

---

## Arquitectura

El proyecto está organizado en capas:

- **domain**: reglas de negocio puras, modelos, puertos e excepciones de dominio.
- **application**: casos de uso, DTOs, mappers y handlers.
- **infrastructure**: controladores, configuración de seguridad, persistencia JPA, filtros y adapters técnicos.

Esto permite desacoplar la lógica de negocio de frameworks y detalles de infraestructura.

---

## Estructura del proyecto

```text
src/
  main/
    java/com/pragma/plazoleta/
      application/
      domain/
      infrastructure/
    resources/
      application.yml
  test/
    java/com/pragma/plazoleta/
