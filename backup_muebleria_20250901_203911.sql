-- DATA-ONLY RESTORE (sin DDL). Inserta todo sobre esquema ya existente.

\set ON_ERROR_STOP on
BEGIN;

-- No disparar triggers/chequeos FK durante la carga
SET session_replication_role = replica;

-- Vaciar en orden hijo -> padre y reiniciar IDs
TRUNCATE TABLE
  public.detalle_factura,
  public.order_items,
  public.entrega_pedido,
  public.factura,
  public.orders,
  public.app_user,
  public.producto,
  public.proveedor,
  public.categoria,
  public.cliente,
  public.medio_pago,
  public.timbrado,
  public.stripe_events
RESTART IDENTITY;

-- =========================
-- ======   DATOS    =======
-- =========================

-- app_user
COPY public.app_user (id, password, role, username, cliente_id) FROM stdin;
2	$2a$10$VC.TS1DohITNJpgZbzpwuex1PA5hk8DI.rQhTd.o7BeF.yCie1962	CLIENT	test	1
1	$2a$10$6URpQD16cwgdfdxugykpDuz2Jgjj.hhJTkZVmOlvnIkyVktB/qHli	ADMIN	admin	2
\.

-- categoria
COPY public.categoria (id, nombre, slug) FROM stdin;
1	Sillas	sillas
2	Mesas	mesas
3	Camas	camas
4	Muebles	muebles
\.

-- cliente
COPY public.cliente (id, apellido, cedula, direccion, email, nombre, telefono, username) FROM stdin;
1	Test	7654321	Test	test@gmail.com	Tester	7654321	test
2	Sistema	0000000-0	Sistema	admin@sistema.com	Administrador	000-000-000	admin
\.

-- detalle_factura
COPY public.detalle_factura (id, cantidad, precio, factura_id, producto_id) FROM stdin;
1	1	602000	1	1
\.

-- entrega_pedido
COPY public.entrega_pedido (order_id, ciudad, departamento, direccion_linea1, direccion_linea2, entregado_en, estado_entrega, indicaciones, nombre_contacto, telefono) FROM stdin;
1	test	test	test	\N	\N	PENDIENTE	test	Tester	7654321
\.

-- factura
COPY public.factura (id, fecha, iva, subtotal, total, cliente_id, medio_pago_id, timbrado_id) FROM stdin;
1	2025-08-31 15:10:19.259	114380	602000	716380	1	4	1
\.

-- medio_pago
COPY public.medio_pago (id, tipo) FROM stdin;
1	Tarjeta de Crédito
2	Tarjeta de Débito
3	Pago QR
4	Stripe
\.

-- order_items
COPY public.order_items (id, product_id, product_name, qty, unit_price, order_id) FROM stdin;
1	1	ZAPATERA 3 PTAS HAVANA PLUS COLOR BLANCO	1	602000.00	1
\.

-- orders
COPY public.orders (id, created_at, currency, provider, provider_ref, status, total_amount, cliente_id) FROM stdin;
1	2025-08-31 15:09:50.417509	PYG	STRIPE	cs_test_a1RU79OH3mSSd8UHzOhr67zDcmB4e2N85W5IWeaZyp55UtWrTFHQ7ehLHt	PAID	602000.00	1
\.

