# Sistema de Inventariado - API REST

## 📋 Descripción

Sistema de gestión de inventario desarrollado con Spring Boot que permite administrar productos, ventas, empleados, clientes, proveedores y contabilidad. Incluye autenticación JWT, roles y permisos granulares.

## ✨ Mejoras Recientes (Última Actualización)

### 🔧 Correcciones Implementadas
- **✅ Referencias Circulares Resueltas**: JSON limpio sin bucles infinitos usando anotaciones Jackson
- **✅ LazyInitializationException Corregida**: Relaciones ManyToMany optimizadas con FetchType.EAGER
- **✅ Inicialización de Admin Mejorada**: Usuario admin se crea/actualiza automáticamente al iniciar
- **✅ Seguridad de Contraseñas**: Contraseñas ocultas en respuestas JSON
- **✅ Validación de Registro**: Validaciones mejoradas para registro de usuarios
- **✅ CORS Configurado**: Soporte completo para peticiones cross-origin
- **✅ Logging Mejorado**: Logs detallados para debugging y monitoreo
- **✅ Actualización de Usuarios**: DTO específico para evitar problemas de serialización
- **✅ Creación de Productos**: Soporte completo para fechas Java 8 (LocalDate)
- **✅ Creación de Ventas**: DTO específico con validaciones robustas
- **✅ Creación de Empleados**: Validaciones completas y verificaciones de unicidad
- **✅ Creación de Proveedores**: Validaciones mejoradas y manejo de errores
- **✅ Creación de Registros Contables**: Validaciones de campos obligatorios
- **✅ Gestión de Inventario**: DTO específico y endpoint de debugging

### 🚀 Características Estables
- **Autenticación JWT**: Sistema de tokens seguro y confiable
- **Roles y Permisos**: 4 roles predefinidos con 30+ permisos granulares
- **API REST Completa**: Endpoints para todos los módulos del sistema
- **Base de Datos Automática**: Inicialización automática con Flyway
- **Documentación Swagger**: API documentada e interactiva
- **Validaciones Robustas**: Todos los endpoints con validaciones completas
- **Manejo de Errores**: Códigos de estado HTTP específicos y logging detallado

## 🚀 Inicio Rápido

### Prerrequisitos
- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Insomnia (o cualquier cliente REST)

### Configuración Inicial

1. **Clonar el repositorio**
```bash
git clone https://github.com/2ariel22/Inventariado.git
cd Inventariado
```

2. **Configurar base de datos**
```sql
CREATE DATABASE inventory;
```

3. **Configurar aplicación**
Editar `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/inventory
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password
```

4. **Ejecutar la aplicación**
```bash
mvn spring-boot:run
```

La aplicación se inicializará automáticamente con:
- ✅ 30+ permisos del sistema
- ✅ 4 roles (ADMIN, VENDEDOR, CONTADOR, GERENTE)
- ✅ Usuario administrador: `admin` / `admin123`

## 👥 Roles de Usuario Disponibles

| ID | Rol | Descripción | Permisos Principales |
|---|---|---|---|
| **1** | **ADMIN** | Administrador del sistema | Acceso completo a todas las funcionalidades |
| **2** | **VENDEDOR** | Vendedor | Productos, ventas, inventario, clientes |
| **3** | **CONTADOR** | Contador | Contabilidad, ventas, productos, clientes |
| **4** | **GERENTE** | Gerente | Reportes y gestión general (lectura/actualización) |

### 📝 Notas sobre Roles:
- **ADMIN**: Control total del sistema, puede crear/eliminar usuarios y modificar roles
- **VENDEDOR**: Rol por defecto para nuevos usuarios (si no se especifica `rolIds`)
- **CONTADOR**: Acceso a módulos financieros y de contabilidad
- **GERENTE**: Acceso de supervisión, principalmente lectura y actualización de datos

## 🧪 Guía de Pruebas con Insomnia

### Configuración de Insomnia

1. **Crear nueva colección** llamada "Inventariado API"
2. **Configurar variable de entorno**:
   - `token`: (se llenará automáticamente después del login)

**📋 Nota:** 
- Todas las rutas están listas para copiar y pegar directamente en Insomnia
- Reemplaza `{token}` con el token JWT obtenido del login
- Todas las URLs usan `localhost:8080` por defecto

### 1. 🔐 Autenticación

#### 1.1 Login como Administrador
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "user": "admin",
  "password": "admin123"
}
```

**Respuesta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "usuario": {
    "id": 1,
    "user": "admin",
    "email": "admin@inventariado.com",
    "nombre": "Administrador",
    "apellido": "Sistema",
    "activo": true,
    "roles": ["ADMIN"]
  }
}
```

