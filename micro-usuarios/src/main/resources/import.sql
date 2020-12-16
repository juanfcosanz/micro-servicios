INSERT INTO usuarios (username, password, enabled, nombre, apellido, email) VALUES ('juan','$2a$10$ykhXmCAam5FUEF9GN.4Z8OwwWJidvMii6VFYe77cmS2X6oF6p4W86',1, 'Juan', 'Sanz','juan@bolsadeideas.com');
INSERT INTO usuarios (username, password, enabled, nombre, apellido, email) VALUES ('admin','$2a$10$qGyDfZLBB.SgLv7GCP3uZe3oM38fVtr58T1iZ1LNOvO61loNUAAaK',1, 'John', 'Doe','jhon.doe@bolsadeideas.com');

INSERT INTO roles (nombre) VALUES ('ROLE_USER');
INSERT INTO roles (nombre) VALUES ('ROLE_ADMIN');

INSERT INTO permisos (usuario_id, role_id) VALUES (1, 1);
INSERT INTO permisos (usuario_id, role_id) VALUES (2, 2);
INSERT INTO permisos (usuario_id, role_id) VALUES (2, 1);
