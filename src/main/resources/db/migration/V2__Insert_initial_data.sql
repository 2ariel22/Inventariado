-- =====================================================
-- V2: Insertar datos iniciales del sistema
-- =====================================================

-- Insertar permisos básicos
INSERT INTO permisos (nombre, descripcion, recurso, accion) VALUES
-- Permisos de Productos
('PRODUCTOS_CREAR', 'Crear productos', 'PRODUCTOS', 'CREAR'),
('PRODUCTOS_LEER', 'Leer productos', 'PRODUCTOS', 'LEER'),
('PRODUCTOS_ACTUALIZAR', 'Actualizar productos', 'PRODUCTOS', 'ACTUALIZAR'),
('PRODUCTOS_ELIMINAR', 'Eliminar productos', 'PRODUCTOS', 'ELIMINAR'),

-- Permisos de Ventas
('VENTAS_CREAR', 'Crear ventas', 'VENTAS', 'CREAR'),
('VENTAS_LEER', 'Leer ventas', 'VENTAS', 'LEER'),
('VENTAS_ACTUALIZAR', 'Actualizar ventas', 'VENTAS', 'ACTUALIZAR'),
('VENTAS_ELIMINAR', 'Eliminar ventas', 'VENTAS', 'ELIMINAR'),

-- Permisos de Inventario
('INVENTARIO_CREAR', 'Crear inventario', 'INVENTARIO', 'CREAR'),
('INVENTARIO_LEER', 'Leer inventario', 'INVENTARIO', 'LEER'),
('INVENTARIO_ACTUALIZAR', 'Actualizar inventario', 'INVENTARIO', 'ACTUALIZAR'),
('INVENTARIO_ELIMINAR', 'Eliminar inventario', 'INVENTARIO', 'ELIMINAR'),

-- Permisos de Usuarios
('USUARIOS_CREAR', 'Crear usuarios', 'USUARIOS', 'CREAR'),
('USUARIOS_LEER', 'Leer usuarios', 'USUARIOS', 'LEER'),
('USUARIOS_ACTUALIZAR', 'Actualizar usuarios', 'USUARIOS', 'ACTUALIZAR'),
('USUARIOS_ELIMINAR', 'Eliminar usuarios', 'USUARIOS', 'ELIMINAR'),

-- Permisos de Contabilidad
('CONTABILIDAD_CREAR', 'Crear registros contables', 'CONTABILIDAD', 'CREAR'),
('CONTABILIDAD_LEER', 'Leer registros contables', 'CONTABILIDAD', 'LEER'),
('CONTABILIDAD_ACTUALIZAR', 'Actualizar registros contables', 'CONTABILIDAD', 'ACTUALIZAR'),
('CONTABILIDAD_ELIMINAR', 'Eliminar registros contables', 'CONTABILIDAD', 'ELIMINAR'),

-- Permisos de Empleados
('EMPLEADOS_CREAR', 'Crear empleados', 'EMPLEADOS', 'CREAR'),
('EMPLEADOS_LEER', 'Leer empleados', 'EMPLEADOS', 'LEER'),
('EMPLEADOS_ACTUALIZAR', 'Actualizar empleados', 'EMPLEADOS', 'ACTUALIZAR'),
('EMPLEADOS_ELIMINAR', 'Eliminar empleados', 'EMPLEADOS', 'ELIMINAR'),

-- Permisos de Clientes
('CLIENTES_CREAR', 'Crear clientes', 'CLIENTES', 'CREAR'),
('CLIENTES_LEER', 'Leer clientes', 'CLIENTES', 'LEER'),
('CLIENTES_ACTUALIZAR', 'Actualizar clientes', 'CLIENTES', 'ACTUALIZAR'),
('CLIENTES_ELIMINAR', 'Eliminar clientes', 'CLIENTES', 'ELIMINAR'),

-- Permisos de Proveedores
('PROVEEDORES_CREAR', 'Crear proveedores', 'PROVEEDORES', 'CREAR'),
('PROVEEDORES_LEER', 'Leer proveedores', 'PROVEEDORES', 'LEER'),
('PROVEEDORES_ACTUALIZAR', 'Actualizar proveedores', 'PROVEEDORES', 'ACTUALIZAR'),
('PROVEEDORES_ELIMINAR', 'Eliminar proveedores', 'PROVEEDORES', 'ELIMINAR'),