**⚠️ Importante:** Copia el token de la respuesta y guárdalo en la variable `token`.

## 🚀 Guía Rápida: Crear Usuarios y Asignar Roles

### Paso 1: Obtener Lista de Roles
```http
GET http://localhost:8080/api/roles
Authorization: Bearer {token}
```

### Paso 2: Crear Usuario con Rol
```http
POST http://localhost:8080/api/usuarios
Authorization: Bearer {token}
Content-Type: application/json

{
  "user": "nuevo_usuario",
  "password": "123456",
  "email": "usuario@empresa.com",
  "nombre": "Nombre",
  "apellido": "Apellido",
  "activo": true,
  "rolIds": [2]
}
```

### Paso 3: Verificar Usuario Creado
```http
GET http://localhost:8080/api/usuarios
Authorization: Bearer {token}
```

### Paso 4: Asignar Roles Adicionales (Opcional)
```http
PUT http://localhost:8080/api/usuarios/{id}/roles
Authorization: Bearer {token}
Content-Type: application/json

{
  "rolIds": [2, 3]
}
```

#### 1.2 Registrar Nuevo Usuario

**Ejemplo básico (asigna automáticamente rol VENDEDOR):**
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "user": "vendedor1",
  "password": "123456",
  "email": "vendedor1@empresa.com",
  "nombre": "Juan",
  "apellido": "Vendedor"
}
```

**Ejemplo con rol específico:**
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "user": "contador1",
  "password": "123456",
  "email": "contador1@empresa.com",
  "nombre": "María",
  "apellido": "Contador",
  "rolIds": [3]
}
```

**Ejemplos por tipo de usuario:**

**👑 Administrador:**
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "user": "admin2",
  "password": "admin123",
  "email": "admin2@empresa.com",
  "nombre": "Admin",
  "apellido": "Secundario",
  "rolIds": [1]
}
```

**🛒 Vendedor:**
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "user": "vendedor1",
  "password": "vendedor123",
  "email": "vendedor1@empresa.com",
  "nombre": "Carlos",
  "apellido": "Vendedor",
  "rolIds": [2]
}
```

**💰 Contador:**
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "user": "contador1",
  "password": "contador123",
  "email": "contador1@empresa.com",
  "nombre": "María",
  "apellido": "Contador",
  "rolIds": [3]
}
```

**👔 Gerente:**
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "user": "gerente1",
  "password": "gerente123",
  "email": "gerente1@empresa.com",
  "nombre": "Ana",
  "apellido": "Gerente",
  "rolIds": [4]
}
```

**📋 Campos disponibles:**
- `user` (requerido): Nombre de usuario único
- `password` (requerido): Contraseña del usuario
- `email` (requerido): Correo electrónico único
- `nombre` (opcional): Nombre del usuario
- `apellido` (opcional): Apellido del usuario
- `rolIds` (opcional): Array de IDs de roles a asignar (ver tabla de roles arriba)

**📝 Respuestas del endpoint:**
- `201 Created`: Usuario creado exitosamente
- `400 Bad Request`: Datos inválidos (campos requeridos vacíos)
- `409 Conflict`: Usuario o email ya existe
- `500 Internal Server Error`: Error del servidor

#### 1.3 Validar Token
```http
POST http://localhost:8080/api/auth/validate?token={token}
```

#### 1.4 Logout
```http
POST http://localhost:8080/api/auth/logout
Authorization: Bearer {token}
```

### 2. 👥 Gestión de Usuarios

#### 2.1 Listar Todos los Usuarios
```http
GET http://localhost:8080/api/usuarios
Authorization: Bearer {token}
```

**Respuesta esperada:**
```json
[
  {
    "id": 1,
    "user": "admin",
    "email": "admin@inventariado.com",
    "nombre": "Administrador",
    "apellido": "Sistema",
    "activo": true,
    "roles": [
      {
        "id": 1,
        "nombre": "ADMIN",
        "descripcion": "Administrador del sistema con acceso completo",
        "permisos": [
          {
            "id": 1,
            "nombre": "PRODUCTOS_CREAR",
            "descripcion": "Crear productos",
            "recurso": "PRODUCTOS",
            "accion": "CREAR"
          },
          {
            "id": 2,
            "nombre": "PRODUCTOS_LEER",
            "descripcion": "Leer productos",
            "recurso": "PRODUCTOS",
            "accion": "LEER"
          }
        ]
      }
    ]
  }
]
```

**📝 Nota**: Las contraseñas están ocultas por seguridad y no aparecen en las respuestas JSON.

