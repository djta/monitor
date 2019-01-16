UPDATE hr_menu SET 
	menu_name = ${fld:menu_name}
	,menu_type = ${fld:menu_type}
	,icon_path = ${fld:icon_path}
	,icon_path2 = ${fld:icon_path2}
	,uri = ${fld:uri}
	,show_order = (CASE WHEN ${fld:show_order} IS NULL THEN 0 ELSE ${fld:show_order} END)
	,updatedby = '${def:user}'
	,updated = {ts '${def:timestamp}'}
WHERE tuid = ${fld:tuid}
