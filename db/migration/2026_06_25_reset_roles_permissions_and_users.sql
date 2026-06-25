-- Reset controlado de roles, permisos y usuarios para dejar el sistema coherente.
-- Objetivo:
--   1) Reconstruir roles y permisos canónicos.
--   2) Mantener al usuario 1-1 como super administrador.
--   3) Dejar usuarios con role_id y rol coherentes para frontend/backend.
--
-- Precaución:
--   Este script NO toca tablas de libros/préstamos. Solo limpia y reconstruye
--   la capa de autenticación/autorización.

BEGIN;

-- 1) Rebuild de roles y permisos canónicos y limpieza total de usuarios.
DELETE FROM users;
DELETE FROM permissions;
DELETE FROM roles;

-- Asegurar IDs canónicos para que frontend y backend compartan el mismo modelo.
INSERT INTO roles (id, name) VALUES
  (2, 'ADMIN'),
  (4, 'BIBLIOTECARIO'),
  (5, 'ALUMNO'),
  (6, 'DOCENTE')
ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name;

INSERT INTO permissions (name, role_id) VALUES
  ('Super Usuario', 2),
  ('Administración', 2),
  ('Gestión Usuarios', 2),
  ('Gestión Roles', 2),
  ('Lectura', 4),
  ('Préstamo', 4),
  ('Devolución', 4),
  ('Lectura', 5),
  ('Préstamo', 5),
  ('Lectura', 6),
  ('Préstamo', 6),
  ('Reserva', 6);

-- 2) Recrear usuarios demo limpios y coherentes con backend/frontend.
INSERT INTO users (id, nombre, email, password, rol, role_id, permisos, estado) VALUES
  (6, 'Administrador', '1-1', '1234', 'ADMIN', 2, 'Super Usuario, Administración, Gestión Usuarios, Gestión Roles', 'Activo'),
  (7, 'Bibliotecario', '2-2', '1234', 'BIBLIOTECARIO', 4, 'Lectura, Préstamo, Devolución', 'Activo'),
  (8, 'Alumno', '3-3', '1234', 'ALUMNO', 5, 'Lectura, Préstamo', 'Activo'),
  (9, 'Docente', '4-4', '1234', 'DOCENTE', 6, 'Lectura, Préstamo, Reserva', 'Activo');

-- 3) Recalcular secuencias para que nuevas inserciones no generen ids conflictivos.
SELECT setval('users_id_seq', COALESCE((SELECT MAX(id) FROM users), 1), true);
SELECT setval('roles_id_seq', COALESCE((SELECT MAX(id) FROM roles), 1), true);
SELECT setval('permissions_id_seq', COALESCE((SELECT MAX(id) FROM permissions), 1), true);

COMMIT;