-- producto
COPY public.producto (id, activo, created_at, descripcion, destacado, imagen_url, nombre, precio, stock, categoria_id, proveedor_id, updated_at) FROM stdin;
1	t	2025-08-31 14:32:08.830535	ZAPATERA 3 PTAS HAVANA PLUS COLOR BLANCO	t	https://www.mecalmuebles.com.py/storage/sku/a275e58c88c9aebca9ba8d2af048b103.png.webp	ZAPATERA 3 PTAS HAVANA PLUS COLOR BLANCO	602000	19	1	2	\N
3	t	2025-08-31 21:39:17.132529	SILLA P/OFICINA GIRATORIA CH-2019 AZUL CONSUMER	t	https://tupi.com.py/imagen_articulo/31306__600__600__SILLA-P-OFICINA-GIRATORIA-CH-2019-AZUL-CONSUMER	SILLA P/OFICINA GIRATORIA CH-2019 AZUL CONSUMER	1048000	1	1	2	\N
4	t	2025-08-31 23:18:14.406755	Silla Ejecutiva Giratoria Consumer 8953 3010K Negro	t	https://www.gonzalezgimenez.com.py/storage/sku/consumer-sillas-y-sofas-silla-ejecutiva-giratoria-consumer-8953-3010k-negro-1-1-1657634222.png.webp	Silla Ejecutiva Giratoria Consumer 8953 3010K Negro	1090000	6	1	2	\N
5	t	2025-08-31 23:19:16.682544	Silla Consumer CH-3004	f	https://www.gonzalezgimenez.com.py/storage/sku/09528001-09965893-537a04d9091ce-b_1.jpg.webp	Silla Consumer CH-3004	1286000	12	1	1	\N
6	t	2025-08-31 23:19:55.446977	Silla Consumer CH-9004	f	https://www.gonzalezgimenez.com.py/storage/sku/09545844-00198960-537a020ba3e97-b.jpg.webp	Silla Consumer CH-9004	1420000	15	1	2	\N
7	t	2025-08-31 23:20:49.671084	La silla Consumer CH-9008 combina diseño ergonómico y funcionalidad, ideal para espacios de trabajo u oficina en casa. Ofrece respaldo alto y ajuste de altura para una postura cómoda y adecuada durante largas jornadas.	f	https://www.gonzalezgimenez.com.py/storage/sku/consumer-sillas-y-sofas-silla-consumer-ch-9008-1-1-1616439845.jpg.webp	Silla Consumer CH-9008	1150000	12	1	1	\N
8	t	2025-08-31 23:24:05.232881	Silla Consumer Gamer Y-2706	f	https://www.gonzalezgimenez.com.py/storage/sku/00386793-00784672-5b869fb7d347f-b.jpg.webp	Silla Consumer Gamer Y-2706	1512000	13	1	2	\N
9	t	2025-08-31 23:25:19.551957	Silla Consumer Y-1846-C Black	f	https://www.gonzalezgimenez.com.py/storage/sku/consumer-sillas-y-sofas-silla-consumer-y-1846-c-black-1-1-1655754288.png.webp	Silla Consumer Y-1846-C Black	1105000	20	1	1	\N
10	t	2025-08-31 23:26:01.540149	Silla Consumer Gamer 7001 Racing Azul	f	https://www.gonzalezgimenez.com.py/storage/sku/consumer-sillas-y-sofas-silla-consumer-gamer-7001-racing-azul-1-1-1681415978.png.webp	Silla Consumer Gamer 7001 Racing Azul	1298000	25	1	1	\N
11	t	2025-08-31 23:26:39.83382	Butaca Consumer GY-1049 Blanco	f	https://www.gonzalezgimenez.com.py/storage/sku/consumer-sillas-para-oficina-butaca-consumer-gy-1049-blanco-1-1-1729191056.jpg.webp	Butaca Consumer GY-1049 Blanco	672000	33	1	2	\N
12	t	2025-08-31 23:27:07.896609	Silla Consumer CH-3008	f	https://www.gonzalezgimenez.com.py/storage/sku/consumer-sillas-para-oficina-silla-consumer-ch-3008-1-1-1731757895.png.webp	Silla Consumer CH-3008	1056000	30	1	1	\N
13	t	2025-08-31 23:27:47.069025	Silla Consumer Gamer Y-2706B Azul	f	https://www.gonzalezgimenez.com.py/storage/sku/consumer-sillas-y-sofas-silla-consumer-gamer-y-2706b-azul-1-1-1614280272.jpg.webp	Silla Consumer Gamer Y-2706B Azul	1100000	10	1	2	\N
14	t	2025-08-31 23:28:28.650499	Taburete Ergonómico Ajustable en Altura RC	f	https://www.gonzalezgimenez.com.py/storage/sku/rc-sillas-para-oficina-taburete-ergonomico-ajustable-en-altura-rc-1-1-1732738531.png.webp	Taburete Ergonómico Ajustable en Altura RC	1053000	7	1	2	\N
15	t	2025-08-31 23:28:59.289419	Taburete con Ruedas Ajustable en Altura RC	f	https://www.gonzalezgimenez.com.py/storage/sku/rc-sillas-para-oficina-taburete-con-ruedas-ajustable-en-altura-rc-1-1-1732738822.png.webp	Taburete con Ruedas Ajustable en Altura RC	936000	16	1	2	\N
16	t	2025-08-31 23:29:42.64372	Silla Operativa KB-2012 Negro	f	https://www.gonzalezgimenez.com.py/storage/sku/consumer-sillas-para-oficina-silla-operativa-kb-2012-negro-1-1-1731672972.png.webp	Silla Operativa KB-2012 Negro	1220000	11	1	1	\N
17	t	2025-08-31 23:30:16.79266	Silla Consumer Simple Black Y-1757	f	https://www.gonzalezgimenez.com.py/storage/sku/00024650-00110601-5b86a2d0365a1-b.jpg.webp	Silla Consumer Simple Black Y-1757	420000	2	1	1	\N
18	t	2025-08-31 23:30:41.188138	Silla Consumer CH-2019 Giratoria c/ rueda Negro	f	https://www.gonzalezgimenez.com.py/storage/sku/consumer-nuevo-silla-consumer-ch-2019-giratoria-c-rueda-negro-1-1-1731676446.png.webp	Silla Consumer CH-2019 Giratoria c/ rueda Negro	1129000	10	1	2	\N
19	t	2025-08-31 23:31:07.872916	Silla Ejecutiva CMR-SE KB-8955 AS	f	https://www.gonzalezgimenez.com.py/storage/sku/consumer-sillas-para-oficina-silla-ejecutiva-cmr-se-kb-8955-as-1-1-1731672084.png.webp	Silla Ejecutiva CMR-SE KB-8955 AS	1150000	10	1	2	\N
20	t	2025-08-31 23:31:37.469929	Silla Consumer Tipo Cajero CMR-TC 2012H Negro	f	https://www.gonzalezgimenez.com.py/storage/sku/consumer-sillas-para-oficina-silla-consumer-tipo-cajero-cmr-tc-2012h-negro-1-1-1731670829.png.webp	Silla Consumer Tipo Cajero CMR-TC 2012H Negro	1220000	8	1	2	\N
21	t	2025-08-31 23:33:34.429453	Sommier Inmapol 120x190 2 MESAS+CAB BEIGE	f	https://www.gonzalezgimenez.com.py/storage/sku/inmapol-sommiers-sommier-inmapol-120x190-2-mesascab-beige-1-1-1723058583.jpg.webp	Sommier Inmapol 120x190 2 MESAS+CAB BEIGE	1648000	11	3	1	\N
22	t	2025-08-31 23:34:23.199506	Sommier Inmapol Flex Pillow Top 1.40 x 1.90	t	https://www.gonzalezgimenez.com.py/storage/sku/inmapol-nuevo-sommier-inmapol-flex-pillow-top-140-x-190-1-1-1728495466.jpg.webp	Sommier Inmapol Flex Pillow Top 1.40 x 1.90	1538000	10	3	1	\N
23	t	2025-08-31 23:34:53.889655	Sommier Inmapol Flex Lite 1.40 x 1.90	f	https://www.gonzalezgimenez.com.py/storage/sku/inmapol-sommiers-sommier-inmapol-flex-lite-140-x-190-1-1-1738157173.jpg.webp	Sommier Inmapol Flex Lite 1.40 x 1.90	1211000	35	3	1	\N
24	t	2025-08-31 23:35:46.27523	Sommier Inmapol Jacard 2.00 x 2.00	t	https://www.gonzalezgimenez.com.py/storage/sku/inmapol-sommiers-sommier-inmapol-jacard-200-x-200-1-1-1620310342.jpg.webp	Sommier Inmapol Jacard 2.00 x 2.00	2270000	30	3	1	\N
26	t	2025-08-31 23:36:39.104244	Sommier Inmapol Flex Lite 1.40 x 1.90	f	https://www.gonzalezgimenez.com.py/storage/sku/inmapol-sommiers-sommier-inmapol-flex-lite-140-x-190-1-1-1617039291.jpg.webp	Sommier Inmapol Flex Lite 1.40 x 1.90	1211000	19	3	2	\N
27	t	2025-08-31 23:37:05.640611	Sommier Inmapol Flex Neo 1.40X1.90 Completo Bordo	f	https://www.gonzalezgimenez.com.py/storage/sku/inmapol-sommiers-sommier-inmapol-flex-neo-140x190-completo-bordo-1-1-1723061678.jpg.webp	Sommier Inmapol Flex Neo 1.40X1.90 Completo Bordo	1305000	21	3	1	\N
28	t	2025-08-31 23:37:41.269857	Sommier Inmapol Jacard 1.00 x 1.90	f	https://www.gonzalezgimenez.com.py/storage/sku/inmapol-sommiers-sommier-inmapol-jacard-100-x-190-1-1-1618781158.jpg.webp	Sommier Inmapol Jacard 1.00 x 1.90	1176000	39	3	2	\N
30	t	2025-08-31 23:38:34.671376	Sommier Inmapol Flex Lite 1.40 x 1.90	f	https://www.gonzalezgimenez.com.py/storage/sku/17-600x316.png.webp	Sommier Inmapol Flex Lite 1.40 x 1.90	1211000	19	3	1	\N
33	t	2025-08-31 23:40:06.473207	Sommier Inmapol Ultra Duo 1.40 x 1.90	f	https://www.gonzalezgimenez.com.py/storage/sku/sin-titulo-2.jpg.webp	Sommier Inmapol Ultra Duo 1.40 x 1.90	2153000	16	3	1	\N
36	t	2025-08-31 23:42:49.247472	Mesa De Noche Multimóveis - Blanco/Louro Freijo	f	https://www.gonzalezgimenez.com.py/storage/sku/multimoveis-muebles-para-living-mesa-de-noche-multimoveis-blancolouro-freijo-1-1-1746450821.jpg.webp	Mesa De Noche Multimóveis - Blanco/Louro Freijo	262000	12	2	1	\N
40	t	2025-08-31 23:47:37.448745	Mesa Office Canto NT2005 Novatel Nogal Trend	t	https://www.gonzalezgimenez.com.py/storage/sku/abba-muebles-mesa-office-canto-nt2005-novatel-nogal-trend-1-1-1715185624.jpg.webp	Mesa Office Canto NT2005 Novatel Nogal Trend	12760000	8	2	2	\N
29	t	2025-08-31 23:38:06.759209	Sommier Flex Neo Inmapol 1.20 x 1.90 Bordo	f	https://www.gonzalezgimenez.com.py/storage/sku/inmapol-sommiers-sommier-inmapol-flex-neo-120-x-190-bordo-1-1-1628607094.jpg.webp	Sommier Flex Neo Inmapol 1.20 x 1.90 Bordo	960000	17	3	2	2025-09-01 00:21:36.865383
31	t	2025-08-31 23:39:07.72542	Beliche Prisma J&A Blanco	f	https://www.gonzalezgimenez.com.py/storage/sku/abba-muebles-beliche-prisma-ja-blanco-1-1-1715115223.jpg.webp	Beliche Prisma J&A Blanco	964000	12	3	1	\N
32	t	2025-08-31 23:39:35.919192	Sommier Inmapol Ultra Lite 1.60 x 2.00	f	https://www.gonzalezgimenez.com.py/storage/sku/inmapol-sommiers-sommier-inmapol-ultra-lite-160-x-200-1-1-1666727193.png.webp	Sommier Inmapol Ultra Lite 1.60 x 2.00	2158000	10	3	2	\N
34	t	2025-08-31 23:40:40.621328	Cama Inflable Bestway 67628	t	https://www.gonzalezgimenez.com.py/storage/sku/bestway-accesorios-cama-inflable-bestway-67628-1-1-1662405422.png.webp	Cama Inflable Bestway 67628	426000	99	3	1	\N
35	t	2025-08-31 23:41:14.957559	Sommier Inmapol Jacard 1.60 x 2.00	f	https://www.gonzalezgimenez.com.py/storage/sku/inmapol-sommiers-sommier-inmapol-jacard-160-x-200-1-1-1620310063.jpg.webp	Sommier Inmapol Jacard 1.60 x 2.00	1793000	10	3	1	\N
37	t	2025-08-31 23:43:16.944748	Mesa de Noche Multimóveis con Cajón	t	https://www.gonzalezgimenez.com.py/storage/sku/multimoveis-muebles-mesa-de-noche-multimoveis-con-cajon-1-1-1746451539.jpg.webp	Mesa de Noche Multimóveis con Cajón	174000	19	2	1	\N
38	t	2025-08-31 23:43:48.02684	Juego de Mesa Henn Yara Bella 6 Sillas N/OW	t	https://www.gonzalezgimenez.com.py/storage/sku/henn-juegos-de-comedor-juego-de-mesa-henn-yara-bella-6-sillas-now-2-1-1756215448.jpg.webp	Juego de Mesa Henn Yara Bella 6 Sillas N/OW	2174000	17	2	1	\N
39	t	2025-08-31 23:44:29.826826	Mesa Multimóveis De Computo C/Cajon Rustic	t	https://www.gonzalezgimenez.com.py/storage/sku/multimoveis-muebles-de-oficina-mesa-multimoveis-de-computo-ccajon-rustic-1-1-1737029974.jpg.webp	Mesa Multimóveis De Computo C/Cajon Rustic	424000	19	2	2	\N
41	t	2025-08-31 23:48:43.190797	Mesa Tabla para Asado Tramontina 13313/050 Plegable	f	https://nissei.com/media/catalog/product/cache/24e3af3791642c18c52611620aeb2e21/c/e/celer_image_30_1_1.jpg	Mesa Tabla para Asado Tramontina 13313/050 Plegable	585185	18	2	1	\N
42	t	2025-08-31 23:49:28.041257	Mesa Cuadrada Plaxmetal Niala	t	https://nissei.com/media/catalog/product/cache/24e3af3791642c18c52611620aeb2e21/p/c/pc-coe-98269-1_1_.jpg	Mesa Cuadrada Plaxmetal Niala	408654	12	2	1	\N
43	t	2025-08-31 23:50:14.727896	Mesa Redonda Level Eames Kids 2212 - 60 cm	f	https://nissei.com/media/catalog/product/cache/24e3af3791642c18c52611620aeb2e21/p/c/pc-rga-lvs-063-ma-ph.jpg	Mesa Redonda Level Eames Kids 2212 - 60 cm	523000	18	4	2	\N
44	t	2025-08-31 23:51:21.374267	Mesa Abba Office 51015 Notavel	f	https://nissei.com/media/catalog/product/cache/b911b00b001dd445be642a5bd71957ac/m/e/mesa-office-51015-notavel-negro-abba-muebles.jpg	Mesa Abba Office 51015 Notavel	542042	25	2	2	\N
45	t	2025-08-31 23:51:59.581706	Mesa Abba Centro Intense Patrimar	f	https://nissei.com/media/catalog/product/cache/24e3af3791642c18c52611620aeb2e21/m/e/mesa-centro-intense-patrimar-negro-abba-muebles.jpg	Mesa Abba Centro Intense Patrimar	1002000	51	2	2	\N
46	t	2025-08-31 23:52:37.840385	Mesa Office Abba NT2010 Notavel	f	https://nissei.com/media/catalog/product/cache/24e3af3791642c18c52611620aeb2e21/m/e/mesa-office-nt2010-notavel-blanco-abba-muebles.jpg	Mesa Office Abba NT2010 Notavel	782293	7	2	2	\N
47	t	2025-08-31 23:53:26.035894	Mesa QRubber MP18070QR Plegable	f	https://nissei.com/media/catalog/product/cache/b911b00b001dd445be642a5bd71957ac/v/6/v6Hzter3qocZVeJDlDuJVwapJwDbLETRXOz.jpg	Mesa QRubber MP18070QR Plegable	573232	9	2	1	\N
48	t	2025-08-31 23:54:15.416931	Juego de Mesa CBC Milano con 6 Sillas - Caoba	f	https://nissei.com/media/catalog/product/cache/24e3af3791642c18c52611620aeb2e21/m/A/mAIgEXZS99k22X2MG5tDAuB5jhYyZY33fpv.jpg	Juego de Mesa CBC Milano con 6 Sillas - Caoba	2483136	16	2	2	\N
49	t	2025-08-31 23:54:56.291714	Juego de Mesa CBC Alaska con 4 Sillas	f	https://nissei.com/media/catalog/product/cache/24e3af3791642c18c52611620aeb2e21/v/T/vTf1dFOJ7d1CfB1BAItVy1cw0brhA4gzLrg.jpg	Juego de Mesa CBC Alaska con 4 Sillas	1439000	18	2	2	\N
50	t	2025-08-31 23:55:41.872155	Mesa Abba Office Notavel - Blanco	t	https://nissei.com/media/catalog/product/cache/24e3af3791642c18c52611620aeb2e21/m/e/mesa-office-notavel-blanco-abba-muebles.jpg	Mesa Abba Office Notavel - Blanco	768000	19	2	2	\N
25	t	2025-08-31 23:36:12.834885	Sommier Inmapol Flex Lite 1.40 x 1.91	t	https://www.gonzalezgimenez.com.py/storage/sku/inmapol-sommiers-sommier-inmapol-flex-lite-140-x-190-1-1-1617039291.jpg.webp	Sommier Inmapol Flex Lite 1.40 x 1.90	1211000	21	3	1	2025-09-01 00:20:55.944486
\.