#### 2.2 Obtener Usuario por ID
```http
GET http://localhost:8080/api/usuarios/1
Authorization: Bearer {token}
```

#### 2.3 Crear Usuario con Roles
```http
POST http://localhost:8080/api/usuarios
Authorization: Bearer {token}
Content-Type: application/json

{
  "user": "contador1",
  "password": "123456",
  "email": "contador1@empresa.com",
  "nombre": "María",
  "apellido": "Contador",
  "activo": true,
  "rolIds": [3]
}
```

**💡 Nota:** Para crear usuarios, es recomendable usar el endpoint de registro (`/api/auth/register`) que es más simple y no requiere autenticación.

**📋 Campos disponibles:**
- `user` (requerido): Nombre de usuario único
- `password` (requerido): Contraseña del usuario
- `email` (requerido): Correo electrónico único
- `nombre` (opcional): Nombre del usuario
- `apellido` (opcional): Apellido del usuario
- `activo` (opcional): Estado del usuario (default: true)
- `rolIds` (opcional): Array de IDs de roles a asignar

#### 2.4 Actualizar Usuario
```http
PUT http://localhost:8080/api/usuarios/2
Authorization: Bearer {token}
Content-Type: application/json

{
  "user": "contador1",
  "email": "maria.contador@empresa.com",
  "nombre": "María",
  "apellido": "Contador Actualizado",
  "activo": true
}
```

#### 2.5 Eliminar Usuario
```http
DELETE http://localhost:8080/api/usuarios/2
Authorization: Bearer {token}
```

#### 2.6 Asignar Roles a Usuario Existente
```http
PUT http://localhost:8080/api/usuarios/2/roles
Authorization: Bearer {token}
Content-Type: application/json

{
  "rolIds": [2, 3]
}
```

#### 2.7 Ejemplos de Creación de Usuarios por Rol

**👨‍💼 Crear Vendedor:**
```http
POST http://localhost:8080/api/usuarios
Authorization: Bearer {token}
Content-Type: application/json

{
  "user": "vendedor1",
  "password": "123456",
  "email": "vendedor1@empresa.com",
  "nombre": "Juan",
  "apellido": "Vendedor",
  "activo": true,
  "rolIds": [2]
}
```

**📊 Crear Contador:**
```http
POST http://localhost:8080/api/usuarios
Authorization: Bearer {token}
Content-Type: application/json

{
  "user": "contador1",
  "password": "123456",
  "email": "contador1@empresa.com",
  "nombre": "María",
  "apellido": "Contador",
  "activo": true,
  "rolIds": [3]
}
```

**👔 Crear Gerente:**
```http
POST http://localhost:8080/api/usuarios
Authorization: Bearer {token}
Content-Type: application/json

{
  "user": "gerente1",
  "password": "123456",
  "email": "gerente1@empresa.com",
  "nombre": "Carlos",
  "apellido": "Gerente",
  "activo": true,
  "rolIds": [4]
}
```

**🔧 Crear Usuario con Múltiples Roles:**
```http
POST http://localhost:8080/api/usuarios
Authorization: Bearer {token}
Content-Type: application/json

{
  "user": "supervisor1",
  "password": "123456",
  "email": "supervisor1@empresa.com",
  "nombre": "Ana",
  "apellido": "Supervisor",
  "activo": true,
  "rolIds": [2, 4]
}
```

### 3. 🏷️ Gestión de Roles

#### 3.1 Listar Roles Disponibles
```http
GET http://localhost:8080/api/roles
Authorization: Bearer {token}
```

**Respuesta esperada:**
```json
[
  {
    "id": 1,
    "nombre": "ADMIN",
    "descripcion": "Administrador del sistema",
    "permisos": [
      {
        "id": 1,
        "nombre": "PRODUCTOS_CREAR",
        "descripcion": "Crear productos",
        "recurso": "PRODUCTOS",
        "accion": "CREAR"
      }
    ]
  },
  {
    "id": 2,
    "nombre": "VENDEDOR",
    "descripcion": "Vendedor",
    "permisos": [...]
  },
  {
    "id": 3,
    "nombre": "CONTADOR",
    "descripcion": "Contador",
    "permisos": [...]
  },
  {
    "id": 4,
    "nombre": "GERENTE",
    "descripcion": "Gerente",
    "permisos": [...]
  }
]
```

#### 3.2 Roles Predefinidos del Sistema

