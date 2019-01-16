INSERT	INTO
	hr_headcount
(
	hc_id
	,hc_name
	,org_post_id
	,show_order
	,created
	,createdby
)
VALUES
(
	 ${seq:nextval@seq_hr_headcount}
	,${fld:hc_name}
	,${fld:org_post_id}
	,${fld:show_order}
	, {ts '${def:timestamp}'}
	,'${def:user}'
)