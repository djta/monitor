delete from 
	${schema}s_menu_item
where
	menu_id in(select menu_id from ${schema}s_menu where app_id = ${fld:id})