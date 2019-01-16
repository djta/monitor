insert into ${schema}s_menu_item  
(
	menu_item_id,
	service_id,
	menu_id,
	position,
	logo_path
)
values 
(
	${seq:nextval@${schema}seq_menu_item},
	${fld:service_id},
	${fld:menu_id},
	${fld:position},
	${fld:logo_path}
)
