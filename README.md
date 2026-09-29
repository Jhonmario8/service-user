# User Service

Microservicio de usuarios del proyecto Reto Pragma (plazoleta de comidas). Registra usuarios con cuatro roles, autentica con email y contraseña y emite los JWT que usan los demás servicios. `plazoleta-service` lo consulta por OpenFeign para obtener el rol y los datos de un usuario y para crear empleados.

Repositorio: [Jhonmario8/service-user](https://github.com/Jhonmario8/service-user). En el repositorio raíz [Reto-Pragma](https://github.com/Jhonmario8/Reto-Pragma) está incluido como submódulo `user-service`.

## Tabla de contenidos

- [Tecnologías](#tecnologías)
- [Roles](#roles)
- [Endpoints](#endpoints)
- [Validaciones](#validaciones)
- [Arquitectura](#arquitectura)
- [Configuración](#configuración)
- [Ejecución en local con MySQL](#ejecución-en-local-con-mysql)
- [Tests](#tests)
- [Deuda técnica conocida](#deuda-técnica-conocida)

## Tecnologías

- Java 17, Spring Boot 3.3.5 (Web, Security, Data JPA, Validation)
- MySQL 8 (Hibernate, `ddl-auto: update`)
- JWT con `io.jsonwebtoken` 0.11.5; contraseñas con BCrypt
- MapStruct 1.5.5 y Lombok
- Gradle 9.4.1 (wrapper incluido)
- JUnit 5, Mockito y AssertJ

## Roles

| Rol | Cómo se crea |
|---|---|
| `ADMIN` | No hay endpoint para crearlo. Se inserta directamente en la base de datos (ver [Ejecución en local](#ejecución-en-local-con-mysql)). |
| `OWNER` | Lo crea un ADMIN con `POST /users/owner`. |
| `EMPLOYEE` | Lo crea un OWNER. En el flujo normal se llama a `POST /users/employee` de `plazoleta-service`, que agrega el `restaurantId` del propietario y reenvía la petición a este servicio. |
| `CLIENT` | Registro público con `POST /users/client`. |

Las restricciones por rol se aplican en `SecurityConfig` a partir de los claims del JWT. El caso de uso asigna el rol según el endpoint e ignora el `role` que venga en el cuerpo de la petición.

## Endpoints

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| POST | `/auth/login` | Público | Recibe `email` y `password`. Devuelve `{ "token": "..." }`. |
| POST | `/users/owner` | ADMIN | Registra un propietario. |
| POST | `/users/employee` | OWNER | Registra un empleado. |
| POST | `/users/client` | Público | Registra un cliente. |
| GET | `/users/{id}` | Autenticado | Devuelve el usuario sin la contraseña. |
| GET | `/users/{id}/role` | Autenticado | Devuelve el rol del usuario (`"OWNER"`, `"CLIENT"`, ...). |

Ejemplo de cuerpo para los endpoints de registro:

```json
{
  "name": "Ana",
  "lastName": "Pérez",
  "identificationNumber": "1020304050",
  "phoneNumber": "+573001234567",
  "birthDate": "1995-05-20",
  "email": "ana@correo.com",
  "password": "secreta123"
}
```

El JWT se firma con HS256. Lleva los claims `user_id` y `role_name`, el email como `sub` y expira según `spring.security.jwt.expiration` (3 600 000 ms, una hora). `PRAGMA_JWT_KEY` debe tener al menos 32 bytes, porque `Keys.hmacShaKeyFor` rechaza claves más cortas.

## Validaciones

- En el DTO (Bean Validation): nombre, apellido, documento, teléfono, email y contraseña obligatorios; email con formato válido; fecha de nacimiento obligatoria.
- En el dominio (`User.validate`):
  - Teléfono: `^\+?\d{3,}$`
  - Documento: solo dígitos, mínimo 3
  - Edad mínima de 18 años, solo para propietarios (y para `createAdmin`, que no está expuesto)
- Unicidad: el email y el teléfono no pueden estar registrados.
- Login: si el email no existe o la contraseña no coincide, responde en ambos casos 401 con el mismo mensaje, "Invalid credentials", para no revelar qué emails están registrados.

## Arquitectura

Arquitectura hexagonal bajo `src/main/java/com/pragma/plazoleta/`:

```
domain/          Modelos (User, Auth, Role), puertos api/spi, excepciones y constantes
application/     Casos de uso (UserUseCase, AuthUseCase), handlers, DTOs y mappers
infrastructure/  Controladores REST, adaptadores JPA, seguridad (JWT, BCrypt) y manejo de errores
```

## Configuración

`src/main/resources/application.yml`:

| Propiedad | Valor | Descripción |
|---|---|---|
| `server.port` | `8080` | Puerto HTTP. |
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/users` | Base de datos MySQL. |
| `spring.datasource.username` | `${MYSQL_USER}` | Usuario MySQL. |
| `spring.datasource.password` | `${MYSQL_PASSWORD}` | Contraseña MySQL. |
| `spring.security.jwt.secret` | `${PRAGMA_JWT_KEY}` | Clave para firmar los JWT. Los demás servicios deben usar la misma. |
| `spring.security.jwt.expiration` | `3600000` | Duración del token en milisegundos. |

## Ejecución en local con MySQL

Requisitos: JDK 17 y MySQL 8 en `localhost:3306`.

```bash
# 1. Crear la base de datos
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS users;"

# 2. Variables de entorno
export MYSQL_USER=root
export MYSQL_PASSWORD=<tu_password>
export PRAGMA_JWT_KEY=<clave_compartida_de_al_menos_32_caracteres>

# 3. Arrancar (Hibernate crea las tablas users y roles)
./gradlew bootRun
```

El servicio queda en `http://localhost:8080`.

Antes de registrar usuarios hay que cargar la tabla `roles`, porque el registro busca el rol por nombre y falla si no existe:

```sql
USE users;
INSERT INTO roles (name) VALUES ('ADMIN'), ('OWNER'), ('EMPLOYEE'), ('CLIENT');
```

Para tener un primer ADMIN, se inserta a mano un registro en `users` con `role_id` del rol ADMIN y la contraseña en BCrypt.

## Tests

```bash
./gradlew test
```

Son tests unitarios con JUnit 5 y Mockito, sin contexto de Spring ni base de datos. `./gradlew build` pasa sin variables de entorno.

| Clase | Qué cubre |
|---|---|
| `UserUseCaseTest` | Registro de propietario (rol OWNER, contraseña codificada, mayor de 18 años con caso límite), empleado y cliente (rol correcto, sin validación de edad), formato de teléfono y documento, email y teléfono duplicados, búsqueda por id. |
| `AuthUseCaseTest` | Login con credenciales válidas; contraseña incorrecta y email inexistente devuelven la misma excepción (`UnauthorizedException`) y el mismo mensaje. |
| `GlobalExceptionHandlerTest` | `UnauthorizedException` se traduce a 401 con su mensaje. |
| `UserDTOValidationTest` | Anotaciones de Bean Validation del DTO (email inválido, campos vacíos, fecha nula) con un `Validator` de Jakarta, sin Spring. |

Las reglas de acceso por rol están en `SecurityConfig` y no las cubren estos tests unitarios.

## Deuda técnica conocida

Hallazgos de las rondas de tests que todavía no se han corregido:

**Validaciones y textos**
- `POST /auth/login` no usa `@Valid`, así que las anotaciones `@NotBlank` de `AuthDTO` no se aplican.
- La contraseña solo exige no estar vacía.
- Si se valida la edad y `birthDate` es nula, `User.validate` lanza `NullPointerException`.
- Los mensajes de `User.validate` están escritos directamente en el código en lugar de usar `DomainConstants`, y uno no coincide: el código dice "Invalid identification number" y la constante `MSG_INVALID_DOCUMENT` dice "Invalid document number".
- `UserDTO` acepta `restaurantId` y `role` desde el cliente. El rol se sobrescribe en el caso de uso, pero `restaurantId` se guarda tal cual, también en el registro público de clientes.

**Configuración inicial**
- No hay endpoint para crear el primer ADMIN ni carga inicial de la tabla `roles`, así que hay que hacerlo a mano en MySQL.

**Nombres**
- El paquete base es `com.pragma.plazoleta` y el método de registro de propietarios se llama `creteOwner`. Son nombres heredados que no se han cambiado.
