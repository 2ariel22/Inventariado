# Sistema de Inventariado - API REST

## 📋 Descripción

Sistema de gestión de inventario desarrollado con Spring Boot que permite administrar productos, ventas, empleados, clientes, proveedores y contabilidad. Incluye autenticación JWT, roles y permisos granulares.

## 🚀 Inicio Rápido

### Prerrequisitos
- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Insomnia (o cualquier cliente REST)

### Configuración Inicial

1. **Clonar el repositorio**
```bash
git clone <tu-repositorio>
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

## 🧪 Guía de Pruebas con Insomnia

### Configuración de Insomnia

1. **Crear nueva colección** llamada "Inventariado API"
2. **Configurar variable de entorno**:
   - `base_url`: `http://localhost:8080`
   - `token`: (se llenará automáticamente)

### 1. 🔐 Autenticación

#### 1.1 Login como Administrador
```http
POST {{base_url}}/api/auth/login
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

#### 1.2 Registrar Nuevo Usuario
```http
POST {{base_url}}/api/auth/register
Content-Type: application/json

{
  "user": "vendedor1",
  "password": "123456",
  "email": "vendedor1@empresa.com",
  "nombre": "Juan",
  "apellido": "Vendedor"
}
```

#### 1.3 Validar Token
```http
GET {{base_url}}/api/auth/validate
Authorization: Bearer {{token}}
```

#### 1.4 Logout
```http
POST {{base_url}}/api/auth/logout
Authorization: Bearer {{token}}
```

### 2. 👥 Gestión de Usuarios

#### 2.1 Listar Todos los Usuarios
```http
GET {{base_url}}/api/usuarios
Authorization: Bearer {{token}}
```

#### 2.2 Obtener Usuario por ID
```http
GET {{base_url}}/api/usuarios/1
Authorization: Bearer {{token}}
```

#### 2.3 Crear Usuario
```http
POST {{base_url}}/api/usuarios
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "user": "contador1",
  "password": "123456",
  "email": "contador1@empresa.com",
  "nombre": "María",
  "apellido": "Contador",
  "activo": true
}
```

#### 2.4 Actualizar Usuario
```http
PUT {{base_url}}/api/usuarios/2
Authorization: Bearer {{token}}
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
DELETE {{base_url}}/api/usuarios/2
Authorization: Bearer {{token}}
```

### 3. 🏷️ Gestión de Roles

#### 3.1 Listar Roles
```http
GET {{base_url}}/api/roles
Authorization: Bearer {{token}}
```

#### 3.2 Crear Rol
```http
POST {{base_url}}/api/roles
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "nombre": "SUPERVISOR",
  "descripcion": "Supervisor de tienda"
}
```

#### 3.3 Asignar Permisos a Rol
```http
PUT {{base_url}}/api/roles/5/permisos
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "permisosIds": [1, 2, 3, 4, 5, 6]
}
```

### 4. 🔐 Gestión de Permisos

#### 4.1 Listar Permisos
```http
GET {{base_url}}/api/permisos
Authorization: Bearer {{token}}
```

#### 4.2 Crear Permiso
```http
POST {{base_url}}/api/permisos
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "nombre": "REPORTES_GENERAR",
  "descripcion": "Generar reportes",
  "recurso": "REPORTES",
  "accion": "GENERAR"
}
```

### 5. 📦 Gestión de Productos

#### 5.1 Listar Productos
```http
GET {{base_url}}/api/productos
Authorization: Bearer {{token}}
```

#### 5.2 Crear Producto
```http
POST {{base_url}}/api/productos
Authorization: Bearer {{token}}
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
PUT {{base_url}}/api/productos/1
Authorization: Bearer {{token}}
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
GET {{base_url}}/api/ventas
Authorization: Bearer {{token}}
```

#### 6.2 Crear Venta
```http
POST {{base_url}}/api/ventas
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "fecha": "2024-01-15",
  "valorVenta": 1600000.00,
  "productosIds": [1],
  "clienteId": 1
}
```

### 7. 👨‍💼 Gestión de Empleados

#### 7.1 Listar Empleados
```http
GET {{base_url}}/api/empleados
Authorization: Bearer {{token}}
```

#### 7.2 Crear Empleado
```http
POST {{base_url}}/api/empleados
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "nombre": "Carlos",
  "cedula": "12345678",
  "edad": 30,
  "telefono": "3001234567",
  "correo": "carlos@empresa.com",
  "antiguedad": 2
}
```

### 8. 👥 Gestión de Clientes

#### 8.1 Listar Clientes
```http
GET {{base_url}}/api/clientes
Authorization: Bearer {{token}}
```

#### 8.2 Crear Cliente
```http
POST {{base_url}}/api/clientes
Authorization: Bearer {{token}}
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
GET {{base_url}}/api/proveedores
Authorization: Bearer {{token}}
```

#### 9.2 Crear Proveedor
```http
POST {{base_url}}/api/proveedores
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "ubicacion": "Bogotá, Colombia",
  "antiguedad": 5,
  "ultimaEntrega": "2024-01-10"
}
```

### 10. 📊 Gestión de Contabilidad

#### 10.1 Listar Registros Contables
```http
GET {{base_url}}/api/contabilidad
Authorization: Bearer {{token}}
```

#### 10.2 Crear Registro Contable
```http
POST {{base_url}}/api/contabilidad
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "gastos": 500000.00,
  "ingresos": 2000000.00,
  "fecha": "2024-01-15"
}
```

### 11. 📦 Gestión de Inventario

#### 11.1 Listar Inventarios
```http
GET {{base_url}}/api/inventarios
Authorization: Bearer {{token}}
```

#### 11.2 Crear Inventario
```http
POST {{base_url}}/api/inventarios
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "productosIds": [1, 2],
  "responsableId": 1,
  "ultimaActualizacion": "2024-01-15"
}
```

## 🔧 Endpoints de Diagnóstico

### Verificar Estado del Sistema
```http
GET {{base_url}}/api/init/status
```

### Diagnóstico de Autenticación
```http
GET {{base_url}}/api/auth/debug
```

## 📚 Documentación Swagger

Accede a la documentación interactiva en:
```
http://localhost:8080/swagger-ui.html
```

## 🛠️ Tecnologías Utilizadas

- **Spring Boot 3.5.5**
- **Spring Security** - Autenticación y autorización
- **JWT** - Tokens de autenticación
- **JPA/Hibernate** - ORM
- **MySQL** - Base de datos
- **Flyway** - Migraciones de BD
- **Maven** - Gestión de dependencias
- **Jackson** - Procesamiento JSON

## 🚨 Solución de Problemas

### Error 500 en Login
1. Verificar que la aplicación esté ejecutándose
2. Revisar logs de la consola
3. Usar endpoint de diagnóstico: `GET /api/auth/debug`

### Error de Conexión a BD
1. Verificar que MySQL esté ejecutándose
2. Confirmar credenciales en `application.properties`
3. Verificar que la BD `inventory` exista

### Token Expirado
1. Hacer login nuevamente
2. Actualizar variable `token` en Insomnia

## 📝 Notas Importantes

- **Usuario admin por defecto**: `admin` / `admin123`
- **Token JWT**: Válido por 24 horas
- **Base de datos**: Se inicializa automáticamente
- **Puerto**: 8080 (configurable en `application.properties`)

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