-- proveedor
COPY public.proveedor (id, direccion, email, nombre, telefono, ciudad, ruc) FROM stdin;
2	Av. Principal, 1234, Asuncion	lunarisdesign@gmail.com	Lunaris Design	0981618903	\N	\N
1	Jose de Antequera, 1234	fuentesyasoc@gmail.com	Fuentes y Asociados	0981618902	Asuncion	666666-6
\.

-- stripe_events
COPY public.stripe_events (id, event_id, processed_at) FROM stdin;
\.

-- timbrado
COPY public.timbrado (id, actividad_economica, establecimiento, estado, fecha_inicio, fecha_vencimiento, numeracion_desde, numeracion_hasta, numero_autorizacion, numero_timbrado, punto_emision, razon_social, ruc, serie, tipo_documento) FROM stdin;
1	Venta de muebles y decoración	001	ACTIVO	2024-01-01	2025-12-31	1	999999	SET-AUTH-2024-001	17217403	001	LUNARIS MOBILIARIO	80012345-6	001	FACTURA
2	Venta de muebles y decoración	001	ACTIVO	2024-01-01	2025-12-31	1	999999	SET-AUTH-2024-002	17217404	001	LUNARIS MOBILIARIO	80012345-6	001	RECIBO
3	Venta de muebles y decoración	001	VENCIDO	2023-01-01	2023-12-31	1	500000	SET-AUTH-2023-001	16217401	001	LUNARIS MOBILIARIO	80012345-6	001	FACTURA
\.

