UPDATE
	hr_headcount
SET
	hc_name = ${fld:hc_name}
	,show_order = ${fld:show_order}
	,updated = {ts '${def:timestamp}'}
	,updatedby = '${def:user}'
WHERE
	hc_id = ${fld:tuid}