| ID | Rol | Descripción | Permisos Principales |
|----|-----|-------------|---------------------|
| 1 | **ADMIN** | Administrador del sistema | Todos los permisos del sistema |
| 2 | **VENDEDOR** | Vendedor | Productos, Ventas, Inventario, Clientes |
| 3 | **CONTADOR** | Contador | Contabilidad, Ventas, Productos, Clientes |
| 4 | **GERENTE** | Gerente | Lectura y actualización de todos los módulos |

#### 3.3 Crear Nuevo Rol
```http
POST http://localhost:8080/api/roles
Authorization: Bearer {token}
Content-Type: application/json

{
  "nombre": "SUPERVISOR",
  "descripcion": "Supervisor de tienda"
}
```

#### 3.4 Asignar Permisos a Rol
```http
PUT http://localhost:8080/api/roles/5/permisos
Authorization: Bearer {token}
Content-Type: application/json

{
  "permisosIds": [1, 2, 3, 4, 5, 6]
}
```

#### 3.5 Obtener Rol por ID
```http
GET http://localhost:8080/api/roles/1
Authorization: Bearer {token}
```

#### 3.6 Actualizar Rol
```http
PUT http://localhost:8080/api/roles/1
Authorization: Bearer {token}
Content-Type: application/json

{
  "nombre": "ADMIN",
  "descripcion": "Administrador del sistema actualizado"
}
```

#### 3.7 Eliminar Rol
```http
DELETE http://localhost:8080/api/roles/5
Authorization: Bearer {token}
```

### 4. 🔐 Gestión de Permisos

#### 4.1 Listar Permisos Disponibles
```http
GET http://localhost:8080/api/permisos
Authorization: Bearer {token}
```

**Respuesta esperada:**
```json
[
  {
    "id": 1,
    "nombre": "PRODUCTOS_CREAR",
    "descripcion": "Crear productos",
    "recurso": "PRODUCTOS",
    "accion": "CREAR"
  },
  {
    "id": 2,
    "nombre": "PRODUCTOS_LEER",
    "descripcion": "Leer productos",
    "recurso": "PRODUCTOS",
    "accion": "LEER"
  }
]
```

#### 4.2 Permisos del Sistema

El sistema incluye **30+ permisos** organizados por módulos:

| Módulo | Permisos Disponibles |
|--------|---------------------|
| **PRODUCTOS** | CREAR, LEER, ACTUALIZAR, ELIMINAR |
| **VENTAS** | CREAR, LEER, ACTUALIZAR, ELIMINAR |
| **INVENTARIO** | CREAR, LEER, ACTUALIZAR, ELIMINAR |
| **USUARIOS** | CREAR, LEER, ACTUALIZAR, ELIMINAR |
| **ROLES** | CREAR, LEER, ACTUALIZAR, ELIMINAR |
| **PERMISOS** | CREAR, LEER, ACTUALIZAR, ELIMINAR |
| **CONTABILIDAD** | CREAR, LEER, ACTUALIZAR, ELIMINAR |
| **EMPLEADOS** | CREAR, LEER, ACTUALIZAR, ELIMINAR |
| **CLIENTES** | CREAR, LEER, ACTUALIZAR, ELIMINAR |
| **PROVEEDORES** | CREAR, LEER, ACTUALIZAR, ELIMINAR |
| **SISTEMA** | ADMINISTRAR |

#### 4.3 Crear Nuevo Permiso
```http
POST http://localhost:8080/api/permisos
Authorization: Bearer {token}
Content-Type: application/json

{
  "nombre": "REPORTES_GENERAR",
  "descripcion": "Generar reportes",
  "recurso": "REPORTES",
  "accion": "GENERAR"
}
```

**📋 Campos disponibles:**
- `nombre` (requerido): Nombre único del permiso
- `descripcion` (opcional): Descripción del permiso
- `recurso` (opcional): Módulo al que pertenece
- `accion` (requerido): Acción permitida (CREAR, LEER, ACTUALIZAR, ELIMINAR, ADMINISTRAR)

#### 4.4 Obtener Permiso por ID
```http
GET http://localhost:8080/api/permisos/1
Authorization: Bearer {token}
```

#### 4.5 Actualizar Permiso
```http
PUT http://localhost:8080/api/permisos/1
Authorization: Bearer {token}
Content-Type: application/json

{
  "nombre": "PRODUCTOS_CREAR",
  "descripcion": "Crear productos actualizado",
  "recurso": "PRODUCTOS",
  "accion": "CREAR"
}
```

#### 4.6 Eliminar Permiso
```http
DELETE http://localhost:8080/api/permisos/1
Authorization: Bearer {token}
```

### 5. 📦 Gestión de Productos

#### 5.1 Listar Productos
```http
GET http://localhost:8080/api/productos
Authorization: Bearer {token}
```

