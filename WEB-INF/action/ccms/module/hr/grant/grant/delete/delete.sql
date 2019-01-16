update
	hr_grant
set
	is_deleted = '1'
where 
	tuid = ${fld:id}