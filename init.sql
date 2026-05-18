CREATE TABLE IF NOT EXISTS dim_cliente (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(150) UNIQUE NOT NULL,
    correo VARCHAR(150)
);
CREATE TABLE IF NOT EXISTS dim_personal (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(150) UNIQUE NOT NULL,
    rol VARCHAR(50)
);
CREATE TABLE IF NOT EXISTS dim_estado (
    id SERIAL PRIMARY KEY,
    nombre_estado VARCHAR(50) UNIQUE NOT NULL
);
CREATE TABLE IF NOT EXISTS fact_ordenes (
    orden_code VARCHAR(50) PRIMARY KEY,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cliente_id INTEGER REFERENCES dim_cliente(id),
    empleado_id INTEGER REFERENCES dim_personal(id),
    encargado_id INTEGER REFERENCES dim_personal(id),
    estado_id INTEGER REFERENCES dim_estado(id),
    vehiculo_placa VARCHAR(20),
    descripcion_problema TEXT,
    total NUMERIC(10, 2) DEFAULT 0.00
);
INSERT INTO dim_estado (nombre_estado) VALUES ('PENDIENTE'), ('EN_PROCESO'), ('ESPERANDO_CONFIRMACION'), ('FINALIZADO'), ('CON_PROBLEMAS'), ('CANCELADO') ON CONFLICT DO NOTHING;
CREATE OR REPLACE FUNCTION sp_registrar_orden_analitica(
    p_orden_code VARCHAR,
    p_estado VARCHAR,
    p_cliente_nombre VARCHAR,
    p_cliente_correo VARCHAR,
    p_empleado_nombre VARCHAR,
    p_encargado_nombre VARCHAR,
    p_vehiculo_placa VARCHAR,
    p_total NUMERIC,
    p_problema TEXT
) RETURNS VOID AS $$
DECLARE
    v_cliente_id INTEGER;
    v_empleado_id INTEGER;
    v_encargado_id INTEGER;
    v_estado_id INTEGER;
BEGIN
    INSERT INTO dim_cliente (nombre, correo) VALUES (p_cliente_nombre, p_cliente_correo) ON CONFLICT (nombre) DO UPDATE SET correo = p_cliente_correo RETURNING id INTO v_cliente_id;
    INSERT INTO dim_personal (nombre, rol) VALUES (p_empleado_nombre, 'EMPLEADO') ON CONFLICT (nombre) DO NOTHING;
    SELECT id INTO v_empleado_id FROM dim_personal WHERE nombre = p_empleado_nombre;
    INSERT INTO dim_personal (nombre, rol) VALUES (p_encargado_nombre, 'ENCARGADO') ON CONFLICT (nombre) DO NOTHING;
    SELECT id INTO v_encargado_id FROM dim_personal WHERE nombre = p_encargado_nombre;
    SELECT id INTO v_estado_id FROM dim_estado WHERE nombre_estado = p_estado;
    INSERT INTO fact_ordenes (orden_code, fecha_creacion, cliente_id, empleado_id, encargado_id, estado_id, vehiculo_placa, descripcion_problema, total)
    VALUES (p_orden_code, CURRENT_TIMESTAMP, v_cliente_id, v_empleado_id, v_encargado_id, v_estado_id, p_vehiculo_placa, p_problema, p_total)
    ON CONFLICT (orden_code) DO UPDATE SET 
        estado_id = EXCLUDED.estado_id,
        descripcion_problema = EXCLUDED.descripcion_problema,
        total = EXCLUDED.total;
END;
$$ LANGUAGE plpgsql;
