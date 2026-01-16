create table reservas(
    id bigint not null auto_increment,
    usuario_id bigint not null,
    prestacion_id bigint not null,
    fecha_de_inicio datetime not null,
    fecha_de_fin datetime not null,
    estado varchar(100) not null,

    primary key(id),
    constraint fk_reservas_usuario_id foreign key (usuario_id) references usuarios(id),
    constraint fk_reservas_prestacion_id foreign key (prestacion_id) references prestaciones(id)
)