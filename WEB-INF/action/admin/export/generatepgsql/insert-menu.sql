insert into ${schema}s_menu  
(
	menu_id,
	app_id,
	title,
	position
)
values 
(
	${seq:nextval@${schema}seq_menu},
	${seq:currval@${schema}seq_application},
	${fld:title},
	${fld:position}
);