#### 5.2 Crear Producto
```http
POST http://localhost:8080/api/productos
Authorization: Bearer {token}
Content-Type: application/json

{
  "nombre": "Laptop Dell Inspiron",
  "fechaExpiracion": "2025-12-31",
  "fechaVencimiento": "2026-12-31",
  "categoria": "Tecnología",
  "stock": 10,
  "precio": 1500000.00
}
```

#### 5.3 Actualizar Producto
```http
PUT http://localhost:8080/api/productos/1
Authorization: Bearer {token}
Content-Type: application/json

{
  "nombre": "Laptop Dell Inspiron 15",
  "fechaExpiracion": "2025-12-31",
  "fechaVencimiento": "2026-12-31",
  "categoria": "Tecnología",
  "stock": 8,
  "precio": 1600000.00
}
```

### 6. 🛒 Gestión de Ventas

#### 6.1 Listar Ventas
```http
GET http://localhost:8080/api/ventas
Authorization: Bearer {token}
```

#### 6.2 Crear Venta
```http
POST http://localhost:8080/api/ventas
Authorization: Bearer {token}
Content-Type: application/json

{
  "clienteId": 1,
  "vendedorId": 1,
  "descripcion": "Venta de laptop Dell",
  "fecha": "2024-01-15",
  "valorVenta": 1600000.00,
  "productoIds": [1, 2],
  "estado": "PENDIENTE"
}
```

### 7. 👨‍💼 Gestión de Empleados

#### 7.1 Listar Empleados
```http
GET http://localhost:8080/api/empleados
Authorization: Bearer {token}
```

#### 7.2 Crear Empleado
```http
POST http://localhost:8080/api/empleados
Authorization: Bearer {token}
Content-Type: application/json

{
  "nombre": "Carlos",
  "cedula": "12345678",
  "edad": 30,
  "telefono": "3001234567",
  "correo": "carlos@empresa.com",
  "antiguedad": "2020-01-15"
}
```

**📋 Campos obligatorios:**
- `nombre`: Nombre del empleado
- `cedula`: Cédula única del empleado
- `edad`: Edad (debe ser positiva)
- `telefono`: Teléfono de contacto
- `correo`: Correo electrónico único
- `antiguedad`: Fecha de ingreso (formato: YYYY-MM-DD)

### 8. 👥 Gestión de Clientes

#### 8.1 Listar Clientes
```http
GET http://localhost:8080/api/clientes
Authorization: Bearer {token}
```

#### 8.2 Crear Cliente
```http
POST http://localhost:8080/api/clientes
Authorization: Bearer {token}
Content-Type: application/json

{
  "nombre": "Ana García",
  "cedula": "87654321",
  "telefono": "3007654321",
  "correo": "ana.garcia@email.com"
}
```

### 9. 🏢 Gestión de Proveedores

#### 9.1 Listar Proveedores
```http
GET http://localhost:8080/api/proveedores
Authorization: Bearer {token}
```

#### 9.2 Crear Proveedor
```http
POST http://localhost:8080/api/proveedores
Authorization: Bearer {token}
Content-Type: application/json

{
  "nombre": "Distribuidora ABC",
  "ubicacion": "Bogotá, Colombia",
  "antiguedad": "2018-01-01",
  "ultimaEntrega": "2024-01-10",
  "nit": "900123456-1",
  "telefono": "6012345678",
  "correo": "contacto@distribuidoraabc.com"
}
```

**📋 Campos obligatorios:**
- `nombre`: Nombre del proveedor
- `ubicacion`: Ubicación del proveedor
- `antiguedad`: Fecha de inicio de relación (formato: YYYY-MM-DD)

**📋 Campos opcionales:**
- `ultimaEntrega`: Fecha de última entrega
- `nit`: NIT del proveedor (debe ser único si se proporciona)
- `telefono`: Teléfono de contacto
- `correo`: Correo electrónico (debe ser único si se proporciona)

### 10. 📊 Gestión de Contabilidad

#### 10.1 Listar Registros Contables
```http
GET http://localhost:8080/api/contabilidad
Authorization: Bearer {token}
```

#### 10.2 Crear Registro Contable General
```http
POST http://localhost:8080/api/contabilidad
Authorization: Bearer {token}
Content-Type: application/json

{
  "tipoMovimiento": "GASTO",
  "fecha": "2024-01-15",
  "gastos": 500000.00,
  "ingresos": 0.00,
  "descripcion": "Compra de suministros de oficina"
}
```

#### 10.3 Crear Registro de Gasto
```http
POST http://localhost:8080/api/contabilidad/gasto
Authorization: Bearer {token}
Content-Type: application/json

{
  "fecha": "2024-01-15",
  "gastos": 250.50,
  "descripcion": "Pago de servicios públicos"
}
```

