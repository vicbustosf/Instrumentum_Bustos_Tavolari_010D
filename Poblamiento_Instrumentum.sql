USE db_auth;
INSERT INTO roles (id, nombre_rol) VALUES
(1, 'ADMIN'),
(2, 'USER');

INSERT INTO usuarios (id, nombre_usuario, contrasena, correo) VALUES
(1, 'jgonzalez', '$2b$10$BOJ5pOh.LBkIHRD3YJ9TAeHwIv8baTNWCHa9zyhn5m0XCvFsfAZH.', 'jorge.gonzalez@instrumentum.cl'),
(2, 'cnarea',    '$2b$10$5K.qsqz4xFsJN9bM6nSFO.1oFc6ufKKsneK80w1..ZJ2wwy/rYbXi', 'claudio.narea@instrumentum.cl'),
(3, 'bcuevas',   '$2b$10$De1203dyHqB1T5cIO4Nqse0swMpTX3IrpbhUzSCRAZHTJ0f0z7/Nm', 'beto.cuevas@instrumentum.cl'),
(4, 'admin',     '$2b$10$fe3YveZat2zRBqES46ZgVeq0BXc.FeyAOXw72xlNLVkhcUy75uydy', 'admin@instrumentum.cl');

INSERT INTO usuario_roles (usuario_id, rol_id) VALUES
(1, 1),  -- jgonzalez -> ADMIN
(1, 2),  -- jgonzalez -> USER
(2, 2),  -- cnarea    -> USER
(3, 2),  -- bcuevas   -> USER
(4, 1);  -- admin     -> ADMIN


USE db_usuarios;
INSERT INTO banda (id_banda, nombre, fecha_registro) VALUES
(1, 'Los Prisioneros', '2024-03-15'),
(2, 'La Ley',           '2023-07-22'),
(3, 'Los Bunkers',      '2025-01-10');

INSERT INTO usuario (id_user, username, email, rol, id_banda) VALUES
(1, 'jorge_gonzalez',     'jorge.gonzalez@instrumentum.cl',     'Musico', 1),
(2, 'claudio_narea',      'claudio.narea@instrumentum.cl',      'Tech',   1),
(3, 'beto_cuevas',        'beto.cuevas@instrumentum.cl',        'Musico', 2),
(4, 'mauricio_clavero',   'mauricio.clavero@instrumentum.cl',   'Tech',   2),
(5, 'alvaro_lopez',       'alvaro.lopez@instrumentum.cl',       'Musico', 3),
(6, 'gonzalo_lopez',      'gonzalo.lopez@instrumentum.cl',      'Tech',   NULL);


USE db_inventario;
INSERT INTO marca (id, nombre) VALUES
(1, 'Fender'),
(2, 'Gibson'),
(3, 'Yamaha'),
(4, 'Shure'),
(5, 'Roland');

INSERT INTO categoria (id, nombre) VALUES
(1, 'Cuerdas'),
(2, 'Percusión'),
(3, 'Teclados'),
(4, 'Audio'),
(5, 'Viento');

INSERT INTO equipo (id, nombre, modelo, marca_id, categoria_id, propietario_id, tipo_propietario, tipo_equipo) VALUES
(1, 'Guitarra Stratocaster', 'American Professional II', 1, 1, 1, 'USUARIO', 'INSTRUMENTO'), -- dueño: jorge_gonzalez (usuario 1)
(2, 'Bajo Jazz Bass',        'Player Plus',              1, 1, 1, 'BANDA',   'INSTRUMENTO'), -- dueño: Los Prisioneros (banda 1)
(3, 'Batería Stage Custom',  'Birch',                    3, 2, 2, 'BANDA',   'INSTRUMENTO'), -- dueño: La Ley (banda 2)
(4, 'Micrófono SM58',        'Cardioide Dinámico',        4, 4, 3, 'BANDA',   'ELECTRONICO'), -- dueño: Los Bunkers (banda 3)
(5, 'Pedal Overdrive DS-1',  'Distortion',                5, 4, 2, 'USUARIO', 'ELECTRONICO'), -- dueño: claudio_narea (usuario 2)
(6, 'Teclado PSR-SX',        'Arranger Workstation',     3, 3, 3, 'USUARIO', 'INSTRUMENTO'); -- dueño: beto_cuevas (usuario 3)


USE db_specs;
INSERT INTO especificacion_instrumento (id_equipo, tipo_madera, config_pastillas, calibre_cuerdas) VALUES
(1, 'Caoba', 'HSS (Humbucker-Single-Single)', '0.010 - 0.046'),
(2, 'Fresno', 'Single-Single (Jazz Bass)',    '0.045 - 0.105');

INSERT INTO especificacion_electronica (id_equipo, voltaje, consumo, tipo_circuito) VALUES
(4, '48V Phantom Power',              5.0,    'Dinámico Cardioide'),
(5, '9V DC',                          150.0,  'Análogo / True Bypass'),
(6, '220V AC / Adaptador 12V', 1200.0, 'Digital / Fuente Conmutada');


USE db_mantenimiento;
INSERT INTO mantenimiento (id, equipo_id, fecha, description, costo) VALUES
(1, 1, '2025-07-10', 'Limpieza general y regulación de acción',                    18000.00),
(2, 1, '2026-01-15', 'Cambio de cuerdas y ajuste de trastes',                      25000.00),
(3, 2, '2026-02-20', 'Cambio de cuerdas de bajo',                                  30000.00),
(4, 3, '2025-11-05', 'Cambio de parches y afinación',                             45000.00),
(5, 5, '2026-03-01', 'Revisión de footswitch y limpieza de potenciómetros',         12000.00),
(6, 6, '2025-09-12', 'Actualización de firmware y limpieza de teclas',             15000.00);


