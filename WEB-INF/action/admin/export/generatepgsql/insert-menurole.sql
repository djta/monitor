insert into ${schema}s_menu_role
(menu_role_id, role_id, menu_id)

select
	${seq:nextval@${schema}seq_menu_role},
	r.role_id, 
	menu_id

from 
	${schema}s_role r,
	${schema}s_menu m
	where 
		r.rolename = ${fld:rolename} 
		and r.app_id = ${seq:currval@${schema}seq_application} 
		and m.title = ${fld:title} 
		and m.app_id = ${seq:currval@${schema}seq_application};
		

