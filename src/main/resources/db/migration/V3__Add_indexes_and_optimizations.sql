-- =====================================================
-- V3: Agregar índices y optimizaciones
-- =====================================================

-- Índices para tabla usuarios
CREATE INDEX idx_usuarios_user ON usuarios(user);
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_activo ON usuarios(activo);

-- Índices para tabla empleados
CREATE INDEX idx_empleados_cedula ON empleados(cedula);
CREATE INDEX idx_empleados_correo ON empleados(correo);
CREATE INDEX idx_empleados_nombre ON empleados(nombre);

-- Índices para tabla clientes
CREATE INDEX idx_clientes_cedula ON clientes(cedula);
CREATE INDEX idx_clientes_correo ON clientes(correo);
CREATE INDEX idx_clientes_nombre ON clientes(nombre);

-- Índices para tabla proveedores
CREATE INDEX idx_proveedores_nit ON proveedores(nit);
CREATE INDEX idx_proveedores_correo ON proveedores(correo);
CREATE INDEX idx_proveedores_nombre ON proveedores(nombre);

-- Índices para tabla productos
CREATE INDEX idx_productos_nombre ON productos(nombre);
CREATE INDEX idx_productos_categoria ON productos(categoria);
CREATE INDEX idx_productos_stock ON productos(stock);
CREATE INDEX idx_productos_precio ON productos(precio);
CREATE INDEX idx_productos_fecha_vencimiento ON productos(fecha_vencimiento);

-- Índices para tabla ventas
CREATE INDEX idx_ventas_fecha ON ventas(fecha);
CREATE INDEX idx_ventas_estado ON ventas(estado);
CREATE INDEX idx_ventas_cliente_id ON ventas(cliente_id);
CREATE INDEX idx_ventas_vendedor_id ON ventas(vendedor_id);
CREATE INDEX idx_ventas_valor_venta ON ventas(valor_venta);

-- Índices para tabla contabilidad
CREATE INDEX idx_contabilidad_fecha ON contabilidad(fecha);
CREATE INDEX idx_contabilidad_tipo_movimiento ON contabilidad(tipo_movimiento);
CREATE INDEX idx_contabilidad_gastos ON contabilidad(gastos);
CREATE INDEX idx_contabilidad_ingresos ON contabilidad(ingresos);

-- Índices para tabla inventario
CREATE INDEX idx_inventario_responsable_id ON inventario(responsable_id);
CREATE INDEX idx_inventario_ultima_actualizacion ON inventario(ultima_actualizacion);

-- Índices para tabla detalle_venta
CREATE INDEX idx_detalle_venta_venta_id ON detalle_venta(venta_id);
CREATE INDEX idx_detalle_venta_producto_id ON detalle_venta(producto_id);
CREATE INDEX idx_detalle_venta_cantidad ON detalle_venta(cantidad);

-- Índices para tablas de relación
CREATE INDEX idx_usuario_roles_usuario_id ON usuario_roles(usuario_id);
CREATE INDEX idx_usuario_roles_rol_id ON usuario_roles(rol_id);
CREATE INDEX idx_rol_permisos_rol_id ON rol_permisos(rol_id);
CREATE INDEX idx_rol_permisos_permiso_id ON rol_permisos(permiso_id);
CREATE INDEX idx_inventario_productos_inventario_id ON inventario_productos(inventario_id);
CREATE INDEX idx_inventario_productos_producto_id ON inventario_productos(producto_id);
CREATE INDEX idx_venta_productos_venta_id ON venta_productos(venta_id);
CREATE INDEX idx_venta_productos_producto_id ON venta_productos(producto_id);

