UPDATE hr_menu_btn SET 
	btn_name = ${fld:btn_name}
	,btn_id = ${fld:btn_id}
	,updatedby = '${def:user}'
	,updated = {ts '${def:timestamp}'}
WHERE tuid = ${fld:tuid}
