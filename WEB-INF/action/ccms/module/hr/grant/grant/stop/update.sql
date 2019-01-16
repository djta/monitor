update
	hr_grant
set
	status = '2'
	,terminate_time = {ts '${def:timestamp}'}
where 
	tuid = ${fld:id}
