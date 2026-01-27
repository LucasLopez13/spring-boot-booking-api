ALTER TABLE usuarios ADD role VARCHAR(20);

UPDATE usuarios SET role = 'USER';

ALTER TABLE usuarios MODIFY role VARCHAR(20) NOT NULL;