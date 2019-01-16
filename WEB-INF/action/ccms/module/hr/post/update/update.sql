UPDATE
	hr_post
SET
	post_name=${fld:post_name}
	,show_order=${fld:show_order}
	,updated={ts '${def:timestamp}'}
	,updatedby='${def:user}'
WHERE
	post_id	=${fld:tuid}