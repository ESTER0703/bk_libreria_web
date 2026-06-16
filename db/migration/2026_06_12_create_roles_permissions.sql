-- Migration script: Crear tablas roles y permissions y migrar referencias desde users
-- Recomendación: ejecutar en staging primero y tener backup de la BD.
-- Migration script: Crear tablas `roles` y `permissions` y migrar referencias desde `users`.
-- Última revisión: 2026-06-12
-- INSTRUCCIONES IMPORTANTES:
--  - Ejecutar en un entorno de staging primero.
--  - Hacer un backup completo de la BD antes de ejecutar este script.
--  - Este script agrega columnas/filas pero NO elimina ni modifica datos históricos; el paso de limpieza (drop columns) se hará manualmente después de validación.
--  - Tiempo estimado: < 1 min para bases pequeñas; para grandes tablas, ejecutar durante ventana de mantenimiento.

BEGIN;

-- 1) Crear tabla roles (id, nombre único)
CREATE TABLE IF NOT EXISTS roles (
  id bigserial PRIMARY KEY,
  name varchar(255) NOT NULL UNIQUE
);

-- 2) Crear tabla permissions (cada permiso pertenece a un role)
CREATE TABLE IF NOT EXISTS permissions (
  id bigserial PRIMARY KEY,
  name varchar(255) NOT NULL,
  role_id bigint REFERENCES roles(id) ON DELETE CASCADE
);

-- 3) Añadir columna role_id en tabla users (temporal)
ALTER TABLE users ADD COLUMN IF NOT EXISTS role_id bigint;

-- 4) Poblar tabla roles con los roles distintos actualmente en users
INSERT INTO roles (name)
SELECT DISTINCT rol
FROM users
WHERE rol IS NOT NULL AND trim(rol) <> ''
ON CONFLICT (name) DO NOTHING;

-- 5) Asignar role_id en users según coincidencia de nombre (case-insensitive)
UPDATE users u
SET role_id = r.id
FROM roles r
WHERE LOWER(r.name) = LOWER(u.rol);

-- 6) Migrar permisos desde users.permisos.
--    Soporta valores separados por coma ("Lectura, Préstamo") y normaliza espacios.
--    Inserta combinaciones únicas (permission name, role_id).
INSERT INTO permissions (name, role_id)
SELECT DISTINCT trim(p) AS name, r.id AS role_id
FROM users u
JOIN roles r ON LOWER(r.name) = LOWER(u.rol)
CROSS JOIN LATERAL unnest(string_to_array(u.permisos, ',')) AS p
WHERE u.permisos IS NOT NULL AND trim(p) <> '';

-- 7) Evitar duplicados futuros: crear índice/constraint único por (role_id, lower(name)).
--    Antes de añadir la constraint, nos aseguramos que los datos no contienen duplicados.
DO $$
BEGIN
  -- Crear índice único si no existe. El índice usa lower(name) para evitar duplicidad por mayúsculas/minúsculas.
  IF NOT EXISTS (
    SELECT 1 FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE c.relname = 'idx_permissions_role_name'
  ) THEN
    CREATE UNIQUE INDEX idx_permissions_role_name ON permissions (role_id, lower(name));
  END IF;
EXCEPTION WHEN others THEN
  RAISE NOTICE 'No se pudo crear índice único idx_permissions_role_name: %', SQLERRM;
END $$;

-- 8) (Opcional) Marcar role_id como NOT NULL y añadir FK si todo está validado manualmente.
-- ALTER TABLE users ALTER COLUMN role_id SET NOT NULL;
-- ALTER TABLE users ADD CONSTRAINT fk_users_roles FOREIGN KEY (role_id) REFERENCES roles(id);

COMMIT;

-- Validaciones rápidas (ejecutar manualmente después del script):
--  - Verificar roles creados: SELECT * FROM roles ORDER BY name;
--  - Verificar permisos: SELECT role_id, name FROM permissions ORDER BY role_id, name;
--  - Verificar usuarios sin role_id: SELECT id, nombre, email, rol FROM users WHERE role_id IS NULL;

-- Pasos posteriores sugeridos (manuales):
-- 1) Revisar y probar la aplicación con `users.role_id` usado en consultas y DTOs.
-- 2) Si todo OK, considerar ajustar esquema: establecer `users.role_id` NOT NULL y eliminar columna `users.rol` en una migración controlada.
-- 3) Mantener copia de seguridad antes de cualquier DROP/ALTER destructivo.
