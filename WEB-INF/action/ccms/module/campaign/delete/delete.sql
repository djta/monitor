update
	cs_campaign
set
	is_deleted = '1'
where 
    tuid = ${fld:id}
