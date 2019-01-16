INSERT INTO ${schema}s_panel
(
	panel_id,
	service_id,
	icon_path,
	orden,
	app_id
)
VALUES
(
	${seq:nextval@${schema}seq_default},
	${fld:service_id},
	${fld:icon_path},
	${fld:orden},
	${fld:app_id}
)