-- =========================
-- =====  SECUENCIAS  ======
-- =========================
SELECT setval('public.app_user_id_seq',      COALESCE((SELECT MAX(id) FROM public.app_user),      1), true);
SELECT setval('public.categoria_id_seq',     COALESCE((SELECT MAX(id) FROM public.categoria),     1), true);
SELECT setval('public.cliente_id_seq',       COALESCE((SELECT MAX(id) FROM public.cliente),       1), true);
SELECT setval('public.detalle_factura_id_seq',COALESCE((SELECT MAX(id) FROM public.detalle_factura),1), true);
SELECT setval('public.factura_id_seq',       COALESCE((SELECT MAX(id) FROM public.factura),       1), true);
SELECT setval('public.medio_pago_id_seq',    COALESCE((SELECT MAX(id) FROM public.medio_pago),    1), true);
SELECT setval('public.order_items_id_seq',   COALESCE((SELECT MAX(id) FROM public.order_items),   1), true);
SELECT setval('public.orders_id_seq',        COALESCE((SELECT MAX(id) FROM public.orders),        1), true);
SELECT setval('public.producto_id_seq',      COALESCE((SELECT MAX(id) FROM public.producto),      1), true);
SELECT setval('public.proveedor_id_seq',     COALESCE((SELECT MAX(id) FROM public.proveedor),     1), true);
SELECT setval('public.stripe_events_id_seq', COALESCE((SELECT MAX(id) FROM public.stripe_events), 1), true);
SELECT setval('public.timbrado_id_seq',      COALESCE((SELECT MAX(id) FROM public.timbrado),      1), true);

-- Reactivar triggers/RI y cerrar
SET session_replication_role = DEFAULT;
COMMIT;
