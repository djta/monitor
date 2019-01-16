UPDATE
	hr_org_post
SET
	post_id = ${fld:post_id}
	,show_order = ${fld:show_order}
	,org_post_name = ${fld:org_post_name}
	,updated = {ts '${def:timestamp}'}
	,updatedby = '${def:user}'
	,pid=${fld:pid}
WHERE
	tuid = ${fld:tuid}