USE db_rig;
INSERT INTO cancion (id, nombre, banda_id, duracion_segundos) VALUES
(1, 'El Baile de los que Sobran', 1, 300), -- Los Prisioneros
(2, 'La Voz de los 80',           1, 280), -- Los Prisioneros
(3, 'Mentira',                    2, 250), -- La Ley
(4, 'Veneno',                     2, 265), -- La Ley
(5, 'Llegando a Casa',            3, 240); -- Los Bunkers

INSERT INTO equipo_cancion (id, cancion_id, equipo_id, posicion, seteo_perillas) VALUES
(1, 1, 1, 1, 'Gain: 6, Treble: 7, Middle: 5, Bass: 6'),
(2, 1, 5, 2, 'Gain: 8, Tone: 6'),
(3, 2, 1, 1, 'Gain: 5, Treble: 6, Middle: 5, Bass: 7'),
(4, 3, 3, 1, 'Afinación estándar, parche resonante'),
(5, 4, 3, 1, 'Afinación Drop D');


USE db_eventos;
INSERT INTO evento (id_evento, id_banda, nombre, fecha, canciones) VALUES
(1, 1, 'Santiago Rock Festival 2026',          '2026-11-15', '1,2'),
(2, 2, 'La Ley en Vivo - Teatro Caupolicán',   '2026-09-05', '3,4'),
(3, 3, 'Los Bunkers Acústico',                 '2026-08-20', '5'),
(4, 1, 'Los Prisioneros en Concepción',        '2026-07-20', '1,2'),
(5, 2, 'La Ley en Viña del Mar',               '2026-09-20', '3,4');


USE db_finanza;
INSERT INTO transaccion (id_transaccion, id_banda, tipo_movimiento, monto, fecha, descripcion) VALUES
(1, 1, 'ingreso', 2500000.00, '2026-11-16', 'Pago por presentación en Santiago Rock Festival 2026'),
(2, 1, 'egreso',   350000.00, '2026-11-10', 'Transporte y alojamiento de la banda'),
(3, 2, 'ingreso', 4000000.00, '2026-09-06', 'Pago por show en Teatro Caupolicán'),
(4, 2, 'egreso',   500000.00, '2026-09-01', 'Arriendo de equipos de sonido'),
(5, 3, 'ingreso', 1200000.00, '2026-08-21', 'Pago por show acústico'),
(6, 3, 'egreso',   150000.00, '2026-08-18', 'Afinación y mantenimiento de instrumentos previo al show');


USE db_gira;
INSERT INTO gira (id_gira, id_banda, nombre_gira, fecha_inicio, fecha_fin) VALUES
(1, 1, 'Volver a los 17 Tour',        '2026-07-01', '2026-08-15'), -- Los Prisioneros
(2, 2, 'La Ley Tour Eléctrico',       '2026-09-01', '2026-09-30'); -- La Ley

INSERT INTO parada_gira (id_parada, id_gira, id_evento, ciudad, alojamiento, transporte) VALUES
(1, 1, 1, 'Santiago',      'Hotel Costanera, 5 habitaciones chb',          'Van privada para 8 personas'),
(2, 1, 4, 'Concepción',    'Hotel Diego de Almagro, 4 habitaciones',       'Bus de gira con trailer de equipos'),
(3, 2, 2, 'Santiago',      'Hotel Plaza San Francisco, 6 habitaciones',    'Van privada con remolque'),
(4, 2, 5, 'Viña del Mar',  'Hotel O''Higgins, 5 habitaciones',             'Bus de gira con trailer de equipos');


USE db_logistica;
INSERT INTO contenedor (id_contenedor, id_banda, nombre_caja, peso) VALUES
(1, 1, 'Flightcase de Guitarras Principales', 25.4), -- Los Prisioneros
(2, 2, 'Case de Percusión',                   40.0), -- La Ley
(3, 1, 'Case de Pedales y Efectos',           12.8); -- Los Prisioneros

INSERT INTO contenedor_equipo (id, contenedor_id, id_equipo) VALUES
(1, 1, 1), -- Guitarra Stratocaster en flightcase de guitarras
(2, 1, 2), -- Bajo Jazz Bass en flightcase de guitarras
(3, 2, 3), -- Batería Stage Custom en case de percusión
(4, 3, 5); -- Pedal Overdrive DS-1 en case de pedales


USE db_merchandising;
INSERT INTO producto_merch (id_producto, id_banda, nombre, tipo, precio, stock) VALUES
(1, 1, 'Polera Tour 2026',             'Polera', 15000.0, 48), -- 50 - 2 vendidas
(2, 1, 'Disco Corazones (Vinilo)',     'Disco',  25000.0, 19), -- 20 - 1 vendido
(3, 2, 'Gorro La Ley',                 'Gorro',   8000.0, 32), -- 35 - 3 vendidos
(4, 3, 'Parche Bordado Bunkers',       'Parche',  5000.0, 95); -- 100 - 5 vendidos

INSERT INTO venta_merch (id_venta, id_producto, cantidad, monto_total, id_evento_origen) VALUES
(1, 1, 2, 30000.0, 1),    -- 2 poleras vendidas en Santiago Rock Festival 2026
(2, 2, 1, 25000.0, 1),    -- 1 vinilo vendido en Santiago Rock Festival 2026
(3, 3, 3, 24000.0, 2),    -- 3 gorros vendidos en La Ley en Vivo - Teatro Caupolicán
(4, 4, 5, 25000.0, NULL); -- 5 parches vendidos fuera de un show (venta online/tienda)