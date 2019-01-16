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
		param_value,
		param_text,
		remark,
		${fld:status},
	    '${def:user}',
	    {ts'${def:timestamp}'},
		${def:org}
	from cc_config
	where tuid = ${fld:cnfg_id}
)
