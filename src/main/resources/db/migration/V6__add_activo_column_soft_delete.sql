ALTER TABLE prestaciones ADD activo BOOLEAN;
UPDATE prestaciones SET activo = 1;
ALTER TABLE prestaciones MODIFY activo BOOLEAN NOT NULL;

ALTER TABLE usuarios ADD activo BOOLEAN;
UPDATE usuarios SET activo = 1;
ALTER TABLE usuarios MODIFY activo BOOLEAN NOT NULL;