-- Permisos de Roles
('ROLES_CREAR', 'Crear roles', 'ROLES', 'CREAR'),
('ROLES_LEER', 'Leer roles', 'ROLES', 'LEER'),
('ROLES_ACTUALIZAR', 'Actualizar roles', 'ROLES', 'ACTUALIZAR'),
('ROLES_ELIMINAR', 'Eliminar roles', 'ROLES', 'ELIMINAR'),

-- Permisos de Permisos
('PERMISOS_CREAR', 'Crear permisos', 'PERMISOS', 'CREAR'),
('PERMISOS_LEER', 'Leer permisos', 'PERMISOS', 'LEER'),
('PERMISOS_ACTUALIZAR', 'Actualizar permisos', 'PERMISOS', 'ACTUALIZAR'),
('PERMISOS_ELIMINAR', 'Eliminar permisos', 'PERMISOS', 'ELIMINAR'),

-- Permiso de Administración
('ADMINISTRAR', 'Administrar sistema', 'SISTEMA', 'ADMINISTRAR');

-- Insertar roles básicos
INSERT INTO roles (nombre, descripcion) VALUES
('ADMIN', 'Administrador del sistema con acceso completo'),
('VENDEDOR', 'Vendedor con acceso a productos, ventas e inventario'),
('CONTADOR', 'Contador con acceso a contabilidad, ventas y productos'),
('GERENTE', 'Gerente con acceso a reportes y gestión general');

-- Asignar permisos al rol ADMIN (todos los permisos)
INSERT INTO rol_permisos (rol_id, permiso_id)
SELECT 1, id FROM permisos;

-- Asignar permisos al rol VENDEDOR
INSERT INTO rol_permisos (rol_id, permiso_id)
SELECT 2, id FROM permisos 
WHERE recurso IN ('PRODUCTOS', 'VENTAS', 'INVENTARIO', 'CLIENTES');

-- Asignar permisos al rol CONTADOR
INSERT INTO rol_permisos (rol_id, permiso_id)
SELECT 3, id FROM permisos 
WHERE recurso IN ('CONTABILIDAD', 'VENTAS', 'PRODUCTOS', 'CLIENTES');

-- Asignar permisos al rol GERENTE
INSERT INTO rol_permisos (rol_id, permiso_id)
SELECT 4, id FROM permisos 
WHERE recurso IN ('PRODUCTOS', 'VENTAS', 'INVENTARIO', 'CLIENTES', 'EMPLEADOS', 'CONTABILIDAD', 'PROVEEDORES')
AND accion IN ('LEER', 'ACTUALIZAR');

-- Crear usuario administrador por defecto
-- La contraseña es 'admin123' encriptada con BCrypt
INSERT INTO usuarios (user, password, email, nombre, apellido, activo) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'admin@inventariado.com', 'Administrador', 'Sistema', TRUE);

-- Asignar rol ADMIN al usuario administrador
INSERT INTO usuario_roles (usuario_id, rol_id) VALUES (1, 1);

-- Insertar algunos empleados de ejemplo
INSERT INTO empleados (nombre, cedula, edad, telefono, correo, antiguedad) VALUES
('Juan Pérez', '12345678', 30, '3001234567', 'juan.perez@empresa.com', '2020-01-15'),
('María González', '87654321', 28, '3007654321', 'maria.gonzalez@empresa.com', '2021-03-20'),
('Carlos López', '11223344', 35, '3001122334', 'carlos.lopez@empresa.com', '2019-06-10');

-- Insertar algunos clientes de ejemplo
INSERT INTO clientes (nombre, cedula, telefono, correo, direccion) VALUES
('Ana Martínez', '99887766', '3009988776', 'ana.martinez@email.com', 'Calle 123 #45-67'),
('Luis Rodríguez', '55443322', '3005544332', 'luis.rodriguez@email.com', 'Carrera 78 #90-12'),
('Sofia Herrera', '77665544', '3007766554', 'sofia.herrera@email.com', 'Avenida 34 #56-78');

-- Insertar algunos proveedores de ejemplo
INSERT INTO proveedores (nombre, ubicacion, antiguedad, ultima_entrega, nit, telefono, correo) VALUES
('Distribuidora ABC', 'Bogotá, Colombia', '2018-01-01', '2024-01-15', '900123456-1', '6012345678', 'contacto@distribuidoraabc.com'),
('Proveedor XYZ', 'Medellín, Colombia', '2019-05-10', '2024-01-10', '900987654-2', '6049876543', 'ventas@proveedorxyz.com'),
('Suministros 123', 'Cali, Colombia', '2020-03-15', '2024-01-05', '900456789-3', '6024567890', 'info@suministros123.com');