-- Validación rápida:
-- SELECT id, name FROM roles ORDER BY id;
-- SELECT id, role_id, name FROM permissions ORDER BY role_id, id;
-- SELECT id, nombre, email, role_id, rol, permisos FROM users ORDER BY id;
## PRUEBA DE EJECUCION
postgres=# \c basebiblioteca
You are now connected to database "basebiblioteca" as user "postgres".
basebiblioteca=# DELETE FROM users;
DELETE 6
basebiblioteca=# DELETE FROM permissions;
DELETE 8
basebiblioteca=# DELETE FROM roles;
DELETE 6
basebiblioteca=# INSERT INTO roles (id, name) VALUES
basebiblioteca-#   (2, 'ADMIN'),
basebiblioteca-#   (4, 'BIBLIOTECARIO'),
basebiblioteca-#   (5, 'ALUMNO'),
basebiblioteca-#   (6, 'DOCENTE')
basebiblioteca-# ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name;
INSERT 0 4
basebiblioteca=#
basebiblioteca=# INSERT INTO permissions (name, role_id) VALUES
basebiblioteca-#   ('Super Usuario', 2),
basebiblioteca-#   ('Administración', 2),
basebiblioteca-#   ('Gestión Usuarios', 2),
basebiblioteca-#   ('Gestión Roles', 2),
basebiblioteca-#   ('Lectura', 4),
basebiblioteca-#   ('Préstamo', 4),
basebiblioteca-#   ('Devolución', 4),
basebiblioteca-#   ('Lectura', 5),
basebiblioteca-#   ('Préstamo', 5),
basebiblioteca-#   ('Lectura', 6),
basebiblioteca-#   ('Préstamo', 6),
basebiblioteca-#   ('Reserva', 6);
INSERT 0 12
basebiblioteca=# INSERT INTO users (id, nombre, email, password, rol, role_id, permisos, estado) VALUES
basebiblioteca-#   (6, 'Administrador', '1-1', '1234', 'ADMIN', 2, 'Super Usuario, Administración, Gestión Usuarios, Gestión Roles', 'Activo'),
basebiblioteca-#   (7, 'Bibliotecario', '2-2', '1234', 'BIBLIOTECARIO', 4, 'Lectura, Préstamo, Devolución', 'Activo'),
basebiblioteca-#   (8, 'Alumno', '3-3', '1234', 'ALUMNO', 5, 'Lectura, Préstamo', 'Activo'),
basebiblioteca-#   (9, 'Docente', '4-4', '1234', 'DOCENTE', 6, 'Lectura, Préstamo, Reserva', 'Activo');
INSERT 0 4
basebiblioteca=# client_loop: send disconnect: Connection reset

C:\WINDOWS\System32>ssh -i C:\KEY\ssh-key-2026-04-14.key opc@144.22.33.111
Last login: Thu Jun 25 19:29:31 2026 from 190.114.33.95
[opc@vnic-final ~]$ sudo -u postgres psql
could not change directory to "/home/opc": Permission denied
Password for user postgres:
psql (13.23)
Type "help" for help.

postgres=# \c basebiblioteca
You are now connected to database "basebiblioteca" as user "postgres".
basebiblioteca=# SELECT setval('users_id_seq', COALESCE((SELECT MAX(id) FROM users), 1), true);
 setval
--------
      9
(1 row)

basebiblioteca=# SELECT setval('roles_id_seq', COALESCE((SELECT MAX(id) FROM roles), 1), true);
 setval
--------
      6
(1 row)

basebiblioteca=# SELECT setval('permissions_id_seq', COALESCE((SELECT MAX(id) FROM permissions), 1), true);
 setval
--------
     21
(1 row)

basebiblioteca=# SELECT id, name FROM roles ORDER BY id;
 id |     name
----+---------------
  2 | ADMIN
  4 | BIBLIOTECARIO
  5 | ALUMNO
  6 | DOCENTE
(4 rows)

basebiblioteca=# SELECT id, role_id, name FROM permissions ORDER BY role_id, id;
 id | role_id |       name
----+---------+------------------
 10 |       2 | Super Usuario
 11 |       2 | Administración
 12 |       2 | Gestión Usuarios
 13 |       2 | Gestión Roles
 14 |       4 | Lectura
 15 |       4 | Préstamo
 16 |       4 | Devolución
 17 |       5 | Lectura
 18 |       5 | Préstamo
 19 |       6 | Lectura
 20 |       6 | Préstamo
 21 |       6 | Reserva
(12 rows)

basebiblioteca=# SELECT id, nombre, email, role_id, rol, permisos FROM users ORDER BY id;
 id |    nombre     | email | role_id |      rol      |
      permisos
----+---------------+-------+---------+---------------+----------------------
------------------------------------------
  6 | Administrador | 1-1   |       2 | ADMIN         | Super Usuario, Admini
stración, Gestión Usuarios, Gestión Roles
  7 | Bibliotecario | 2-2   |       4 | BIBLIOTECARIO | Lectura, Préstamo, De
volución
  8 | Alumno        | 3-3   |       5 | ALUMNO        | Lectura, Préstamo
  9 | Docente       | 4-4   |       6 | DOCENTE       | Lectura, Préstamo, Re
serva
(4 rows)

basebiblioteca=#