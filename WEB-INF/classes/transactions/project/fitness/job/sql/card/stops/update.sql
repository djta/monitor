update cc_savestopcard 
set 
	status = 2,
	enddate ='${def:date}'::date,
	updatedby = NULL,
	updated = {ts'${def:timestamp}'},
	factstopdays = '${def:date}'::date - startdate::date
where code = ${fld:code} 
and org_id = ${fld:org_id}
