insert into hr_org_info
(
	tuid
	,org_id
	,business_hours_begin
	,business_hours_end
	,business_hours_type
	,created
	,createdby
	,updated 
	,updatedby 
)
values 
(
	 ${seq:nextval@seq_hr_org_info},
	${seq:currval@seq_hr_org},
	${fld:business_hours_begin},
	${fld:business_hours_end},
	'1',
	{ts '${def:timestamp}'},
	'${def:user}',
	{ts '${def:timestamp}'},
	'${def:user}'
)
