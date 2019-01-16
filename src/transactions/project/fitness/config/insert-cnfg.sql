insert into cc_config
(
	tuid,
	category,
	param_value,
	param_text,
	remark,
	status,
	createdby,
	created,
	org_id
)

(
	select 
		${seq:nextval@seq_cc_config},
		category,
		${fld:param_text},
		param_text,
		remark,
		status,
	    '${def:user}',
	    {ts'${def:timestamp}'},
		${def:org}
	from cc_config
	where tuid = ${fld:tuid}
)