#### 10.4 Crear Registro de Ingreso
```http
POST http://localhost:8080/api/contabilidad/ingreso
Authorization: Bearer {token}
Content-Type: application/json

{
  "fecha": "2024-01-15",
  "ingresos": 500.00,
  "descripcion": "Venta de productos"
}
```

#### 10.5 Obtener Registros por Tipo de Movimiento
```http
GET http://localhost:8080/api/contabilidad/tipo/GASTO
Authorization: Bearer {token}
```

#### 10.6 Obtener Registros por Rango de Fechas
```http
GET http://localhost:8080/api/contabilidad/fechas?fechaInicio=2024-01-01&fechaFin=2024-01-31
Authorization: Bearer {token}
```

### 11. 📦 Gestión de Inventario

#### 11.1 Listar Inventarios
```http
GET http://localhost:8080/api/inventario
Authorization: Bearer {token}
```

#### 11.2 Crear Inventario (Recomendado)
```http
POST http://localhost:8080/api/inventario
Authorization: Bearer {token}
Content-Type: application/json

{
  "responsableId": 1,
  "descripcion": "Inventario principal de la empresa",
  "productoIds": [1, 2, 3]
}
```

#### 11.3 Crear Inventario (Debug)
```http
POST http://localhost:8080/api/inventario/debug
Authorization: Bearer {token}
Content-Type: application/json

{
  "responsable": {
    "id": 1
  },
  "descripcion": "Inventario debug"
}
```

#### 11.4 Obtener Inventario por ID
```http
GET http://localhost:8080/api/inventario/1
Authorization: Bearer {token}
```

#### 11.5 Obtener Inventarios por Responsable
```http
GET http://localhost:8080/api/inventario/responsable/1
Authorization: Bearer {token}
```

#### 11.6 Agregar Producto al Inventario
```http
PATCH http://localhost:8080/api/inventario/1/agregar-producto?productoId=2
Authorization: Bearer {token}
```

#### 11.7 Remover Producto del Inventario
```http
PATCH http://localhost:8080/api/inventario/1/remover-producto?productoId=2
Authorization: Bearer {token}
```

#### 11.8 Actualizar Responsable del Inventario
```http
PATCH http://localhost:8080/api/inventario/1/responsable?responsableId=2
Authorization: Bearer {token}
```


## 📚 Documentación Swagger

Accede a la documentación interactiva en:
```
http://localhost:8080/swagger-ui.html
```

## 📡 Resumen de Endpoints Principales

### 🔐 Autenticación (Sin token requerido)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/auth/login` | Iniciar sesión |
| `POST` | `/api/auth/register` | Registrar nuevo usuario |
| `POST` | `/api/auth/validate` | Validar token JWT |
| `POST` | `/api/auth/logout` | Cerrar sesión |

### 👥 Usuarios (Requiere token)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/usuarios` | Listar todos los usuarios |
| `GET` | `/api/usuarios/{id}` | Obtener usuario por ID |
| `POST` | `/api/usuarios` | Crear usuario (admin) |
| `PUT` | `/api/usuarios/{id}` | Actualizar usuario |
| `DELETE` | `/api/usuarios/{id}` | Eliminar usuario |

### 🏷️ Roles (Requiere token)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/roles` | Listar roles disponibles |
| `GET` | `/api/roles/{id}` | Obtener rol por ID |
| `POST` | `/api/roles` | Crear nuevo rol |
| `PUT` | `/api/roles/{id}` | Actualizar rol |
| `DELETE` | `/api/roles/{id}` | Eliminar rol |

### 🛍️ Productos (Requiere token)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/productos` | Listar productos |
| `GET` | `/api/productos/{id}` | Obtener producto por ID |
| `POST` | `/api/productos` | Crear producto |
| `PUT` | `/api/productos/{id}` | Actualizar producto |
| `DELETE` | `/api/productos/{id}` | Eliminar producto |

### 💰 Ventas (Requiere token)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/ventas` | Listar ventas |
| `GET` | `/api/ventas/{id}` | Obtener venta por ID |
| `POST` | `/api/ventas` | Crear venta |
| `PUT` | `/api/ventas/{id}` | Actualizar venta |
| `DELETE` | `/api/ventas/{id}` | Eliminar venta |