-- Insertar algunas categorías de productos de ejemplo
INSERT INTO productos (nombre, fecha_expiracion, fecha_vencimiento, categoria, stock, precio) VALUES
('Laptop Dell Inspiron 15', '2025-12-31', '2026-12-31', 'Tecnología', 10, 2500000.00),
('Mouse Inalámbrico Logitech', '2025-06-30', '2026-06-30', 'Tecnología', 50, 85000.00),
('Teclado Mecánico RGB', '2025-08-15', '2026-08-15', 'Tecnología', 25, 150000.00),
('Monitor 24" Samsung', '2025-10-20', '2026-10-20', 'Tecnología', 15, 1200000.00),
('Café Premium 500g', '2024-12-31', '2025-12-31', 'Alimentos', 100, 25000.00),
('Azúcar 1kg', '2025-06-30', '2026-06-30', 'Alimentos', 200, 3500.00),
('Leche Entera 1L', '2024-02-28', '2024-02-28', 'Alimentos', 150, 4500.00),
('Papel A4 500 hojas', '2026-12-31', '2027-12-31', 'Oficina', 80, 18000.00),
('Bolígrafo Azul', '2025-12-31', '2026-12-31', 'Oficina', 500, 2000.00),
('Cuaderno 100 hojas', '2025-12-31', '2026-12-31', 'Oficina', 120, 8500.00);

-- Crear un inventario inicial
INSERT INTO inventario (descripcion, ultima_actualizacion, responsable_id) VALUES
('Inventario General - Almacén Principal', NOW(), 1),
('Inventario Tecnología - Sección A', NOW(), 2),
('Inventario Alimentos - Sección B', NOW(), 3);

-- Asignar productos al inventario general
INSERT INTO inventario_productos (inventario_id, producto_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10);

-- Asignar productos tecnológicos al inventario de tecnología
INSERT INTO inventario_productos (inventario_id, producto_id) VALUES
(2, 1), (2, 2), (2, 3), (2, 4);

-- Asignar productos alimenticios al inventario de alimentos
INSERT INTO inventario_productos (inventario_id, producto_id) VALUES
(3, 5), (3, 6), (3, 7);

-- Insertar algunas ventas de ejemplo
INSERT INTO ventas (fecha, valor_venta, descripcion, estado, cliente_id, vendedor_id) VALUES
('2024-01-15', 2500000.00, 'Venta de laptop Dell', 'COMPLETADA', 1, 1),
('2024-01-16', 85000.00, 'Venta de mouse inalámbrico', 'COMPLETADA', 2, 2),
('2024-01-17', 150000.00, 'Venta de teclado mecánico', 'PENDIENTE', 3, 1),
('2024-01-18', 1200000.00, 'Venta de monitor Samsung', 'COMPLETADA', 1, 2);

-- Asignar productos a las ventas
INSERT INTO venta_productos (venta_id, producto_id) VALUES
(1, 1),  -- Laptop en venta 1
(2, 2),  -- Mouse en venta 2
(3, 3),  -- Teclado en venta 3
(4, 4);  -- Monitor en venta 4

-- Insertar detalles de venta
INSERT INTO detalle_venta (venta_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
(1, 1, 1, 2500000.00, 2500000.00),
(2, 2, 1, 85000.00, 85000.00),
(3, 3, 1, 150000.00, 150000.00),
(4, 4, 1, 1200000.00, 1200000.00);

-- Insertar algunos registros contables de ejemplo
INSERT INTO contabilidad (gastos, ingresos, fecha, descripcion, tipo_movimiento) VALUES
(0.00, 2500000.00, '2024-01-15', 'Venta de laptop Dell', 'INGRESO'),
(0.00, 85000.00, '2024-01-16', 'Venta de mouse inalámbrico', 'INGRESO'),
(0.00, 1200000.00, '2024-01-18', 'Venta de monitor Samsung', 'INGRESO'),
(500000.00, 0.00, '2024-01-20', 'Compra de productos tecnológicos', 'GASTO'),
(200000.00, 0.00, '2024-01-21', 'Gastos de oficina', 'GASTO'),
(0.00, 150000.00, '2024-01-17', 'Venta de teclado mecánico', 'INGRESO');
