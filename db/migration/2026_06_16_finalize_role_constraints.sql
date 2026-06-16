-- Migration script: Aplicar FK y NOT NULL a users.role_id (paso final)
-- Última revisión: 2026-06-16
-- INSTRUCCIONES:
--  - Ejecutar DESPUÉS de validar que todos los usuarios tienen role_id.
--  - Hacer backup antes de ejecutar.
--  - Este script hace role_id NOT NULL y añade constraint FK.

BEGIN;

-- 1) Verificar que no hay usuarios sin role_id (debería retornar 0 filas)
-- SELECT COUNT(*) FROM users WHERE role_id IS NULL;

-- 2) Establecer role_id como NOT NULL
ALTER TABLE users ALTER COLUMN role_id SET NOT NULL;

-- 3) Añadir constraint FK si no existe
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.table_constraints
    WHERE table_name = 'users' AND constraint_name = 'fk_users_roles'
  ) THEN
    ALTER TABLE users ADD CONSTRAINT fk_users_roles FOREIGN KEY (role_id) REFERENCES roles(id);
  END IF;
EXCEPTION WHEN others THEN
  RAISE NOTICE 'No se pudo añadir FK fk_users_roles: %', SQLERRM;
END $$;

-- 4) (Opcional) Una vez validado en producción, considerar eliminar columna legacy `rol`
-- ALTER TABLE users DROP COLUMN rol;

COMMIT;

-- Validaciones post-aplicación:
--  - SELECT COUNT(*) FROM users WHERE role_id IS NULL;  -- Debería retornar 0
--  - SELECT * FROM users u JOIN roles r ON u.role_id = r.id LIMIT 5;  -- Verificar JOIN