### 📦 Inventario (Requiere token)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/inventario` | Listar inventarios |
| `GET` | `/api/inventario/{id}` | Obtener inventario por ID |
| `POST` | `/api/inventario` | Crear inventario (DTO) |
| `POST` | `/api/inventario/debug` | Crear inventario (entidad) |
| `PATCH` | `/api/inventario/{id}/agregar-producto` | Agregar producto |
| `PATCH` | `/api/inventario/{id}/remover-producto` | Remover producto |

### 📊 Contabilidad (Requiere token)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/contabilidad` | Listar registros contables |
| `POST` | `/api/contabilidad` | Crear registro contable |
| `POST` | `/api/contabilidad/gasto` | Crear gasto |
| `POST` | `/api/contabilidad/ingreso` | Crear ingreso |
| `GET` | `/api/contabilidad/tipo/{tipo}` | Obtener por tipo |
| `GET` | `/api/contabilidad/fechas` | Obtener por rango de fechas |

## 🛠️ Tecnologías Utilizadas

- **Spring Boot 3.5.5**
- **Spring Security** - Autenticación y autorización
- **JWT** - Tokens de autenticación
- **JPA/Hibernate** - ORM
- **MySQL** - Base de datos
- **Flyway** - Migraciones de BD
- **Maven** - Gestión de dependencias
- **Jackson** - Procesamiento JSON

## 🗄️ Estructura de Base de Datos

### 📊 Relaciones Entre Tablas

El sistema utiliza una base de datos MySQL con **13 tablas principales** y **4 tablas de relación** que forman un esquema completo de gestión empresarial.

#### 🔐 **Sistema de Autenticación y Autorización**

| Tabla | Relación | Tabla Relacionada | Tipo | Descripción |
|-------|----------|-------------------|------|-------------|
| `usuarios` | ↔ | `roles` | Many-to-Many | Un usuario puede tener múltiples roles |
| `roles` | ↔ | `permisos` | Many-to-Many | Un rol puede tener múltiples permisos |

**Tablas de Relación:**
- `usuario_roles`: Conecta usuarios con sus roles asignados
- `rol_permisos`: Conecta roles con sus permisos específicos

#### 👥 **Módulo de Personas**

| Tabla | Relación | Tabla Relacionada | Tipo | Descripción |
|-------|----------|-------------------|------|-------------|
| `empleados` | → | `inventario` | One-to-Many | Un empleado puede ser responsable de múltiples inventarios |
| `empleados` | → | `ventas` | One-to-Many | Un empleado puede realizar múltiples ventas |
| `clientes` | → | `ventas` | One-to-Many | Un cliente puede realizar múltiples compras |

#### 📦 **Módulo de Productos e Inventario**

| Tabla | Relación | Tabla Relacionada | Tipo | Descripción |
|-------|----------|-------------------|------|-------------|
| `productos` | ↔ | `inventario` | Many-to-Many | Un producto puede estar en múltiples inventarios |
| `productos` | ↔ | `ventas` | Many-to-Many | Un producto puede ser vendido en múltiples ventas |
| `productos` | → | `detalle_venta` | One-to-Many | Un producto puede tener múltiples detalles de venta |

**Tablas de Relación:**
- `inventario_productos`: Conecta inventarios con productos
- `venta_productos`: Conecta ventas con productos

#### 💰 **Módulo de Ventas**

| Tabla | Relación | Tabla Relacionada | Tipo | Descripción |
|-------|----------|-------------------|------|-------------|
| `ventas` | → | `detalle_venta` | One-to-Many | Una venta puede tener múltiples detalles |
| `ventas` | → | `clientes` | Many-to-One | Una venta pertenece a un cliente |
| `ventas` | → | `empleados` | Many-to-One | Una venta es realizada por un empleado |

### 🔗 **Diagrama de Relaciones Principales**

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   USUARIOS  │◄──►│    ROLES    │◄──►│  PERMISOS   │
└─────────────┘    └─────────────┘    └─────────────┘
       │                   │                   │
       │                   │                   │
       ▼                   ▼                   ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  EMPLEADOS  │    │  INVENTARIO │    │  PRODUCTOS  │
└─────────────┘    └─────────────┘    └─────────────┘
       │                   │                   │
       │                   │                   │
       ▼                   ▼                   ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   CLIENTES  │    │    VENTAS   │    │DETALLE_VENTA│
└─────────────┘    └─────────────┘    └─────────────┘
       │                   │                   │
       │                   │                   │
       ▼                   ▼                   ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ PROVEEDORES │    │CONTABILIDAD │    │             │
