update 
	${schema}s_menu_item 
set
	position = ${fld:position}
	,logo_path = ${fld:logo_path}
where
	menu_item_id = ${fld:tuid}