-- Agregar restricciones de integridad adicionales
ALTER TABLE productos ADD CONSTRAINT chk_stock_positive CHECK (stock >= 0);
ALTER TABLE productos ADD CONSTRAINT chk_precio_positive CHECK (precio > 0);
ALTER TABLE ventas ADD CONSTRAINT chk_valor_venta_positive CHECK (valor_venta >= 0);
ALTER TABLE detalle_venta ADD CONSTRAINT chk_cantidad_positive CHECK (cantidad > 0);
ALTER TABLE detalle_venta ADD CONSTRAINT chk_precio_unitario_positive CHECK (precio_unitario > 0);
ALTER TABLE detalle_venta ADD CONSTRAINT chk_subtotal_positive CHECK (subtotal > 0);
ALTER TABLE contabilidad ADD CONSTRAINT chk_gastos_non_negative CHECK (gastos >= 0);
ALTER TABLE contabilidad ADD CONSTRAINT chk_ingresos_non_negative CHECK (ingresos >= 0);
ALTER TABLE empleados ADD CONSTRAINT chk_edad_positive CHECK (edad > 0);

-- Agregar comentarios a las tablas para documentación
ALTER TABLE usuarios COMMENT = 'Tabla de usuarios del sistema con autenticación';
ALTER TABLE roles COMMENT = 'Tabla de roles para control de acceso';
ALTER TABLE permisos COMMENT = 'Tabla de permisos específicos del sistema';
ALTER TABLE empleados COMMENT = 'Tabla de empleados de la empresa';
ALTER TABLE clientes COMMENT = 'Tabla de clientes del negocio';
ALTER TABLE proveedores COMMENT = 'Tabla de proveedores de productos';
ALTER TABLE productos COMMENT = 'Tabla de productos en inventario';
ALTER TABLE inventario COMMENT = 'Tabla de inventarios con productos';
ALTER TABLE ventas COMMENT = 'Tabla de ventas realizadas';
ALTER TABLE detalle_venta COMMENT = 'Tabla de detalles de cada venta';
ALTER TABLE contabilidad COMMENT = 'Tabla de registros contables';

-- Crear vista para reportes de ventas
CREATE VIEW vista_ventas_detalladas AS
SELECT 
    v.id as venta_id,
    v.fecha,
    v.valor_venta,
    v.estado,
    c.nombre as cliente_nombre,
    c.cedula as cliente_cedula,
    e.nombre as vendedor_nombre,
    e.cedula as vendedor_cedula,
    COUNT(dv.producto_id) as cantidad_productos,
    SUM(dv.cantidad) as total_unidades
FROM ventas v
JOIN clientes c ON v.cliente_id = c.id
JOIN empleados e ON v.vendedor_id = e.id
LEFT JOIN detalle_venta dv ON v.id = dv.venta_id
GROUP BY v.id, v.fecha, v.valor_venta, v.estado, c.nombre, c.cedula, e.nombre, e.cedula;

-- Crear vista para productos con stock bajo
CREATE VIEW vista_productos_stock_bajo AS
SELECT 
    p.id,
    p.nombre,
    p.categoria,
    p.stock,
    p.precio,
    p.fecha_vencimiento,
    CASE 
        WHEN p.fecha_vencimiento < CURDATE() THEN 'VENCIDO'
        WHEN p.fecha_vencimiento < DATE_ADD(CURDATE(), INTERVAL 30 DAY) THEN 'POR_VENCIR'
        ELSE 'VIGENTE'
    END as estado_vencimiento
FROM productos p
WHERE p.stock < 10
ORDER BY p.stock ASC, p.fecha_vencimiento ASC;

-- Crear vista para resumen contable mensual
CREATE VIEW vista_resumen_contable_mensual AS
SELECT 
    YEAR(fecha) as año,
    MONTH(fecha) as mes,
    SUM(ingresos) as total_ingresos,
    SUM(gastos) as total_gastos,
    SUM(ingresos) - SUM(gastos) as utilidad_neta,
    COUNT(CASE WHEN tipo_movimiento = 'INGRESO' THEN 1 END) as cantidad_ingresos,
    COUNT(CASE WHEN tipo_movimiento = 'GASTO' THEN 1 END) as cantidad_gastos
FROM contabilidad
GROUP BY YEAR(fecha), MONTH(fecha)
ORDER BY año DESC, mes DESC;