└─────────────┘    └─────────────┘    └─────────────┘
```

### 📋 **Resumen de Relaciones por Módulo**

#### **🔐 Autenticación (3 tablas + 2 relaciones)**
- **usuarios** ↔ **roles** (Many-to-Many)
- **roles** ↔ **permisos** (Many-to-Many)

#### **👥 Gestión de Personas (3 tablas)**
- **empleados** → **inventario** (One-to-Many)
- **empleados** → **ventas** (One-to-Many)
- **clientes** → **ventas** (One-to-Many)

#### **📦 Productos e Inventario (3 tablas + 1 relación)**
- **productos** ↔ **inventario** (Many-to-Many)
- **empleados** → **inventario** (One-to-Many)

#### **💰 Ventas (3 tablas + 1 relación)**
- **ventas** → **detalle_venta** (One-to-Many)
- **productos** ↔ **ventas** (Many-to-Many)
- **productos** → **detalle_venta** (One-to-Many)

#### **📊 Contabilidad (1 tabla)**
- **contabilidad** (tabla independiente)

### 🎯 **Características de las Relaciones**

#### **Integridad Referencial**
- **Foreign Keys**: Todas las relaciones tienen claves foráneas
- **CASCADE**: Eliminación en cascada para relaciones dependientes
- **RESTRICT**: Prevención de eliminación si hay dependencias

#### **Optimizaciones**
- **Índices**: En todas las claves foráneas
- **Únicos**: En campos críticos (user, email, cedula, nit)
- **Timestamps**: Auditoría automática en todas las tablas

#### **Tipos de Relación**
- **One-to-Many**: 8 relaciones principales
- **Many-to-Many**: 4 relaciones con tablas intermedias
- **One-to-One**: 0 relaciones (diseño normalizado)

### 📊 **Estadísticas de la Base de Datos**

| Métrica | Cantidad |
|---------|----------|
| **Tablas Principales** | 13 |
| **Tablas de Relación** | 4 |
| **Total de Tablas** | 17 |
| **Relaciones One-to-Many** | 8 |
| **Relaciones Many-to-Many** | 4 |
| **Campos Totales** | 80+ |
| **Foreign Keys** | 15+ |
| **Índices Únicos** | 10+ |

### 🚀 **Beneficios del Diseño**

✅ **Normalización**: Evita redundancia de datos
✅ **Integridad**: Relaciones consistentes y válidas
✅ **Escalabilidad**: Preparado para crecimiento
✅ **Mantenibilidad**: Estructura clara y organizada
✅ **Performance**: Índices optimizados para consultas
✅ **Flexibilidad**: Relaciones que permiten múltiples escenarios


## 📝 Notas Importantes

- **Usuario admin por defecto**: `admin` / `admin123`
- **Token JWT**: Válido por 24 horas
- **Base de datos**: Se inicializa automáticamente
- **Puerto**: 8080 (configurable en `application.properties`)

## 🎯 Estado Actual del Sistema

### ✅ Funcionalidades Completamente Operativas
- **Autenticación y Autorización**: Login, registro, validación de tokens
- **Gestión de Usuarios**: CRUD completo con roles y permisos
- **Gestión de Roles**: Creación, asignación y modificación de roles
- **Gestión de Permisos**: Sistema granular de permisos por módulo
- **API REST**: Todos los endpoints funcionando correctamente
- **Base de Datos**: Inicialización automática y migraciones
- **Documentación**: Swagger UI disponible y actualizada

### 🔧 Problemas Resueltos Recientemente
1. **Referencias Circulares**: JSON limpio sin bucles infinitos
2. **LazyInitializationException**: Relaciones optimizadas
3. **Inicialización de Admin**: Usuario admin siempre disponible
4. **Seguridad**: Contraseñas ocultas en respuestas
5. **CORS**: Soporte completo para aplicaciones frontend
6. **Validaciones**: Registro de usuarios mejorado
7. **Actualización de Usuarios**: DTO específico para evitar problemas de serialización
8. **Creación de Productos**: Soporte completo para fechas Java 8 (LocalDate)
9. **Creación de Ventas**: DTO específico con validaciones robustas
10. **Creación de Empleados**: Validaciones completas y verificaciones de unicidad
11. **Creación de Proveedores**: Validaciones mejoradas y manejo de errores
12. **Creación de Registros Contables**: Validaciones de campos obligatorios
13. **Gestión de Inventario**: DTO específico y endpoint de debugging

### 🚀 Listo para Producción
El sistema está completamente funcional y listo para ser utilizado en un entorno de producción con las siguientes características:
- Seguridad robusta con JWT
- Manejo de errores mejorado
- Logging detallado para monitoreo
- API REST bien documentada
- Base de datos optimizada

## 🤝 Contribución

1. Fork el proyecto
2. Crear rama para feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para detalles.

---

**¡Disfruta probando la API! 🚀**
