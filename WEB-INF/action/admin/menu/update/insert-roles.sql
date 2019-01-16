insert into ${schema}s_menu_role  
(
	menu_role_id,
	menu_id,
	role_id
)
values 
(
	${seq:nextval@${schema}seq_menu_role},
	${fld:tuid},
	${fld:role_id}
)
