insert into hr_org_info(
	tuid
	,org_id
	,contact_phone
	,province
	,city
	,district
	,business_hours_begin
	,business_hours_end
	,business_hours_type
	,created
	,createdby
) values(
	${seq:nextval@seq_hr_org_info},
	${seq:currval@seq_hr_org},
	${fld:contact_phone},
	${fld:province},
	${fld:city},
	${fld:district},
	${fld:business_hours_begin},
	${fld:business_hours_end},
	'1',
	{ts '${def:timestamp}'},
	'${def:user}'
)
