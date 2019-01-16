UPDATE hr_menu_btn SET 
	is_deleted = 1
	,updatedby = '${def:user}'
	,updated = {ts '${def:timestamp}'}
WHERE tuid = ${fld:id}
